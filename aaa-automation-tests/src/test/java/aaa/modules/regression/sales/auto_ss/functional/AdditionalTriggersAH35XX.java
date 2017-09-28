/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.ComboBox;

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
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
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

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.update().perform(getTestSpecificTD("TestData_UpdateBilling"));
        //TODO SSH for win is not supported, additional checks required
        //JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
        //DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH35XX);

        autopaySelection("contains=Visa");
        //TODO SSH for win is not supported, additional checks required
        //JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
        //DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH35XX);

        autopaySelection("contains=Master");
        //TODO SSH for win is not supported, additional checks required
        //JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
        //DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH35XX);
    }

    private void autopaySelection(String autopaySelectionValue) {
        UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
        BillingSummaryPage.linkUpdateBillingAccount.click();
        updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION.getLabel(), ComboBox.class).setValue(autopaySelectionValue);
        UpdateBillingAccountActionTab.buttonSave.click();
    }
}
