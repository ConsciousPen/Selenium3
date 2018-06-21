package aaa.helpers.ssh;

import java.util.Objects;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public class ConnectionParams {
	private static final int DEFAULT_EXEC_TIMEOUT_IN_SECONDS = 300;
	private static final int DEFAULT_EXEC_RETRY_INTERVAL_IN_MILLISECONDS = 100;
	private static final int DEFAULT_SSH_PORT = 22;
	private static final boolean DEFAULT_FAIL_IF_EXEC_TIMEOUT_EXCEEDED = false;

	static ConnectionParams defaultConnectionParams;

	private String host;
	private int port;
	private String user;
	private String password;
	private String privateKeyPath;
	private int execTimeoutInSeconds;
	private int execRetryIntervalInMilliseconds;
	private boolean failIfExecTimeoutExceeded;

	ConnectionParams() {
		this.host = PropertyProvider.getProperty(TestProperties.APP_HOST);
		this.port = DEFAULT_SSH_PORT;
		this.user = PropertyProvider.getProperty(TestProperties.SSH_USER);
		this.password = PropertyProvider.getProperty(TestProperties.SSH_PASSWORD);
		this.privateKeyPath = null;
		this.execTimeoutInSeconds = DEFAULT_EXEC_TIMEOUT_IN_SECONDS;
		this.execRetryIntervalInMilliseconds = DEFAULT_EXEC_RETRY_INTERVAL_IN_MILLISECONDS;
		this.failIfExecTimeoutExceeded = DEFAULT_FAIL_IF_EXEC_TIMEOUT_EXCEEDED;
	}

	static ConnectionParams getDefault() {
		if (defaultConnectionParams == null) {
			defaultConnectionParams = new ConnectionParams();
		}
		return defaultConnectionParams;
	}

	public ConnectionParams host(String host) {
		this.host = host;
		return this;
	}

	public ConnectionParams port(int port) {
		this.port = port;
		return this;
	}

	public ConnectionParams user(String user) {
		return user(user, null);
	}

	public ConnectionParams user(String user, String password) {
		this.user = user;
		this.password = password;
		return this;
	}

	public ConnectionParams privateKey(String privateKey) {
		this.privateKeyPath = privateKey;
		return this;
	}

	public ConnectionParams execTimeoutInSeconds(int execTimeoutInSeconds) {
		this.execTimeoutInSeconds = execTimeoutInSeconds;
		return this;
	}

	public ConnectionParams execRetryIntervalInMilliseconds(int execRetryIntervalInMilliseconds) {
		this.execRetryIntervalInMilliseconds = execRetryIntervalInMilliseconds;
		return this;
	}

	public ConnectionParams failIfExecTimeoutExceeded(boolean failIfExecTimeoutExceeded) {
		this.failIfExecTimeoutExceeded = failIfExecTimeoutExceeded;
		return this;
	}

	String getHost() {
		return host;
	}

	int getPort() {
		return port;
	}

	String getUser() {
		return user;
	}

	String getPassword() {
		return password;
	}

	String getPrivateKeyPath() {
		return privateKeyPath;
	}

	int getExecTimeoutInSeconds() {
		return execTimeoutInSeconds;
	}

	int getExecRetryIntervalInMilliseconds() {
		return execRetryIntervalInMilliseconds;
	}

	boolean isFailIfExecTimeoutExceeded() {
		return failIfExecTimeoutExceeded;
	}

	public RemoteHelper get() {
		return RemoteHelper.get(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ConnectionParams other = (ConnectionParams) o;
		return port == other.port &&
				execTimeoutInSeconds == other.execTimeoutInSeconds &&
				execRetryIntervalInMilliseconds == other.execRetryIntervalInMilliseconds &&
				failIfExecTimeoutExceeded == other.failIfExecTimeoutExceeded &&
				Objects.equals(host, other.host) &&
				Objects.equals(user, other.user) &&
				Objects.equals(password, other.password) &&
				Objects.equals(privateKeyPath, other.privateKeyPath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(host, port, user, password, privateKeyPath, execTimeoutInSeconds, execRetryIntervalInMilliseconds, failIfExecTimeoutExceeded);
	}

	@Override
	public String toString() {
		return "ConnectionParams{" +
				"host='" + host + '\'' +
				", port=" + port +
				", user='" + user + '\'' +
				", privateKeyPath='" + privateKeyPath + '\'' +
				", execTimeoutInSeconds=" + execTimeoutInSeconds +
				", execRetryIntervalInMilliseconds=" + execRetryIntervalInMilliseconds +
				", failIfExecTimeoutExceeded=" + failIfExecTimeoutExceeded +
				'}';
	}
}
