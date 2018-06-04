package aaa.modules.regression.sales.template.functional;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestInvoiceWithNoDownPaymentTemplate extends PolicyBaseTest {

    protected void pas9001_testInvoiceWithNoDownPaymentNB() {

        Purchase purchaseTab;
        if (getPolicyType().isCaProduct()) {
            purchaseTab = new PurchaseTab();
        } else {
            purchaseTab = new aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab();
        }

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create policy with zero deposit on Purchase tab
        getPolicyType().get().initiate();
        getPolicyType().get().getDefaultView().fillUpTo(getPolicyDefaultTD(), purchaseTab.getClass());
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.CHANGE_MINIMUM_DOWNPAYMENT).setValue(true);
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.MINIMUM_REQUIRED_DOWNPAYMENT).setValue("0.00");
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.REASON_FOR_CHANGING).setValue("index=1");
        purchaseTab.submitTab();

        // Capture policy number from summary page
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        //JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        //JobUtils.executeJob(Jobs.offCycleBillingInvoiceAsyncJob);

    }

    protected void pas9001_testInvoiceWithNoDownPaymentEndorsement() {

        Tab premiumAndCoveragesQuoteTab;
        Tab bindTab;
        if (getPolicyType().isCaProduct()) {
            premiumAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
            bindTab = new BindTab();
        } else {
            premiumAndCoveragesQuoteTab = new aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab();
            bindTab = new aaa.main.modules.policy.home_ss.defaulttabs.BindTab();
        }

        // Create customer and policy
        mainApp().open();
        createCustomerIndividual();
        getPolicyType().get().createPolicy(getPolicyDefaultTD());
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();

        // Create a premium-bearing endorsement (increase) at effective date plus 30 days
        TimeSetterUtil.getInstance().confirmDateIsAfter(effectiveDate.plusDays(30));
        reopenPolicy(policyNumber);
        getPolicyType().get().endorse().perform(getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Endorsement"), "TestData"));
        navigateToPremiumAndCoveragesQuoteTab();
        premiumAndCoveragesQuoteTab.getAssetList().getAsset(getDeductible()).setValueByIndex(0);
        premiumAndCoveragesQuoteTab.getAssetList().getAsset(getCalculatePremiumButton()).click();
        navigateToBindTab();
        bindTab.submitTab();

        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        JobUtils.executeJob(Jobs.offCycleBillingInvoiceAsyncJob);

        reopenPolicy(policyNumber);
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        BillingSummaryPage.getInstallmentDueDate(1);

    }

    private void navigateToPremiumAndCoveragesQuoteTab() {
        if (getPolicyType().isCaProduct()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        } else {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        }
    }

    private void navigateToBindTab() {
        if (getPolicyType().isCaProduct()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        } else {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        }
    }

    private AssetDescriptor<ComboBox> getDeductible() {
        if (getPolicyType().isCaProduct()) {
            return HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE;
        }
        return HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE;
    }

    private AssetDescriptor<JavaScriptButton> getCalculatePremiumButton() {
        if (getPolicyType().isCaProduct()) {
            return HomeCaMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM_BUTTON;
        }
        return HomeSSMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM;
    }

    private void reopenPolicy(String policyNumber) {
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
    }

}
