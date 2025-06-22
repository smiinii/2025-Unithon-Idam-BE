package com.team7.Idam.global.event;

import com.team7.Idam.domain.user.repository.TagCategoryRepository;
import com.team7.Idam.domain.user.repository.TagOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
@RequiredArgsConstructor
public class DbReadyLogger {

    private final TagCategoryRepository tagCategoryRepository;
    private final TagOptionRepository tagOptionRepository;

    /**
     * 애플리케이션이 완전히 초기화되고 DB가 모두 준비된 뒤 실행되는 메서드입니다.
     * 여기서 DB 상태나 초기화 완료 로그를 찍을 수 있습니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        long categoryCount = tagCategoryRepository.count();
        long tagCount = tagOptionRepository.count();

        System.out.println("\n\n🎉 [IDam] 서버 초기화 및 DB 로딩 완료!");
        System.out.println("🚀 이담 서비스를 시작합니다!\n");
    }
}
