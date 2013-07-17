package org.openntf.xpt.demo;

import lotus.domino.Database;
import lotus.domino.Session;
import org.openntf.xpt.agents.XPageAgentJob;
import org.openntf.xpt.agents.annotations.ExecutionMode;
import org.openntf.xpt.agents.annotations.XPagesAgent;;

@XPagesAgent(Alias = "updateAllDocuments", Name = "Update All Documents", executionMode = ExecutionMode.SCHEDULE, intervall = 1)
public class TestAgentBackend extends XPageAgentJob {

	public TestAgentBackend(String name) {
		super(name);
	}

	@Override
	public int executeCode(Session arg0, Database arg1) {
		try {
			setCurrentTaskStatus("Task started");
			setTaskCompletion(0);
			System.out.println(arg0.getCommonUserName());
			System.out.println(arg0.getEffectiveUserName());
			System.out.println(arg1.getFilePath());
			setCurrentTaskStatus("Task finished");
			setTaskCompletion(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


}
