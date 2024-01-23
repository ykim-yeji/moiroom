package com.ssafy.moiroomserver.global.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    //일반
    NOT_EXISTS_ID(BAD_REQUEST, "존재하지 않는 id 입니다.");

    private final HttpStatus status;
    private final String message;
}