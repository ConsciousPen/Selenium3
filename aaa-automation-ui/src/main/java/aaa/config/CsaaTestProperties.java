package aaa.config;

import toolkit.config.TestProperties;

public class CsaaTestProperties extends TestProperties {

	public static final String AD_PORT = "app.ad.port";            //	e.g. "9082"
	public static final String AD_PATH = "app.ad.path";            //	e.g. "/aaa-admin/admin/login.xhtml"
	public static final String OR_PATH = "app.or.path";
	public static final String OR_PORT = "app.or.port";

	public static final String IS_CI_MODE = "isCiMode";
	public static final String DXP_PORT = "dxp.port";
	public static final String DXP_PROTOCOL = "dxp.protocol";
	public static final String APP_STUB_URL_TEMPLATE = "app.stub.urltemplate";
	public static final String APP_STUB_FOLDER_TEMPLATE = "app.stub.foldertemplate";
	public static final String APP_STUB_SCRIPT_WORKDIR = "app.stub.script.workdir";
	public static final String APP_STUB_SCRIPT_START = "app.stub.script.start";
	public static final String APP_STUB_SCRIPT_STOP = "app.stub.script.stop";
	public static final String DOMAIN_NAME = "domain.name";
	public static final String JOB_FOLDER = "job.folder";
	public static final String REMOTE_DOWNLOAD_FOLDER_PROP = "test.remotefile.location";
	public static final String USER_DIR_PROP = "user.dir";
	public static final String LOCAL_DOWNLOAD_FOLDER_PROP = "test.downloadfiles.location";
	public static final String WIRE_MOCK_STUB_URL_TEMPLATE = "wire.mock.stub.urltemplate";
	public static final String SCRUM_ENVS_SSH = "scrum.envs.ssh";
	public static final String TEST_USSTATE = "test.usstate";
	public static final String CUSTOM_DATE1 = "test.date1";
	public static final String CUSTOM_DATE2 = "test.date2";
	public static final String OAUTH2_ENABLED = "oauth2.enabled";
	public static final String PING_HOST = "ping.host";
	public static final String DXP_CLIENT_ID = "dxp.clientId";
	public static final String DXP_CLIENT_SECRET = "dxp.clientSecret";
	public static final String DXP_GRANT_TYPE = "dxp.grantType";
	public static final String BATCHJOB_RUN_MODE = "batchjob.run.mode"; // available values: http, soap
	public static final String RATING_REPO_USER = "rating.repo.user";
	public static final String RATING_REPO_PASSWORD = "rating.repo.password";
	public static final String RATING_REPO_BRANCH = "rating.repo.branch"; // for openl tests only - name of the branch from "pas-rating" repository from which test excel files should be taken
	public static final String APP_ADMIN_USER = "app.admin.user";
	public static final String APP_ADMIN_PASSWORD = "app.admin.password"; // not necessary if APP_SSH_AUTH_KEYPATH is set
	public static final String APP_SSH_AUTH_KEYPATH = "app.ssh.auth.keypath";
	public static final String OPENL_RATING_LOGS_FOLDER = "openl.rating.logs.folder"; // for openl tests only - path to rating engine logs folder
	public static final String OPENL_RATING_LOGS_FILENAME_REGEXP = "openl.rating.logs.filename.regexp"; // for openl tests only - regexp which matches with all rating log filenames including initial one (e.g. "aaa-rating-engine-app.log, aaa-rating-engine-app.log.1, etc...")
	public static final String OPENL_ATTACH_RATING_LOGS = "openl.attach.rating.logs"; // for openl tests only, available values: "true|always|all" to attach logs always and "failed" to attach only if test fails
	public static final String OPENL_ARCHIVE_RATING_LOGS = "openl.archive.rating.logs"; // for openl tests only - make zip archive of grabbed logs, available values: true, false
	public static final String PAS_ADMIN_LOG_FOLDER = "pas.admin.logs.folder";
	public static final String JIRA_URL = "jira.url";
	public static final String JIRA_LOGIN = "jira.login";
	public static final String JIRA_PASSWORD = "jira.password";
}
