package aaa.helpers.ssh;

import java.util.Objects;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public final class ConnectionParams {
	static final ConnectionParams DEFAULT = new ConnectionParams();

	private String host;
	private int port;
	private String user;
	private String password;
	private String privateKeyPath;

	ConnectionParams() {
		this.port = 22;
		this.host = PropertyProvider.getProperty(TestProperties.APP_HOST);
		this.user = PropertyProvider.getProperty(TestProperties.SSH_USER);
		this.password = PropertyProvider.getProperty(TestProperties.SSH_PASSWORD);
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
				", pass='" + (password != null ? "*****" : "<NOT USED>") + '\'' +
				", privateKeyPath='" + (privateKeyPath != null ? privateKeyPath : "<NOT USED>") + '\'' +
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
