package com.team7.Idam.domain.chat.service;

import com.team7.Idam.domain.chat.dto.ChatRoomRequestDto;
import com.team7.Idam.domain.chat.dto.ChatRoomResponseDto;
import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.chat.repository.ChatRoomRepository;
import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.entity.enums.UserType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team7.Idam.global.util.SecurityUtil.getCurrentUserType;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public static void validateCompanyAccess() {
        if (getCurrentUserType() != UserType.COMPANY) {
            throw new AccessDeniedException("해당 기능은 기업 회원만 사용할 수 있습니다.");
        }
    }

    public static void validateStudentAccess() {
        if (getCurrentUserType() != UserType.STUDENT) {
            throw new AccessDeniedException("해당 기능은 학생 회원만 사용할 수 있습니다.");
        }
    }

    // 1. 새로운 채팅방 생성
    public ChatRoomResponseDto createRoom(User company, User student, ChatRoomRequestDto request) {
        validateCompanyAccess(); // 기업 타입만 실행 가능
        ChatRoom chatRoom = ChatRoom.builder()
                .company(company)
                .student(student)
                .projectTitle(request.getProjectTitle())
                .build();
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.from(savedRoom, company);
    }

    // 2. 기존에 살아있는 채팅방 조회 (DTO 반환)
    public Optional<ChatRoomResponseDto> getActiveRoom(User company, User student) {
        return chatRoomRepository.findActiveRoomByUsers(company, student)
                .map(room -> ChatRoomResponseDto.from(room, company));
    }

    // 3. 기업의 채팅방 목록 조회 (DTO 반환)
    public List<ChatRoomResponseDto> getCompanyChatRooms(User company) {
        validateCompanyAccess(); // 기업 타입만 실행 가능
        return chatRoomRepository.findByCompanyAndIsDeletedByCompanyFalse(company).stream()
                .map(room -> ChatRoomResponseDto.from(room, company))
                .collect(Collectors.toList());
    }

    // 4. 학생의 채팅방 목록 조회 (DTO 반환)
    public List<ChatRoomResponseDto> getStudentChatRooms(User student) {
        validateStudentAccess(); // 학생 타입만 실행 가능
        return chatRoomRepository.findByStudentAndIsDeletedByStudentFalse(student).stream()
                .map(room -> ChatRoomResponseDto.from(room, student))
                .collect(Collectors.toList());
    }

    // 5. 기업이 채팅방 soft delete
    @Transactional
    public void deleteRoomByCompany(Long roomId, User company) {
        validateCompanyAccess(); // 기업 타입만 실행 가능
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        if (!room.getCompany().equals(company)) {
            throw new SecurityException("해당 기업의 채팅방이 아닙니다.");
        }
        room.deleteByCompany();
    }

    // 6. 학생이 채팅방 soft delete
    @Transactional
    public void deleteRoomByStudent(Long roomId, User student) {
        validateStudentAccess(); // 학생 타입만 실행 가능
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        if (!room.getStudent().equals(student)) {
            throw new SecurityException("해당 학생의 채팅방이 아닙니다.");
        }
        room.deleteByStudent();
    }

    // 7. 기업이 학생 닉네임으로 채팅방 검색 (DTO 반환)
    public List<ChatRoomResponseDto> searchRoomsByStudentNickname(User company, String keyword) {
        validateCompanyAccess(); // 기업 타입만 실행 가능
        return chatRoomRepository.searchRoomsByStudentNickname(company, keyword).stream()
                .map(room -> ChatRoomResponseDto.from(room, company))
                .collect(Collectors.toList());
    }

    // 8. 학생이 기업 이름으로 채팅방 검색 (DTO 반환)
    public List<ChatRoomResponseDto> searchRoomsByCompanyName(User student, String keyword) {
        validateStudentAccess(); // 학생 타입만 실행 가능
        return chatRoomRepository.searchRoomsByCompanyName(student, keyword).stream()
                .map(room -> ChatRoomResponseDto.from(room, student))
                .collect(Collectors.toList());
    }
}
