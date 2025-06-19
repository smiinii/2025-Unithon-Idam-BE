package com.team7.Idam.domain.chat.controller;

import com.team7.Idam.domain.chat.dto.ChatMessageResponseDto;
import com.team7.Idam.domain.chat.dto.ChatRoomRequestDto;
import com.team7.Idam.domain.chat.dto.ChatRoomResponseDto;
import com.team7.Idam.domain.chat.dto.SendMessageRequestDto;
import com.team7.Idam.domain.chat.service.ChatMessageService;
import com.team7.Idam.domain.chat.service.ChatRoomService;
import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.service.UserService;
import com.team7.Idam.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final UserService userService;

    // 채팅방 생성 (기업이 학생과)
    @PostMapping("/room")
    public ResponseEntity<ChatRoomResponseDto> createRoom(
            @RequestParam Long targetUserId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatRoomRequestDto request
    ) {
        User managedCompany = userService.getUserById(userDetails.getId());
        User managedStudent = userService.getUserById(targetUserId);
        System.out.println(">> 로그인한 유저 ID: " + managedCompany.getId());
        return ResponseEntity.ok(chatRoomService.createRoom(managedCompany, managedStudent, request));
    }

    // 채팅방 목록 조회 (기업)
    @GetMapping("/rooms/company")
    public ResponseEntity<List<ChatRoomResponseDto>> getCompanyRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User company = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(chatRoomService.getCompanyChatRooms(company));
    }

    // 채팅방 목록 조회 (학생)
    @GetMapping("/rooms/student")
    public ResponseEntity<List<ChatRoomResponseDto>> getStudentRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User student = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(chatRoomService.getStudentChatRooms(student));
    }

    // 채팅방 메시지 조회
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(chatMessageService.getMessagesByRoom(roomId, user));
    }

    // 메시지 전송
    @PostMapping("/room/{roomId}/message")
    public ResponseEntity<ChatMessageResponseDto> sendMessage(
            @PathVariable Long roomId,
            @RequestBody SendMessageRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User sender = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(chatMessageService.sendMessage(roomId, sender, request.getContent()));
    }

    // 채팅방 삭제 (기업)
    @DeleteMapping("/room/{roomId}/company")
    public ResponseEntity<Void> deleteRoomByCompany(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User company = userService.getUserById(userDetails.getId());
        chatRoomService.deleteRoomByCompany(roomId, company);
        return ResponseEntity.ok().build();
    }

    // 채팅방 삭제 (학생)
    @DeleteMapping("/room/{roomId}/student")
    public ResponseEntity<Void> deleteRoomByStudent(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User student = userService.getUserById(userDetails.getId());
        chatRoomService.deleteRoomByStudent(roomId, student);
        return ResponseEntity.ok().build();
    }
}
