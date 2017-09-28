package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;

public class InstallmentFeeTest extends AutoSSBaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void installmentfee() {

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

        autopaySelection("contains=Visa");

        JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
        //DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH35XX);

        autopaySelection("contains=Master");
        //DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH35XX);
    }

    private void autopaySelection(String autopaySelectionValue) {
        UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
        BillingSummaryPage.linkUpdateBillingAccount.click();
        updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION.getLabel(), ComboBox.class).setValue(autopaySelectionValue);
        UpdateBillingAccountActionTab.buttonSave.click();
    }
}
