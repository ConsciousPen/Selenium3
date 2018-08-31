package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestCarryOverValuesTemplate extends PolicyBaseTest {

    // Home SS Tabs
    private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private BindTab bindTab = new BindTab();

    // Home Ca Tabs
    private aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab propertyInfoTabCa = new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab();
    private aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabCa = new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab();
    private aaa.main.modules.policy.home_ca.defaulttabs.BindTab bindTabCa = new aaa.main.modules.policy.home_ca.defaulttabs.BindTab();


    protected void pas15831_TestReplacementCostReasonEndorsementOnRenewal() {

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get TD Home.
        TestData tdHome = getPolicyTD("DataGather", "TestData");

        // Create Property Policy
        createPolicy(tdHome);
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        // Change system date Initiate Renewal
        TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getExpirationDate());
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().perform();

        // Navigate To property Info Tab and Select Replacement cost reason to Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        propertyInfoTab.getPropertyValueAssetList().
                getAsset(HomeSSMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), ComboBox.class).setValueContains("Renewal");

        // Purchase Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.submitTab();
        purchaseRenewal(policyNumber);

        // Navigate to Renewal And Endorse it
        PolicySummaryPage.buttonRenewals.click();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

        // Navigate to Property Info tab and Make sure Replacement Cost Reason is set to Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        assertThat(propertyInfoTab.getPropertyValueAssetList().
                getAsset(HomeSSMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), ComboBox.class))
                .hasValue("Renewal");
    }

    protected void pas15831_TestReplacementCostReasonEndorsementOnRenewalCA() {

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get TD Home.
        TestData tdHome = getPolicyTD("DataGather", "TestData");

        // Create Property Policy
        createPolicy(tdHome);
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        // Change system date Initiate Renewal
        TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getExpirationDate());
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().perform();

        // Navigate To property Info Tab and Select Replacement cost reason to Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        propertyInfoTabCa.getPropertyValueAssetList().
                getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), ComboBox.class).setValueContains("Renewal");

        // Purchase Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTabCa.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        bindTabCa.submitTab();
        purchaseRenewal(policyNumber);

        // Navigate to Renewal And Endorse it
        PolicySummaryPage.buttonRenewals.click();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

        // Navigate to Property Info tab and Make sure Replacement Cost Reason is set to Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        assertThat(propertyInfoTabCa.getPropertyValueAssetList().
                getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), ComboBox.class))
                .hasValue("Renewal");
    }


    private void purchaseRenewal(String policyNumber){
        // Open Billing account and Pay min due for the renewal
        SearchPage.openBilling(policyNumber);
        Dollar minDue = new Dollar(BillingSummaryPage.getTotalDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

        // Open Policy
        SearchPage.openPolicy(policyNumber);
    }

}
