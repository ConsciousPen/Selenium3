package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.common.pages.QuoteDataGatherPage;
import aaa.helpers.constants.HomeGranularityConstants;
import aaa.helpers.db.queries.HomeGranularityQueries;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;

import java.util.Map;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestHomeGranularityAbstract extends PolicyBaseTest {

    private QuoteDataGatherPage quoteDataGatherPage = new QuoteDataGatherPage();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    //Keypath for the Dwelling Address section on the Applicant tab
    String keypathDwellingAddress = TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel());
    String keypathZipCode = TestData.makeKeyPath(keypathDwellingAddress, HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel());
    String keypathAddress1 = TestData.makeKeyPath(keypathDwellingAddress, HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel());

    protected void validateCensusBlockGroupAndLatLong() {
        TestData policyTd = getPolicyTD();
        String mockCensusBlock = HomeGranularityConstants.MOCK_CENSUS_BLOCK;
        String mockLatitude    = HomeGranularityConstants.MOCK_LATITUDE;
        String mockLongitude   = HomeGranularityConstants.MOCK_LONGITUDE;
        createQuoteAndFillUpTo(policyTd, PremiumsAndCoveragesQuoteTab.class);
        String quoteNumber = quoteDataGatherPage.getQuoteNumber();
        String censusBlockGroupID = validateCensusBlockGroupAndLatLong(quoteNumber, mockCensusBlock, mockLatitude, mockLongitude);
        checkVRD(censusBlockGroupID);
    }

    protected void validateCensusBlockGroupAndLatLongFromEADS() {
        TestData policyTd = getPolicyTD()
                .adjust(keypathZipCode, "90201")
                .adjust(keypathAddress1, "265 CHIPMAN ST");
        String defaultCensusBlock = HomeGranularityConstants.DEFAULT_CENSUS_BLOCK;
        String defaultLatitude    = HomeGranularityConstants.DEFAULT_LATITUDE;
        String defaultLongitude   = HomeGranularityConstants.DEFAULT_LONGITUDE;
        createQuoteAndFillUpTo(policyTd, ApplicantTab.class);
        Tab.buttonTopSave.click();
        String quoteNumber = quoteDataGatherPage.getQuoteNumber();
        validateCensusBlockGroupAndLatLong(quoteNumber, null, null, null);
        Tab.buttonNext.click();
        policy.getDefaultView().fillFromTo(policyTd, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        validateCensusBlockGroupAndLatLong(quoteNumber, defaultCensusBlock, defaultLatitude, defaultLongitude);
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
