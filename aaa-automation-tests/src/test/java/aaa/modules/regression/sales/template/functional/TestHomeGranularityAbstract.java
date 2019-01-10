package aaa.modules.regression.sales.template.functional;

import aaa.common.pages.QuoteDataGatherPage;
import aaa.helpers.db.queries.HomeGranularityQueries;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestHomeGranularityAbstract extends PolicyBaseTest {

    private QuoteDataGatherPage quoteDataGatherPage = new QuoteDataGatherPage();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    protected void pas23203_validateCensusBlockGroupAndLatLong(String address, String censusBlockGroup) {
        TestData policyTd = getPolicyTD();
        policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
                HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
                HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), address);
        createQuoteAndFillUpTo(policyTd, PremiumsAndCoveragesQuoteTab.class);
        String quoteNumber = quoteDataGatherPage.getQuoteNumber();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        String censusBlockGroupID = validateCensusBlockGroupAndLatLong(quoteNumber, censusBlockGroup);
        checkVRD(censusBlockGroupID);
    }

    protected String validateCensusBlockGroupAndLatLong(String policyNumber, String censusBlockGroup) {
        Map<String,String> censusBlockGroupAndLatLongFromDb = DBService.get().getRow(String.format(HomeGranularityQueries.SELECT_CENSUS_BLOCK_GROUP,policyNumber));
        String censusBlockGroupDbValue = censusBlockGroupAndLatLongFromDb.get("CENSUSBLOCK");
        assertThat(censusBlockGroupDbValue).as("Wrong Census Block Group, Expecting %1$s but found %2$s", censusBlockGroup, censusBlockGroupDbValue).isEqualTo(censusBlockGroup);
        return censusBlockGroupDbValue;
    }

    /**
     Method validates VRD: Census Block ID
     */
    protected void checkVRD(String censusBlockGroupID){

        premiumsAndCoveragesQuoteTab.linkViewRatingDetails.click();

        assertSoftly(softly -> {
            //softly.assertThat(premiumsAndCoveragesQuoteTab.tableRatingDetailsVehicles.getRow(1,"BI Symbol").getCell(2).getValue()).isEqualTo(censusBlockGroupID);
            softly.assertThat(premiumsAndCoveragesQuoteTab.tableViewRatingDetails.getRow(3, "Census Block").getCell(4).getValue()).isEqualTo(censusBlockGroupID);
        });
        //premiumsAndCoveragesQuoteTab.buttonRatingDetailsOk.click();

    }
}
