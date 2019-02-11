package org.wubin.activiti.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activiti")
public class ActivitiDemoController {

	private static final Logger logger = LoggerFactory.getLogger(ActivitiDemoController.class);
	
	@Autowired  
	private RuntimeService runtimeService;  
	@Autowired  
	private TaskService taskService; 
	@Autowired  
	private HistoryService historyService;  
	
	@GetMapping("/groupTask")
	public List<HistoricTaskInstance> groupTask() {
		
		// 实例
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("userIDs", "达达,个个,中中");
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("groupTask", "BusinessKey=单据类名+单据id", variables);
		String processId = pi.getId();
		logger.info("流程创建成功，当前流程实例ID："+processId);
		
		// 任务
		Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		List<String> userIds = printTaskInfo(task);
		// 组任务，拾取
		if(userIds.size() > 0) {
			String userId = userIds.get(0);
			taskService.claim(task.getId(), userIds.get(0));
			logger.info("userId:" + userId + "，拾取任务！");
		}
		// 增加批注
//		Authentication.setAuthenticatedUserId("当前登录人");
//		taskService.addComment(task.getId(), pi.getId(), "批注");
		// 完成任务
		taskService.complete(task.getId());
		
		// 执行下个任务
		task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		if(task == null) {
			// 任务结束
			logger.info("任务结束！");
		} else {
			// ...
		}
		
		// 使用历史的流程变量查询批注
//		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
//			.processInstanceBusinessKey("单据类名+单据id")
//			.singleResult();
//		List<Comment> list = taskService.getProcessInstanceComments(historicProcessInstance.getId());
		return historyService
				.createHistoricTaskInstanceQuery()
				.processInstanceId(pi.getId())
				.orderByHistoricTaskInstanceEndTime().asc()
				.list();
	}
	
	private List<String> printTaskInfo(Task hti) {
		List<String> userIds = new ArrayList<>();
		
		logger.info("任务id:" + hti.getId());
		logger.info("任务名称：" + hti.getName());
		logger.info("执行人:" + hti.getAssignee());

		if(hti.getAssignee() == null) {
			List<IdentityLink> ilList = taskService.getIdentityLinksForTask(hti.getId());
			for(IdentityLink il : ilList) {
				userIds.add(il.getUserId());
				logger.info("组任务未拾取，候选人:" + il.getUserId());
			}
		} else {
			logger.info("个人任务，执行人：" + hti.getAssignee());
			userIds.add(hti.getAssignee());
		}
		return userIds;
	}
}
