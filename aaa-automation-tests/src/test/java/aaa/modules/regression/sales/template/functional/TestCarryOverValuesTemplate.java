package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestDisableReorderReport;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;

import java.time.LocalDateTime;

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

        // Initiate Renewal
        LocalDateTime renewalTime = PolicySummaryPage.getExpirationDate();
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(renewalTime);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().perform();

        // Navigate To property Info Tab and Select Replacement cost reason to Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        propertyInfoTab.getPropertyValueAssetList().
                getAsset(HomeSSMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), ComboBox.class).setValueContains("Renewal");

        // Purchase Renewal
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.submitTab();
        mainApp().close();
        purchaseRenewal(renewalTime, policyNumber);

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
        LocalDateTime renewalTime = PolicySummaryPage.getExpirationDate();
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(renewalTime);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().perform();

        // Navigate To property Info Tab and Select Replacement cost reason to Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        propertyInfoTabCa.getPropertyValueAssetList().
                getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), ComboBox.class).setValueContains("Renewal");

        // Purchase Renewal
        premiumsAndCoveragesQuoteTabCa.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        bindTabCa.submitTab();
        mainApp().close();
        purchaseRenewal(renewalTime, policyNumber);

        // Navigate to Renewal And Endorse it
        PolicySummaryPage.buttonRenewals.click();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

        // Navigate to Property Info tab and Make sure Replacement Cost Reason is set to Renewal
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        assertThat(propertyInfoTabCa.getPropertyValueAssetList().
                getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), ComboBox.class))
                .hasValue("Renewal");
    }

    protected void pas17736_TestRenewalStatusChangeCA() {

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get TD Home.
        TestData tdHome = getPolicyTD("DataGather", "TestData");

        // Create Property Policy
        createPolicy(tdHome);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime renewalDate = PolicySummaryPage.getExpirationDate();
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(renewalDate.minusDays(2));

        // Create Proposed renewal
        searchForPolicy(policyNumber);
        policy.renew().perform();
        premiumsAndCoveragesQuoteTabCa.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        bindTabCa.submitTab();

        // Customer Decline the Renewal and Check that Status is displayed in BA
        PolicySummaryPage.buttonRenewals.click();
        policy.declineByCustomerQuote().perform(getPolicyTD("DeclineByCustomer", "TestData"));
        SearchPage.openBilling(policyNumber);
        assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS))
                .hasValue(BillingConstants.BillingAccountPoliciesPolicyStatus.CUSTOMER_DECLINED);
        // Open Policy and endorse it
        BillingSummaryPage.openPolicy(1);

        // Create Endorsement
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());

        // get Dollar Value of Cov A add 1000$ and setvalue
        Dollar covAValue = new Dollar(propertyInfoTabCa.getPropertyValueAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT).getValue());
        propertyInfoTabCa.getPropertyValueAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT).setValue(covAValue.add(-4000).toString());

        // Calculate Premium and Bind Endorsement
        premiumsAndCoveragesQuoteTabCa.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        bindTabCa.submitTab();

        // Open BA and and check that the status of renewal was not updated
        SearchPage.openBilling(policyNumber);
        assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS))
                .hasValue(BillingConstants.BillingAccountPoliciesPolicyStatus.CUSTOMER_DECLINED);
    }
    }