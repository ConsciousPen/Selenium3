package aaa.modules.regression.sales.template.functional;

import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestInvoiceWithNoDownPaymentTemplate extends PolicyBaseTest {

    protected void pas9001_testInvoiceWithNoDownPaymentNB(PolicyType policyType) {

        TestData td = getPolicyDefaultTD(policyType);
        Purchase purchaseTab;
        if (policyType.isCaProduct()) {
            purchaseTab = new PurchaseTab();
        } else {
            purchaseTab = new aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab();
        }

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create policy with zero deposit on Purchase tab
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(td, purchaseTab.getClass());
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.CHANGE_MINIMUM_DOWNPAYMENT).setValue(true);
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.MINIMUM_REQUIRED_DOWNPAYMENT).setValue("0.00");
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.REASON_FOR_CHANGING).setValue("index=1");
        purchaseTab.submitTab();

        // Capture policy number from summary page
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        //JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        //JobUtils.executeJob(Jobs.offCycleBillingInvoiceAsyncJob);

    }

    protected void pas9001_testInvoiceWithNoDownPaymentEndorsement(PolicyType policyType) {

        // TODO implement this
    }

}
