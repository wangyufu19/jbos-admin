package com.jbos.admin.infrastructure.repository.wf;

import com.jbos.admin.domain.entity.wf.ProcessInst;
import com.jbos.admin.infrastructure.repository.wf.mapper.ProcessInstMapper;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.page.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ProcessInstRepo
 * @author youfu.wang
 * @date 2023/4/6
 **/
@Component
public class ProcessInstRepo {
    @Autowired
    private ProcessInstMapper processInstMapper;
    @Paging
    public List<ProcessInst> getProcessInstList(PageParam pageParam, Map<String, Object> parameterObject){
        return processInstMapper.getProcessInstList(parameterObject);
    }

    public void addProcessInst(ProcessInst processInst){
        processInstMapper.addProcessInst(processInst);
    }

    public void updateProcState(ProcessInst processInst){
        processInstMapper.updateProcState(processInst);
    }
}
