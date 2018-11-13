package aaa.helpers.mock;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.timesetter.client.TimeSetterClient;
import aaa.config.CsaaTestProperties;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;
import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;
import aaa.helpers.mock.model.property_risk_reports.RetrievePropertyRiskReportsMock;
import aaa.helpers.openl.mock_generator.MockGenerator;
import aaa.helpers.ssh.CommandResults;
import aaa.helpers.ssh.ExecutionParams;
import aaa.helpers.ssh.RemoteHelper;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import aaa.utils.excel.bind.ReflectionHelper;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.exceptions.IstfException;

public class ApplicationMocksManager {
	protected static final Logger log = LoggerFactory.getLogger(ApplicationMocksManager.class);

	private static final String APP_ADMIN_USER = PropertyProvider.getProperty(CsaaTestProperties.APP_ADMIN_USER);
	private static final String APP_ADMIN_PASSWORD = PropertyProvider.getProperty(CsaaTestProperties.APP_ADMIN_PASSWORD);
	private static final String APP_AUTH_KEYPATH = PropertyProvider.getProperty(CsaaTestProperties.APP_SSH_AUTH_KEYPATH);
	private static final String ENV_NAME = PropertyProvider.getProperty(TestProperties.APP_HOST).split("\\.")[0];
	private static final String TEMP_MOCKS_FOLDER = "src/test/resources/mock";
	private static final String APP_MOCKS_FOLDER = String.format(PropertyProvider.getProperty(CsaaTestProperties.APP_STUB_FOLDER_TEMPLATE), ENV_NAME);
	private static final String APP_MOCKS_SCRIPT_WORKDIR = PropertyProvider.getProperty(CsaaTestProperties.APP_STUB_SCRIPT_WORKDIR);
	private static final String APP_MOCKS_SCRIPT_START = String.format(PropertyProvider.getProperty(CsaaTestProperties.APP_STUB_SCRIPT_START), ENV_NAME);
	private static final String APP_MOCKS_SCRIPT_STOP = String.format(PropertyProvider.getProperty(CsaaTestProperties.APP_STUB_SCRIPT_STOP), ENV_NAME);
	private static final String START_STUB_KNOWN_EXCEPTION = "com.ibm.websphere.management.exception.ConnectorException: java.net.SocketTimeoutException: Async operation timed out";
	private static final int START_STUB_FAILURE_EXIT_CODE = 103;
	private static OS currentOS;

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
				getRemoteHelper().uploadFiles(TEMP_MOCKS_FOLDER, APP_MOCKS_FOLDER);
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

	public static void restartStubServer() {
		stopStubServer();
		sleep(10); // Possibly to avoid error 103 we need to sleep some time before starting stub server. To be investigated...
		CommandResults results = startStubServer();

		int retry = 1;
		int maxRetries = 5;
		long retryDelay = 60;
		while (results.getExitCode() == START_STUB_FAILURE_EXIT_CODE && results.getOutput().contains(START_STUB_KNOWN_EXCEPTION) && retry <= maxRetries) {
			retry++;
			log.warn("Retry #{} of {} max attempts to start stub server will be performed after {} seconds", retry, maxRetries, retryDelay);
			sleep(retryDelay);
			results = startStubServer();
		}

		if (retry == maxRetries && results.getOutput().contains(START_STUB_KNOWN_EXCEPTION)) {
			throw new IstfException(String.format("Stub server restart has been failed after %s retries", retry));
		}
	}

	public static synchronized CommandResults stopStubServer() {
		assertTimeIsNotShifted();
		String stopCommand = getExecuteScriptCommand(APP_MOCKS_SCRIPT_STOP);
		log.info("Stopping stub server...");
		CommandResults results = getRemoteHelper().executeCommand(stopCommand, ExecutionParams.with().timeout(Duration.ofMinutes(5)).failOnTimeout().failOnError());
		log.info("Stub server has been stopped");
		return results;
	}

	public static synchronized CommandResults startStubServer() {
		assertTimeIsNotShifted();
		String startCommand = getExecuteScriptCommand(APP_MOCKS_SCRIPT_START);
		log.info("Starting stub server... it may take up to 10 minutes");
		ExecutionParams params = ExecutionParams.with().timeout(Duration.ofMinutes(12)).failOnTimeout().failOnErrorIgnoring(START_STUB_FAILURE_EXIT_CODE);
		CommandResults results = getRemoteHelper().executeCommand(startCommand, params);
		if (results.getExitCode() == START_STUB_FAILURE_EXIT_CODE && results.getOutput().contains(START_STUB_KNOWN_EXCEPTION)) {
			log.warn("Failed to start stub server because of known error");
		} else {
			log.info("Stub server has been started");
		}
		return results;
	}

	public static synchronized OS getCurrentOS() {
		if (currentOS == null) {
			String osType = getRemoteHelper().executeCommand("uname -s").getOutput();
			if (osType.contains("Unable to execute command or shell on remote system") || osType.contains("CYGWIN") || osType.contains("MINGW32") || osType.contains("MSYS")) {
				currentOS = OS.WINDOWS;
			} else if (osType.contains("Linux")) {
				currentOS = OS.LINUX;
			} else if (osType.contains("Darwin")) {
				currentOS = OS.MAC_OS;
			} else {
				currentOS = OS.UNKNOWN;
			}
		}
		return currentOS;
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
				log.error("Unable to delete temp mock file: {}", mockTempDestinationPath);
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

	private static RemoteHelper getRemoteHelper() {
		if (APP_ADMIN_USER.isEmpty() && (APP_ADMIN_PASSWORD.isEmpty() || APP_AUTH_KEYPATH.isEmpty())) {
			return RemoteHelper.get();
		}
		return RemoteHelper.with().user(APP_ADMIN_USER, APP_ADMIN_PASSWORD).privateKey(APP_AUTH_KEYPATH).get();
	}

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
					mock = ReflectionHelper.getInstance(mockModelClass);
				} catch (RuntimeException e) {
					throw new IstfException("Unable to get filename for mock of class: " + mockModelClass.getName());
				}
				return mock.getFileName();
		}
	}

	private static void assertTimeIsNotShifted() {
		LocalDate serverDate = TimeSetterUtil.istfDateToJava(getTimeSetterClient().getStartTime()).toLocalDate();
		assertThat(serverDate).as("Stub server start/stop/restart is not allowed on instance with shifted time.\nCurrent date is %1$s, Current date on server is: %2$s", LocalDate.now(), serverDate)
				.isEqualTo(LocalDate.now());
	}

	private static String getExecuteScriptCommand(String scriptFileName) {
		switch (getCurrentOS()) {
			case WINDOWS:
				return String.format("cmd /c cd %1$s && cmd /c %2$s", APP_MOCKS_SCRIPT_WORKDIR, scriptFileName);
			case LINUX:
			case MAC_OS:
				return String.format("cd %1$s && ./%2$s", APP_MOCKS_SCRIPT_WORKDIR, scriptFileName);
			case UNKNOWN:
			default:
				throw new IstfException("Unknown OS detected, unable to start/stop stub server");
		}
	}

	private static void sleep(long seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static synchronized TimeSetterClient getTimeSetterClient() {
		return TimeSetterClientHolder.timeSetterClient;
	}

	private static class TimeSetterClientHolder {
		private static final TimeSetterClient timeSetterClient = new TimeSetterClient();
	}

	private enum OS {
		WINDOWS,
		LINUX,
		MAC_OS,
		UNKNOWN
	}
}
