/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;

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
public class AdditionalTriggersAH35XX extends AutoSSBaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void pas2241_AdditionalTriggersAH35XX(@Optional("") String state) {

        mainApp().open();
        createCustomerIndividual();

        String paymentPlan = "Eleven Pay - Standard";
        TestData premiumCoveragesAdjusted = getTestSpecificTD("TestData").getTestData("PremiumAndCoveragesTab").adjust("Payment Plan", paymentPlan);
        TestData bigPolicy_td = getTestSpecificTD("TestData").adjust("PremiumAndCoveragesTab", premiumCoveragesAdjusted);
        getPolicyType().get().createPolicy(bigPolicy_td);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String policyNum = PolicySummaryPage.getPolicyNumber();

        CustomAssert.enableSoftMode();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.update().perform(getTestSpecificTD("TestData_UpdateBilling"));
        String numberACH = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(1).getValue("Account #"); //ACH
        documentCheckInDb(policyNum, numberACH);

        autopaySelection("contains=Visa");
        String visaNumber = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(0).getValue("Number");  //Visa
        documentCheckInDb(policyNum, visaNumber);

        autopaySelection("contains=Master");
        String numberMaster = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(2).getValue("Number"); //Master
        documentCheckInDb(policyNum, numberMaster);
        CustomAssert.disableSoftMode();
    }

    private void documentCheckInDb(String policyNum, String numberCCACH) {
        String VisaNumberScreened = "***"+numberCCACH.substring(numberCCACH.length()-4, numberCCACH.length());
        CustomAssert.assertTrue(DBService.get().getValue(String.format(GET_DOCUMENT_BY_EVENT_NAME + " and data like '%%"+VisaNumberScreened+"%%'", policyNum, "AH35XX", "AUTO_PAY_METNOD_CHANGED")).isPresent());
    }

    private void autopaySelection(String autopaySelectionValue) {
        UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
        BillingSummaryPage.linkUpdateBillingAccount.click();
        updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION.getLabel(), ComboBox.class).setValue(autopaySelectionValue);
        UpdateBillingAccountActionTab.buttonSave.click();
    }


}
