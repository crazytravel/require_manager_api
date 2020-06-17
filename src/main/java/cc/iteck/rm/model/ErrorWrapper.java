package cc.iteck.rm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorWrapper {
    private Integer code;
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
}
