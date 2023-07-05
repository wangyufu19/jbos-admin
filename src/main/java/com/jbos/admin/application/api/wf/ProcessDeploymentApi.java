package com.jbos.admin.application.api.wf;

import com.jbos.admin.application.service.wf.ProcessDeploymentService;
import com.jbos.admin.domain.entity.wf.ProcessDeployment;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.response.ResponseResult;
import com.jbos.admin.common.utils.StringUtils;
import com.jbos.admin.infrastructure.camunda.DeploymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * ProcessDefApi
 *
 * @author youfu.wang
 * @date 2023/6/9
 **/
@RestController
@RequestMapping("/workflow/deployment")
@Slf4j
@Api("流程部署接口")
public class ProcessDeploymentApi {
    @Autowired
    private ProcessDeploymentService processDeploymentService;
    @Autowired
    private DeploymentService deploymentService;
    /**
     * 查询流程定义列表
     * @param params
     * @return
     */
    @ResponseBody
    @GetMapping("/getProcessDeploymentList")
    @ApiOperation("查询流程部署列表")
    public ResponseResult getProcessDeploymentList(@RequestParam Map<String, Object> params) {
        ResponseResult res = ResponseResult.ok();
        try {
            PageParam pageParam = PageParam.getPageParam(params);
            res=processDeploymentService.getProcessDeploymentList(pageParam,params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res = ResponseResult.error(ResponseResult.CODE_FAILURE, ResponseResult.MSG_FAILURE);
        }
        return res;
    }
    @ResponseBody
    @PostMapping("/deploy")
    @ApiOperation("部署流程")
    @Transactional
    public ResponseResult deploy(MultipartFile file, @RequestParam Map<String, Object> params){
        ResponseResult res = ResponseResult.ok();
        try {
            String resource= file.getOriginalFilename();
            //判断合法的文件类型
            if(deploymentService.includeExtensions(resource)){
                Map<String,Object> deploymentMap=deploymentService.deploy(file);
                if(deploymentMap!=null){
                    ProcessDeployment processDeployment=new ProcessDeployment();
                    processDeployment.setId(StringUtils.replaceNull(deploymentMap.get("id")));
                    processDeployment.setDeploymentId(StringUtils.replaceNull(deploymentMap.get("deploymentId")));
                    processDeployment.setProcKey(StringUtils.replaceNull(deploymentMap.get("key")));
                    processDeployment.setProcName(StringUtils.replaceNull(deploymentMap.get("name")));
                    processDeployment.setResource(StringUtils.replaceNull(deploymentMap.get("source")));
                    processDeployment.setVersion(StringUtils.replaceNull(deploymentMap.get("version")));
                    processDeploymentService.deployProcessDeployment(processDeployment);
                    res.data(deploymentMap);
                }
            }else{
                res= ResponseResult.error(ResponseResult.CODE_FAILURE,"对不起，请上传合法的Camunda文件类型[bmmn]");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res = ResponseResult.error(ResponseResult.CODE_FAILURE, ResponseResult.MSG_FAILURE);
        }
        return res;
    }

    @ResponseBody
    @PostMapping("/unDeploy")
    @ApiOperation("下架流程")
    @Transactional
    public ResponseResult unDeploy(@RequestBody Map<String, Object> params){
        ResponseResult res = ResponseResult.ok();
        String id=StringUtils.replaceNull(params.get("id"));
        String cascade=StringUtils.replaceNull(params.get("cascade"));
        try {
            if("true".equals(cascade)){
                deploymentService.deleteDeployment(id,true);
            }else{
                deploymentService.deleteDeployment(id,false);
            }
            processDeploymentService.deployProcessDeployment(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res = ResponseResult.error(ResponseResult.CODE_FAILURE, ResponseResult.MSG_FAILURE);
        }
        return res;
    }
    @ResponseBody
    @GetMapping(value = "/getProcessDefinitionList")
    @ApiOperation("得到流程定义任务")
    public ResponseResult getProcessDefinitionList(@RequestParam Map<String, Object> params) {
        ResponseResult res = ResponseResult.ok();
        try{
            String processDefinitionId= StringUtils.replaceNull(params.get("processDefinitionId"));
            List<Map<String, String>> data=deploymentService.getProcessDefinitionList(processDefinitionId);
            res.setData(data);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            res= ResponseResult.error(ResponseResult.CODE_FAILURE,e.getMessage());
        }
        return res;
    }
}
