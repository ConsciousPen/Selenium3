package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestUpdateWildfireScore extends PolicyBaseTest {

    private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
    private aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab propertyInfoTabCA = new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab();

    private ReportsTab reportsTab = new ReportsTab();
    private aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab reportsTabCA = new aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab();

    private TextBox wildfireScore = propertyInfoTab.getFireReportAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.FireReport.WILDFIRE_SCORE);
    private TextBox wildfireScoreCA = propertyInfoTabCA.getFireReportAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.FireReport.WILDFIRE_SCORE);

    private String updatedValue = "99";


    public void pas12922_UpdateWildfireScoreNoPrivilegeNB(PolicyType policyType) {

        // Create Test data for appropriate policy type. Mask is there because field is disabled for unprivileged user
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()));

        // Open App with unprivileged user. Create Customer. get Policy Type and initiate policy. Fill policy to PropertyInfo Tab.
        loginA30();
        createCustomerIndividual();
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(testData, PropertyInfoTab.class, true);

        // Assert that Wildfire Score is disabled.
        assertThat(wildfireScore).isDisabled();
        mainApp().close();
    }

    public void pas12922_UpdateWildfireScoreNoPrivilegeEndorsement(PolicyType policyType) {

        // Create Test data for appropriate policy type. Mask is there because field is disabled for unprivileged user
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App. Create Customer. get Policy Type and initiate policy. Fill policy to PropertyInfo Tab.
        mainApp().open();
        createCustomerIndividual();
        policyType.get().createPolicy(testData);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // Endorse Policy. Assert that Wildfire Score is disabled. Save and Exit.
        loginA30();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policyType.get().endorse().perform(testDataManager.policy.get(policyType).getTestData("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        assertThat(wildfireScore).isDisabled();
        mainApp().close();
    }

    public void pas12922_UpdateWildfireScoreNB(PolicyType policyType) {

        // Create Test data for appropriate policy type
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App with user. Create Customer. get Policy Type and initiate policy. Fill policy to Reports Tab and save wildfire score
        mainApp().open();
        createCustomerIndividual();
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(testData, ReportsTab.class, true);
        String wildfireScoreValue = reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue();
        reportsTab.submitTab();

        // Assert that Wildfire Score is enabled.
        propertyInfoTab.fillTab(testData);
        assertThat(wildfireScore).isEnabled();

        // Update Wildfire Score and check if it is used.
        wildfireScore.setValue(updatedValue);

        // Check that report value is the same
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue())
                .isEqualTo(wildfireScoreValue);
        mainApp().close();
    }

    public void pas12922_UpdateWildfireScoreEndorsement(PolicyType policyType) {

        // Create Test data for appropriate policy type
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App with user. Create Customer. get Policy Type and create policy.
        mainApp().open();
        createCustomerIndividual();
        policyType.get().createPolicy(testData);

        // Endorse Policy. Open reports tab Save Wildfire Score. Submit Tab.
        policyType.get().endorse().perform(testDataManager.policy.get(policyType).getTestData("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        String wildfireScoreValue = reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue();
        reportsTab.submitTab();

        // Make sure Wildfire Score is enabled
        assertThat(wildfireScore).isEnabled();

        // Update Wildfire Score .
        wildfireScore.setValue(updatedValue);

        // Check that Reports Tab Wildfire Score did not change
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue())
                .isEqualTo(wildfireScoreValue);
        mainApp().close();
    }

    public void pas12922_UpdateWildfireScoreRenewal(PolicyType policyType) {

        // Create Test data for appropriate policy type
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App with user. Create Customer. get Policy Type and initiate policy. Fill policy to PropertyInfo Tab.
        mainApp().open();
        createCustomerIndividual();
        policyType.get().createPolicy(testData);

        // Renew Policy. Open reports tab Save Wildfire Score. Submit Tab.
        policyType.get().renew().perform();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        String wildfireScoreValue = reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue();
        reportsTab.submitTab();

        // Make sure Wildfire Score is enabled
        assertThat(wildfireScore).isEnabled();

        // Update Wildfire Score .
        wildfireScore.setValue(updatedValue);

        // Check that Reports Tab Wildfire Score did not change
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue())
                .isEqualTo(wildfireScoreValue);
        mainApp().close();
    }

    public void pas12922_UpdateWildfireScoreManualEntry(PolicyType policyType) {

        // Create Test data for appropriate policy type Conversion
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("Conversion"), "TestData");

        // Create Test data for Initiate Renewal Entry
        TestData initiateRenewal = getStateTestData(testDataManager.policy.get(policyType).getTestData("InitiateRenewalEntry"), "TestData");

        // Open App with user. Create Customer. get Policy Type and initiate policy. Fill policy to PropertyInfo Tab.
        mainApp().open();
        createCustomerIndividual();

        // Initiate manual entry. fill policy to reports Tab. Save Wildfire Score. Submit Tab.
        customer.initiateRenewalEntry().perform(initiateRenewal);
        policyType.get().getDefaultView().fillUpTo(testData, ReportsTab.class, true);
        String wildfireScoreValue = reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue();
        reportsTab.submitTab();

        // Assert that Wildfire Score is enabled.
        propertyInfoTab.fillTab(testData);
        assertThat(wildfireScore).isEnabled();

        // Update Wildfire Score and check if it is used.
        wildfireScore.setValue(updatedValue);

        // Check that report value is the same
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue())
                .isEqualTo(wildfireScoreValue);
        mainApp().close();
    }

    public void pas12922_UpdateCAWildfireScoreNoPrivilegeNB(PolicyType policyType) {

        // Create Test data for appropriate policy type. Mask is there because field is disabled for unprivileged user
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.CurrentCarrier.class.getSimpleName(), HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel()));

        // Open App with unprivileged user. Create Customer. get Policy Type and initiate policy. Fill policy to PropertyInfo Tab.
        loginA30();
        createCustomerIndividual();
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(testData, aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab.class, true);

        // Assert that Wildfire Score is disabled.
        assertThat(wildfireScoreCA).isDisabled();
        mainApp().close();
    }

    public void pas12922_UpdateCAWildfireScoreNoPrivilegeEndorsement(PolicyType policyType) {

        // Create Test data for appropriate policy type. Mask is there because field is disabled for unprivileged user
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App. Create Customer. get Policy Type and initiate policy. Fill policy to PropertyInfo Tab.
        mainApp().open();
        createCustomerIndividual();
        policyType.get().createPolicy(testData);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // Log in with unprivileged user. Endorse Policy. Assert that Wildfire Score is disabled. Save and Exit.
        loginA30();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policyType.get().endorse().perform(testDataManager.policy.get(policyType).getTestData("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        assertThat(wildfireScoreCA).isDisabled();
        mainApp().close();
    }

    public void pas12922_UpdateCAWildfireScoreNB(PolicyType policyType) {

        // Create Test data for appropriate policy type
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App with user. Create Customer. get Policy Type and initiate policy. Fill policy to Reports Tab and save wildfire score
        mainApp().open();
        createCustomerIndividual();
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(testData, ReportsTab.class, true);
        String wildfireScoreValue = reportsTabCA.tblFirelineReport.getRow(1).getCell(HomeCaMetaData.ReportsTab.FirelineReportTblHeaders.WILDFIRE_SCORE.get()).getValue();
        reportsTabCA.submitTab();
        propertyInfoTabCA.fillTab(testData);

        // Make sure Wildfire Score is enabled.
        assertThat(wildfireScoreCA).isEnabled();

        // Update Wildfire Score.
        wildfireScoreCA.setValue(updatedValue);

        // Check that Reports Tab Wildfire Score did not change
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        assertThat(reportsTabCA.tblFirelineReport.getRow(1).getCell(HomeCaMetaData.ReportsTab.FirelineReportTblHeaders.WILDFIRE_SCORE.get()).getValue())
                .isEqualTo(wildfireScoreValue);

        checkFirelineScoreCA();
        mainApp().close();
    }

    public void pas12922_UpdateCAWildfireScoreEndorsement(PolicyType policyType) {

        // Create Test data for appropriate policy type
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App with user. Create Customer. get Policy Type and create policy.
        mainApp().open();
        createCustomerIndividual();
        policyType.get().createPolicy(testData);

        // Endorse Policy. Open reports tab Save Wildfire Score. Submit Tab.
        policyType.get().endorse().perform(testDataManager.policy.get(policyType).getTestData("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        String wildfireScoreValue = reportsTabCA.tblFirelineReport.getRow(1).getCell(HomeCaMetaData.ReportsTab.FirelineReportTblHeaders.WILDFIRE_SCORE.get()).getValue();
        reportsTabCA.submitTab();

        // Make sure Wildfire Score is enabled.
        assertThat(wildfireScoreCA).isEnabled();

        // Update Wildfire Score.
        wildfireScoreCA.setValue(updatedValue);

        // Check that Reports Tab Wildfire Score did not change
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        assertThat(reportsTabCA.tblFirelineReport.getRow(1).getCell(HomeCaMetaData.ReportsTab.FirelineReportTblHeaders.WILDFIRE_SCORE.get()).getValue())
                .isEqualTo(wildfireScoreValue);

        checkFirelineScoreCA();
        mainApp().close();
    }

    public void pas12922_UpdateCAWildfireScoreRenewal(PolicyType policyType) {

        // Create Test data for appropriate policy type
        TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Open App with user. Create Customer. get Policy Type and initiate policy. Fill policy to PropertyInfo Tab.
        mainApp().open();
        createCustomerIndividual();
        policyType.get().createPolicy(testData);

        // Renew Policy. Open reports tab Save Wildfire Score. Submit Tab.
        policyType.get().renew().perform();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        String wildfireScoreValue = reportsTabCA.tblFirelineReport.getRow(1).getCell(HomeCaMetaData.ReportsTab.FirelineReportTblHeaders.WILDFIRE_SCORE.get()).getValue();
        reportsTabCA.submitTab();

        // Make sure Wildfire Score is enabled.
        assertThat(wildfireScoreCA).isEnabled();

        // Update Wildfire Score.
        wildfireScoreCA.setValue(updatedValue);

        // Check that Reports Tab Wildfire Score did not change
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        assertThat(reportsTabCA.tblFirelineReport.getRow(1).getCell(HomeCaMetaData.ReportsTab.FirelineReportTblHeaders.WILDFIRE_SCORE.get()).getValue())
                .isEqualTo(wildfireScoreValue);

        checkFirelineScoreCA();
        mainApp().close();
    }


    private void checkFirelineScoreCA(){

        // Check that View Rating Details shows the updated Value
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        PropertyQuoteTab.RatingDetailsView.open();
        assertThat(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Fireline score")).contains(updatedValue);
        PropertyQuoteTab.RatingDetailsView.close();
    }

    private void loginA30(){
        TestData loginTD = initiateLoginTD().adjust("Groups", "A30");
        loginTD.adjust("User", "qa_roles");
        mainApp().open(loginTD);
    }

}