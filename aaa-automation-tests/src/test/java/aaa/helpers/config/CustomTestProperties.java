package aaa.helpers.config;

import toolkit.config.TestProperties;

public class CustomTestProperties extends TestProperties {

	public static final String IS_CI_MODE = "isCiMode";
	public static final String DXP_PORT = "dxp.port";
	public static final String DXP_PROTOCOL = "dxp.protocol";
	public static final String ADMIN_PORT = "admin.port";
	public static final String APP_STUB_URL_TEMPLATE = "app.stub.urltemplate";
	public static final String APP_STUB_FOLDER_TEMPLATE = "app.stub.foldertemplate";
	public static final String APP_STUB_RESTART_SCRIPT = "app.stub.restart.script";
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
	public static final String OPENL_RATING_LOGS_FOLDER = "openl.rating.logs.folder"; // for openl rating tests only - path to rating engine logs folder
	public static final String OPENL_RATING_LOGS_FILENAME_REGEXP = "openl.rating.logs.filename.regexp"; // for openl rating tests only - rating log filename regexp template
	public static final String OPENL_GRAB_RATING_LOGS = "openl.grab.rating.logs"; // for openl rating tests only, available values: "true|always|all" to grab logs always and "failed" to grab only if test fails
	public static final String OPENL_ARCHIVE_RATING_LOGS = "openl.archive.rating.logs"; // for openl rating tests only - make zip archive of grabbed logs, available values: true, false
}
