package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.Map;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.QuoteDataGatherPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.HomeGranularityConstants;
import aaa.helpers.db.queries.HomeGranularityQueries;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;

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
        String quoteNumber = QuoteDataGatherPage.getQuoteNumber();
        String censusBlockGroupID = validateCensusBlockGroupAndLatLong(quoteNumber, mockCensusBlock, mockLatitude, mockLongitude, HomeGranularityQueries.SELECT_CENSUS_BLOCK_GROUP);
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
        String quoteNumber = QuoteDataGatherPage.getQuoteNumber();
        validateCensusBlockGroupAndLatLong(quoteNumber, null, null, null, HomeGranularityQueries.SELECT_CENSUS_BLOCK_GROUP);
        Tab.buttonNext.click();
        policy.getDefaultView().fillFromTo(policyTd, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        validateCensusBlockGroupAndLatLong(quoteNumber, defaultCensusBlock, defaultLatitude, defaultLongitude, HomeGranularityQueries.SELECT_CENSUS_BLOCK_GROUP);
    }

    protected void riskAddressChangeDuringRenewal(TestData tdChangedAddress) {
        String mockCensusBlock = HomeGranularityConstants.DEFAULT_DYNAMIC_CENSUS_BLOCK;
        String mockLatitude    = HomeGranularityConstants.DEFAULT_DYNAMIC_LATITUDE;
        String mockLongitude   = HomeGranularityConstants.DEFAULT_DYNAMIC_LONGITUDE;
        mainApp().open();
        createCustomerIndividual();
        createPolicy();
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
        processRenewalGenerationJob(expirationDate);
        mainApp().reopen();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(tdChangedAddress, ApplicantTab.class, true );
        Tab.buttonTopSave.click();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        policy.getDefaultView().fillFromTo(tdChangedAddress, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        String censusBlockGroupID = validateCensusBlockGroupAndLatLong(policyNumber, mockCensusBlock, mockLatitude, mockLongitude, HomeGranularityQueries.SELECT_RECAPTURED_CENSUS_BLOCK_GROUP);
        checkVRD(censusBlockGroupID);
    }

    protected String validateCensusBlockGroupAndLatLong(String policyNumber, String censusBlockGroup, String latitude, String longitude, String censusBlockQuery) {
        Map<String,String> censusBlockGroupAndLatLongFromDb = DBService.get().getRow(String.format(censusBlockQuery, policyNumber));
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

        PropertyQuoteTab.linkViewRatingDetails.click();

        assertSoftly(softly -> {
            softly.assertThat(PremiumsAndCoveragesQuoteTab.tableViewRatingDetails.getRow(3, "Census Block").getCell(4).getValue()).isEqualTo(censusBlockGroupID);
        });
    }

    /**
     * Process renewal jobs to get policy in Proposed status
     * @param expirationDate term expiration date
     */
    private void processRenewalGenerationJob(LocalDateTime expirationDate) {
        //move time to R-45 and run renewal batch job
        LocalDateTime renewImageGenDate = getTimePoints().getRenewPreviewGenerationDate(expirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
    }
}
