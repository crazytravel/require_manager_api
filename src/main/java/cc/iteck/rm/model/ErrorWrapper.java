package cc.iteck.rm.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ErrorWrapper {
    private HttpStatus error;
    private Integer status;
    private String message;
}
