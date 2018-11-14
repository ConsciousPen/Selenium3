package aaa.helpers.ssh;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class CommandResults {
	private CharSequence output;
	private CharSequence errorOutput;
	private int exitCode;
	private Instant executionStartTime;
	private Instant executionEndTime;

	// Constructor has package access since this class should be instantiated/used only within 'ssh' package
	CommandResults() {
	}

	public String getOutput() {
		return output.toString().trim();
	}

	void setOutput(CharSequence output) {
		this.output = Objects.requireNonNull(output);
	}

	public String getErrorOutput() {
		return errorOutput.toString().trim();
	}

	void setErrorOutput(CharSequence errorOutput) {
		this.errorOutput = Objects.requireNonNull(errorOutput);
	}

	public int getExitCode() {
		return exitCode;
	}

	void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public Instant getExecutionStartTime() {
		return executionStartTime;
	}

	void setExecutionStartTime(Instant executionStartTime) {
		this.executionStartTime = Objects.requireNonNull(executionStartTime);
	}

	public Instant getExecutionEndTime() {
		return executionEndTime;
	}

	void setExecutionEndTime(Instant executionEndTime) {
		this.executionEndTime = Objects.requireNonNull(executionEndTime);
	}

	public Duration getDuration() {
		return Duration.between(getExecutionStartTime(), getExecutionEndTime()).abs();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		boolean seconds = getDuration().getSeconds() > 0;
		sb.append("{\n");
		sb.append("    Command output: '").append(output.length() > 0 ? getOutput() : "<EMPTY>").append("'\n");
		sb.append("    Exit code:      ").append(getExitCode()).append("\n");
		if (errorOutput.length() > 0) {
			sb.append("    Error output:   '").append(getErrorOutput()).append("'\n");
		}
		sb.append("    Execution time: ").append(seconds ? getDuration().getSeconds() : getDuration().toMillis()).append(seconds ? " second(s)" : " millisecond(s)").append("\n}");
		return sb.toString();
	}
}
