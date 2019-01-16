package start;

import java.io.InputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

public class StartTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@Test
	public void deploymentProcessDefinition_inputStream() {
		InputStream inputStreamBpmn = this.getClass().getClassLoader().getResourceAsStream("diagrams/start.xml");
		InputStream inputStreamPng = this.getClass().getClassLoader().getResourceAsStream("diagrams/start.png");
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.name("开始活动")
				.addInputStream("start.bpmn",inputStreamBpmn)
				.addInputStream("start.png", inputStreamPng)
				.deploy();
		System.out.println("部署ID:"+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	/**
	 * 启动流程实例+判断流程是否结束+查询历史
	 * act_hi_procinst
	 * act_ru_task
	 */
	@Test
	public void startProcessInstance() {
		
		String processDefinitionKey = "start";
		ProcessInstance pi = processEngine.getRuntimeService()
			.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID："+pi.getId());
		System.out.println("流程定义ID："+pi.getProcessDefinitionId());
		
		ProcessDefinition pd = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(pi.getProcessDefinitionId()).singleResult();
		System.out.println("部署id："+pd.getDeploymentId());
		
		//判断流程是否结束，查询正在执行的流程对象
		ProcessInstance rpi = processEngine.getRuntimeService()
			.createProcessInstanceQuery()
			.processInstanceId(pi.getId())
			.singleResult();
		if(rpi == null) {
			// 结束，查历史
			HistoricProcessInstance hpi = processEngine.getHistoryService()
				.createHistoricProcessInstanceQuery()
				.processInstanceId(pi.getId())
				.singleResult();
			System.out.println(hpi.getId() + "   " + hpi.getStartTime()
					+"   "+ hpi.getEndTime() + "   " + hpi.getDurationInMillis());
		}
	}
	
	/**
	 * 删除流程定义
	 */
	@Test
	public void deleteProcessDefinition() {
		String deploymentId = "97501";
		// 级联删除
		processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
		System.out.println("删除成功");
	}
}
