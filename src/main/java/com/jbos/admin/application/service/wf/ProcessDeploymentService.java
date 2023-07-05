package com.jbos.admin.application.service.wf;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jbos.admin.domain.entity.wf.ProcessDeployment;
import com.jbos.admin.infrastructure.camunda.DeploymentService;
import com.jbos.admin.infrastructure.repository.wf.ProcessDeploymentRepo;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.response.ResponseResult;
import com.jbos.admin.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ProcessDefService
 *
 * @author youfu.wang
 * @date 2023/6/9
 **/
@Service
public class ProcessDeploymentService {
    @Autowired
    private ProcessDeploymentRepo processDeploymentRepo;

    public ResponseResult getProcessDeploymentList(PageParam pageParam, Map<String, Object> parameterObject){
        List<ProcessDeployment> processDefList=processDeploymentRepo.getProcessDeploymentList(pageParam,parameterObject);
        return ResponseResult.ok().isPage(true).data(processDefList);
    }
    public void deployProcessDeployment(ProcessDeployment processDeployment){
        processDeployment.setDeployTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
        processDeploymentRepo.save(processDeployment);
    }
    @Transactional
    public void deployProcessDeployment(String id){
        UpdateWrapper<ProcessDeployment> updateWrapper=new UpdateWrapper();
        updateWrapper.eq("ID",id);
        processDeploymentRepo.remove(updateWrapper);
    }
}
