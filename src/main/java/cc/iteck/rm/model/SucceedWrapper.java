package cc.iteck.rm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SucceedWrapper<T> {
    private Integer code;
    private String message;
    private T data;
}
