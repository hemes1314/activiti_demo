package groupTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
  *  租任务表
  *  个人任务只有participant有参与者
 * act_ru_identitylink 任务办理人表  对一条candidate候选人
 * act_hi_identitylink 历史任务办理人表
 */
public class GroupTaskTest3 {
ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@Test
	public void deploymentProcessDefinition_inputStream() {
		InputStream inputStreamBpmn = this.getClass().getClassLoader().getResourceAsStream("diagrams/groupTask3.xml");
		InputStream inputStreamPng = this.getClass().getClassLoader().getResourceAsStream("diagrams/groupTask3.png");
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.name("组任务")
				.addInputStream("groupTask3.bpmn",inputStreamBpmn)
				.addInputStream("groupTask3.png", inputStreamPng)
				.deploy();
		System.out.println("部署ID:"+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	/**
	 * 启动流程实例
	 */
	@Test
	public void startProcessInstance() {
		
		String processDefinitionKey = "groupTask3";

		ProcessInstance pi = processEngine.getRuntimeService()
			.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID："+pi.getId());
		System.out.println("流程定义ID："+pi.getProcessDefinitionId());
		
		ProcessDefinition pd = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(pi.getProcessDefinitionId()).singleResult();
		System.out.println("部署id："+pd.getDeploymentId());
		
	}
	
	/**
	 * 查询当前人的组任务
	 */
	@Test
	public void findMyPersionalTask() {
		List<Task> list = processEngine.getTaskService()
			.createTaskQuery()
			.taskCandidateUser("小D")
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
	
	/**
	 * 拾取任务
	 */
	@Test
	public void claim() {
		String taskId = "332504";
		String userId = "黄蓉";
		processEngine.getTaskService().claim(taskId, userId);
		System.out.println("taskId："+taskId+",userId:"+userId+",拾取任务成功");
	}
	
	/**完成我的任务**/
	@Test
	public void completeMyPersionalTask() {
		String taskId = "332504";
		processEngine.getTaskService()//与正在执行的任务管理相关的service
			.complete(taskId);
		System.out.println("完成任务：任务ID："+taskId);
	}
	
	/**
	 * 查询正在执行的任务办理人表
	 */
	@Test
	public void findRunPersonTask() {
		
		List<IdentityLink> list = processEngine.getTaskService()
			.getIdentityLinksForTask("292504");
		for(IdentityLink il : list) {
			System.out.println("iser_id"+il.getUserId()
			+";task_id:"+il.getTaskId()+";procInsId"+il.getProcessInstanceId()
			+";type:"+il.getType());
		}
	}
	
	/**
	 * 查询历史任务的办理人表
	 */
	@Test
	public void findHisPersonTask() {
		List<HistoricIdentityLink> list = processEngine.getHistoryService()
				.getHistoricIdentityLinksForProcessInstance("292501");
		for(HistoricIdentityLink il : list) {
			System.out.println("iser_id"+il.getUserId()
			+";task_id:"+il.getTaskId()+";procInsId"+il.getProcessInstanceId()
			+";type:"+il.getType());
		}
	}
	
	/**
	  * 向组任务中添加成员
	 */
	@Test
	public void addGroupPerson() {
		String taskId = "310004";
		String userId = "大B";
		processEngine.getTaskService()
			.addCandidateUser(taskId, userId);
	}
	
	/**
	  * 向组任务中删除成员
	 */
	@Test
	public void deleteGroupPerson() {
		String taskId = "310004";
		String userId = "大B";
		processEngine.getTaskService()
			.deleteCandidateUser(taskId, userId);
	}
	
	/**
	 * 流程历史任务实例查询
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void finHistoryTask() {
		String processInstanceId = "332501";
		List<HistoricTaskInstance> list = processEngine.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricTaskInstanceStartTime().asc()
				.list();
		if(list != null && list.size() > 0) {
			for(HistoricTaskInstance hti : list) {
				
				System.out.println("任务id:" + hti.getId() + "   " + hti.getName()
						+ "/" + hti.getAssignee() + "   " 
						+ hti.getProcessInstanceId() + "   StartTime:" + hti.getStartTime()
						+ "   EndTime:" + hti.getEndTime() + "   " + hti.getDurationInMillis());
				
				if(hti.getAssignee() == null) {
					List<IdentityLink> ilList = processEngine.getTaskService()
							.getIdentityLinksForTask(hti.getId());
					for(IdentityLink il : ilList) {
						System.out.println("组任务未拾取，iser_id"+il.getUserId()
						+";task_id:"+il.getTaskId()+";procInsId"+il.getProcessInstanceId()
						+";type:"+il.getType());
					}
				}
			}
		} else {
			System.out.println("已拾取！");
		}
	}

	/**
	 * 查询流程实例历史表
	 */
	@Test
	public void findHistoryProcessInstance() {
		HistoricProcessInstance hpi = processEngine.getHistoryService()
			.createHistoricProcessInstanceQuery()
			.processInstanceId("332501")
			.orderByProcessInstanceStartTime().asc()
			.singleResult();
		System.out.println("instanceId:"+hpi.getId());
		System.out.println("ProcessDefinitionId:"+hpi.getProcessDefinitionId());
		System.out.println("StartTime:"+hpi.getStartTime());
		System.out.println("EndTime:"+hpi.getEndTime());
		System.out.println("ProcessDefinitionName:"+hpi.getProcessDefinitionName());
	}	
	
	@Test
	public void deleteProcessDefinitionByKey() {
		// 一个key多个版本
		String processDefinitionKey = "groupTask";
		List<ProcessDefinition> list = processEngine.getRepositoryService()
			.createProcessDefinitionQuery()
			.processDefinitionKey(processDefinitionKey)
			.list();
		if(list != null && list.size() > 0) {
			for(ProcessDefinition pd : list) {
				String deploymentId = pd.getDeploymentId();
				processEngine.getRepositoryService()
					.deleteDeployment(deploymentId, true);
			}
		}
	}
}
