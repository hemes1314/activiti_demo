package processVariables;

import java.io.InputStream;
import java.util.Date;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

public class ProcessVariablesTest {
	
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@Test
	public void deploymentProcessDefinition_inputStream() {
		InputStream inputStreamBpmn = this.getClass().getClassLoader().getResourceAsStream("diagrams/processVariablesProcess.bpmn20.xml");
		InputStream inputStreamPng = this.getClass().getClassLoader().getResourceAsStream("diagrams/processVariablesProcess.processVariables.png");
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.name("流程定义")
				.addInputStream("processVariables.bpmn",inputStreamBpmn)
				.addInputStream("processVariables.png", inputStreamPng)
				.deploy();
		System.out.println("部署ID:"+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	@Test
	public void startProcessInstance() {
		String processDefinitionKey = "processVariables";
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID："+pi.getId()); // act_hi_procinst
		System.out.println("流程定义ID："+pi.getProcessDefinitionId());// act_re_procdef
		ProcessDefinition pd = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(pi.getProcessDefinitionId()).singleResult();
		System.out.println("部署id："+pd.getDeploymentId());
	}
	
	/**
	 * 设置流程变量
	 * act_ru_variable
	 */
	@Test
	public void setVariables() {
		TaskService taskService = processEngine.getTaskService();
		String taskId = "87504";
//		taskService.setVariableLocal(taskId, "请假天数", 3);//与本次任务绑定
//		taskService.setVariable(taskId, "请假日期", new Date());
//		taskService.setVariable(taskId, "请假原因", "回家探亲1");
		Person p = new Person();
		p.setId(10);
		p.setName("翠花");
		taskService.setVariable(taskId, "人员信息", p);
		System.out.println("设置流程变量成功！");
	}
	
	/**
	 * 获取流程变量
	 */
	@Test
	public void getVariables() {
		TaskService taskService = processEngine.getTaskService();
		String taskId = "67502";
		Integer days = (Integer) taskService.getVariable(taskId, "请假天数");
		Date date = (Date) taskService.getVariable(taskId, "请假日期");
		String reasean = (String) taskService.getVariable(taskId, "请假原因");
		System.out.println("请假天数："+days);
		System.out.println("请假日期:"+date);
		System.out.println("请假原因:"+reasean);
	}
	
	/**完成我的任务**/
	@Test
	public void completeMyPersionalTask() {
		String taskId = "80002";
		processEngine.getTaskService()//与正在执行的任务管理相关的service
			.complete(taskId);
		System.out.println("完成任务：任务ID："+taskId);
	}
	
	/**
	 * 模拟设置和获取流程变量场景
	 */
	@Test
	public void setAndGetVariables() {
		// 正在执行流程实例
		RuntimeService runtimeService = processEngine.getRuntimeService();
		// 正在执行的任务
		TaskService taskService = processEngine.getTaskService();
		
		// 设置流程变量
//		runtimeService.setVariable(executionId, variableName, value);//一次只能设置一个值
//		runtimeService.setVariables(executionId, variables);
//		taskService.setVariable(taskId, variableName, value);
//		taskService.setVariable(taskId, variables);
//		runtimeService.startProcessInstanceById(processDefinitionId, variables);
//		taskService.complete(taskId, variables);
		
		// 获取流程变量
//		runtimeService.getVariable(executionId, variableName)
//		runtimeService.getVariables(executionId)
//		runtimeService.getVariables(executionId, variableNames);
//		taskService.getVariable(taskId, variableName)
//		taskService.getVariables(taskId)
//		taskService.getVariables(taskId, variableNames)
		
	}
}
