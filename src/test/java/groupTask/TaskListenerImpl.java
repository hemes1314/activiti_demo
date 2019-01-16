package groupTask;

import java.util.Arrays;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TaskListenerImpl implements TaskListener {

	private static final long serialVersionUID = 462915622784644381L;

	@Override
	public void notify(DelegateTask delegateTask) {
		// 制定个人任务的办理人，也可以制定组任务的办理人
		// 个人任务：通过类去查询数据库，将下一个任务的办理人查询获取
		delegateTask.addCandidateUsers(Arrays.asList("郭靖","黄蓉"));
	}

}
