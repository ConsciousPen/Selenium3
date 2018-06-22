package aaa.helpers.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.ssh.ExecutionParams;
import aaa.helpers.ssh.RemoteHelper;
import aaa.utils.excel.bind.ExcelMarshaller;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

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
		List<UpdatableMock> mocksToUpload = new ArrayList<>();

		for (UpdatableMock mock : requiredMocks) {
			UpdatableMock appMock = getMock(mock.getType());
			mocksToUpload.add(appMock.merge(mock));
		}

		if (!mocksToUpload.isEmpty()) {
			RemoteHelper.get().clearFolder(TEMP_MOCKS_FOLDER);
			ExcelMarshaller excelMarshaller = new ExcelMarshaller();
			for (UpdatableMock mock : mocksToUpload) {
				File output = new File(TEMP_MOCKS_FOLDER, mock.getType().getFileName());
				excelMarshaller.marshal(mock, output);
			}
			RemoteHelper.get()
					.uploadFiles(TEMP_MOCKS_FOLDER, APP_MOCKS_FOLDER)
					.clearFolder(TEMP_MOCKS_FOLDER);

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
}
