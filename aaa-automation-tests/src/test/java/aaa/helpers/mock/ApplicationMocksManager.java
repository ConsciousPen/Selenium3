package aaa.helpers.mock;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;
import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;
import aaa.helpers.mock.model.property_risk_reports.RetrievePropertyRiskReportsMock;
import aaa.helpers.openl.mock_generator.MockGenerator;
import aaa.helpers.ssh.ExecutionParams;
import aaa.helpers.ssh.RemoteHelper;
import aaa.utils.excel.bind.BindHelper;
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

	public static RetrieveMembershipSummaryMock getRetrieveMembershipSummaryMock() {
		return getMock(RetrieveMembershipSummaryMock.class, RetrieveMembershipSummaryMock.FILE_NAME);
	}

	public static RetrievePropertyClassificationMock getRetrievePropertyClassificationMock() {
		return getMock(RetrievePropertyClassificationMock.class, RetrievePropertyClassificationMock.FILE_NAME);
	}

	public static RetrievePropertyRiskReportsMock getRetrievePropertyRiskReportsMock() {
		return getMock(RetrievePropertyRiskReportsMock.class, RetrievePropertyRiskReportsMock.FILE_NAME);
	}

	public static synchronized <M extends UpdatableMock> M getMock(Class<M> mockModelClass) {
		return getMock(mockModelClass, getFileName(mockModelClass));
	}

	@SuppressWarnings("unchecked")
	public static synchronized <M extends UpdatableMock> M getMock(Class<M> mockModelClass, String fileName) {
		if (!appMocks.has(mockModelClass)) {
			M mock = getMockDataObject(mockModelClass, fileName);
			appMocks.add(mock);
		}
		return (M) appMocks.get(mockModelClass);
	}

	public static synchronized void updateMocks(MocksCollection requiredMocks) {
		assertThat(requiredMocks).as("Unable to update application mocks with null value").isNotNull();
		log.info("Application mocks update initiated");
		MocksCollection mocksToUpload = new MocksCollection();
		for (UpdatableMock mock : requiredMocks) {
			UpdatableMock appMock = getMock(mock.getClass(), mock.getFileName());
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
			MockGenerator.flushGeneratedMocks();
			log.info("Application mocks update has been finished");
		} else {
			log.info("Application server has all required mocks, nothing to update");
		}
	}

	public static synchronized void restartStubServer() {
		LocalDate serverDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
		assertThat(serverDate).as("Stub server restart is not allowed on instance with shifted time.\nCurrent date is %1$s, Current date on server is: %2$s", LocalDate.now(), serverDate)
				.isEqualTo(LocalDate.now());

		//TODO-dchubkov: restart on Windows and Tomcat
		String command = APP_MOCKS_RESTART_SCRIPT + " -lang jacl -user admin -password admin -c \"\\$AdminControl %1$s cluster_external_stub_server %2$sNode01\"";
		RemoteHelper ssh = RemoteHelper.with().user(APP_ADMIN_USER, APP_ADMIN_PASSWORD).privateKey(APP_AUTH_KEYPATH).get();

		log.info("Stopping stub server...");
		ssh.executeCommand(String.format(command, "stopServer", ENV_NAME), ExecutionParams.with().timeoutInSeconds(300).failOnTimeout().failOnError());
		log.info("Stub server has been stopped");

		log.info("Starting stub server...it may take up to 10 minutes");
		ssh.executeCommand(String.format(command, "startServer", ENV_NAME), ExecutionParams.with().timeoutInSeconds(700).failOnTimeout().failOnError());
		log.info("Stub server has been started");
	}

	private static <M extends UpdatableMock> M getMockDataObject(Class<M> mockDataClass, String fileName) {
		String mockSourcePath = Paths.get(APP_MOCKS_FOLDER, fileName).normalize().toString();
		String mockTempDestinationPath = Paths.get(TEMP_MOCKS_FOLDER, RandomStringUtils.randomNumeric(10) + "_" + fileName).normalize().toString();

		if (!RemoteHelper.get().isPathExist(mockSourcePath)) {
			log.warn("There is no \"{}\" mock in application server, empty mock object will be returned", mockSourcePath);
			return MockGenerator.getEmptyMock(mockDataClass);
		}

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

	@SuppressWarnings("unchecked")
	private static <M extends UpdatableMock> String getFileName(Class<M> mockModelClass) {
		switch (mockModelClass.getSimpleName()) {
			case "RetrieveMembershipSummaryMock":
				return RetrieveMembershipSummaryMock.FILE_NAME;
			case "RetrievePropertyClassificationMock":
				return RetrievePropertyClassificationMock.FILE_NAME;
			case "RetrievePropertyRiskReportsMock":
				return RetrievePropertyRiskReportsMock.FILE_NAME;
			default:
				M mock;
				try {
					mock = (M) BindHelper.getInstance(mockModelClass);
				} catch (RuntimeException e) {
					throw new IstfException("Unable to get filename for mock of class: " + mockModelClass.getName());
				}
				return mock.getFileName();
		}
	}
}
