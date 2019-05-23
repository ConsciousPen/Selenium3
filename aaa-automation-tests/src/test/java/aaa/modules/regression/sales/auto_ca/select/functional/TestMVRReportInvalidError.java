package aaa.modules.regression.sales.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test MVR Report Invalid License Error
 * @scenario
 * 1. Create Customer 
 * 2. Create CA Select Auto Quote
 * 3. Add a driver with an invalid license status
 * 4. Try to bind policy
 * 5. Verify there is an Invalid License Status Error
 * 6. Repeat steps with all invalid statuses
 * @details
 */
public class TestMVRReportInvalidError extends AutoCaSelectBaseTest {

	private Tab documentTab = new DocumentsAndBindTab();

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29837")
	public void pas29837_testMVRReportInvalidErrorForExpiredLicense(@Optional("CA") String state) {

		// Expired License
		testInvalidLicenseError("Cool1", "Boy1", "A3222299");

		}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29837")
	public void pas29837_testMVRReportInvalidErrorForSuspendedLicense(@Optional("CA") String state) {

		// Suspended License
		testInvalidLicenseError("Cool2", "Boy2", "A3222298");

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29837")
	public void pas29837_testMVRReportInvalidErrorForRevokedLicense(@Optional("CA") String state) {

		// Revoked License
		testInvalidLicenseError("Cool3", "Boy3", "A3222297");

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29837")
	public void pas29837_testMVRReportInvalidErrorForCancelledLicense(@Optional("CA") String state) {

		// Cancelled License
		testInvalidLicenseError("Cool4", "Boy4", "A3222296");

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29837")
	public void pas29837_testMVRReportInvalidErrorForDeniedLicense(@Optional("CA") String state) {

		// Denied License
		testInvalidLicenseError("Cool5", "Boy5", "A3222295");

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29837")
	public void pas29837_testMVRReportInvalidErrorForIDOnlyLicense(@Optional("CA") String state) {

		// ID Only License
		testInvalidLicenseError("Cool6", "Boy6", "A3222294");

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29837")
	public void pas29837_testMVRReportInvalidErrorForDeceasedLicense(@Optional("CA") String state) {

		// Deceased License
		testInvalidLicenseError("Cool7", "Boy7", "A3222293");

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29837")
	public void pas29837_testMVRReportInvalidErrorForNoValidLicenseLicense(@Optional("CA") String state) {

		// No Valid License License
		testInvalidLicenseError("Cool8", "Boy8", "A3222292");

	}

		private void testInvalidLicenseError(String name, String lname, String license){

			TestData tdCustomer = getCustomerIndividualTD("DataGather", "TestData")
					.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()),name)
					.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()),lname);
			TestData tdPolicy = getPolicyTD()
					.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), license);

			mainApp().open();
			createCustomerIndividual(tdCustomer);
			policy.initiate();
			policy.getDefaultView().fillUpTo(tdPolicy, DriverActivityReportsTab.class,true);
			NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
			documentTab.fillTab(tdPolicy);
			DocumentsAndBindTab.btnPurchase.click();

			new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_CSA190521);
			new ErrorTab().overrideErrors(ErrorEnum.Errors.ERROR_AAA_CSA190521);
			new ErrorTab().override();
			documentTab.submitTab();
			assertThat(new PurchaseTab().isVisible()).isTrue();
		}
	}
