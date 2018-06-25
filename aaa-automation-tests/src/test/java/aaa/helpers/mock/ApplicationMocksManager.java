package aaa.helpers.mock;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.ssh.ExecutionParams;
import aaa.helpers.ssh.RemoteHelper;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.exceptions.IstfException;

public class ApplicationMocksManager {
	protected static final Logger log = LoggerFactory.getLogger(ApplicationMocksManager.class);

	private static final String APP_ADMIN_USER = PropertyProvider.getProperty(CustomTestProperties.APP_ADMIN_USER);
	private static final String APP_ADMIN_PASSWORD = PropertyProvider.getProperty(CustomTestProperties.APP_ADMIN_PASSWORD);
	private static final String APP_AUTH_KEYPATH = PropertyProvider.getProperty(CustomTestProperties.APP_SSH_AUTH_KEYPATH);
	private static final String ENV_NAME = PropertyProvider.getProperty(TestProperties.APP_HOST).split("\\.")[0];
	private static final String TEMP_MOCKS_FOLDER = "src/test/resources/mock";
	private static final String APP_MOCKS_FOLDER = String.format(PropertyProvider.getProperty(CustomTestProperties.APP_STUB_FOLDER_TEMPLATE), ENV_NAME);
	private static final String APP_MOCKS_RESTART_SCRIPT = PropertyProvider.getProperty(CustomTestProperties.APP_STUB_RESTART_SCRIPT);

	private static MocksCollection appMocks = new MocksCollection();

	@SuppressWarnings("unchecked")
	public static synchronized <M extends UpdatableMock> M getMock(MockType mockType) {
		if (!appMocks.has(mockType)) {
			M mock = getMockDataObject(mockType.getFileName(), mockType.getMockModel());
			appMocks.add(mock);
		}
		return (M) appMocks.get(mockType);
	}

	public static synchronized void updateMocks(MocksCollection requiredMocks) {
		MocksCollection mocksToUpload = new MocksCollection();

		for (UpdatableMock mock : requiredMocks) {
			UpdatableMock appMock = getMock(mock.getType());
			if (appMock.add(mock)) {
				mocksToUpload.add(appMock);
			}
		}

		if (!mocksToUpload.isEmpty()) {
			cleanTempDirectory();
			try {
				mocksToUpload.dump(TEMP_MOCKS_FOLDER);
				RemoteHelper.with().user(APP_ADMIN_USER, APP_ADMIN_PASSWORD).privateKey(APP_AUTH_KEYPATH).get().uploadFiles(TEMP_MOCKS_FOLDER, APP_MOCKS_FOLDER);
			} finally {
				cleanTempDirectory();
			}
			restartStubServer();
		}
	}

	public static void restartStubServer() {
		//TODO-dchubkov: restart on Windows and Tomcat
		String command = APP_MOCKS_RESTART_SCRIPT + " -lang jacl -user admin -password admin -c \"\\$AdminControl %1$s cluster_external_stub_server %2$sNode01\"";
		//TimeSetterUtil.getInstance().adjustTime(); // set date to today
		RemoteHelper ssh = RemoteHelper.with().user(APP_ADMIN_USER, APP_ADMIN_PASSWORD).privateKey(APP_AUTH_KEYPATH).get();

		log.info("Stopping stub server...");
		ssh.executeCommand(String.format(command, "stopServer", ENV_NAME), ExecutionParams.with().timeoutInSeconds(300).failOnTimeout().failOnError());
		log.info("Stub server has been stopped");

		log.info("Starting stub server...it may take up to 10 minutes");
		ssh.executeCommand(String.format(command, "startServer", ENV_NAME), ExecutionParams.with().timeoutInSeconds(700).failOnTimeout().failOnError());
		log.info("Stub server has been started");
	}

	private static <M> M getMockDataObject(String fileName, Class<M> mockDataClass) {
		String mockSourcePath = APP_MOCKS_FOLDER + "/" + fileName;
		String mockTempDestinationPath = TEMP_MOCKS_FOLDER + "/" + RandomStringUtils.randomNumeric(10) + "_" + fileName;

		RemoteHelper.get().downloadFile(mockSourcePath, mockTempDestinationPath);
		File mockTempFile = new File(mockTempDestinationPath);
		M mockObject;
		try (ExcelUnmarshaller excelUnmarshaller = new ExcelUnmarshaller(mockTempFile)) {
			mockObject = excelUnmarshaller.unmarshal(mockDataClass);
		} finally {
			if (!mockTempFile.delete()) {
				log.error("Unable to delete temp mock file: %s", mockTempDestinationPath);
			}
		}
		return mockObject;
	}

	private static void cleanTempDirectory() {
		try {
			FileUtils.cleanDirectory(new File(TEMP_MOCKS_FOLDER));
		} catch (IOException e) {
			throw new IstfException("Unable to clean mocks temp directory: " + TEMP_MOCKS_FOLDER, e);
		}
	}
}
