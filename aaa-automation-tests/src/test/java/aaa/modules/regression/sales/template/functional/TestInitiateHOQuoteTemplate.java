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

    protected void pas21410_testInitiateHOQuoteFromAutoAndHO29IsAddedHO3() {

        policy.initiateHOQuote().start();
        PolicyType.HOME_CA_HO3.get().getDefaultView().fillUpTo(getHO3TD(), EndorsementTab.class, false);

        // Assert that HO-29 is added by default.
        checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
    }

    protected void pas21410_testInitiateHOQuoteFromAutoAndHO29IsAddedHO4() {

        TestData tdHO4 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO4), "DataGather");
        policy.initiateHOQuote().start();
        new GeneralTab().fillTab(tdHO4).submitTab();
        new ApplicantTab().fillTab(getHO3TD()).submitTab();
        PolicyType.HOME_CA_HO4.get().getDefaultView().fillFromTo(tdHO4, ReportsTab.class, PropertyInfoTab.class, true);
        new PropertyInfoTab().submitTab();

        // Assert that HO-29 is added by default.
        checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
    }

    protected void pas21410_testInitiateHOQuoteFromAutoAndHO29IsAddedHO6() {

        TestData tdHO6 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO6), "DataGather");
        policy.initiateHOQuote().start();
        new GeneralTab().fillTab(tdHO6).submitTab();
        PolicyType.HOME_CA_HO6.get().getDefaultView().fillFromTo(getHO3TD(), ApplicantTab.class, PropertyInfoTab.class, false);
        new PropertyInfoTab().fillTab(tdHO6).submitTab();

        // Assert that HO-29 is added by default.
        checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
    }

    protected void pas21410_testInitiateHOQuoteFromAutoAndHO29IsAddedDP3() {

        TestData tdDP3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_DP3), "DataGather");
        policy.initiateHOQuote().start();
        new GeneralTab().fillTab(tdDP3).submitTab();
        PolicyType.HOME_CA_DP3.get().getDefaultView().fillFromTo(getHO3TD(), ApplicantTab.class, PropertyInfoTab.class, false);
        new PropertyInfoTab().fillTab(tdDP3).submitTab();

        // Assert that HO-29 is not added by default.
        checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
    }

    private TestData getHO3TD(){
        return getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO3), "DataGather");
    }
}