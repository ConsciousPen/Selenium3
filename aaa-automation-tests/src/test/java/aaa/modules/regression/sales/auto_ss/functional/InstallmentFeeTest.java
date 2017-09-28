package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class InstallmentFeeTest extends AutoSSBaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void installmentfee() {

        mainApp().open();

        createCustomerIndividual();

        log.info("Policy Creation Started...");

        String paymentPlan = "Eleven Pay - Standard";


        TestData premiumCoveragesAdjusted = getTestSpecificTD("TestData").getTestData("PremiumAndCoveragesTab").adjust("Payment Plan", paymentPlan);

        TestData bigPolicy_td = getTestSpecificTD("TestData").adjust("PremiumAndCoveragesTab", premiumCoveragesAdjusted);
        getPolicyType().get().createPolicy(bigPolicy_td);

/*
        TestData policyInformationSectionAdjustment = getTestSpecificTD("TestData_UT").getTestData("GeneralTab").getTestData("PolicyInformation");
        TestData adjustedTestData = getPolicyTD().adjust(getTestSpecificTD("GeneralTab").getTestData("PolicyInformation"), policyInformationSectionAdjustment);
*/
/*
        TestData policyInformationSection = getTestSpecificTD("TestData_UT").getTestData("GeneralTab").getTestData("PolicyInformation");
        TestData generalTab = getPolicyTD().getTestData("GeneralTab").getTestData("PolicyInformation").adjust(policyInformationSection);
        TestData policyTd = getPolicyTD().getTestData("GeneralTab").adjust(generalTab);

        policy.createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_UT").getTestData("GeneralTab").getTestData("PolicyInformation")));
*/
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

/*
        TestData tdEndorsement = getTestSpecificTD("TestData");
        policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
*/

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        BillingSummaryPage.linkUpdateBillingAccount.click();



        BillingAccount billingAccount = new BillingAccount();
        billingAccount.update().perform(getTestSpecificTD("TestData_UpdateBilling"));

        UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();



        //testdata/default/billing/PaymentMethods.yaml
    }
}
