package com.winitech.common.library.log;

import net.sf.log4jdbc.Properties;
import net.sf.log4jdbc.log.SpyLogDelegator;
import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.sql.Spy;
import net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy;
import net.sf.log4jdbc.sql.resultsetcollector.ResultSetCollector;
import net.sf.log4jdbc.sql.resultsetcollector.ResultSetCollectorPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.library.log
 * └ Log4jdbcSpyLogDelegator.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-07 13:38
 **/
public class Log4jdbcSpyLogDelegator  implements SpyLogDelegator {
	private final Logger jdbcLogger = LoggerFactory.getLogger("jdbc.audit");
	private final Logger resultSetLogger = LoggerFactory.getLogger("jdbc.resultset");
	private final Logger sqlOnlyLogger = LoggerFactory.getLogger("jdbc.sqlonly");
	private final Logger sqlTimingLogger = LoggerFactory.getLogger("jdbc.sqltiming");
	private final Logger connectionLogger = LoggerFactory.getLogger("jdbc.connection");
	private final Logger debugLogger = LoggerFactory.getLogger("log4jdbc.debug");
	private final Logger resultSetTableLogger = LoggerFactory.getLogger("jdbc.resultsettable");
	private static String nl = System.getProperty("line.separator");

	public Log4jdbcSpyLogDelegator() {
	}

	public boolean isJdbcLoggingEnabled() {
		return this.jdbcLogger.isErrorEnabled() || this.resultSetLogger.isErrorEnabled() || this.sqlOnlyLogger.isErrorEnabled() || this.sqlTimingLogger.isErrorEnabled() || this.connectionLogger.isErrorEnabled();
	}

