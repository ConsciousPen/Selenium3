package aaa.modules.regression.sales.template.functional;

import aaa.main.enums.ClaimConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestSameDayLossWaiverOKTemplate extends PolicyBaseTest {

    protected void testFirstLossWaivedClueNB() {
        createSpecificCustomerIndividual("TestOK", "FirstLoss");
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class, true);

        PropertyQuoteTab.RatingDetailsView.open();
        TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
        assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1)
                .getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
        assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2)
                .getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo("0");
        assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3)
                .getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo("4");
        PropertyQuoteTab.RatingDetailsView.close();

    }

    protected void testSameDayWaiverClueNB() {
        createSpecificCustomerIndividual("TestOK", "SameDay");
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class, true);

        PropertyQuoteTab.RatingDetailsView.open();
        TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
        assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1)
                .getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
        assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2)
                .getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo("0");
        assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3)
                .getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo("6");
        PropertyQuoteTab.RatingDetailsView.close();

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
