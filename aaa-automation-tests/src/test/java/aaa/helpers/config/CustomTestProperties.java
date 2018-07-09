package aaa.helpers.config;

import toolkit.config.TestProperties;

public class CustomTestProperties extends TestProperties {

	public static final String IS_CI_MODE = "isCiMode";
	public static final String DXP_PORT = "dxp.port";
	public static final String DXP_PROTOCOL = "dxp.protocol";
	public static final String ADMIN_PORT = "admin.port";
	public static final String APP_STUB_URL_TEMPLATE = "app.stub.urltemplate";
	public static final String APP_STUB_FOLDER_TEMPLATE = "app.stub.foldertemplate";
	public static final String APP_STUB_SCRIPT_WORKDIR = "app.stub.script.workdir";
	public static final String APP_STUB_SCRIPT_START = "app.stub.script.start";
	public static final String APP_STUB_SCRIPT_STOP = "app.stub.script.stop";
	public static final String DOMAIN_NAME = "domain.name";
	public static final String JOB_FOLDER = "job.folder";
	public static final String WIRE_MOCK_STUB_URL_TEMPLATE = "wire.mock.stub.urltemplate";
	public static final String SCRUM_ENVS_SSH = "scrum.envs.ssh";
	public static final String CUSTOM_DATE1 = "test.date1";
	public static final String CUSTOM_DATE2 = "test.date2";
	public static final String OAUTH2_ENABLED = "oauth2.enabled";
	public static final String PING_HOST = "ping.host";
	public static final String DXP_CLIENT_ID = "dxp.clientId";
	public static final String DXP_CLIENT_SECRET = "dxp.clientSecret";
	public static final String DXP_GRANT_TYPE = "dxp.grantType";
	public static final String SOAP_BATCHJOB_TEMLATE = "soap.batchjob.template";
	public static final String BATCHJOB_RUN_MODE = "batchjob.run.mode"; // available values: http, soap
	public static final String RATING_REPO_USER = "rating.repo.user";
	public static final String RATING_REPO_PASSWORD = "rating.repo.password";
	public static final String APP_ADMIN_USER = "app.admin.user";
	public static final String APP_ADMIN_PASSWORD = "app.admin.password"; // not necessary if APP_SSH_AUTH_KEYPATH is set
	public static final String APP_SSH_AUTH_KEYPATH = "app.ssh.auth.keypath";
}
