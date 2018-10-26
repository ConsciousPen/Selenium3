package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.*;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.PolicyBaseTest;
import org.apache.commons.lang.StringUtils;
import toolkit.datax.TestData;

import java.util.HashMap;
import java.util.Map;

import static toolkit.verification.CustomAssertions.assertThat;

public abstract class TestAutoClueResponseTemplate extends PolicyBaseTest {

	protected abstract Tab getDriverTab();
	protected abstract Tab getFirstReportsTab();
	protected abstract Tab getPremiumAndCoveragesTab();
	protected abstract Tab getDriverActivityReportsTab();
	protected abstract Tab getDocumentsAndBindTab();

	protected void testReturnedClaimsForNB(Map<String, String> drivers){

		HashMap.Entry<String, String> driversList = drivers.entrySet().iterator().next();

		// Create Customer TD with First Name in the Map used for the customer
		TestData customerTD = getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), driversList.getKey())
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), driversList.getValue());

		// Remove First Name from the Map for adding additional drivers
		drivers.remove(driversList.getKey());

		mainApp().open();
		createCustomerIndividual(customerTD);
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), getDriverTab().getClass(), true);

		// Taking License numbers for drivers
		String licenseNumber = getPolicyTD().getTestData(AutoSSMetaData.DriverTab.class.getSimpleName()).getValue(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel());
		String licenseNumberCa = getPolicyTD().getTestData(AutoCaMetaData.DriverTab.class.getSimpleName()).getValue(AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel());

		for (HashMap.Entry<String, String> entry : drivers.entrySet()) {
			// Checks if Policy is for CA
			if (isStateCA()){

				// increment license number for next driver
				String licenseStart = StringUtils.substring(licenseNumberCa, 0, 3);
				String licenseAdd = StringUtils.substring(licenseNumberCa, 3, licenseNumberCa.length());
				int licenseIncrement = Integer.valueOf(licenseAdd);
				++licenseIncrement;
				licenseNumberCa = licenseStart + licenseIncrement;

				// Create TestData for Driver
				TestData tdDrivers = testDataManager.getDefault(TestAutoClueResponseTemplate.class).getTestData("TestData_CA");

				tdDrivers.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.FIRST_NAME.getLabel()), entry.getKey())
						.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.LAST_NAME.getLabel()), entry.getValue())
						.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), licenseNumberCa);
				// Add Driver
				getDriverTab().fillTab(tdDrivers);
			} else {

				// increment license number for next driver
				String licenseStart = StringUtils.substring(licenseNumber, 0, 3);
				String licenseAdd = StringUtils.substring(licenseNumber, 3, licenseNumber.length());
				int licenseIncrement = Integer.valueOf(licenseAdd);
				++licenseIncrement;
				licenseNumber = licenseStart + licenseIncrement;

				// Create TestData for Driver
				TestData tdDrivers = testDataManager.getDefault(TestAutoClueResponseTemplate.class).getTestData("TestData_SS");

				tdDrivers.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.FIRST_NAME.getLabel()), entry.getKey())
						.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LAST_NAME.getLabel()), entry.getValue())
						.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), licenseNumber)
						.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), getState());
				// Add Driver
				getDriverTab().fillTab(tdDrivers);
			}
		}

		//submit tab and fill policy until DriverActivityReports
		getDriverTab().submitTab();
		policy.getDefaultView().fillFromTo(getPolicyTD(), getFirstReportsTab().getClass(), getDriverActivityReportsTab().getClass(), true);
	}

	protected void pas_20371_ClueActivityMappingToDriver(){

		if (isStateCA()) {
			// Navigate to Driver Tab
			NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
			// Check That driver1 has 3 Claims returned
			checkTblActivityRowCount(3);
			// Switch to driver 2
			new aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab().tableDriverList.selectRow(1);
			// Check That driver2 has 3 Claims returned
			checkTblActivityRowCount(3);
		} else {
			// Navigate to Driver Tab
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
			// Check That driver1 has 3 Claims returned
			checkTblActivityRowCount(3);
			// Switch to driver 2
			new DriverTab().tableDriverList.selectRow(1);
			// Check That driver2 has 3 Claims returned
			checkTblActivityRowCount(3);
		}
	}

	private void checkTblActivityRowCount(Integer rowCount){
		if (isStateCA()){
			assertThat(new aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab().tableActivityInformationList.getRowsCount()).isEqualTo(rowCount);
		} else {
			assertThat(new DriverTab().tableActivityInformationList.getRowsCount()).isEqualTo(rowCount);
		}
	}

}