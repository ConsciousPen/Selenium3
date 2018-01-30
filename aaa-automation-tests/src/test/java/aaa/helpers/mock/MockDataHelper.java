package aaa.helpers.mock;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import aaa.helpers.mock.model.membership.MembershipMockData;
import aaa.helpers.ssh.RemoteHelper;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public class MockDataHelper {
	public static final String MEMBERSHIP_SUMMARY_MOCK_DATA_FILENAME = "RetrieveMembershipSummaryMockData.xls";

	private static final String ENV_NAME = PropertyProvider.getProperty(TestProperties.APP_HOST).split("\\.")[0];
	private static final String DESTINATION_TEMP_MOCKS_FOLDER = "src/test/resources/mock";
	private static final String SOURCE_MOCKS_FOLDER_PATTERN = String.format("/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/installedApps/%sCell01/aaa-external-stub-services-app-ear.ear/"
			+ "aaa-external-stub-services-app.war/WEB-INF/classes/META-INF/mock", ENV_NAME);

	public static MembershipMockData getMembershipData() {
		return MocksHolder.membershipMockData;
	}

	private static class MocksHolder {
		private static MembershipMockData membershipMockData = getMockDataObject(MEMBERSHIP_SUMMARY_MOCK_DATA_FILENAME, MembershipMockData.class);

		private static <M> M getMockDataObject(String mockFilename, Class<M> mockDataClass) {
			String mockSourcePath = SOURCE_MOCKS_FOLDER_PATTERN + File.separator + mockFilename;
			String mockTempDestinationPath = DESTINATION_TEMP_MOCKS_FOLDER + File.separator + System.currentTimeMillis() + "_" + mockFilename;

			RemoteHelper.downloadFile(mockSourcePath, mockTempDestinationPath);
			File mockTempFile = new File(mockTempDestinationPath);
			ExcelUnmarshaller excelUnmarshaller = new ExcelUnmarshaller();
			M mockObject;
			try {
				mockObject = excelUnmarshaller.unmarshal(mockTempFile, mockDataClass);
			} finally {
				assertThat(mockTempFile.delete()).as("Unambe to delete temp mock file: %s", mockTempDestinationPath).isTrue();
			}
			return mockObject;
		}
	}
}
