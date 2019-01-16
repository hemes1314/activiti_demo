package receiveTask;

import java.io.InputStream;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

public class ReceiveTaskTest {
ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@Test
	public void deploymentProcessDefinition_inputStream() {
		InputStream inputStreamBpmn = this.getClass().getClassLoader().getResourceAsStream("diagrams/receiveTask.xml");
		InputStream inputStreamPng = this.getClass().getClassLoader().getResourceAsStream("diagrams/receiveTask.png");
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.name("接收活动任务")
				.addInputStream("receiveTask.bpmn",inputStreamBpmn)
				.addInputStream("receiveTask.png", inputStreamPng)
				.deploy();
		System.out.println("部署ID:"+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	/**
	 * 启动流程实力+设置流程变量+获取流程变量+向后执行
	 */
	@Test
	public void startProcessInstance() {
		
		String processDefinitionKey = "receiveTask";
		ProcessInstance pi = processEngine.getRuntimeService()
			.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID："+pi.getId());
		System.out.println("流程定义ID："+pi.getProcessDefinitionId());
		
		ProcessDefinition pd = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(pi.getProcessDefinitionId()).singleResult();
		System.out.println("部署id："+pd.getDeploymentId());
		
		Execution execution = processEngine.getRuntimeService()
			.createExecutionQuery()
			.processInstanceId(pi.getId())
//			.activityId("rt1")//对应任务id
			.singleResult();
		
		// 使用流程变量设置当日销售额，用来传递业务参数
		processEngine.getRuntimeService()
			.setVariable(execution.getId(), "汇总当日销售额", 21000);
		
		// 向后执行，如果流程处于等待状态，使得流程继续执行
		processEngine.getRuntimeService()
			.signal(execution.getId());
		
		// 查询执行对象ID
		Execution execution2 = processEngine.getRuntimeService()
				.createExecutionQuery()
				.processInstanceId(pi.getId())
//				.activityId("rt2")
				.singleResult();
		
		// 从流程变量中获取汇总当日销售额的值
		Integer value = (Integer)processEngine.getRuntimeService()
			.getVariable(execution2.getId(), "汇总当日销售额");
		
		System.out.println("给老板发送短信：金额是："+value);
		
		processEngine.getRuntimeService().signal(execution2.getId());
	}
	
	/**
	 * 流程历史任务实例查询
	 */
	@Test
	public void finHistoryTask() {
		String processInstanceId = "257501";
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
			.processInstanceId("257501")
			.orderByProcessInstanceStartTime().asc()
			.singleResult();
		System.out.println("instanceId:"+hpi.getId());
		System.out.println("ProcessDefinitionId:"+hpi.getProcessDefinitionId());
		System.out.println("StartTime:"+hpi.getStartTime());
		System.out.println("EndTime:"+hpi.getEndTime());
		System.out.println("ProcessDefinitionName:"+hpi.getProcessDefinitionName());
	}
}
