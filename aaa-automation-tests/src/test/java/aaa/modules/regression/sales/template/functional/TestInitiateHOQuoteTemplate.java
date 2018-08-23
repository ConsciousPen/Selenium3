package aaa.modules.regression.sales.template.functional;

import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;

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

        // Assert that HO-29 is added by default. Purchase Policy.
        checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
    }
}