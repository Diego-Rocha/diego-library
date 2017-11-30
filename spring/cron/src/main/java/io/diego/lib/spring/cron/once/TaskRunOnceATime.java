package io.diego.lib.spring.cron.once;

import java.util.Date;

public interface TaskRunOnceATime extends Task {

	Date getTaskRunAt();
}
