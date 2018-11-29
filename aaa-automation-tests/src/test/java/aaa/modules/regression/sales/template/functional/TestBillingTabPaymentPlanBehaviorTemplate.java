package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestBillingTabPaymentPlanBehaviorTemplate extends PolicyBaseTest {


    protected void pas21539_BillingTabPaymentPlanSS(){

        String policyNumber = openAppAndCreatePolicy();
        billingTabPaymentPlan(false);

        SearchPage.openPolicy(policyNumber);
        //initiate endorsement and change Pay Plan and validate on Billing Tab
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValueContains("Eleven Pay - Standard");
        new PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

        billingTabPaymentPlan(false);
    }

    protected void pas21539_BillingTabPaymentPlanCA(){

        String policyNumber = openAppAndCreatePolicy();
        billingTabPaymentPlan(true);

        SearchPage.openPolicy(policyNumber);
        policy.renew().perform(getPolicyTD("Endorsement", "TestData"));

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        new aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().submitTab();

        billingTabPaymentPlan(true);
        assertThat(BillingSummaryPage.tableBillingAccountPolicies
                .getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN).controls.links.getFirst()).isEnabled();

    }

    protected void billingTabPaymentPlan(boolean isEnabled){
        //Navigate to billing tab
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        if (isEnabled){
            assertThat(BillingSummaryPage.tableBillingAccountPolicies
                    .getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN).controls.links.getFirst()).isPresent();
        }
        else {
            assertThat(BillingSummaryPage.tableBillingAccountPolicies
                    .getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN).controls.links.getFirst()).isPresent(false);
        }

    }
}
