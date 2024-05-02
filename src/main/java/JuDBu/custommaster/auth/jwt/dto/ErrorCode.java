package JuDBu.custommaster.auth.jwt.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.FORBIDDEN, "지원하지 않는 토큰입니다."),
    JWT_DECODE_FAIL(HttpStatus.UNAUTHORIZED, "올바른 토큰이 필요합니다."),
    JWT_SIGNATURE_FAIL(HttpStatus.UNAUTHORIZED, "서명 오류입니다."),
    ILLEGAL_EXCEPTION(HttpStatus.BAD_REQUEST, "올바른 토큰이 필요합니다.");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}