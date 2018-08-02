package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;

public class TestCarryOverValuesTemplate extends PolicyBaseTest {

    private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();


    public void pas15831_TestReplacementCostReasonEndorsementOnRenwal(PolicyType policyType) {

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get TD Home.
        TestData tdHome = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Create HO Policy
        policyType.get().createPolicy(tdHome);
        String policyNum = PolicySummaryPage.getPolicyNumber();

        // Change system date Initiate Renewal
        TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getExpirationDate());
        mainApp().open();
        SearchPage.openPolicy(policyNum);
        policyType.get().renew().perform();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        propertyInfoTab.getPropertyValueAssetList().
                getAsset(HomeSSMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), ComboBox.class).setValueContains("Renewal");





    }




}
