package kr.hhplus.be.server.interfaces.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String errorCode;      // 에러 코드
    private String message;        // 에러 메시지
    private int status;            // HTTP 상태 코드
    private LocalDateTime timestamp; // 에러 발생 시간
}
