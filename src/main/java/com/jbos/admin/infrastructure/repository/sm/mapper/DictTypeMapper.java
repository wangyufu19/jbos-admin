package com.jbos.admin.infrastructure.repository.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbos.admin.domain.entity.sm.DictType;

import java.util.List;
import java.util.Map;

/**
 * DictTypeMapper
 * @author youfu.wang
 * @date 2020-07-22
 */
public interface DictTypeMapper extends BaseMapper<DictType> {
    /**
     * 得到字典类型数据
     * @return
     */
    public List<DictType> getDictTypeList(Map<String, String> params);
}
