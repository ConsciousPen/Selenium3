package aaa.helpers.ssh;

import java.util.Objects;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public final class ConnectionParams {
	static final ConnectionParams DEFAULT = new ConnectionParams();
	private static final int DEFAULT_SSH_PORT = 22;
	private static final String DEFAULT_HOST = PropertyProvider.getProperty(TestProperties.APP_HOST);
	private static final String DEFAULT_USER = PropertyProvider.getProperty(TestProperties.SSH_USER);
	private static final String DEFAULT_PASSWORD = PropertyProvider.getProperty(TestProperties.SSH_PASSWORD);

	private String host;
	private int port;
	private String user;
	private String password;
	private String privateKeyPath;

	ConnectionParams() {
		this.port = DEFAULT_SSH_PORT;
		this.host = DEFAULT_HOST;
		this.user = DEFAULT_USER;
		this.password = DEFAULT_PASSWORD;
		this.privateKeyPath = null;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ConnectionParams other = (ConnectionParams) o;
		return getPort() == other.getPort() &&
				Objects.equals(getHost(), other.getHost()) &&
				Objects.equals(getUser(), other.getUser()) &&
				Objects.equals(getPassword(), other.getPassword()) &&
				Objects.equals(getPrivateKeyPath(), other.getPrivateKeyPath());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getHost(), getPort(), getUser(), getPassword(), getPrivateKeyPath());
	}

	@Override
	public String toString() {
		return "ConnectionParams{" +
				"host='" + host + '\'' +
				", port=" + port +
				", user='" + user + '\'' +
				", privateKeyPath='" + privateKeyPath + '\'' +
				'}';
	}

	public ConnectionParams port(int port) {
		this.port = port;
		return this;
	}

	public ConnectionParams host(String host) {
		this.host = host;
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

	public RemoteHelper get() {
		return RemoteHelper.get(this);
	}
}
