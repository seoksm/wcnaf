package com.winitech.common.library;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * com.winitech.common.library
 * └ WiniDebouncer.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-09 09:48
 **/
public class WiniDebouncer {
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> debounceTask;

	public void debounce(Runnable task, long delayMs) {
		if (debounceTask != null && !debounceTask.isDone()) {
			debounceTask.cancel(false); // 이전 작업 취소
		}
		debounceTask = scheduler.schedule(task, delayMs, TimeUnit.MILLISECONDS);
	}
}
