package aaa.modules.regression.sales.template.functional;

import aaa.common.pages.QuoteDataGatherPage;
import aaa.helpers.db.queries.HomeGranularityQueries;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;

import java.util.Map;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestHomeGranularityAbstract extends PolicyBaseTest {

    private QuoteDataGatherPage quoteDataGatherPage = new QuoteDataGatherPage();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    protected void pas23203_validateCensusBlockGroupAndLatLong(String latitude, String longitude, String censusBlockGroup) {
        TestData policyTd = getPolicyTD();
        createQuoteAndFillUpTo(policyTd, PremiumsAndCoveragesQuoteTab.class);
        String quoteNumber = quoteDataGatherPage.getQuoteNumber();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        String censusBlockGroupID = validateCensusBlockGroupAndLatLong(quoteNumber, censusBlockGroup, latitude, longitude);
        checkVRD(censusBlockGroupID);
    }

    protected String validateCensusBlockGroupAndLatLong(String policyNumber, String censusBlockGroup, String latitude, String longitude) {
        Map<String,String> censusBlockGroupAndLatLongFromDb = DBService.get().getRow(String.format(HomeGranularityQueries.SELECT_CENSUS_BLOCK_GROUP,policyNumber));
        String censusBlockGroupDbValue = censusBlockGroupAndLatLongFromDb.get("CENSUSBLOCK");
        String latitudeDbValue = censusBlockGroupAndLatLongFromDb.get("LATITUDE");
        String longitudeDbValue = censusBlockGroupAndLatLongFromDb.get("LONGITUDE");

        assertSoftly(softly -> {
            softly.assertThat(censusBlockGroupDbValue).as("Wrong Census Block Group, Expecting %1$s but found %2$s", censusBlockGroup, censusBlockGroupDbValue).isEqualTo(censusBlockGroup);
            softly.assertThat(latitudeDbValue).as("Wrong Latitude value, Expecting %1$s but found %2$s", latitude, latitudeDbValue).isEqualTo(latitude);
            softly.assertThat(longitudeDbValue).as("Wrong Longitude value, Expecting %1$s but found %2$s", longitude, longitudeDbValue).isEqualTo(longitude);
        });
        return censusBlockGroupDbValue;
    }

    /**
     Method validates VRD: Census Block ID
     */
    protected void checkVRD(String censusBlockGroupID){

        premiumsAndCoveragesQuoteTab.linkViewRatingDetails.click();

        assertSoftly(softly -> {
            softly.assertThat(premiumsAndCoveragesQuoteTab.tableViewRatingDetails.getRow(3, "Census Block").getCell(4).getValue()).isEqualTo(censusBlockGroupID);
        });
    }
}
