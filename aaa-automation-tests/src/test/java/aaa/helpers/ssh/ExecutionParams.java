package aaa.helpers.ssh;

import java.time.Duration;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;

public final class ExecutionParams {
	public static final ExecutionParams DEFAULT = new ExecutionParams();

	private Duration timeout;
	private Duration retryPollingInterval;
	private boolean failOnTimeout;
	private boolean failOnError;
	private List<Integer> exitCodesToIgnore;

	private ExecutionParams() {
		this.timeout = Duration.ofMinutes(5);
		this.retryPollingInterval = Duration.ofMillis(100);
		this.failOnTimeout = false;
		this.failOnError = false;
		this.exitCodesToIgnore = new ArrayList<>();
	}

	Duration getTimeout() {
		return timeout;
	}

	Duration getRetryPollingInterval() {
		return retryPollingInterval;
	}

	boolean isFailOnTimeout() {
		return failOnTimeout;
	}

	boolean isFailOnError() {
		return failOnError;
	}

	List<Integer> getExitCodesToIgnore() {
		return Collections.unmodifiableList(exitCodesToIgnore);
	}

	public static ExecutionParams with() {
		return new ExecutionParams();
	}

	public ExecutionParams timeoutInSeconds(long timeoutInSeconds) {
		return timeout(Duration.ofSeconds(timeoutInSeconds));
	}

	public ExecutionParams timeout(Duration timeout) {
		this.timeout = Objects.requireNonNull(timeout);
		return this;
	}

	public ExecutionParams retryPollingInterval(Duration retryPollingInterval) {
		this.retryPollingInterval = Objects.requireNonNull(retryPollingInterval);
		return this;
	}

	public ExecutionParams retryPollingIntervalInMilliseconds(long retryPollingIntervalInMilliseconds) {
		return retryPollingInterval(Duration.ofMillis(retryPollingIntervalInMilliseconds));
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

	public ExecutionParams failOnErrorIgnoring(int... exitCodes) {
		Objects.requireNonNull(exitCodes);
		failOnError(true);
		Arrays.stream(exitCodes).forEach(e -> exitCodesToIgnore.add(e));
		return this;
	}

	@Override
	public String toString() {
		String params = "ExecutionParams{" +
				"timeout(s)=" + timeout.getSeconds() +
				", retryPollingInterval(ms)=" + retryPollingInterval.toMillis() +
				", failOnTimeout=" + failOnTimeout +
				", failOnError=" + failOnError;

		if (CollectionUtils.isNotEmpty(exitCodesToIgnore)) {
			params += ", exitCodesToIgnore=" + exitCodesToIgnore;
		}
		params += '}';
		return params;
	}
}
