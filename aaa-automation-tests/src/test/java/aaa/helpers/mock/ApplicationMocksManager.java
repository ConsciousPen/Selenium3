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
	private static final String DESTINATION_TEMP_MOCKS_FOLDER = "src/test/resources/mock";
	private static final String SOURCE_MOCKS_FOLDER_PATTERN = String.format(
			"/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/installedApps/%sCell01/aaa-external-stub-services-app-ear.ear/aaa-external-stub-services-app.war/WEB-INF/classes/META-INF/mock", ENV_NAME);
	
	private static Map<MockType, UpdatableMock> appMocks;
	
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
			for (Map.Entry<MockType, UpdatableMock> mock : updatedMocks.entrySet()) {
				ExcelMarshaller excelMarshaller = new ExcelMarshaller();
				excelMarshaller.marshal(mock.getValue(), mock.getKey().getMockModel(), new File("/temp/path", mock.getKey().getFileName()));
			}
			
			//TODO-dchubkov: upload files and restart server
		}
	}
	
	private static <M> M getMockDataObject(String fileName, Class<M> mockDataClass) {
		String mockSourcePath = SOURCE_MOCKS_FOLDER_PATTERN + "/" + fileName;
		String mockTempDestinationPath = DESTINATION_TEMP_MOCKS_FOLDER + "/" + RandomStringUtils.randomNumeric(10) + "_" + fileName;
		
		RemoteHelper.downloadFile(mockSourcePath, mockTempDestinationPath);
		File mockTempFile = new File(mockTempDestinationPath);
		M mockObject;
		try (ExcelUnmarshaller excelUnmarshaller = new ExcelUnmarshaller(mockTempFile)) {
			mockObject = excelUnmarshaller.unmarshal(mockDataClass);
		} finally {
			if (!mockTempFile.delete()) {
				log.error("Unambe to delete temp mock file: %s", mockTempDestinationPath);
			}
		}
		return mockObject;
	}
}
