package com.jbos.admin.application.service.wf;


import com.jbos.admin.common.exception.CamundaException;
import com.jbos.admin.domain.entity.sm.Role;
import com.jbos.admin.domain.entity.wf.ProcessInst;
import com.jbos.admin.domain.entity.wf.ProcessTask;
import com.jbos.admin.domain.entity.wf.TaskStep;
import com.jbos.admin.infrastructure.camunda.ProcessInstanceService;
import com.jbos.admin.infrastructure.camunda.TaskMgrService;
import com.jbos.admin.infrastructure.repository.wf.ProcessTaskRepo;
import com.jbos.admin.application.request.wf.ProcessTaskDto;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.response.ResponseResult;
import com.jbos.admin.common.utils.DateUtils;
import com.jbos.admin.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProcessTaskService
 *
 * @author youfu.wang
 * @date 2023/4/6
 **/
@Service
public class ProcessTaskService  {
    @Autowired
    ProcessInstanceService processInstanceService;
    @Autowired
    private ProcessMgrService processMgrService;
    @Autowired
    private ProcessTaskRepo processTaskRepo;
    @Autowired
    private TaskMgrService taskMgrService;

    /**
     * 查询用户待办列表
     * @param pageParam
     * @param parameterObject
     * @return
     */
    public ResponseResult getUserTaskList(PageParam pageParam, Map<String, Object> parameterObject) {
        List<ProcessTask> processTasks = processTaskRepo.getUserTaskList(pageParam, parameterObject);
        return ResponseResult.ok().isPage(true).data(processTasks);
    }

    /**
     * 查询用户已办列表
     * @param pageParam
     * @param parameterObject
     * @return
     */
    public ResponseResult getUserTaskProcessedList(PageParam pageParam, Map<String, Object> parameterObject) {
        List<ProcessTask> processTasks = processTaskRepo.getUserTaskProcessedList(pageParam, parameterObject);
        return ResponseResult.ok().isPage(true).data(processTasks);
    }

    /**
     * 查询流程任务处理明细
     * @param pageParam
     * @param parameterObject
     * @return
     */
    public ResponseResult getProcessTaskDetailList(PageParam pageParam,Map<String,Object> parameterObject){
        List<ProcessTask> processTasks = processTaskRepo.getProcessTaskDetailList(pageParam,parameterObject);
        return ResponseResult.ok().isPage(true).data(processTasks);
    }

    /**
     * 查询流程任务处理步骤
     * @param parameterObject
     * @return
     */
    public List<TaskStep> getProcessTaskStepList(Map<String, Object> parameterObject) {
        List<TaskStep> taskSteps = processTaskRepo.getProcessTaskStepList(parameterObject);
        return taskSteps;
    }

    /**
     * 查询除当前处理用户必须全部已处理才能得到下一个任务的领取人
     * @param parameterObject
     * @param multiInstance
     * @return
     */
    public String getTaskAssigneeList(Map<String, Object> parameterObject, boolean multiInstance) {
        List<Map> processedAssigneeList;
        String assignees = "";
        //判断当前实例任务是否多实例(除当前处理用户必须全部已处理才能得到下一个任务的领取人)
        if (multiInstance) {
            processedAssigneeList = processTaskRepo.getInstanceTaskProcessedAssigneeList(parameterObject);
            if(processedAssigneeList!=null&&processedAssigneeList.size()>0){
                return assignees;
            }
        }
        List<Map> assigneeList = processTaskRepo.getTaskAssigneeList(parameterObject);
        if (assigneeList != null) {
            for (int i = 0; i < assigneeList.size(); i++) {
                if (i == assigneeList.size() - 1) {
                    assignees = assignees + StringUtils.replaceNull(assigneeList.get(i).get("BADGE"));
                } else {
                    assignees = assignees + StringUtils.replaceNull(assigneeList.get(i).get("BADGE")) + ",";
                }
            }
        }
        return assignees;
    }

    /**
     * 新增实例任务
     * @param processTask
     */
    public void addProcessTask(ProcessTask processTask) {
        processTaskRepo.addProcessTask(processTask);
    }

    /**
     * 更新实例任务审批意见
     * @param processTask
     */
    public void updateProcessTaskOpinion(ProcessTask processTask) {
        processTaskRepo.updateProcessTaskOpinion(processTask);
    }

