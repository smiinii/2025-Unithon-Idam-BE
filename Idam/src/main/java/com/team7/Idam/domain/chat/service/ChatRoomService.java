package com.team7.Idam.domain.chat.service;

import com.team7.Idam.domain.chat.dto.ChatMessageResponseDto;
import com.team7.Idam.domain.chat.dto.ChatRoomRequestDto;
import com.team7.Idam.domain.chat.dto.ChatRoomResponseDto;
import com.team7.Idam.domain.chat.entity.ChatMessage;
import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.chat.repository.ChatMessageRepository;
import com.team7.Idam.domain.chat.repository.ChatRoomRepository;
import com.team7.Idam.domain.notification.entity.enums.NotificationType;
import com.team7.Idam.domain.notification.service.NotificationService;
import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.entity.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team7.Idam.global.util.SecurityUtil.getCurrentUserType;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationService notificationService;

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
        validateCompanyAccess();

        Optional<ChatRoom> existingRoom =
                chatRoomRepository.findByCompanyAndStudentAndProjectTitle(company, student, request.getProjectTitle());

        if (existingRoom.isPresent()) {
            return ChatRoomResponseDto.from(existingRoom.get(), company);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .company(company)
                .student(student)
                .projectTitle(request.getProjectTitle())
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // ✅ 알림 전송 (채팅방 생성 후)
        notificationService.createAndSend(
                student,                          // 학생에게 알림 보냄
                savedRoom,                        // 새로 생성된 채팅방
                "새 채팅방이 생성되었습니다.",      // 알림 메시지
                NotificationType.NEW_CHAT_ROOM   // 알림 타입
        );

        return ChatRoomResponseDto.from(savedRoom, company);
    }

    private Map<Long, Integer> getUnreadMap(User user) {
        List<ChatMessageRepository.UnreadCountProjection> unreadCounts =
                chatMessageRepository.findUnreadCountsForUser(user.getId());

        return unreadCounts.stream()
                .collect(Collectors.toMap(
                        ChatMessageRepository.UnreadCountProjection::getRoomId,
                        ChatMessageRepository.UnreadCountProjection::getUnreadCount
                ));
    }

    private Map<Long, ChatMessage> getLastMessageMap(List<ChatRoom> rooms) {
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessagesForRooms(rooms);

        return lastMessages.stream()
                .collect(Collectors.toMap(
                        m -> m.getChatRoom().getId(),
                        m -> m
                ));
    }

    // 2. 기업 채팅방 목록 조회
    public List<ChatRoomResponseDto> getCompanyChatRooms(User company) {
        validateCompanyAccess();

        List<ChatRoom> rooms = chatRoomRepository.findByCompanyAndIsDeletedByCompanyFalse(company);
        Map<Long, Integer> unreadMap = getUnreadMap(company);
        Map<Long, ChatMessage> lastMessageMap = getLastMessageMap(rooms);

        return rooms.stream()
                .map(room -> {
                    int unreadCount = unreadMap.getOrDefault(room.getId(), 0);
                    ChatMessage lastMessage = lastMessageMap.get(room.getId());
                    ChatMessageResponseDto messageDto = lastMessage != null
                            ? ChatMessageResponseDto.from(lastMessage)
                            : null;
                    return ChatRoomResponseDto.from(room, company, unreadCount, messageDto);
                })
                .collect(Collectors.toList());
    }

    // 3. 학생 채팅방 목록 조회
    public List<ChatRoomResponseDto> getStudentChatRooms(User student) {
        validateStudentAccess();

        List<ChatRoom> rooms = chatRoomRepository.findByStudentAndIsDeletedByStudentFalse(student);
        Map<Long, Integer> unreadMap = getUnreadMap(student);
        Map<Long, ChatMessage> lastMessageMap = getLastMessageMap(rooms);

        return rooms.stream()
                .map(room -> {
                    int unreadCount = unreadMap.getOrDefault(room.getId(), 0);
                    ChatMessage lastMessage = lastMessageMap.get(room.getId());
                    ChatMessageResponseDto messageDto = lastMessage != null
                            ? ChatMessageResponseDto.from(lastMessage)
                            : null;
                    return ChatRoomResponseDto.from(room, student, unreadCount, messageDto);
                })
                .collect(Collectors.toList());
    }

    // 4. 기업 soft delete
    @Transactional
    public void deleteRoomByCompany(Long roomId, User company) {
        validateCompanyAccess();
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        if (!room.getCompany().equals(company)) {
            throw new SecurityException("해당 기업의 채팅방이 아닙니다.");
        }
        room.deleteByCompany();
    }

    // 5. 학생 soft delete
    @Transactional
    public void deleteRoomByStudent(Long roomId, User student) {
        validateStudentAccess();
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        if (!room.getStudent().equals(student)) {
            throw new SecurityException("해당 학생의 채팅방이 아닙니다.");
        }
        room.deleteByStudent();
    }
}
