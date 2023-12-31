package com.jbos.admin.application.service.sm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbos.admin.domain.entity.sm.DictType;
import com.jbos.admin.infrastructure.repository.sm.DictTypeRepository;
import com.jbos.admin.infrastructure.repository.sm.mapper.DictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * DictTypeService
 * @author youfu.wang
 * @date 2020-07-22
 */
@Service
public class DictTypeService extends ServiceImpl<DictTypeMapper, DictType> implements IService<DictType> {
    @Autowired
    private DictTypeRepository dictTypeRepository;

    /**
     * 得到字典类型数据
     * @return
     */
    public List<DictType> getDictTypeList(Map<String, String> params){
        return dictTypeRepository.getDictTypeList(params);
    }
}
