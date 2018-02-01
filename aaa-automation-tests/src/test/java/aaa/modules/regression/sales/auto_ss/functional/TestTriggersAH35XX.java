/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.BillingConstants.BillingAccountPoliciesTable.POLICY_NUM;
import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
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
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

public class TestTriggersAH35XX extends AutoSSBaseTest {
    private VehicleTab vehicleTab = new VehicleTab();

    private static final String PAYMENT_CENTRAL_CONFIG_CHECK = "select value from PROPERTYCONFIGURERENTITY\n" +
            "where propertyname in('aaaBillingAccountUpdateActionBean.ccStorateEndpointURL','aaaPurchaseScreenActionBean.ccStorateEndpointURL','aaaBillingActionBean.ccStorateEndpointURL')\n";

    @Test(description = "Preconditions")
    private void paymentCentralConfigCheck() {
        String appHost = PropertyProvider.getProperty("app.host");
        CustomAssert.assertTrue("Adding Payment methods will not be possible because PaymentCentralEndpoints are looking at real service. Please run paymentCentralConfigUpdate", DBService.get()
                .getValue(PAYMENT_CENTRAL_CONFIG_CHECK).get().contains(appHost));
    }

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
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, dependsOnMethods = "paymentCentralConfigCheck")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2241")
    public void pas2241_TriggersUiAH35XX(@Optional("") String state) {

        String paymentPlan = "contains=Eleven";
        String premiumCoverageTabMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
        TestData policyTdAdjusted = getPolicyTD().adjust(premiumCoverageTabMetaKey, paymentPlan);

        mainApp().open();
        createCustomerIndividual();

        getPolicyType().get().createPolicy(policyTdAdjusted);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        String vehicle1 = getVehicleInfo(1);

        CustomAssert.enableSoftMode();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.update().perform(getTestSpecificTD("TestData_UpdateBilling"));
        //ACH
        String numberACH = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(1).getValue("Account #");
        documentPaymentMethodCheckInDb(policyNumber, numberACH, 1);
        pas2777_documentContainsVehicleInfoCheckInDb(policyNumber, "AUTO_PAY_METNOD_CHANGED", 1, vehicle1);
        //Visa
        autopaySelection("contains=Visa");
        String visaNumber = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(0).getValue("Number");
        documentPaymentMethodCheckInDb(policyNumber, visaNumber, 2);
        pas2777_documentContainsVehicleInfoCheckInDb(policyNumber, "AUTO_PAY_METNOD_CHANGED", 2, vehicle1);
        //Master Card
        autopaySelection("contains=Master");
        String numberMaster = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(2).getValue("Number");
        documentPaymentMethodCheckInDb(policyNumber, numberMaster, 3);
        pas2777_documentContainsVehicleInfoCheckInDb(policyNumber, "AUTO_PAY_METNOD_CHANGED", 3, vehicle1);

        BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_NUM).controls.links.get(policyNumber).click();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        VehicleTab.buttonAddVehicle.click();
        vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE).setValue(getTestSpecificTD("VehicleTab").getValue("Usage"));
        vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue(getTestSpecificTD("VehicleTab").getValue("VIN"));

        //PAS-2777 start
        PremiumAndCoveragesTab.calculatePremium();
        vehicleTab.saveAndExit();

        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
        testEValueDiscount.simplifiedPendedEndorsementIssue();
        String vehicle2 = getVehicleInfo(2);

        pas2777_documentContainsVehicleInfoCheckInDb(policyNumber, "ENDORSEMENT_ISSUE", 1, vehicle1, vehicle2);

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        autopaySelection("contains=Visa");
        documentPaymentMethodCheckInDb(policyNumber, visaNumber, 4);
        pas2777_documentContainsVehicleInfoCheckInDb(policyNumber, "AUTO_PAY_METNOD_CHANGED", 4, vehicle1, vehicle2);
        //PAS-2777 end

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private String getVehicleInfo(int rowNum) {
        String yearVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(YEAR).getValue();
        String makeVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(MAKE).getValue();
        String modelVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(MODEL).getValue();
        return yearVeh + " " + makeVeh + " " + modelVeh;
    }

    private void documentPaymentMethodCheckInDb(String policyNum, String numberCCACH, int numberOfDocuments) {
        String visaNumberScreened = "***" + numberCCACH.substring(numberCCACH.length() - 4, numberCCACH.length());
        String query = GET_DOCUMENT_BY_EVENT_NAME + " and data like '%%" + visaNumberScreened + "%%'";
        String queryFull = String.format(query, policyNum, "AH35XX", "AUTO_PAY_METNOD_CHANGED");
        CustomAssert.assertTrue(DbAwaitHelper.waitForQueryResult(queryFull, 5));
        CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("AcctNum", DocGenEnum.Documents.AH35XX, queryFull).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()
                .contains(visaNumberScreened));

        String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, "AH35XX", "AUTO_PAY_METNOD_CHANGED");
        CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(query2).get()), numberOfDocuments);
    }

    private void pas2777_documentContainsVehicleInfoCheckInDb(String policyNum, String eventName, int numberOfDocuments, String... vehicleInfos) {
        String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AH35XX", eventName);

        CustomAssert.assertEquals(DocGenHelper.getDocumentDataSectionsByName("VehicleDetails", DocGenEnum.Documents.AH35XX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
                .getTextField(), vehicleInfos[0]);
        CustomAssert.assertEquals(DocGenHelper.getDocumentDataElemByName("PlcyVehInfo", DocGenEnum.Documents.AH35XX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
                .getTextField(), vehicleInfos[0]);

        for (int index = 0; index < vehicleInfos.length; index++) {
            CustomAssert.assertEquals(DocGenHelper.getDocumentDataElemByName("PlcyVehInfo", DocGenEnum.Documents.AH35XX, query).get(0).getDocumentDataElements().
                            get(index).getDataElementChoice().getTextField(),
                    vehicleInfos[index++]);
            ++index;
        }

        String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, "AH35XX", eventName);
        CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(query2).get()), numberOfDocuments);
    }

    private void autopaySelection(String autopaySelectionValue) {
        UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
        BillingSummaryPage.linkUpdateBillingAccount.click();
        updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION.getLabel(), ComboBox.class).setValue(autopaySelectionValue);
        UpdateBillingAccountActionTab.buttonSave.click();
    }

}
