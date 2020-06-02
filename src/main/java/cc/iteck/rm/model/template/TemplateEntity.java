package cc.iteck.rm.model.template;

import com.baomidou.mybatisplus.annotation.TableName;
import cc.iteck.rm.model.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("rm_template")
@Data
public class TemplateEntity extends AbstractEntity {
}
