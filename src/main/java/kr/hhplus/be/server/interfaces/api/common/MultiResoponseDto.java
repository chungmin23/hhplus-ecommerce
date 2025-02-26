package kr.hhplus.be.server.interfaces.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultiResoponseDto<T> {

    @JsonProperty("success")
    private boolean success;
    @JsonProperty("data")
    private List<T> data;

    public MultiResoponseDto(List<T> data) {
        this.success = true;
        this.data = data;
    }
}
