package history;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

public class HistoryQueryTest {
	
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	
	/**
	 * 查询流程变量历史表
	 */
	@Test
	public void findHistoryProcessVariables() {
		List<HistoricVariableInstance> hvis = processEngine.getHistoryService()
			.createHistoricVariableInstanceQuery()
			.variableName("人员信息")
			.list();
		for(HistoricVariableInstance hvi : hvis) {
			System.out.println(hvi.getId()+"   "+hvi.getProcessInstanceId()+"  "+hvi.getVariableName()
			+"   "+hvi.getVariableTypeName());
			System.out.println("#################################");
		}
	}
	
	/**
	 * 查询流程实例历史表
	 */
	@Test
	public void findHistoryProcessInstance() {
		HistoricProcessInstance hpi = processEngine.getHistoryService()
			.createHistoricProcessInstanceQuery()
			.processInstanceId("77501")
			.orderByProcessInstanceStartTime().asc()
			.singleResult();
		System.out.println(hpi.getId()+"   "+hpi.getProcessDefinitionId()+"   "
				+hpi.getStartTime()+"   "+hpi.getEndTime()+"   "+hpi.getProcessDefinitionName());
	}
	
	/**
	 * 查询历史活动实例
	 */
	@Test
	public void findHistoryActiviti() {
		List<HistoricActivityInstance> list = processEngine.getHistoryService()
			.createHistoricActivityInstanceQuery()
			.processInstanceId("77501")
			.orderByHistoricActivityInstanceStartTime().asc()
			.list();
		for(HistoricActivityInstance hai:list) {
			System.out.println(hai.getId()+"   "+hai.getProcessInstanceId()+"   "
					+hai.getActivityType()+"   "+hai.getStartTime()+"   "+hai.getEndTime()
					+"   "+hai.getDurationInMillis());
		}   
	}
	
	/**
	 * 流程历史任务实例查询
	 */
	@Test
	public void finHistoryTask() {
		String processInstanceId = "77501";
		List<HistoricTaskInstance> list = processEngine.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricTaskInstanceStartTime().asc()
				.list();
		if(list != null && list.size() > 0) {
			for(HistoricTaskInstance hti : list) {
				System.out.println(hti.getId() + "   " + hti.getName() + "   " +
						hti.getProcessInstanceId() + "   " + hti.getStartTime()
						+ "   " + hti.getEndTime() + "   " + hti.getDurationInMillis());
			}
		}
				
	}
}
