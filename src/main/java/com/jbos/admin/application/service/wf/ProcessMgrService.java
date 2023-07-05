package com.jbos.admin.application.service.wf;

import com.jbos.admin.common.exception.CamundaException;
import com.jbos.admin.domain.entity.sm.Role;
import com.jbos.admin.domain.entity.wf.ProcessInst;
import com.jbos.admin.domain.entity.wf.ProcessTask;
import com.jbos.admin.infrastructure.camunda.ProcessInstanceService;
import com.jbos.admin.infrastructure.repository.wf.ProcessInstRepo;
import com.jbos.admin.application.request.wf.ProcessInstanceDto;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.response.ResponseResult;
import com.jbos.admin.common.utils.DateUtils;
import com.jbos.admin.common.utils.StringUtils;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author youfu.wang
 * @date 2023/4/6
 **/
@Service
public class ProcessMgrService {
    @Autowired
    private ProcessInstanceService processInstanceService;
    @Autowired
    private ProcessTaskService processTaskService;
    @Autowired
    private ProcessInstRepo processInstRepo;

    /**
     * 得到流程实例列表
     * @param pageParam
     * @param parameterObject
     * @return
     */
    public ResponseResult getProcessInstList(PageParam pageParam,Map<String, Object> parameterObject){
        List<ProcessInst> processInstList=processInstRepo.getProcessInstList(pageParam,parameterObject);
        return ResponseResult.ok().isPage(true).data(processInstList);
    }

    /**
     * 启动流程实例
     * @param variable
     * @return
     */
    @Transactional
    public ResponseResult startProcessInstance(Map<String, Object> variable,ProcessCallback processCallback)
            throws CamundaException {
        ResponseResult res=ResponseResult.ok();
        String userId= StringUtils.replaceNull(variable.get("userId"));
        String processDefinitionKey= StringUtils.replaceNull(variable.get("processDefinitionKey"));
        String businessKey= StringUtils.replaceNull(variable.get("businessKey"));
        String startActivityId=StringUtils.replaceNull(variable.get("startActivityId"));
        variable.put(startActivityId,userId);
        //启动流程实例
        ProcessInstance processInstance=
                processInstanceService.startProcessInstance(userId,processDefinitionKey,businessKey,variable);
        //启动实例成功，则处理相关业务逻辑
        if(processInstance!=null) {
            String processDefinitionId=processInstance.getProcessDefinitionId();
            String processInstanceId=processInstance.getProcessInstanceId();
            Map<String,String> data=new HashMap();
            data.put("processDefinitionId",processDefinitionId);
            data.put("processInstanceId",processInstanceId);
            res.setData(data);
            //新增流程实例数据
            ProcessInst processInst=ProcessInstanceDto.build(variable);
            processInst.setProcDefId(processDefinitionId);
            processInst.setProcInstId(processInstanceId);
            this.addProcessInst(processInst);
            //新增流程任务数据
            ProcessTask processTask = new ProcessTask();
            processTask.setId(StringUtils.getUUID());
            processTask.setProcInstId(processInstanceId);
            processTask.setActivityId(startActivityId);
            processTask.setActivityName(Role.ROLE_PROCESS_STARTER_DESC);
            processTask.setAssignee(userId);
            processTask.setTaskState(ProcessTask.PROCESS_STATE_ACTIVE);
            processTask.setStartTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
            processTaskService.addProcessTask(processTask);
            //处理流程回调业务
            if(processCallback!=null){
                processCallback.call(data);
            }
        }
        return res;
    }
    /**
     * 新增流程实例业务数据
     * @param processInst
     */
    public void addProcessInst(ProcessInst processInst){
        processInstRepo.addProcessInst(processInst);
    }

    /**
     * 更新流程实例状态
     * @param processInstanceId
     * @param procState
     */
    public void updateProcState(String processInstanceId,String procState){
        ProcessInst processInst=new ProcessInst();
        processInst.setProcInstId(processInstanceId);
        processInst.setProcState(procState);
        processInst.setEndTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
        processInstRepo.updateProcState(processInst);
    }
}
