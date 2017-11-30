package io.diego.lib.spring.cron.once;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJobListener implements JobListener {

	private final Task tarefa;
	private static final Logger logger = LoggerFactory.getLogger(MyJobListener.class.getName());

	public MyJobListener(Task tarefa) {
		this.tarefa = tarefa;
	}

	@Override
	public String getName() {
		return String.format("job_listener_%s", tarefa.getId());
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
		logger.debug(String.format("Tarefa %s será executada", tarefa.getId()));
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
		logger.debug(String.format("Tarefa %s não será executada", tarefa.getId()));
	}

	@Override
	public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
		logger.debug(String.format("Tarefa %s foi executada", tarefa.getId()));
		CronSchedulerHolder.remove(tarefa.getId());
	}
}
