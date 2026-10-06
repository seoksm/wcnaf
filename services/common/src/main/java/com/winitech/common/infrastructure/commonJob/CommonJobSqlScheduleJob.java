package com.winitech.common.infrastructure.commonJob;

import com.winitech.common.annotation.WiniJobSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * com.winitech.common.infrastructure.commonJob
 * └ CommonJobSqlScheduleJob.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-17 15:06
 **/
@Component
@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
@ConditionalOnExpression("${spring.quartz.auto-startup:false}")
public class CommonJobSqlScheduleJob extends QuartzJobBean implements InterruptableJob {
	private final JdbcTemplate jdbcTemplate;
	private Thread sqlExecutionThread = null;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		String sql = context.getMergedJobDataMap().getString("sql");
		
		SqlExecutionTask task = new SqlExecutionTask(sql);
		sqlExecutionThread = new Thread(task);
		sqlExecutionThread.start();
		
		try {
			sqlExecutionThread.join();
		} catch (InterruptedException e) {
			log.info("SQL job interrupted: {}", e.getMessage());
		}
		
		if (task.getException() != null) {
			if (task.getException() instanceof InterruptedException) {
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
	
	private class SqlExecutionTask implements Runnable {
		private final String sql;
		
		@Getter
		private Exception exception;
		
		public SqlExecutionTask(String sql) {
			this.sql = sql;
		}
		
		@Override
		public void run() {
			try {
				// SQL 실행
				jdbcTemplate.execute(sql);
			} catch (DataAccessException ex) {
				this.exception = ex;
			}
		}
	}
}
