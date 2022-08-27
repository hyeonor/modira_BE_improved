package com.example.modiraa.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    NICKNAME_DUPLICATION_CODE(400, "C001", "중복된 닉네임이 있습니다"),
    ID_DUPLICATION_CODE(400, "C002", "중복된 유저 입니다."),
    LENGTH_CHECK_CODE(400, "C003", "아이디를 2-8자로 입력해 주세요");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) { //enum 은 생성자가 존재하지만 Default 생성자는 private 로 되어 있으며 public 으로 변경하는 경우 컴파일 에러가 발생
        //다른 클래스나 인터페이스에서의 상수선언이 클래스 로드 시점에서 생성되는 것 처럼 Enum 또한 생성자가 존재하지만
        // 클래스가 로드되는 시점에서 생성되기 때문에 임의로 생성하여 사용 할 수 없다
        this.status = status;
        this.code = code;
        this.message = message;
    }
}