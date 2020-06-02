package cc.iteck.rm.service.impl;

import cc.iteck.rm.exception.ResourceOperateFailedException;
import cc.iteck.rm.mapper.PermissionMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cc.iteck.rm.model.account.PermissionDto;
import cc.iteck.rm.model.account.PermissionEntity;
import cc.iteck.rm.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {


    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public PermissionDto createPermission(PermissionDto permissionDto) {
        var permissionEntity = PermissionEntity.builder().build();
        BeanUtils.copyProperties(permissionDto, permissionEntity);
        try {
            permissionMapper.insert(permissionEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("Create permission occur exception", e);
        }
        var entity = permissionMapper.selectById(permissionEntity.getId());
        BeanUtils.copyProperties(entity, permissionDto);
        return permissionDto;
    }

    @Override
    public List<PermissionDto> findPermissions() {
        var entities = permissionMapper.selectList(Wrappers.emptyWrapper());
        return entities.stream().map(entity -> {
            var dto = PermissionDto.builder().build();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}
