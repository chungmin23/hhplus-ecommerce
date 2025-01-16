package kr.hhplus.be.server.interfaces.api.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class MultiResoponseDto<T> {

    private boolean success;
    private List<T> data;

    public MultiResoponseDto(List<T> data) {
        this.success = true;
        this.data = data;
    }
}
