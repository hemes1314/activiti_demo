package personalTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class TaskTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@Test
	public void deploymentProcessDefinition_inputStream() {
		InputStream inputStreamBpmn = this.getClass().getClassLoader().getResourceAsStream("diagrams/task.xml");
		InputStream inputStreamPng = this.getClass().getClassLoader().getResourceAsStream("diagrams/task.png");
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.name("任务")
				.addInputStream("task.bpmn",inputStreamBpmn)
				.addInputStream("task.png", inputStreamPng)
				.deploy();
		System.out.println("部署ID:"+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	/**
	 * 启动流程实例
	 */
	@Test
	public void startProcessInstance() {
		
		String processDefinitionKey = "task";
		
		// 启动流程实例的同时，设置流程变量用来制定任务的办理人
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("userID", "周芷若");
		
		ProcessInstance pi = processEngine.getRuntimeService()
			.startProcessInstanceByKey(processDefinitionKey, variables);
		System.out.println("流程实例ID："+pi.getId());
		System.out.println("流程定义ID："+pi.getProcessDefinitionId());
		
		ProcessDefinition pd = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(pi.getProcessDefinitionId()).singleResult();
		System.out.println("部署id："+pd.getDeploymentId());
		
	}
	
	/**
	 * 查询当前人的个人任务
	 */
	@Test
	public void findMyPersionalTask() {
		List<Task> list = processEngine.getTaskService()
			.createTaskQuery()
			.taskAssignee("周芷若")
			.list();
		if(list != null && list.size() > 0) {
			for(Task task : list) {
				ProcessDefinition pd = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
				System.out.println("任务ID:"+task.getId());
				System.out.println("任务名称："+task.getName());
				System.out.println("任务创建时间："+task.getCreateTime());
				System.out.println("任务的办理人："+task.getAssignee());
				System.out.println("流程实例ID:"+task.getProcessInstanceId());
				System.out.println("执行对象ID："+task.getExecutionId());
				System.out.println("流程定义ID："+task.getProcessDefinitionId());
				System.out.println("部署id："+pd.getDeploymentId());
				System.out.println("###################################################");
			}
		} else {
			System.out.println("无待办任务");
		}
	}
	
	/**完成我的任务**/
	@Test
	public void completeMyPersionalTask() {
		String taskId = "270005";
		Map<String,Object> variables = new HashMap<String,Object>();
		variables.put("money", 800);
		processEngine.getTaskService()//与正在执行的任务管理相关的service
			.complete(taskId, variables);
		System.out.println("完成任务：任务ID："+taskId);
	}

	/**
	  *  可以分配个人任务从一个人到另外一个人
	 */
	@Test
	public void setAssigneeTask() {
		String taskId = "280004";
		processEngine.getTaskService()
			.setAssignee(taskId, "张翠山");
	}
	
	/**
	 * 流程历史任务实例查询
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void finHistoryTask() {
		String processInstanceId = "280001";
		List<HistoricTaskInstance> list = processEngine.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricTaskInstanceStartTime().asc()
				.list();
		if(list != null && list.size() > 0) {
			for(HistoricTaskInstance hti : list) {
				System.out.println("任务id:" + hti.getId() + "   " + hti.getName() + "/" + hti.getAssignee() + "   " +
						hti.getProcessInstanceId() + "   " + hti.getStartTime()
						+ "   " + hti.getEndTime() + "   " + hti.getDurationInMillis());
			}
		}
	}

	/**
	 * 查询流程实例历史表
	 */
	@Test
	public void findHistoryProcessInstance() {
		HistoricProcessInstance hpi = processEngine.getHistoryService()
			.createHistoricProcessInstanceQuery()
			.processInstanceId("280001")
			.orderByProcessInstanceStartTime().asc()
			.singleResult();
		System.out.println("instanceId:"+hpi.getId());
		System.out.println("ProcessDefinitionId:"+hpi.getProcessDefinitionId());
		System.out.println("StartTime:"+hpi.getStartTime());
		System.out.println("EndTime:"+hpi.getEndTime());
		System.out.println("ProcessDefinitionName:"+hpi.getProcessDefinitionName());
	}	
}