    /**
     * 新增流程实例下一任务领取人
     * @param processInstanceId
     * @param variables
     */
    public void addProcessNexTask(String processInstanceId, Map<String, Object> variables) {
        //新增物品采购流程下一个任务数据
        String nextActivityId = StringUtils.replaceNull(variables.get("nextActivityId"));
        String nextActivityName = StringUtils.replaceNull(variables.get("nextActivityName"));
        String nextAssignees = StringUtils.replaceNull(variables.get("nextAssignees"));
        String[] assigneeList = StringUtils.split(nextAssignees,',');
        if (assigneeList != null && assigneeList.length > 0) {
            for (String assignee : assigneeList) {
                ProcessTask processNextTask = new ProcessTask();
                processNextTask.setId(StringUtils.getUUID());
                processNextTask.setProcInstId(processInstanceId);
                processNextTask.setActivityId(nextActivityId);
                processNextTask.setActivityName(nextActivityName);
                processNextTask.setAssignee(assignee);
                processNextTask.setTaskState(ProcessTask.PROCESS_STATE_ACTIVE);
                processNextTask.setStartTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
                this.addProcessTask(processNextTask);
            }
        }
    }

    /**
     * 处理领取任务
     * @param processTask
     */
    @Transactional
    public void handleAssigneeTask(ProcessTask processTask) throws CamundaException {
        //领取任务
        taskMgrService.assignee(
                processTask.getUserId(),processTask.getProcInstId(),processTask.getTaskId(),processTask.getAssignee());
        //领取成功则新增流程任务数据,同时授权的用户任务状态标记NONE
        processTask.setId(StringUtils.getUUID());
        processTask.setTaskState(ProcessTask.PROCESS_STATE_ACTIVE);
        processTask.setStartTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
        this.addProcessTask(processTask);
        //更新授权的用户任务状态标记NONE
        processTask.setAssignee(processTask.getUserId());
        processTask.setTaskState(ProcessTask.PROCESS_STATE_NONE);
        processTask.setEndTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
        this.updateProcessTaskOpinion(processTask);
    }
    /**
     * 处理新增任务领取人
     * @param params
     */
    @Transactional
    public void handleTaskAddAssignee(Map<String, Object> params) throws CamundaException {
        ProcessTask processTask= ProcessTaskDto.build(params);
        String userId=StringUtils.replaceNull(params.get("userId"));
        String processInstanceId=StringUtils.replaceNull(params.get("processInstanceId"));
        String activityId=StringUtils.replaceNull(params.get("activityId"));
        String activityName=StringUtils.replaceNull(params.get("activityName"));
        String elementVariable=StringUtils.replaceNull(params.get("elementVariable"));
        String assignees=StringUtils.replaceNull(params.get("assignees"));
        //新增任务领取人
        taskMgrService.addAssignee(userId,processInstanceId,activityId,activityName,elementVariable,assignees);
        //领取成功则新增流程任务数据
        String[] assigneeList = StringUtils.split(assignees,',');
        if (assigneeList != null && assigneeList.length > 0) {
            for (String assignee : assigneeList) {
                processTask.setId(StringUtils.getUUID());
                processTask.setAssignee(assignee);
                processTask.setTaskState(ProcessTask.PROCESS_STATE_ACTIVE);
                processTask.setStartTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
                this.addProcessTask(processTask);
            }
        }
    }
    /**
     * 处理减去任务领取人
     * @param params
     */
    @Transactional
    public void handleTaskReduceAssignee(Map<String, Object> params) throws CamundaException {
        ProcessTask processTask= ProcessTaskDto.build(params);
        String userId=StringUtils.replaceNull(params.get("userId"));
        String processInstanceId=StringUtils.replaceNull(params.get("processInstanceId"));
        String assignee=StringUtils.replaceNull(params.get("assignee"));

        //减去任务领取人
        taskMgrService.reduceAssignee(userId,processInstanceId,assignee);
        //减去成功则标记该用户流程任务状态NONE
        processTask.setAssignee(assignee);
        processTask.setTaskState(ProcessTask.PROCESS_STATE_NONE);
        processTask.setEndTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
        this.processTaskRepo.updateProcessTaskState(processTask);
    }
    /**
     * 处理审批任务
     * @param variables
     * @param processCallback
     * @return
     */
    @Transactional
    public void handleCompleteTask(ProcessTask processCurrentTask,
                                             Map<String, Object> variables,
                                             ProcessCallback processCallback) throws CamundaException {
        String processInstanceId = StringUtils.replaceNull(variables.get("processInstanceId"));
        //完成任务
        String taskId=taskMgrService.complete(variables);
        //查询流程实例状态
        String processInstanceState=processInstanceService.getProcessInstanceState(processInstanceId);
         //更新流程当前任务状态和审批意见
        processCurrentTask.setTaskId(taskId);
        processCurrentTask.setTaskState(ProcessTask.PROCESS_STATE_COMPLETED);
        processCurrentTask.setEndTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
        this.updateProcessTaskOpinion(processCurrentTask);

        //多实例节点更新流程任务处理后未审批的所有任务状态为NONE(除当前用户任务)
        if (Role.ROLE_DEP_LEADER.equals(processCurrentTask.getActivityId())) {
            this.updateNoneStatePostHandleTask(processCurrentTask);
        }
        //判断流程实例是否结束
        if ("isEnded".equals(processInstanceState)) {
            //更新回调业务
            if(processCallback!=null){
                Map<String,String> data=new HashMap();
                data.put("taskId",taskId);
                data.put("processInstanceState",processInstanceState);
                processCallback.call(data);
            }
            //更新业务流程实例状态
            processMgrService.updateProcState(processCurrentTask.getProcInstId(), ProcessInst.PROCESS_STATE_COMPLETED);
        } else {
            //新增物品采购流程下一个任务数据
            this.addProcessNexTask(processCurrentTask.getProcInstId(),variables);
        }
    }
    /**
     * 处理撤回流程任务
     * @param processCurrentTask
     * @return
     */
    @Transactional
    public void handleDrawbackProcessTask(ProcessTask processCurrentTask) throws CamundaException {
        //撤回任务
        boolean isDrawback=taskMgrService.drawback(
                processCurrentTask.getUserId(),processCurrentTask.getProcDefId(),processCurrentTask.getProcInstId(),
                processCurrentTask.getTaskId()
        );
        //撤回成功则处理任务数据
        if (isDrawback) {
            //新增流程任务数据
            ProcessTask processTask = new ProcessTask();
            processTask.setId(StringUtils.getUUID());
            processTask.setProcInstId(processCurrentTask.getProcInstId());
            processTask.setActivityId(processCurrentTask.getActivityId());
            processTask.setActivityName(processCurrentTask.getActivityName());
            processTask.setAssignee(processCurrentTask.getAssignee());
            processTask.setTaskState(ProcessTask.PROCESS_STATE_ACTIVE);
            processTask.setStartTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
            this.addProcessTask(processTask);
            //更新流程任务撤回任务后未审批的任务状态为NONE
            processTaskRepo.updateDrawbackPostProcessTask(processTask);
        }
    }

