package aaa.helpers.ssh;

public final class ExecutionParams {
	public static final ExecutionParams DEFAULT = new ExecutionParams();

	private int timeoutInSeconds;
	private int retryIntervalInMilliseconds;
	private boolean failOnTimeout;
	private boolean failOnError;
	private boolean returnErrorOutput;

	private ExecutionParams() {
		this.timeoutInSeconds = 300;
		this.retryIntervalInMilliseconds = 100;
		this.failOnTimeout = false;
		this.failOnError = false;
		this.returnErrorOutput = false;
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
