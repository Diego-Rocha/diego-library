package io.diego.lib.spring.cron.once;

import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CronSchedulerImpl implements CronScheduler {

	private static final String CRON_ONCE_AT_TIME = "s m H d M ? yyyy";
	private TaskRunOnceATime tarefa;
	private JobDetail job;
	private Trigger trigger;
	private Scheduler scheduler;

	@Override
	public Scheduler getNewScheduler(TaskRunOnceATime tarefa) {
		try {
			this.tarefa = tarefa;
			this.job = buildNewJobDetail();
			this.trigger = buildNewTrigger();
			this.scheduler = buildNewScheduler();
			this.scheduler.start();
			return scheduler;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private JobDetail buildNewJobDetail() throws NoSuchMethodException, ClassNotFoundException {
		MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
		jobDetailFactoryBean.setName("job_" + tarefa.getId());
		jobDetailFactoryBean.setTargetClass(tarefa.getTaskRunnerClass());
		jobDetailFactoryBean.setTargetMethod(tarefa.getTaskRunnerMethod());
		jobDetailFactoryBean.setArguments(tarefa.getTaskRunnerMethodArgs());
		jobDetailFactoryBean.afterPropertiesSet();
		JobDetail jobDetail = jobDetailFactoryBean.getObject();
		return jobDetail;
	}

	private Trigger buildNewTrigger() throws ParseException {
		Date date = tarefa.getTaskRunAt();
		if (date.compareTo(new Date()) <= 0) {
			date = new Date();
		}
		CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(this.job);
		cronTriggerFactoryBean.setName("trigger_" + tarefa.getId());
		cronTriggerFactoryBean.setCronExpression(new SimpleDateFormat(CRON_ONCE_AT_TIME).format(date));
		cronTriggerFactoryBean.afterPropertiesSet();
		return cronTriggerFactoryBean.getObject();
	}

	private Scheduler buildNewScheduler() throws Exception {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setSchedulerName("scheduler_" + tarefa.getId());
		schedulerFactoryBean.setJobDetails(this.job);
		schedulerFactoryBean.setTriggers(this.trigger);
		schedulerFactoryBean.setAutoStartup(false);
		schedulerFactoryBean.afterPropertiesSet();
		Scheduler scheduler = schedulerFactoryBean.getObject();
		scheduler.getListenerManager().addJobListener(getJobListener());
		return scheduler;
	}

	private JobListener getJobListener() {
		return new MyJobListener(tarefa);
	}
}
