package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),
    USERS_EMPTY_USER_PW(false, 2011, "유저 비밀번호 값을 확인해주세요."),
    USERS_ID_NOT_EXISTS(false,2012, "존재하지않는 아이디입니다."),
    USERS_IDX_NOT_EXISTS(false,2013, "존재하지않는 유저입니다."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_EMPTY_ID(false,2018, "아이디를 입력해주세요."),
    POST_USERS_EMPTY_PW(false,2019, "패스워드를 입력해주세요."),
    POST_USERS_EMPTY_PHONE_EMAIL(false,2020, "전화번호 또는 이메일을 입력해주세요."),
    POST_USERS_INVALID_PHONE(false, 2021, "전화번호 형식을 확인해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2022, "비밀번호 형식을 확인해주세요."),
    POST_USERS_EXISTS_ID(false,2023, "중복된 아이디입니다."),
    POST_USERS_EXISTS_PHONE(false, 2024, "중복된 전화번호입니다."),

    // [POST] /storys
    POST_STORY_EMPTY_VIDEO_URL(false, 2025, "사진이나 동영상을 업로드해주세요."),
    DELETE_STORY_NOT_EXISTS(false,2026, "존재하지않는 스토리입니다."),

    // [POST] /posts
    POST_POSTS_EMPTY_IMAGE(false, 2030, "사진이나 동영상을 업로드해주세요."),
    DELETE_POSTS_NOT_EXISTS(false, 2031, "존재하지않는 게시물입니다."),

    // [PATCH] /posts
    PATCH_POSTS_NOT_EXISTS(false, 2040, "존재하지않는 게시물입니다."),

    // [GET] /search/history/type=
    NOT_EXISTS_SEARCH_TYPE(false, 2050, "존재하지않는 검색타입입니다."),

    INVALID_USERS_STATUS(false, 2060, "잘못된 계정 상태입니다."),

    DELETE_SEARCH_HISTORY_NOT_EXISTS(false, 2070, "존재하지않는 검색기록입니다."),

    // [POST] /highlights
    POST_HIGHLIGHT_EMPTY_STORY(false, 2100, "하이라이트에 게시할 스토리를 선택해주세요."),
    POST_HIGHLIGHT_EMPTY_THUMBNAIL(false, 2101, "썸네일 이미지를 업로드해주세요."),
    HIGHLIGHT_NOT_EXISTS(false, 2110, "존재하지않는 하이라이트입니다."),

    // [DELETE] /follows
    FOLLOWS_NOT_EXISTS(false, 2200, "팔로우 상태가 아닙니다."),

    SEARCH_EMPTY_KEYWORD(false, 2300, "검색어를 입력해주세요."),

    /**
     * 3000 : Response 오류 - 이메일이 중복이되었다면? -> 존재하지않는 사용자라면 3000번대 에러다.
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
