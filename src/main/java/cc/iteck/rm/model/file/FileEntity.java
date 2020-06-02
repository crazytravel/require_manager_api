package cc.iteck.rm.model.file;

import cc.iteck.rm.model.AbstractEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("dc_file")
public class FileEntity extends AbstractEntity {

}
