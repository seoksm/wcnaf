package com.winitech.common.infrastructure.commonJob;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.validation.constraints.NotNull;

/**
 * <pre>
 * com.winitech.common.infrastructure.commonJob
 * └ BaseWiniJobBean.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-18 11:09
 **/
@Slf4j
public abstract class BaseWiniJobBean  extends QuartzJobBean implements InterruptableJob {
	private Thread sqlExecutionThread = null;
	
	protected abstract void executeJob(JobExecutionContext context);
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		String sql = context.getMergedJobDataMap().getString("sql");
		
		Task task = new Task(new Runnable() {
			@Override
			public void run() {
				executeJob(context);
			}
		});
		sqlExecutionThread = new Thread(task);
		sqlExecutionThread.start();

		try {
			sqlExecutionThread.join();
		} catch (InterruptedException e) {
			log.info("SQL job interrupted: {}", e.getMessage());
		}

		if (task.getException() != null) {
			if (task.getException().getCause() != null && task.getException().getCause() instanceof InterruptedException) {
				// 작업 실행이 취소된 경우
				throw new RuntimeException("작업이 취소되었습니다.", new InterruptedException());
			} else if (task.getException() instanceof InterruptedException) {
				// 작업 실행이 취소된 경우
				throw new RuntimeException("작업이 취소되었습니다.", new InterruptedException());
			} else {
				log.error("SQL execution failed: {}", task.getException().getMessage());
				throw new JobExecutionException(task.getException());
			}
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		if (sqlExecutionThread != null) {
			sqlExecutionThread.interrupt();
		}
	}

	private class Task implements Runnable {
		@Getter
		private Exception exception;
		private Runnable runnable;

		public Task(@NotNull Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
                runnable.run();
            } catch (NullPointerException e){
                log.warn("NullPointerException occurred during job execution: {}", e.getMessage());
                this.exception = e;
			} catch (Exception ex) {
				this.exception = ex;
			}
		}
	}
}
