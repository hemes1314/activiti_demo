package junit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ProcessDefinitionTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/**
	 * 部署流程定义（从classpath）
	 */
	@Test
	public void deploymentProcessDefinition_classpath() {
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.name("流程定义")
				.addClasspathResource("diagrams/helloworld.bpmn20.xml")
				.addClasspathResource("diagrams/helloworld.helloworld.png")
				.deploy();
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	@Test
	public void deploymentProcessDefinition_zip() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.name("流程定义")
				.addZipInputStream(zipInputStream)
				.deploy();
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	/**查询流程定义**/
	@Test
	public void findProcessDefinition() {
		List<ProcessDefinition> list = processEngine.getRepositoryService()//与流程定义和部署相关的service
			.createProcessDefinitionQuery()//创建一个流程定义的查询
//			.deploymentId("20001")//使用部署对象id查询
			.processDefinitionKey("helloworld")
			.list();//.count() .listPage
		for(ProcessDefinition pd : list) {
			System.out.println(pd);
			System.out.println("流程定义ID:"+pd.getId());//流程定义key+版本+随机数
			System.out.println("流程定义名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值
			System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值
			System.out.println("流程定义的版本:"+pd.getVersion());//key相同，version升级，默认1
			System.out.println("资源名称bpmn文件:"+pd.getResourceName());
			System.out.println("资源名称png文件:"+pd.getDiagramResourceName());
			System.out.println("部署对象id："+pd.getDeploymentId());
			System.out.println("#############################################");
		}
	}
	
	/**
	 * 删除流程定义
	 */
	@Test
	public void deleteProcessDefinition() {
		String deploymentId = "72505";
		// 不带级联的删除，只能删除没有启动的流程，如果流程启动，就会抛异常
//		processEngine.getRepositoryService().deleteDeployment("32501");
		// 级联删除
		processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
		System.out.println("删除成功");
	}
	
	/**
	 * 查看流程图
	 * @throws IOException 
	 */
	@Test
	public void viewPic() throws IOException {
		String deploymentId = "37501";
		// 将生成图片放到文件夹下
		// 获取图片资源名称
		List<String> list = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);
		// 定义图片资源的名称
		String resourceName = "";
		if(list != null && list.size() > 0) {
			for(String name : list) {
				if(name.indexOf(".png") >= 0) {
					resourceName = name;
				}
			}
		}
		// 获取图片的输入流
		InputStream is = processEngine.getRepositoryService()
			.getResourceAsStream(deploymentId, resourceName);
		File file = new File("D:/"+resourceName);
		FileUtils.copyInputStreamToFile(is, file);
	}
	
	@Test
	public void findLastVersionDefinition() {
		List<ProcessDefinition> list = processEngine.getRepositoryService()
				.createProcessDefinitionQuery().orderByProcessDefinitionVersion().asc()
				.list();
		Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
		if(list != null && list.size() > 0) {
			for(ProcessDefinition pd : list) {
				map.put(pd.getKey(), pd);
			}
		}
		List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());
		if(pdList != null && pdList.size() > 0) {
			for(ProcessDefinition pd : pdList) {
				System.out.println("流程定义ID:"+pd.getId());//流程定义key+版本+随机数
				System.out.println("流程定义名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值
				System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值
				System.out.println("流程定义的版本:"+pd.getVersion());//key相同，version升级，默认1
				System.out.println("资源名称bpmn文件:"+pd.getResourceName());
				System.out.println("资源名称png文件:"+pd.getDiagramResourceName());
				System.out.println("部署对象id："+pd.getDeploymentId());
				System.out.println("#############################################");
			}
		}
	}
	
	@Test
	public void deleteProcessDefinitionByKey() {
		// 一个key多个版本
		String processDefinitionKey = "helloworld";
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
