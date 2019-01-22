package aaa.modules.regression.sales.template.functional;

import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import toolkit.datax.TestData;

public class TestInitiateHOQuoteTemplate extends TestEndorsementsTabAbstract {

    protected void pas13261_testInitiateHOQuoteHO29added() {

        createQuoteAndFillUpTo(EndorsementTab.class);

        // Assert that HO-29 is added by default. Purchase Policy.
        checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());

        policy.getDefaultView().fillFromTo(getPolicyTD(), EndorsementTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        // Initiate HO Quote from Policy Summary Page. Fill Policy up to Endorsement Tab.
        policy.initiateHOQuote().start();
        policy.getDefaultView().fillUpTo(getPolicyTD(), EndorsementTab.class);

        // Assert that HO-29 is added by default.
        checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
    }

    protected void pas21410_testInitiateHOQuoteFromAutoAndHO29IsAdded(PolicyType policyType) {

        TestData td = getStateTestData(testDataManager.policy.get(policyType), "DataGather", "TestData_CA");
        policy.initiateHOQuote().start();
        policyType.get().getDefaultView().fillUpTo(td, EndorsementTab.class, false);

        if(policyType.equals(PolicyType.HOME_CA_DP3)){
            // Assert that HO-29 is not added by default. For DP3
            checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
        }else {
            // Assert that HO-29 is added by default. For HO3 HO4 HO6
            checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
        }
    }
}