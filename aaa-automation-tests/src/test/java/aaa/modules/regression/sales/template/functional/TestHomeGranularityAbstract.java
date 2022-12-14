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
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;

public abstract class TestHomeGranularityAbstract extends PolicyBaseTest {

    private QuoteDataGatherPage quoteDataGatherPage = new QuoteDataGatherPage();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    protected abstract Tab getApplicantTab();
    protected abstract Tab getReportsTab();
    protected abstract Tab getPremiumAndCoveragesQuoteTab();

    //Keypath for the Dwelling Address section on the Applicant tab
    String keypathDwellingAddress = TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel());
    String keypathZipCode = TestData.makeKeyPath(keypathDwellingAddress, HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel());
    String keypathAddress1 = TestData.makeKeyPath(keypathDwellingAddress, HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel());

    protected void validateCensusBlockGroupAndLatLong(String censusBlock, String latitude, String longitude) {
        TestData policyTd = getPolicyTD();
        createQuoteAndFillUpTo(policyTd, getPremiumAndCoveragesQuoteTab().getClass());
        String quoteNumber = quoteDataGatherPage.getQuoteNumber();
        String censusBlockGroupID = validateCensusBlockGroupAndLatLong(quoteNumber, censusBlock, latitude, longitude, "quote","rated");
        checkVRD(censusBlockGroupID);
    }

    protected void validateCensusBlockGroupAndLatLongFromEADS(String zipCode, String address, String censusBlock, String latitude, String longitude) {
        TestData policyTd = getPolicyTD()
                .adjust(keypathZipCode, zipCode)
                .adjust(keypathAddress1, address);
        createQuoteAndFillUpTo(policyTd, getApplicantTab().getClass());
        Tab.buttonTopSave.click();
        String quoteNumber = quoteDataGatherPage.getQuoteNumber();
        validateCensusBlockGroupAndLatLong(quoteNumber, null, null, null, "quote","dataGather");
        Tab.buttonNext.click();
        policy.getDefaultView().fillFromTo(policyTd, getReportsTab().getClass(), getPremiumAndCoveragesQuoteTab().getClass(), true);
        validateCensusBlockGroupAndLatLong(quoteNumber, censusBlock, latitude, longitude, "quote","rated");
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
        policy.getDefaultView().fillUpTo(tdChangedAddress, getApplicantTab().getClass(), true );
        Tab.buttonTopSave.click();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        policy.getDefaultView().fillFromTo(tdChangedAddress, getReportsTab().getClass(), getPremiumAndCoveragesQuoteTab().getClass(), true);
        String censusBlockGroupID = validateCensusBlockGroupAndLatLong(policyNumber, mockCensusBlock, mockLatitude, mockLongitude, "renewal", "rated");
        checkVRD(censusBlockGroupID);
    }

    protected String validateCensusBlockGroupAndLatLong(String policyNumber, String censusBlockGroup, String latitude, String longitude, String txtype, String status) {
        String censusBlockQuery = HomeGranularityQueries.SELECT_LAT_LONG_CENSUS_BLOCK_GROUP;
        Map<String,String> censusBlockGroupAndLatLongFromDb = DBService.get().getRow(String.format(censusBlockQuery, policyNumber,txtype,status));
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
        if(isStateCA()){
            assertSoftly(softly -> {
                softly.assertThat(premiumsAndCoveragesQuoteTab.tableViewRatingDetails.getRow(3, "Census Block").getCell(4).getValue()).isEqualTo(censusBlockGroupID);
            });
        } else {
            assertSoftly(softly -> {
                softly.assertThat(premiumsAndCoveragesQuoteTab.tableViewRatingDetailsValues.getRow(1, "Census Block").getCell(2).getValue()).isEqualTo(censusBlockGroupID);
            });
        }
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

    protected void riskAddressChangeDuringEndorsement(TestData tdChangeAddress) {
        String mockCensusBlock = HomeGranularityConstants.DEFAULT_DYNAMIC_CENSUS_BLOCK;
        String mockLatitude    = HomeGranularityConstants.DEFAULT_DYNAMIC_LATITUDE;
        String mockLongitude   = HomeGranularityConstants.DEFAULT_DYNAMIC_LONGITUDE;
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        tdChangeAddress.adjust(getTestSpecificTD("TestData_Endorsement"));
        policy.endorse().perform(tdChangeAddress);
        policy.getDefaultView().fillUpTo(tdChangeAddress, getPremiumAndCoveragesQuoteTab().getClass(), true );
        String censusBlockGroupID = validateCensusBlockGroupAndLatLong(policyNumber, mockCensusBlock, mockLatitude, mockLongitude, "endorsement","rated");
        checkVRD(censusBlockGroupID);
    }
}
