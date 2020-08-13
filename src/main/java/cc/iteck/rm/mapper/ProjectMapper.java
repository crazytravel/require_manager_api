package cc.iteck.rm.mapper;

import cc.iteck.rm.model.project.ProjectEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectMapper extends BaseMapper<ProjectEntity> {

    List<ProjectEntity> findProjectsWithUserId(@Param("userId") String userId,
                                               @Param("owner") Boolean owner,
                                                @Param("active") Boolean active);
}
