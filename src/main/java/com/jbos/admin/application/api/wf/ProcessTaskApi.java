package com.jbos.admin.application.api.wf;

import com.jbos.admin.application.request.wf.ProcessTaskDto;
import com.jbos.admin.application.service.wf.ProcessTaskService;
import com.jbos.admin.domain.entity.wf.ProcessTask;
import com.jbos.admin.domain.entity.wf.TaskStep;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.response.ResponseResult;
import com.jbos.admin.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ProcessTaskApi
 *
 * @author youfu.wang
 * @date 2023/6/9
 **/
@RestController
@RequestMapping("/workflow/task")
@Slf4j
@Api("实例任务接口")
public class ProcessTaskApi {
    @Autowired
    private ProcessTaskService processTaskService;
    /**
     * 查询我的工作列表
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getMyWorkList")
    @ApiOperation("查询我的工作列表")
    public ResponseResult getMyWorkList(@RequestParam Map<String, Object> params) {
        ResponseResult res;
        String workType = StringUtils.replaceNull(params.get("workType"));
        try {
            PageParam pageParam = PageParam.getPageParam(params);
            if ("waiting".equals(workType)) {
                res = processTaskService.getUserTaskList(pageParam,params);
            } else {
                res = processTaskService.getUserTaskProcessedList(pageParam,params);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res = ResponseResult.error(ResponseResult.CODE_FAILURE, ResponseResult.MSG_FAILURE);
        }
        return res;
    }

    /**
     * 查询流程任务处理步骤
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getUserTaskStepList")
    @ApiOperation("查询流程任务处理步骤")
    public ResponseResult getUserTaskStepList(@RequestParam Map<String, Object> params) {
        ResponseResult res = ResponseResult.ok();
        try {
            List<TaskStep> taskSteps = processTaskService.getProcessTaskStepList(params);
            res.setData(taskSteps);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res = ResponseResult.error(ResponseResult.CODE_FAILURE, ResponseResult.MSG_FAILURE);
        }
        return res;
    }

    /**
     * 查询实例任务明细列表
     * @param params
     * @return
     */
    @ResponseBody
    @GetMapping("/getProcessTaskDetailList")
    @ApiOperation("查询实例任务明细列表")
    public ResponseResult getProcessTaskDetailList(@RequestParam Map<String, Object> params) {
        ResponseResult res = ResponseResult.ok();
        try {
            PageParam pageParam = PageParam.getPageParam(params);
            res=processTaskService.getProcessTaskDetailList(pageParam,params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res = ResponseResult.error(ResponseResult.CODE_FAILURE, ResponseResult.MSG_FAILURE);
        }
        return res;
    }
    @ResponseBody
    @PostMapping(value = "/assignee")
    @ApiOperation("领取任务")
    public ResponseResult assignee(@RequestBody Map<String, Object> params){
        ResponseResult res=ResponseResult.ok();
        try {
            ProcessTask processTask= ProcessTaskDto.build(params);
            processTaskService.handleAssigneeTask(processTask);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            res=ResponseResult.error(ResponseResult.CODE_FAILURE,e.getMessage());
        }
        return res;
    }
    @ResponseBody
    @PostMapping(value = "/addAssignee")
    @ApiOperation("新增任务领取人")
    public ResponseResult addAssignee(@RequestBody Map<String, Object> params){
        ResponseResult res=ResponseResult.ok();
        try {
            processTaskService.handleTaskAddAssignee(params);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            res=ResponseResult.error(ResponseResult.CODE_FAILURE,e.getMessage());
        }
        return res;
    }
    @ResponseBody
    @PostMapping(value = "/reduceAssignee")
    @ApiOperation("减去任务领取人")
    public ResponseResult reduceAssignee(@RequestBody Map<String, Object> params){
        ResponseResult res=ResponseResult.ok();
        try {
            processTaskService.handleTaskReduceAssignee(params);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            res=ResponseResult.error(ResponseResult.CODE_FAILURE,e.getMessage());
        }
        return res;
    }
    /**
     * 完成用户任务
     *
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/completeUserTask")
    @ApiOperation("完成用户任务")
    public ResponseResult completeUserTask(@RequestBody Map<String, Object> params) {
        ResponseResult res = ResponseResult.ok();
        try {
            ProcessTask processCurrentTask= ProcessTaskDto.build(params);
            List<Map<String,String>> variables=(ArrayList<Map<String,String>>)params.get("variables");
            if(!CollectionUtils.isEmpty(variables)) {
                for (Map<String, String> variableMap : variables) {
                    params.put(variableMap.get("DICTID"),variableMap.get("variableValue"));
                }
            }
            processTaskService.handleCompleteTask(processCurrentTask,params,null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res = ResponseResult.error(ResponseResult.CODE_FAILURE, e.getMessage());
        }
        return res;
    }
    /**
     * 撤回用户任务
     *
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/drawbackUserTask")
    @ApiOperation("撤回用户任务")
    public ResponseResult drawbackUserTask(@RequestBody Map<String, Object> params) {
        ResponseResult res = ResponseResult.ok();
        try {
            ProcessTask processCurrentTask= ProcessTaskDto.build(params);
            processTaskService.handleDrawbackProcessTask(processCurrentTask);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res = ResponseResult.error(ResponseResult.CODE_FAILURE, ResponseResult.MSG_FAILURE);
        }
        return res;
    }


}
