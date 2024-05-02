package JuDBu.custommaster.auth.jwt.dto;

import lombok.*;

@Data
@RequiredArgsConstructor
public class JwtErrorResponse {

    private String errorCode;
    private String message;
    private String newToken;

    public void setResponse(ErrorCode errorCode) {
        this.errorCode = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public void setToken(String token){
        this.newToken = token;
    }
}