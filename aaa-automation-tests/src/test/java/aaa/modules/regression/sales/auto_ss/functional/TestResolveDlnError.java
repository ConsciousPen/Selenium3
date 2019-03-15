package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Lina Li
 * @name Test Mask Driver License Number for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy
 * 3. Login with E34 role, when enter wrong DLN, fire the error and can resolve the error
 * 4. Login with F35 role, when enter wrong DLN, fire the error and can resolve the error
 * 5. Bind the policy
 * @details
 */


public class TestResolveDlnError extends AutoSSBaseTest{

	private DriverTab driverTab = new DriverTab();
	private DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private String customerNum;



	@Parameters({"state"})
	@StateList(statesExcept = { Constants.States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS,testCaseId = "PASBB-947")

	// Login with E34 user, error message fired  (PASBB-947)
	public void testResolveDlnWithPrivilege (@Optional("") String state) {

		mainApp().open(getLoginTD(Constants.UserGroups.E34));
		customerNum = createCustomerIndividual();
		getPolicyType().get().initiate();
		TestData tdPolicy = getPolicyTD().adjust(getTestSpecificTD("TestData_ErrorLicense").resolveLinks());
		policy.dataGather().getView().fillUpTo(tdPolicy,driverTab.getClass(),true);

		//Verify the error message fired
		assertThat(AutoSSMetaData.DriverTab.LICENSE_NUMBER).withFailMessage("License number is inconsistent with state format");
		policy.dataGather().getView().fillFromTo(getPolicyTD(), driverTab.getClass(), driverActivityReportsTab.getClass(), true);

		//Verify the driver license number doesn't mask
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue()).doesNotContain("*");
		assertThat(DriverActivityReportsTab.tableInternalClaim.getRow(1).getCell("License #").getValue()).doesNotContain("*");
		driverActivityReportsTab.submitTab();
		policy.dataGather().getView().fillFromTo(getPolicyTD(), documentsAndBindTab.getClass(), purchaseTab.getClass(), true);
		purchaseTab.submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo("Policy Active");
		mainApp().close();
	}


	@Parameters({"state"})
	@StateList(statesExcept = { Constants.States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS,testCaseId = "PASBB-947"+"PASBB-931")

	public void testResolveDlnWithoutPrivilege (@Optional("") String state) {

		mainApp().open(getLoginTD(Constants.UserGroups.F35));
		SearchPage.openCustomer(customerNum);
		getPolicyType().get().initiate();
		TestData tdPolicy = getPolicyTD().adjust(getTestSpecificTD("TestData_ErrorLicense").resolveLinks());
		policy.dataGather().getView().fillUpTo(tdPolicy,driverTab.getClass(),true);
		//Verify the error message fired
		assertThat(AutoSSMetaData.DriverTab.LICENSE_NUMBER).withFailMessage("License number is inconsistent with state format");
		policy.dataGather().getView().fillFromTo(getPolicyTD(), driverTab.getClass(), driverActivityReportsTab.getClass(),true);
		//Verify the driver license number was masked
		String reg = "^[*]+[A-Za-z0-9]{4}$";
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue().matches(reg));
		driverActivityReportsTab.submitTab();
		//PASBB-931 bind the quote successful when the driver license number masked
		policy.dataGather().getView().fillFromTo(getPolicyTD(), documentsAndBindTab.getClass(), purchaseTab.getClass(), true);
		purchaseTab.submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo("Policy Active");
	}
}


