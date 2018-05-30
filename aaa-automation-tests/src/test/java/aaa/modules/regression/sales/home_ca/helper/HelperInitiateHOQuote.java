package aaa.modules.regression.sales.home_ca.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class HelperInitiateHOQuote extends PolicyBaseTest {

    private EndorsementTab endorsementTab = new EndorsementTab();


    public void pas13261_testInitiateHOQuoteHO29added(PolicyType policyType) {

        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        mainApp().open();
        createCustomerIndividual();

        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(testData, EndorsementTab.class);
        assertThat(endorsementTab.tblIncludedEndorsements.getColumn("Form ID").getValue()).contains("HO-29");
        policyType.get().getDefaultView().fillFromTo(testData, EndorsementTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        policyType.get().initiateHOQuote().start();
        policyType.get().getDefaultView().fillUpTo(testData, EndorsementTab.class);

        assertThat(endorsementTab.tblIncludedEndorsements.getColumn("Form ID").getValue()).contains("HO-29");
    }

}