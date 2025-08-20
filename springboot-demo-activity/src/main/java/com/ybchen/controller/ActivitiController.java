package com.ybchen.controller;

import com.ybchen.utils.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @description: activiti 控制层
 * @author: Alex
 * @create: 2023-08-16 22:21
 */
@Api(tags = "工作流")
@RestController
@Slf4j
public class ActivitiController {
    //提供对流程定义和部署存储库的访问服务
    @Autowired
    RepositoryService repositoryService;
    //运行时的接口
    @Autowired
    RuntimeService runtimeService;
    // 任务处理接口
    @Autowired
    TaskService taskService;
    // 历史处理接口
    @Autowired
    HistoryService historyService;

    /**
     * 部署流程
     * <p>
     * 1、设计器设计流程xml/png
     * 2、部署流程
     * 3、发起流程
     * 4、执行流程
     *
     * @param file 上传流程压缩包
     */
    @ApiOperation("zip部署流程")
    @PostMapping("deploy")
    public ReturnData deploy(@RequestPart("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new NullPointerException("部署压缩包不能为空");
            }
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            //压缩流
            ZipInputStream zip = new ZipInputStream(file.getInputStream());
            deploymentBuilder.addZipInputStream(zip);
            //设置部署流程名称
            deploymentBuilder.name("请假审批");
            //部署流程
            Deployment deploy = deploymentBuilder.deploy();
            log.info("部署流程 {}", deploy);
            return ReturnData.buildSuccess(deploy);

        } catch (Exception e) {
            e.printStackTrace();
            return ReturnData.buildError(e.toString());
        }
    }


    @ApiOperation("查询流程部署信息")
    @PostMapping("queryDeploymentInfo")
    public ReturnData queryDeploymentInfo() {
        //也可以设置查询部署筛选条件，自行查询API，基本上都是见名知意的
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        log.info("流程部署信息：{}", list);
        return ReturnData.buildSuccess(list.toString());
    }

    @ApiOperation("查询流程定义信息")
    @PostMapping("queryProcessInfo")
    public ReturnData queryProcessInfo() {
        //也可以设置查询流程定义筛选条件，自行查询API，基本上都是见名知意的
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        log.info("流程定义信息：{}", list);
        return ReturnData.buildSuccess(list.toString());
    }

    @ApiOperation("根据部署id删除流程部署")
    @GetMapping("deleteDeploymentById")
    public ReturnData deleteDeploymentById(
            @ApiParam(value = "流程部署id", required = true) String deploymentId
    ) {
        List<Deployment> list = repositoryService.createDeploymentQuery().deploymentId(deploymentId).list();
        if (list.size() != 1) {
            return ReturnData.buildError("流程定义未找到");
        }
        //根据部署id删除流程部署
        repositoryService.deleteDeployment(deploymentId);
        return ReturnData.buildSuccess("删除成功");
    }

    @ApiOperation("发起流程")
    @GetMapping("startProcess")
    public ReturnData startProcess(
            @ApiParam(value = "流程定义id", required = true) String processDefinitionId
    ) {
        log.info("发起流程，processDefinitionId：{}", processDefinitionId);
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).list();
        if (list.size() != 1) {
            return ReturnData.buildError("流程定义不存在");
        }
        //流程节点中变量，替换占位符
        Map<String, Object> variablesMap = new HashMap<>();
        //设置流程变量
        variablesMap.put("userName", "老陈同学");
        variablesMap.put("day", "2");
        //通过流程定义ID启动一个流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variablesMap);
        log.info("流程实例：{}", processInstance);
        return ReturnData.buildSuccess("发起成功 " + processInstance);
    }

    @ApiOperation("完成任务")
    @GetMapping("completeTask")
    public ReturnData completeTask(
            @ApiParam(value = "流程实例id", required = true) String processInstanceId
    ) {
        //根据流程实例id，查询任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (taskList.size() != 1) {
            return ReturnData.buildError("当前没有任务");
        }
        log.info("任务列表：{}", taskList);
        //根据任务id，完成任务
        taskService.complete(taskList.get(0).getId());
        return ReturnData.buildSuccess("完成任务");
    }

    @ApiOperation("查询历史流程实例")
    @GetMapping("queryHistoryProcessInstance")
    public ReturnData queryHistoryProcessInstance() {
        //也可以设置查询条件，自行查询API
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        log.info("查询历史流程实例 {}", list);
        return ReturnData.buildSuccess(list.toString());
    }

    @ApiOperation("查询历史任务")
    @GetMapping("queryHistoryTask")
    public ReturnData queryHistoryTask() {
        //也可以设置查询条件，自行查询API
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().list();
        log.info("查询历史任务 {}", list);
        return ReturnData.buildSuccess(list.toString());
    }

    @ApiOperation("查看历史活动流程实例")
    @GetMapping("queryActivityInstance")
    public ReturnData queryActivityInstance() {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().list();
        log.info("查看历史活动流程实例 {}", list);
        return ReturnData.buildSuccess(list.toString());
    }

    @ApiOperation("根据代办人查询任务")
    @GetMapping("queryByAssigneeTask")
    public ReturnData queryByAssigneeTask(
            @ApiParam(value = "代办人", required = true) String assignee
    ) {
        List<Task> taskList = taskService.createTaskQuery()
                //代办人姓名
                .taskAssignee(assignee)
                //活动状态
                .active()
                .list();
        log.info("根据代办人查询任务 {}", taskList);
        return ReturnData.buildSuccess(taskList.toString());
    }

    @ApiOperation("按任务id更新代办人")
    @GetMapping("updateAssigneeByTaskId")
    public ReturnData updateAssigneeByTaskId(
            @ApiParam(value = "任务id", required = true) String taskId,
            @ApiParam(value = "新代办人", required = true) String assignee
    ) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ReturnData.buildError("任务不存在");
        }
        //更新当前任务的代办人
        taskService.setAssignee(taskId, assignee);
        return ReturnData.buildSuccess("更新成功");
    }

    @ApiOperation("添加审批人意见")
    @GetMapping("addComment")
    public ReturnData addComment(
            @ApiParam(value = "任务id", required = true) String taskId,
            @ApiParam(value = "流程实例id", required = true) String processInstanceId,
            @ApiParam(value = "意见内容", required = true) String message
    ) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .processInstanceId(processInstanceId)
                .singleResult();
        if (task == null) {
            return ReturnData.buildError("任务不存在");
        }
        taskService.addComment(taskId, processInstanceId, message);
        return ReturnData.buildSuccess("添加成功");
    }

    @ApiOperation("查询个人审批意见")
    @GetMapping("queryComment")
    public ReturnData queryComment(
            @ApiParam(value = "任务id") String taskId
    ) {
        //注意，这里也可以使用type做搜索，通过添加意见的第三个参数，指定用户id
        //taskService.addComment("任务id", "流程实例id", "自定义变量type，可以用作用户id", "意见");
        List<Comment> taskComments = taskService.getTaskComments(taskId);
        //taskService.getTaskComments(taskId,"自定义变量type，可以用作用户id");
        log.info("查询个人审批意见 {}", taskComments);
        return ReturnData.buildSuccess(taskComments.toString());
    }

    @ApiOperation("根据候选人查询任务")
    @GetMapping("queryTaskByCandidateUser")
    public ReturnData queryTaskByCandidateUser(
            @ApiParam(value = "候选人名称") String userName
    ) {
        List<Task> taskList = taskService.createTaskQuery()
                //候选人名称
                .taskCandidateUser(userName)
                .list();
        return ReturnData.buildSuccess(taskList);
    }

    /**
     * 拾取任务，拾取后的任务，该候选人才可以完成任务
     *
     * @param taskId   任务id
     * @param userName 候选人名称
     * @return
     */
    @ApiOperation("候选人拾取任务，拾取后的任务，候选人才可以完成")
    @GetMapping("claimTask")
    public ReturnData claimTask(
            @ApiParam(value = "任务id") String taskId,
            @ApiParam(value = "候选人名称") String userName
    ) {
        Task task = taskService.createTaskQuery()
                //任务id
                .taskId(taskId)
                //候选人名称
                .taskCandidateUser(userName)
                .singleResult();
        if (task == null) {
            return ReturnData.buildError("任务不存在");
        }
        //拾取任务
        taskService.claim(taskId, userName);
        return ReturnData.buildSuccess("拾取任务成功");
    }

    @ApiOperation("任务委派")
    @GetMapping("delegateTask")
    public ReturnData delegateTask(
            @ApiParam(value = "任务id", required = true) @RequestParam("taskId") String taskId,
            @ApiParam(value = "新代办人", required = true) @RequestParam("userName") String userName
    ) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ReturnData.buildError("任务不存在");
        }
        taskService.delegateTask(taskId, userName);
        return ReturnData.buildSuccess();
    }

    @ApiOperation("任务转办")
    @GetMapping("setAssignee")
    public ReturnData setAssignee(
            @ApiParam(value = "任务id", required = true) @RequestParam("taskId") String taskId,
            @ApiParam(value = "新代办人", required = true) @RequestParam("userName") String userName
    ) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ReturnData.buildError("任务不存在");
        }
        taskService.setAssignee(taskId, userName);
        return ReturnData.buildSuccess();
    }
}
