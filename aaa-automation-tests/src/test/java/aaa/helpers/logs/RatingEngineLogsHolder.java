package aaa.helpers.logs;

public class RatingEngineLogsHolder {
	private RatingEngineRequestLog requestLog;
	private RatingEngineLog responseLog;

	public RatingEngineLogsHolder() {
		this("", "", "");
	}

	public RatingEngineLogsHolder(String ratingRequestLogContent, String ratingResponseLogContent, String logId) {
		this.requestLog = new RatingEngineRequestLog(ratingRequestLogContent, logId);
		this.responseLog = new RatingEngineLog(ratingResponseLogContent, logId);
	}

	public RatingEngineRequestLog getRequestLog() {
		return requestLog;
	}

	public void setRequestLog(String requestLogContent) {
		this.requestLog = new RatingEngineRequestLog(requestLogContent, "");
	}

	public void setRequestLog(String requestLogContent, String logSectionId) {
		this.requestLog = new RatingEngineRequestLog(requestLogContent, logSectionId);
	}

	public RatingEngineLog getResponseLog() {
		return responseLog;
	}

	public void setResponseLog(String responseLogContent) {
		this.responseLog = new RatingEngineLog(responseLogContent, "");
	}

	public void setResponseLog(String responseLogContent, String logSectionId) {
		this.responseLog = new RatingEngineLog(responseLogContent, logSectionId);
	}

	public void dumpLogs(String requestLogDestinationPath, String responseLogDestinationPath, boolean archiveLogs) {
		getRequestLog().dump(requestLogDestinationPath, archiveLogs);
		getResponseLog().dump(responseLogDestinationPath, archiveLogs);
	}
}
