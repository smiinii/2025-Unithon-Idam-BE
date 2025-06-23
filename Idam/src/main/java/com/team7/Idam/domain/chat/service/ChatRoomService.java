package com.team7.Idam.domain.chat.service;

import com.team7.Idam.domain.chat.dto.ChatRoomRequestDto;
import com.team7.Idam.domain.chat.dto.ChatRoomResponseDto;
import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.chat.repository.ChatMessageRepository;
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
    private final ChatMessageRepository chatMessageRepository;

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

    // 2. 기업의 채팅방 목록 조회 (DTO 반환)
    public List<ChatRoomResponseDto> getCompanyChatRooms(User company) {
        validateCompanyAccess();
        return chatRoomRepository.findByCompanyAndIsDeletedByCompanyFalse(company).stream()
                .map(room -> {
                    int unreadCount = chatMessageRepository
                            .countByChatRoomAndSenderNotAndIsReadFalse(room, company);
                    return ChatRoomResponseDto.from(room, company, unreadCount);
                })
                .collect(Collectors.toList());
    }

    // 3. 학생의 채팅방 목록 조회 (DTO 반환)
    public List<ChatRoomResponseDto> getStudentChatRooms(User student) {
        validateStudentAccess();
        return chatRoomRepository.findByStudentAndIsDeletedByStudentFalse(student).stream()
                .map(room -> {
                    int unreadCount = chatMessageRepository
                            .countByChatRoomAndSenderNotAndIsReadFalse(room, student);
                    return ChatRoomResponseDto.from(room, student, unreadCount);
                })
                .collect(Collectors.toList());
    }

    // 4. 기업이 채팅방 soft delete
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

    // 5. 학생이 채팅방 soft delete
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
}
