package Uniton.Fring.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류가 발생했습니다."),
    MISSING_PART(HttpStatus.BAD_REQUEST, 400, "요청에 필요한 부분이 없습니다."),
    NO_HANDLER_FOUND(HttpStatus.NOT_FOUND, 404, "요청하신 API가 존재하지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 405, "지원하지 않는 HTTP 메서드입니다."),

    // Jwt
    JWT_NOT_VALID(HttpStatus.UNAUTHORIZED, 401, "[Jwt] 유효하지 않은 Jwt"),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 419, "[Jwt] 만료된 엑세스 토큰입니다."),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 420, "[Jwt] 만료된 리프레시 토큰입니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, 401, "[Jwt] 잘못된 토큰 형식입니다."),
    JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, 401, "[Jwt] 유효하지 않은 서명입니다."),
    JWT_UNSUPPORTED(HttpStatus.UNAUTHORIZED, 401, "[Jwt] 지원하지 않는 토큰입니다."),
    JWT_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "[Jwt] 리프레시 토큰 조회 실패"),
    JWT_NOT_MATCH(HttpStatus.BAD_REQUEST, 400, "[Jwt] 리프레시 토큰 불일치"),
    JWT_ENTRY_POINT(HttpStatus.UNAUTHORIZED, 401, "[Jwt] 인증되지 않은 사용자입니다."),
    JWT_ACCESS_DENIED(HttpStatus.FORBIDDEN, 403, "[Jwt] 리소스에 접근할 권한이 없습니다."),

    // Validation
    VALIDATION_FAILED(HttpStatus.BAD_GATEWAY, 400, "요청한 값이 올바르지 않습니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "회원을 찾을 수 없습니다."),
    LOGIN_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 이메일입니다."),
    PASSWORD_NOT_CORRECT(HttpStatus.BAD_REQUEST, 400, "비밀번호가 일치하지 않습니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, 400, "이메일이 중복되었습니다."),
    USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, 400, "아이디가 중복되었습니다."),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, 400, "닉네임이 중복되었습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 401, "로그인이 실패하였습니다."),

    // Like
    INVALID_LIKE_TARGET(HttpStatus.BAD_REQUEST, 400, "잘못된 좋아요 대상입니다."),

    // Mail
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 500, "이메일 전송 실패"),
    EMAIL_AUTH_NUMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, 400, "인증 번호가 유효하지 않습니다."),
    EMAIL_MISMATCH(HttpStatus.BAD_REQUEST, 400, "이메일이 일치하지 않습니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "상품을 찾을 수 없습니다."),
    PRODUCT_MEMBER_NOT_MATCH(HttpStatus.FORBIDDEN, 403, "상품에 접근할 권한이 없는 회원입니다."),
    PRODUCT_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "상품 옵션을 찾을 수 없습니다."),

    // Recipe
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "레시피를 찾을 수 없습니다."),
    RECIPE_STEP_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "레시피 순서를 찾을 수 없습니다."),
    RECIPE_MEMBER_NOT_MATCH(HttpStatus.FORBIDDEN, 403, "레시피에 접근할 권한이 없는 회원입니다."),

    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "리뷰를 찾을 수 없습니다."),
    ALREADY_REVIEWED(HttpStatus.BAD_REQUEST, 400, "이미 리뷰를 작성한 레시피입니다."),

    // Purchase
    PURCHASE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "구매 정보를 찾을 수 없습니다."),

    // S3
    FILE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 500, "파일 변환에 실패했습니다."),
    FILE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 500, "파일 삭제에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
