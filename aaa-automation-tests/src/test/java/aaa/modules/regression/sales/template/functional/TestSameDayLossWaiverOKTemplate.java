package aaa.modules.regression.sales.template.functional;

import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestSameDayLossWaiverOKTemplate extends PolicyBaseTest {

    private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    protected void testFirstLossWaivedClueNB() {
        createSpecificCustomerIndividual("TestOK", "FirstLoss");
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PropertyInfoTab.class, true);

        propertyInfoTab.tblClaimsList.getRow(PolicyConstants.PropertyInfoClaimHistoryTable.AMOUNT_OF_LOSS, "12000")
                .getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();

        assertThat(propertyInfoTab.tblClaimsList.getRow(PolicyConstants.PropertyInfoClaimHistoryTable.AMOUNT_OF_LOSS, "12000")
                .getCell(PolicyConstants.PropertyInfoClaimHistoryTable.INCLUDED_IN_RATING_AND_ELIGIBILITY).getValue()).isEqualTo("Waived");

        premiumsAndCoveragesQuoteTab.calculatePremium();
        PropertyQuoteTab.RatingDetailsView.open();
        TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();


    }

    protected void testFirstLossWaivedClueEndorsement() {

    }

    protected void testSameDayWaiverClueNB() {

    }

    protected void testSameDayWaiverClueEndorsement() {

    }

    private void createSpecificCustomerIndividual(String fName, String lName) {
        mainApp().open();

        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            fName += "DP";
        }
        createCustomerIndividual(getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), fName)
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), lName));
    }

}
