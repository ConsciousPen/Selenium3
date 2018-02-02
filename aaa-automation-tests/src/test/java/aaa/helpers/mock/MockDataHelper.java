package aaa.helpers.mock;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import aaa.helpers.mock.model.membership.MembershipMockData;
import aaa.helpers.mock.model.vehicle.VehicleUBIDetailsMockData;
import aaa.helpers.ssh.RemoteHelper;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public class MockDataHelper {
	public static final String MEMBERSHIP_SUMMARY_MOCK_DATA_FILENAME = "RetrieveMembershipSummaryMockData.xls";
	public static final String VEHICLE_UBI_DETAIL_SMOCK_DATA_FILENAME = "VehicleUBIDetailsMockData.xls";

	private static final String ENV_NAME = PropertyProvider.getProperty(TestProperties.APP_HOST).split("\\.")[0];
	private static final String DESTINATION_TEMP_MOCKS_FOLDER = "src/test/resources/mock";
	private static final String SOURCE_MOCKS_FOLDER_PATTERN = String.format("/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/installedApps/%sCell01/aaa-external-stub-services-app-ear.ear/"
			+ "aaa-external-stub-services-app.war/WEB-INF/classes/META-INF/mock", ENV_NAME);

	private static MembershipMockData membershipData;
	private static VehicleUBIDetailsMockData vehicleUBIDetailsData;

	public static synchronized MembershipMockData getMembershipData() {
		if (membershipData == null) {
			membershipData = getMockDataObject(MEMBERSHIP_SUMMARY_MOCK_DATA_FILENAME, MembershipMockData.class);
		}
		return membershipData;
	}

	public static synchronized VehicleUBIDetailsMockData getVehicleUBIDetailsData() {
		if (vehicleUBIDetailsData == null) {
			vehicleUBIDetailsData = getMockDataObject(VEHICLE_UBI_DETAIL_SMOCK_DATA_FILENAME, VehicleUBIDetailsMockData.class);
		}
		return vehicleUBIDetailsData;
	}

	private static <M> M getMockDataObject(String mockFilename, Class<M> mockDataClass) {
		String mockSourcePath = SOURCE_MOCKS_FOLDER_PATTERN + "/" + mockFilename;
		String mockTempDestinationPath = DESTINATION_TEMP_MOCKS_FOLDER + "/" + System.currentTimeMillis() + "_" + mockFilename;

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
