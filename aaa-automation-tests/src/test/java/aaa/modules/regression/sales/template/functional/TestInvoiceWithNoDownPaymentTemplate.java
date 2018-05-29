package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestInvoiceWithNoDownPaymentTemplate extends PolicyBaseTest {

    protected void pas9001_testInvoiceWithNoDownPaymentNB(PolicyType policyType) {

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
        policyType.get().getDefaultView().fillUpTo(getPolicyDefaultTD(policyType), purchaseTab.getClass());
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

        Tab premiumAndCoveragesQuoteTab;
        Tab bindTab;
        if (policyType.isCaProduct()) {
            premiumAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
            bindTab = new BindTab();
        } else {
            premiumAndCoveragesQuoteTab = new aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab();
            bindTab = new aaa.main.modules.policy.home_ss.defaulttabs.BindTab();
        }

        // Create customer and policy
        mainApp().open();
        createCustomerIndividual();
        policyType.get().createPolicy(getPolicyDefaultTD(policyType));
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        // Create a premium-bearing endorsement (increase)
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData"));
        navigateToPremiumAndCoveragesQuoteTab(policyType);
        premiumAndCoveragesQuoteTab.getAssetList().getAsset(getDeductible(policyType)).setValueByIndex(0);
        premiumAndCoveragesQuoteTab.getAssetList().getAsset(getCalculatePremiumButton(policyType)).click();
        navigateToBindTab(policyType);
        bindTab.submitTab();

    }

    private void navigateToPremiumAndCoveragesQuoteTab(PolicyType policyType) {
        if (policyType.isCaProduct()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        } else {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        }
    }

    private void navigateToBindTab(PolicyType policyType) {
        if (policyType.isCaProduct()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        } else {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        }
    }

    private AssetDescriptor<ComboBox> getDeductible(PolicyType policyType) {
        if (policyType.isCaProduct()) {
            return HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE;
        }
        return HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE;
    }

    private AssetDescriptor<JavaScriptButton> getCalculatePremiumButton(PolicyType policyType) {
        if (policyType.isCaProduct()) {
            return HomeCaMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM_BUTTON;
        }
        return HomeSSMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM;
    }

}
