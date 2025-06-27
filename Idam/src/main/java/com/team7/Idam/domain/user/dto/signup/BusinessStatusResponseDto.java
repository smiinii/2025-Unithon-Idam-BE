package com.team7.Idam.domain.user.dto.signup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessStatusResponseDto {
    private String b_no;         // 사업자번호
    private String b_stt;        // 상태 (예: 계속사업자, 폐업자 등)
    private String b_stt_cd;
    private String tax_type;     // 과세 유형
    private String tax_type_cd;
    private String end_dt;
    private String utcc_yn;
    private String rbf_tax_type;
    private String rbf_tax_type_cd;
}
