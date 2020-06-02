package cc.iteck.rm.model;

import cc.iteck.rm.enums.ResStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResWrapper<T> {
    private ResStatus status;
    private T body;
}