    /**
     * 驳回流程任务
     * @param processCurrentTask
     * @return
     */
    @Transactional
    public void handleRejectProcessTask(ProcessTask processCurrentTask) throws CamundaException {
        Map<String,Object> data=taskMgrService.reject(
                processCurrentTask.getAssignee(),processCurrentTask.getProcDefId(),processCurrentTask.getProcInstId());
       if(data!=null) {
           //新增流程任务数据
           ProcessTask processTask = new ProcessTask();
           processTask.setId(StringUtils.getUUID());
           processTask.setProcInstId(processCurrentTask.getProcInstId());
           processTask.setActivityId(StringUtils.replaceNull(data.get("toActivityId")));
           processTask.setActivityName(StringUtils.replaceNull(data.get("toActivityName")));
           processTask.setAssignee(StringUtils.replaceNull(data.get("toAssignee")));
           processTask.setTaskState(ProcessTask.PROCESS_STATE_ACTIVE);
           processTask.setStartTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
           this.addProcessTask(processTask);
           //更新流程驳回任务审批状态数据
           processCurrentTask.setTaskId(StringUtils.replaceNull(data.get("taskId")));
           processCurrentTask.setTaskState(ProcessTask.PROCESS_STATE_REJECT);
           processCurrentTask.setEndTime(DateUtils.format(DateUtils.getCurrentDate(), DateUtils.YYYYMMDDHIMMSS));
           processTaskRepo.updateProcessTaskOpinion(processCurrentTask);
           //更新流程任务驳回后未审批的所有任务状态为NONE(除当前用户任务)
           processTaskRepo.updateNoneStatePostHandleTask(processCurrentTask);
       }
    }

    /**
     * 更新流程节点处理后未审批的所有任务状态为NONE(除当前用户任务)
     * @param processTask
     */
    public void updateNoneStatePostHandleTask(ProcessTask processTask) {
        processTaskRepo.updateNoneStatePostHandleTask(processTask);
    }
}
