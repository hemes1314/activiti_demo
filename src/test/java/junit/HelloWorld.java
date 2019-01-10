package junit;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * act_re_deployment #部署对象
 * act_re_procdef #流程定义
 * act_ge_bytearray #资源文件
 * act_ge_property #主键生成策略表
 * act_ru_execution #正在执行对象
 * act_hi_procinst #流程实例历史表
 * act_ru_task #正在执行任务
 * act_hi_taskinst #任务历史
 * act_hi_varinst #变量历史

	账号	密码	角色
	kermit	kermit	admin
	gonzo	gonzo	manager
	fozzie	fozzie	user
 */
public class HelloWorld {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/**
	 * 部署流程定义
	 * act_re_deployment
	 * act_re_procdef
	 */
	@Test
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的service
			.createDeployment()//创建部署对象
			.name("helloworld入门程序") // 添加部署名称
			.addClasspathResource("diagrams/helloworld.bpmn20.xml")// 从classpath的资源中加载，一次只能加载一个文件\\
			.addClasspathResource("diagrams/helloworld.helloworld.png")// 从classpath的资源中加载，一次只能加载一个文件\\
			.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	/**
	 * 启动流程实例
	 * act_hi_procinst
	 * act_ru_task
	 */
	@Test
	public void startProcessInstance() {
		String processDefinitionKey = "helloworld";
		ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service
			.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID："+pi.getId()); // 101 act_hi_procinst
		System.out.println("流程定义ID："+pi.getProcessDefinitionId());// helloworld:1:4 act_re_procdef
		ProcessDefinition pd = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(pi.getProcessDefinitionId()).singleResult();
		System.out.println("部署id："+pd.getDeploymentId());
	}
	
	/**
	 * 查询当前人的个人任务
	 * 
	 */
	@Test
	public void findMyPersionalTask() {
		List<Task> list = processEngine.getTaskService()
			.createTaskQuery()
			.taskAssignee("张三")
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
		}
	}
	
	/**完成我的任务**/
	@Test
	public void completeMyPersionalTask() {
		String taskId = "12508";
		processEngine.getTaskService()//与正在执行的任务管理相关的service
			.complete(taskId);
		System.out.println("完成任务：任务ID："+taskId);
	}
}
