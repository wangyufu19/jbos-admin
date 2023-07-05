package com.jbos.admin.infrastructure.repository.wf;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbos.admin.domain.entity.wf.ProcessDeployment;
import com.jbos.admin.infrastructure.repository.wf.mapper.ProcessDeploymentMapper;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.page.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ProcessDefRepo
 * @author youfu.wang
 * @date 2023/4/6
 **/
@Component
public class ProcessDeploymentRepo extends ServiceImpl<ProcessDeploymentMapper, ProcessDeployment> {
    @Autowired
    private ProcessDeploymentMapper processDeploymentMapper;
    @Paging
    public List<ProcessDeployment> getProcessDeploymentList(PageParam pageParam, Map<String, Object> parameterObject){
        return processDeploymentMapper.getProcessDeploymentList(parameterObject);
    }

}
