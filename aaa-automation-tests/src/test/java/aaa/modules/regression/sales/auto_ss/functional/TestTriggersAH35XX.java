/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.BillingConstants.BillingAccountPoliciesTable.POLICY_NUM;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.ComboBox;

public class TestTriggersAH35XX extends AutoSSBaseTest {
	private static final String PAYMENT_CENTRAL_CONFIG_CHECK = "select value from PROPERTYCONFIGURERENTITY\n" + "where propertyname in('aaaBillingAccountUpdateActionBean.ccStorateEndpointURL','aaaPurchaseScreenActionBean.ccStorateEndpointURL','aaaBillingActionBean.ccStorateEndpointURL')\n";
	private VehicleTab vehicleTab = new VehicleTab();

	/**
	 * @author Oleg Stasyuk
	 * @name Test backdated policy
	 * @scenario 1. Create new Policy with Non-Annual Payment Plan
	 * 2. Add ACH, CC_Visa, CC_Master
	 * 3. Set Autopay to EFT, run DocGenJob, check AH35XX generated and contains EFT data
	 * 4. Set Autopay to CC_Visa, run DocGenJob, check AH35XX generated and contains EFT data
	 * 5. Set Autopay to CC_Master, run DocGenJob, check AH35XX generated and contains EFT data
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = Constants.States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-2241", "PAS-250"})
	public void pas2241_TriggersUiAH35XX(@Optional("") String state) {
		paymentCentralConfigCheck();
		String paymentPlan = "contains=Eleven";
		String premiumCoverageTabMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
		TestData policyTdAdjusted = getPolicyTD().adjust(premiumCoverageTabMetaKey, paymentPlan);

		mainApp().open();
		createCustomerIndividual();

		getPolicyType().get().createPolicy(policyTdAdjusted);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		String vehicle1 = PolicySummaryPage.getVehicleInfo(1);

		//PAS-250 preconditions start
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.linkPaymentPlan.click();
		PremiumAndCoveragesTab.linkViewApplicableFeeSchedule.click();
		//Direct Fee is not applicable for AH35XX
		//String directFee = PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE).getValue();
		String eftFee = PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE).getValue();
		String ccFee = PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE).getValue();
		String dcFee = PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE).getValue();
		Page.dialogConfirmation.buttonCloseWithCross.click();
		Tab.buttonTopCancel.click();
		//PAS-250 preconditions end

		CustomSoftAssertions.assertSoftly(softly -> {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			BillingAccount billingAccount = new BillingAccount();
			billingAccount.update().perform(getTestSpecificTD("TestData_UpdateBilling"));
			//ACH
			String numberACH = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(1).getValue("Account #");
			documentPaymentMethodCheckInDb(policyNumber, numberACH, 1, softly);
			pas2777_documentContainsVehicleInfoCheckInDb(softly, policyNumber, "AUTO_PAY_METNOD_CHANGED", eftFee, 1, vehicle1);
			//Visa
			autopaySelection("contains=Visa");
			String visaNumber = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(0).getValue("Number");
			documentPaymentMethodCheckInDb(policyNumber, visaNumber, 2, softly);
			pas2777_documentContainsVehicleInfoCheckInDb(softly, policyNumber, "AUTO_PAY_METNOD_CHANGED", ccFee, 2, vehicle1);
			//Master Card
			autopaySelection("contains=Master");
			String numberMaster = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(2).getValue("Number");
			documentPaymentMethodCheckInDb(policyNumber, numberMaster, 3, softly);
			pas2777_documentContainsVehicleInfoCheckInDb(softly, policyNumber, "AUTO_PAY_METNOD_CHANGED", dcFee, 3, vehicle1);

			BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_NUM).controls.links.get(policyNumber).click();
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.buttonAddVehicle.click();
			vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE).setValue(getTestSpecificTD("VehicleTab").getValue("Usage"));
			vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue(getTestSpecificTD("VehicleTab").getValue("VIN"));

			//PAS-2777 start
			new PremiumAndCoveragesTab().calculatePremium();
			vehicleTab.saveAndExit();

			TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
			testEValueDiscount.simplifiedPendedEndorsementIssue();
			String vehicle2 = PolicySummaryPage.getVehicleInfo(2);

			pas2777_documentContainsVehicleInfoCheckInDb(softly, policyNumber, "ENDORSEMENT_ISSUE", ccFee, 1, vehicle1, vehicle2);

			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			autopaySelection("contains=Visa");
			documentPaymentMethodCheckInDb(policyNumber, visaNumber, 4, softly);
			pas2777_documentContainsVehicleInfoCheckInDb(softly, policyNumber, "AUTO_PAY_METNOD_CHANGED", ccFee, 4, vehicle1, vehicle2);
			//PAS-2777 end
		});
	}

	private void paymentCentralConfigCheck() {
		String appHost = PropertyProvider.getProperty(TestProperties.APP_HOST);
		assertThat(DBService.get().getValue(PAYMENT_CENTRAL_CONFIG_CHECK).orElse("")).as("Adding Payment methods will not be possible because PaymentCentralEndpoints are looking at real service. Please run paymentCentralConfigUpdate").contains(appHost);
	}

	private void documentPaymentMethodCheckInDb(String policyNum, String numberCCACH, int numberOfDocuments, ETCSCoreSoftAssertions softly) {
		String visaNumberScreened = numberCCACH.substring(numberCCACH.length() - 4, numberCCACH.length());
		String query = GET_DOCUMENT_BY_EVENT_NAME + " and data like '%%" + visaNumberScreened + "%%'";
		String queryFull = String.format(query, policyNum, "AH35XX", "AUTO_PAY_METNOD_CHANGED");
		softly.assertThat(DbAwaitHelper.waitForQueryResult(queryFull, 5)).isTrue();
		softly.assertThat(DocGenHelper.getDocumentDataElemByName("AcctNum", DocGenEnum.Documents.AH35XX, queryFull).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).contains(visaNumberScreened);

		String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, "AH35XX", "AUTO_PAY_METNOD_CHANGED");
		softly.assertThat(DBService.get().getValue(query2).map(Integer::parseInt)).hasValue(numberOfDocuments);
	}

	private void pas2777_documentContainsVehicleInfoCheckInDb(ETCSCoreSoftAssertions softly, String policyNum, String eventName, String feeAmount, int numberOfDocuments, String... vehicleInfos) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AH35XX", eventName);

		softly.assertThat(DocGenHelper.getDocumentDataSectionsByName("VehicleDetails", DocGenEnum.Documents.AH35XX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).isEqualTo(vehicleInfos[0]);
		softly.assertThat(DocGenHelper.getDocumentDataElemByName("PlcyVehInfo", DocGenEnum.Documents.AH35XX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).isEqualTo(vehicleInfos[0]);
		//PAS-250 start
		softly.assertThat("$" + DocGenHelper.getDocumentDataElemByName("InstlFee", DocGenEnum.Documents.AH35XX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).isEqualTo(feeAmount);
		//PAS-250 end
		for (int index = 0; index < vehicleInfos.length; index++) {
			softly.assertThat(DocGenHelper.getDocumentDataElemByName("PlcyVehInfo", DocGenEnum.Documents.AH35XX, query).get(0).getDocumentDataElements().
					get(index).getDataElementChoice().getTextField()).isEqualTo(vehicleInfos[index++]);
			++index;
		}

		String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, "AH35XX", eventName);
		softly.assertThat(DBService.get().getValue(query2).map(Integer::parseInt)).hasValue(numberOfDocuments);
	}

	private void autopaySelection(String autopaySelectionValue) {
		UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
		BillingSummaryPage.linkUpdateBillingAccount.click();
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION.getLabel(), ComboBox.class).setValue(autopaySelectionValue);
		UpdateBillingAccountActionTab.buttonSave.click();
	}

}
