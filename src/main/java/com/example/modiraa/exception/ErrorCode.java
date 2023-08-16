package com.example.modiraa.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "C001", "유효하지 않거나 만료된 리프레시 토큰입니다."),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "C002", "리프레시 토큰의 유저 정보가 일치하지 않습니다."),
    AGE_CONDITION_NOT_MET(BAD_REQUEST, "C003", "회원의 나이가 조건을 만족하지 않습니다."),
    GENDER_CONDITION_NOT_MET(BAD_REQUEST, "C004", "회원의 성별이 조건을 만족하지 않습니다."),
    PARTICIPATION_EXISTENCE(BAD_REQUEST, "C005", "이미 참여 중인 모임이나 작성한 모임이 존재합니다."),
    ROOM_FULL_CAPACITY(BAD_REQUEST, "C006", "모임 인원이 마감 되었습니다."),
    SELF_RATING_NOT_ALLOWED(BAD_REQUEST, "C007", "자기 자신을 평가할 수 없습니다."),
    DUPLICATE_RATING(BAD_REQUEST, "C008", "이미 이 유저에게 좋아요 또는 싫어요를 평가했습니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "C009", "권한 정보가 없는 토큰입니다."),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    PERMISSION_DENIED(FORBIDDEN, "C010", "모임을 삭제할 권한이 없습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(NOT_FOUND, "C011", "해당 유저 정보를 찾을 수 없습니다."),
    POST_NOT_FOUND(NOT_FOUND, "C012", "해당 게시글을 찾을 수 없습니다."),
    ROOM_CODE_NOT_FOUND(NOT_FOUND, "C013", "해당 방 코드를 찾을 수 없습니다."),
    MEMBER_ROOM_NOT_FOUND(NOT_FOUND, "C014", "회원의 방 정보를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "C015", "로그아웃 된 사용자입니다."),
    NOT_FOLLOW(NOT_FOUND, "C016", "팔로우 중이지 않습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    ALREADY_JOINED_ROOM(CONFLICT, "C017", "이미 해당 방에 참여한 회원입니다."),
    DUPLICATE_RESOURCE(CONFLICT, "C018", "데이터가 이미 존재합니다");


    private final HttpStatus status;
    private final String code;
    private final String message;

    /**
     * enum 은 생성자가 존재하지만 Default 생성자는 private 로 되어 있으며 public 으로 변경하는 경우 컴파일 에러가 발생
     * 다른 클래스나 인터페이스에서의 상수선언이 클래스 로드 시점에서 생성되는 것 처럼 Enum 또한 생성자가 존재하지만
     * 클래스가 로드되는 시점에서 생성되기 때문에 임의로 생성하여 사용 할 수 없다
     */
    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}