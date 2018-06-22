package aaa.helpers.ssh;

public final class ExecutionParams {
	public static final ExecutionParams DEFAULT = new ExecutionParams();

	private static final int DEFAULT_TIMEOUT_IN_SECONDS = 300;
	private static final int DEFAULT_RETRY_INTERVAL_IN_MILLISECONDS = 100;
	private static final boolean DEFAULT_FAIL_ON_TIMEOUT = false;
	private static final boolean DEFAULT_FAIL_ON_ERROR = false;
	private static final boolean DEFAULT_RETURN_ERROR_OUTPUT = false;

	private int timeoutInSeconds;
	private int retryIntervalInMilliseconds;
	private boolean failOnTimeout;
	private boolean failOnError;
	private boolean returnErrorOutput;

	private ExecutionParams() {
		this.timeoutInSeconds = DEFAULT_TIMEOUT_IN_SECONDS;
		this.retryIntervalInMilliseconds = DEFAULT_RETRY_INTERVAL_IN_MILLISECONDS;
		this.failOnTimeout = DEFAULT_FAIL_ON_TIMEOUT;
		this.failOnError = DEFAULT_FAIL_ON_ERROR;
		this.returnErrorOutput = DEFAULT_RETURN_ERROR_OUTPUT;
	}

	int getTimeoutInSeconds() {
		return timeoutInSeconds;
	}

	int getRetryIntervalInMilliseconds() {
		return retryIntervalInMilliseconds;
	}

	boolean isFailOnTimeout() {
		return failOnTimeout;
	}

	boolean isFailOnError() {
		return failOnError;
	}

	boolean isReturnErrorOutput() {
		return returnErrorOutput;
	}

	public static ExecutionParams with() {
		return new ExecutionParams();
	}

	@Override
	public String toString() {
		return "ExecutionParams{" +
				"timeoutInSeconds=" + timeoutInSeconds +
				", retryIntervalInMilliseconds=" + retryIntervalInMilliseconds +
				", failOnTimeout=" + failOnTimeout +
				", failOnError=" + failOnError +
				", returnErrorOutput=" + returnErrorOutput +
				'}';
	}

	public ExecutionParams timeoutInSeconds(int timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
		return this;
	}

	public ExecutionParams retryIntervalInMilliseconds(int retryIntervalInMilliseconds) {
		this.retryIntervalInMilliseconds = retryIntervalInMilliseconds;
		return this;
	}

	public ExecutionParams failOnTimeout() {
		return failOnTimeout(true);
	}

	public ExecutionParams failOnTimeout(boolean failOnTimeout) {
		this.failOnTimeout = failOnTimeout;
		return this;
	}

	public ExecutionParams failOnError() {
		return failOnError(true);
	}

	public ExecutionParams failOnError(boolean failOnError) {
		this.failOnError = failOnError;
		return this;
	}

	public ExecutionParams returnErrorOutput() {
		return returnErrorOutput(true);
	}

	public ExecutionParams returnErrorOutput(boolean returnErrorOutput) {
		this.returnErrorOutput = returnErrorOutput;
		return this;
	}
}
