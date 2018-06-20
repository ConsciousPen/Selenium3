package aaa.helpers.mock;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.ssh.RemoteHelper;
import aaa.utils.excel.bind.ExcelMarshaller;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public class ApplicationMocksManager {
	protected static final Logger log = LoggerFactory.getLogger(ApplicationMocksManager.class);

	private static final String ENV_NAME = PropertyProvider.getProperty(TestProperties.APP_HOST).split("\\.")[0];
	private static final String TEMP_MOCKS_FOLDER = "src/test/resources/mock";
	//TODO-dchubkov: move to property
	private static final String APP_MOCKS_FOLDER = String.format(
			"/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/installedApps/%sCell01/aaa-external-stub-services-app-ear.ear/aaa-external-stub-services-app.war/WEB-INF/classes/META-INF/mock", ENV_NAME);

	private static Map<MockType, UpdatableMock> appMocks = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static synchronized <M extends UpdatableMock> M getMock(MockType mockType) {
		if (!appMocks.containsKey(mockType)) {
			M mock = getMockDataObject(mockType.getFileName(), mockType.getMockModel());
			appMocks.put(mockType, mock);
		}
		return (M) appMocks.get(mockType);
	}

	public static synchronized void updateMocks(Map<MockType, UpdatableMock> requiredMocks) {
		Map<MockType, UpdatableMock> updatedMocks = new HashMap<>();

		for (Map.Entry<MockType, UpdatableMock> requiredMock : requiredMocks.entrySet()) {
			UpdatableMock appMock = getMock(requiredMock.getKey());
			if (appMock.update(requiredMock.getValue())) {
				updatedMocks.put(requiredMock.getKey(), appMock);
			}
		}

		if (!updatedMocks.isEmpty()) {
			RemoteHelper.clearFolder(TEMP_MOCKS_FOLDER);
			
			ExcelMarshaller excelMarshaller = new ExcelMarshaller();
			for (Map.Entry<MockType, UpdatableMock> mock : updatedMocks.entrySet()) {
				File updatedMock = new File(TEMP_MOCKS_FOLDER, mock.getKey().getFileName());
				excelMarshaller.marshal(mock.getValue(), updatedMock);
				RemoteHelper.uploadFile(updatedMock.getAbsolutePath(), APP_MOCKS_FOLDER);
				if (!updatedMock.delete()) {
					log.error("Unable to delete mock file: %s", updatedMock);
				}
			}
			restartStubServer();
		}
	}
	
	public static void restartStubServer() {
		//TODO-dchubkov: restart on Windows
		String eisUser = "UNKNOWN";
		String eisPasword = "UNKNOWN";
		String command = "/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/wsadmin.sh -lang jacl -user admin -password admin -c \"\\$AdminControl %1$s cluster_external_stub_server %2$sNode01\"";
		
		//TimeSetterUtil.getInstance().adjustTime(); // set date to today
		
		//TODO-dchubkov: add executeCommandAsUser by timeout
		RemoteHelper.executeCommandAsUser(String.format(command, "stopServer", ENV_NAME), eisUser, eisPasword);
		RemoteHelper.executeCommandAsUser(String.format(command, "startServer", ENV_NAME), eisUser, eisPasword);
	}
	
	private static <M> M getMockDataObject(String fileName, Class<M> mockDataClass) {
		String mockSourcePath = APP_MOCKS_FOLDER + "/" + fileName;
		String mockTempDestinationPath = TEMP_MOCKS_FOLDER + "/" + RandomStringUtils.randomNumeric(10) + "_" + fileName;

		RemoteHelper.downloadFile(mockSourcePath, mockTempDestinationPath);
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
