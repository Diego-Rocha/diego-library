package io.diego.lib.spring.cron.once;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class CronSchedulerHolder {

	private static final Logger logger = LoggerFactory.getLogger(CronSchedulerHolder.class.getName());
	private static final Map<String, Scheduler> tarefaMap = new HashMap<>();
	private static CronScheduler cronScheduler;
	private static DateFormat dateFormat;

	@Autowired
	public CronSchedulerHolder(CronScheduler cronScheduler, LocaleContext localeContext) {
		CronSchedulerHolder.cronScheduler = cronScheduler;
		CronSchedulerHolder.dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, localeContext.getLocale());
	}

	public static void add(TaskRunOnceATime tarefa) {
		logger.debug(String.format("Tarefa %s agendada para %s", tarefa.getId(), dateFormat.format(tarefa.getTaskRunAt())));
		tarefaMap.put(tarefa.getId(), cronScheduler.getNewScheduler(tarefa));
	}

	public static void remove(String id) {
		if (!tarefaMap.containsKey(id)) {
			return;
		}
		Scheduler scheduler = tarefaMap.get(id);
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		tarefaMap.remove(id);
		logger.debug(String.format("Tarefa %s removida", id));
	}
}