	public void exceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime) {
		String classType = spy.getClassType();
		Integer spyNo = spy.getConnectionNumber();
		String header = spyNo + ". " + classType + "." + methodCall;
		if (sql == null) {
			this.jdbcLogger.error(header, e);
			this.sqlOnlyLogger.error(header, e);
			this.sqlTimingLogger.error(header, e);
		} else {
			sql = this.processSql(sql);
			this.jdbcLogger.error(header + " " + sql, e);
			if (this.sqlOnlyLogger.isDebugEnabled()) {
				this.sqlOnlyLogger.error(getDebugInfo() + nl + spyNo + ". " + sql, e);
			} else {
				this.sqlOnlyLogger.error(header + " " + sql, e);
			}

			if (this.sqlTimingLogger.isDebugEnabled()) {
				this.sqlTimingLogger.error(getDebugInfo() + nl + spyNo + ". " + sql + " {FAILED after " + execTime + " msec}", e);
			} else {
				this.sqlTimingLogger.error(header + " FAILED! " + sql + " {FAILED after " + execTime + " msec}", e);
			}
		}

	}

	public void methodReturned(Spy spy, String methodCall, String returnMsg) {
		String classType = spy.getClassType();
		Logger logger = "ResultSet".equals(classType) ? this.resultSetLogger : this.jdbcLogger;
		if (logger.isInfoEnabled()) {
			String header = spy.getConnectionNumber() + ". " + classType + "." + methodCall + " returned " + returnMsg;
			if (logger.isDebugEnabled()) {
				logger.debug(header + " " + getDebugInfo());
			} else {
				logger.info(header);
			}
		}

	}

	public void constructorReturned(Spy spy, String constructionInfo) {
	}

	private boolean shouldSqlBeLogged(String sql) {
		if (sql == null) {
			return false;
		} else {
			sql = sql.trim();
			if (sql.length() < 6) {
				return false;
			} else {
				sql = sql.substring(0, 6).toLowerCase();
				return Properties.isDumpSqlSelect() && "select".equals(sql) || Properties.isDumpSqlInsert() && "insert".equals(sql) || Properties.isDumpSqlUpdate() && "update".equals(sql) || Properties.isDumpSqlDelete() && "delete".equals(sql) || Properties.isDumpSqlCreate() && "create".equals(sql);
			}
		}
	}

	public void sqlOccurred(Spy spy, String methodCall, String sql) {
		if (isSkipableThread()) {
			return;
		}

		if (!Properties.isDumpSqlFilteringOn() || this.shouldSqlBeLogged(sql)) {
			if (this.sqlOnlyLogger.isDebugEnabled()) {
				this.sqlOnlyLogger.debug(getDebugInfo() + nl + spy.getConnectionNumber() + ". " + "{SQL}" + nl + this.processSql(sql));
			} else if (this.sqlOnlyLogger.isInfoEnabled()) {
				this.sqlOnlyLogger.info("{SQL}" + nl + this.processSql(sql));
			}
		}

	}

	private String processSql(String sql) {
		if (sql == null) {
			return null;
		} else {
			if (Properties.isSqlTrim()) {
				sql = sql.trim();
			}

			StringBuilder output = new StringBuilder();
			if (Properties.getDumpSqlMaxLineLength() <= 0) {
				output.append(sql);
			} else {
				StringTokenizer st = new StringTokenizer(sql);
				int linelength = 0;

				while(st.hasMoreElements()) {
					if (linelength > Properties.getDumpSqlMaxLineLength()) {
						output.append(nl);
						linelength = 0;
						output.append("    ");
						linelength = "    ".length();
					}
					
					String token = (String)st.nextElement();
					
					if (token != null) {
						if (output.length() > 0) {
							switch (token.toUpperCase()) {
								case "SELECT":
								case "FROM":
								case "WHERE":
								case "ORDER":
									output.append(nl);
									linelength = 0;
									break;
							}
						}

						output.append(token);

						int currentTokenLength = token.length();
						linelength = linelength + currentTokenLength;

						output.append(" ");
						++linelength;
					}
				}
			}
			
			if (Properties.isDumpSqlAddSemicolon()) {
				output.append(";");
			}

			String stringOutput = output.toString();
			if (Properties.isTrimExtraBlankLinesInSql()) {
				try (LineNumberReader lineReader = new LineNumberReader(new StringReader(stringOutput))) {
					output = new StringBuilder();
					int contiguousBlankLines = 0;

					while(true) {
						String line = lineReader.readLine();
						if (line == null) {
							break;
						}

						if (line.trim().length() == 0) {
							++contiguousBlankLines;
							if (contiguousBlankLines > 1) {
								continue;
							}
						} else {
							if (output.length() > 0) {
								output.append(nl);
							}

							contiguousBlankLines = 0;
							output.append(line);
						}
					}

					stringOutput = output.toString();
				} catch (IOException e) {
					throw new IllegalStateException("Unexpected IOException from StringReader", e);
				}
			}

			return stringOutput;
		}
	}

	public void sqlTimingOccurred(Spy spy, long execTime, String methodCall, String sql) {
		if (isSkipableThread()) {
			return;
		}

		if (this.sqlTimingLogger.isErrorEnabled() && (!Properties.isDumpSqlFilteringOn() || this.shouldSqlBeLogged(sql))) {
			if (Properties.isSqlTimingErrorThresholdEnabled() && execTime >= Properties.getSqlTimingErrorThresholdMsec()) {
				this.sqlTimingLogger.error(this.buildSqlTimingDump(spy, execTime, methodCall, sql, this.sqlTimingLogger.isDebugEnabled()));
			} else if (this.sqlTimingLogger.isWarnEnabled()) {
				if (Properties.isSqlTimingWarnThresholdEnabled() && execTime >= Properties.getSqlTimingWarnThresholdMsec()) {
					this.sqlTimingLogger.warn(this.buildSqlTimingDump(spy, execTime, methodCall, sql, this.sqlTimingLogger.isDebugEnabled()));
				} else if (this.sqlTimingLogger.isDebugEnabled()) {
					this.sqlTimingLogger.debug(this.buildSqlTimingDump(spy, execTime, methodCall, sql, true));
				} else if (this.sqlTimingLogger.isInfoEnabled()) {
					this.sqlTimingLogger.info(this.buildSqlTimingDump(spy, execTime, methodCall, sql, false));
				}
			}
		}

	}

	private String buildSqlTimingDump(Spy spy, long execTime, String methodCall, String sql, boolean debugInfo) {
		StringBuffer out = new StringBuffer();
		if (debugInfo) {
			out.append(getDebugInfo());
			out.append(nl);
			out.append(spy.getConnectionNumber());
			out.append(". ");
		}

		sql = this.processSql(sql);
		out.append("{SQL executed in ");
		out.append(execTime);
		out.append(" msec} ");
		out.append(nl);
		out.append(sql);
		return out.toString();
	}

	private static String getDebugInfo() {
		Throwable t = new Throwable();
		t.fillInStackTrace();
		StackTraceElement[] stackTrace = t.getStackTrace();
		if (stackTrace == null) {
			return null;
		} else {
			StringBuffer dump = new StringBuffer();
			if (Properties.isDumpFullDebugStackTrace()) {
				boolean first = true;

				for(int i = 0; i < stackTrace.length; ++i) {
					String className = stackTrace[i].getClassName();
					if (!className.startsWith("net.sf.log4jdbc")) {
						if (first) {
							first = false;
						} else {
							dump.append("  ");
						}

						dump.append("at ");
						dump.append(stackTrace[i]);
						dump.append(nl);
					}
				}
			} else {
				dump.append(" ");
				int firstLog4jdbcCall = 0;
				int lastApplicationCall = 0;

				for(int i = 0; i < stackTrace.length; ++i) {
					String className = stackTrace[i].getClassName();
					if (className.startsWith("net.sf.log4jdbc")) {
						firstLog4jdbcCall = i;
					} else if (Properties.isTraceFromApplication() && Pattern.matches(Properties.getDebugStackPrefix(), className)) {
						lastApplicationCall = i;
						break;
					}
				}

				int j = lastApplicationCall;
				if (lastApplicationCall == 0) {
					j = 1 + firstLog4jdbcCall;
				}

				dump.append(stackTrace[j].getClassName()).append(".").append(stackTrace[j].getMethodName()).append("(").append(stackTrace[j].getFileName()).append(":").append(stackTrace[j].getLineNumber()).append(")");
			}

			return dump.toString();
		}
	}

	public void debug(String msg) {
		this.debugLogger.debug(msg);
	}

	public void connectionOpened(Spy spy, long execTime) {
		this.connectionOpened(spy);
	}

	private void connectionOpened(Spy spy) {
		if (this.connectionLogger.isDebugEnabled()) {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection opened " + getDebugInfo());
			this.connectionLogger.debug(ConnectionSpy.getOpenConnectionsDump());
		} else {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection opened");
		}

	}

	public void connectionClosed(Spy spy, long execTime) {
		this.connectionClosed(spy);
	}

	private void connectionClosed(Spy spy) {
		if (this.connectionLogger.isDebugEnabled()) {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection closed " + getDebugInfo());
			this.connectionLogger.debug(ConnectionSpy.getOpenConnectionsDump());
		} else {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection closed");
		}

	}

	public void connectionAborted(Spy spy, long execTime) {
		this.connectionAborted(spy);
	}

	private void connectionAborted(Spy spy) {
		if (this.connectionLogger.isDebugEnabled()) {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection aborted " + getDebugInfo());
			this.connectionLogger.debug(ConnectionSpy.getOpenConnectionsDump());
		} else {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection aborted");
		}

	}

	public boolean isResultSetCollectionEnabled() {
		return this.resultSetTableLogger.isInfoEnabled();
	}

	public boolean isResultSetCollectionEnabledWithUnreadValueFillIn() {
		return this.resultSetTableLogger.isDebugEnabled();
	}

	public void resultSetCollected(ResultSetCollector resultSetCollector) {
		if (isSkipableThread()) {
			return;
		}
		
		String resultsToPrint = (new ResultSetCollectorPrinter()).getResultSetToPrint(resultSetCollector);
		this.resultSetTableLogger.info(resultsToPrint);
	}
	
	private boolean isSkipableThread() {
		String threadName = Thread.currentThread().getName();
		if (threadName.startsWith("WiniJobScheduler") || threadName.equals("SchedulerThread") || threadName.endsWith("_ClusterManager") || threadName.endsWith("_MisfireHandler") ) {
			// WiniScheduler에서 발생한 SQL 로그는 무시
			return true;
		}
		
		return false;
	}
}
