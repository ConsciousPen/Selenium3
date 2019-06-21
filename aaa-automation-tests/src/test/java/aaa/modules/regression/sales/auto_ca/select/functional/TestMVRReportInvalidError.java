package aaa.modules.regression.sales.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.actiontabs.UpdateRulesOverrideActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.regression.sales.auto_ca.select.TestPolicyCreationBig;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
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

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-30347")
	public void pas30347_testMVRReportInvalidErrorOverrideRenewal(@Optional("CA") String state) {

		// Cancelled License
		testInvalidLicenseError("Cool4", "Boy4", "A3222296");
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime expDate = PolicySummaryPage.getExpirationDate();

		// Create renewal image at R-63 with renewal jobs
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(expDate.minusDays(63));

		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(expDate.minusDays(35));

		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.updateRulesOverride().start();
		checkOverridenRule();
	}



	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-30347")
	public void pas30347_testMVRReportInvalidErrorOverrideEndorsement(@Optional("CA") String state) {

		TestData driverTab = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
				.mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel())
				.adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Cool3")
				.adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Boy3")
				.adjust(AutoCaMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), "06/10/1990")
				.adjust(AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel(), "A3222297")
				.adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");

		TestData tdEndorsementFill = DataProviderFactory.dataOf(AutoCaMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData(),
				DriverTab.class.getSimpleName(), driverTab);

		// Cancelled License
		testInvalidLicenseError("Cool4", "Boy4", "A3222296");
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
		policy.getDefaultView().fill(tdEndorsementFill);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
		new DriverActivityReportsTab().getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
		new DriverActivityReportsTab().getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();
		DocumentsAndBindTab.confirmEndorsementPurchase.buttonYes.click();

		// Updating the message of the error to have exact driver name and lname
		ErrorEnum.Errors.ERROR_AAA_CSA190521.setMessage("The MVR license status returned for " + "Cool3" + " " + "Boy3" + " is unacceptable (AAA_CSA190521) [for AAADARTabBindRules.attributeForRules]");

		new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_CSA190521);
		new ErrorTab().overrideAllErrors(ErrorEnum.Duration.TERM, ErrorEnum.ReasonForOverride.OTHER);
		new ErrorTab().override();
		documentTab.submitTab();
		PolicySummaryPage.getPolicyNumber();

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

			// Updating the message of the error to have exact driver name and lname
			ErrorEnum.Errors.ERROR_AAA_CSA190521.setMessage("The MVR license status returned for " + name + " " + lname + " is unacceptable (AAA_CSA190521) [for AAADARTabBindRules.attributeForRules]");
            ErrorEnum.Errors.ERROR_AAA_AUTO_CA_MEM_LASTNAME.setMessage("Membership Validation Failed. Please review the Membership Report and confirm Member details. (AAA_AUTO_CA_MEM_LASTNAME) [for ExistingPolicies.attributeForRules]");
			new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_CSA190521);
            new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_AUTO_CA_MEM_LASTNAME);
			new ErrorTab().overrideAllErrors(ErrorEnum.Duration.TERM, ErrorEnum.ReasonForOverride.OTHER);
			new ErrorTab().override();
			documentTab.submitTab();
			assertThat(new PurchaseTab().isVisible()).isTrue();
		}

		private void checkOverridenRule(){
			Map<String, String> query = new HashMap<>();
			query.put(AutoCaMetaData.UpdateRulesOverrideActionTab.RuleRow.STATUS.getLabel(), "overridden");
			query.put(AutoCaMetaData.UpdateRulesOverrideActionTab.RuleRow.RULE_NAME.getLabel(), "AAA_CSA190521");

			assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRow(query)).isPresent();
			assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRow(query).getCell(AutoCaMetaData.UpdateRulesOverrideActionTab.RuleRow.DURATION.getLabel()).controls.radioGroups.getFirst()).hasValue(ErrorEnum.Duration.TERM.get());
			UpdateRulesOverrideActionTab.btnCancel.click();
		}
	}
