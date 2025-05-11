package com.team7.Idam.domain.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiTagResultDto {
    private String domain;
    private String role;

    @Builder.Default
    private List<String> languages = new ArrayList<>();

    @Builder.Default
    private Map<String, List<String>> frameworks = new HashMap<>();

    @Builder.Default
    private Map<String, List<String>> tools = new HashMap<>();
}
