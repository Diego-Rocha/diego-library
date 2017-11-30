package io.diego.lib.spring.cron.once;

import org.quartz.Scheduler;

public interface CronScheduler {

	Scheduler getNewScheduler(TaskRunOnceATime tarefa);
}
