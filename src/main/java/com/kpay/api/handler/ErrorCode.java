package com.kpay.api.handler;

import lombok.Getter;

@Getter
public enum ErrorCode  {
    E1000("1000", "잘못된 요청입니다."),
    E2000("2000", "뿌릴금액보다 뿌릴 인원이 더 많습니다."),
    E3000("3000", "만료되었거나 존재하지 않는 받기 입니다."),
    E4000("4000", "본인이 뿌리기 한 금액은 받을 수 없습니다."),
    E5000("5000", "이미 받으셨습니다. 다음 뿌리기를 기다려주세요^^"),
    E6000("6000","만료되었거나 받기가 완료된 건입니다."),
    E7000("6000","본인이외 뿌리기 조회하실 수 없습니다.")
;

    ErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }

    private String code;
    private String message;

}
