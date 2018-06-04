package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class InitiateHOQuoteTemplate extends PolicyBaseTest {

    private EndorsementTab endorsementTab = new EndorsementTab();


    public void pas13261_testInitiateHOQuoteHO29added(PolicyType policyType) {

        // Create Test data for appropriate policy type
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App. Create Customer. get Policy Type and initiate policy. Fill policy to Endorsement Tab.
        mainApp().open();
        createCustomerIndividual();
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(testData, EndorsementTab.class);

        // Assert that HO-29 is added by default. Purchase Policy.
        assertThat(endorsementTab.tblIncludedEndorsements.getColumn("Form ID").getValue()).contains("HO-29");
        policyType.get().getDefaultView().fillFromTo(testData, EndorsementTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        // Initiate HO Quote from Policy Summary Page. Fill Policy up to Endorsement Tab.
        policyType.get().initiateHOQuote().start();
        policyType.get().getDefaultView().fillUpTo(testData, EndorsementTab.class);

        // Assert that HO-29 is added by default.
        assertThat(endorsementTab.tblIncludedEndorsements.getColumn("Form ID").getValue()).contains("HO-29");
    }

}