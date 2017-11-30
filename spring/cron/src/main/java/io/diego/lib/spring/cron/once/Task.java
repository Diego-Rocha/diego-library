package io.diego.lib.spring.cron.once;

public interface Task {

	String getId();

	Class<?> getTaskRunnerClass();

	String getTaskRunnerMethod();

	Object[] getTaskRunnerMethodArgs();
}
