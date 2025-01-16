package kr.hhplus.be.server.interfaces.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Generic Single Response DTO")
public class SingleResponseDto<T> {

    @Schema(description = "Success status of the operation", example = "true")
    private boolean success;

    @Schema(description = "Response data payload", implementation = Object.class)
    private T data;

    public SingleResponseDto(T data) {
        this.success = true;
        this.data = data;
    }

}
