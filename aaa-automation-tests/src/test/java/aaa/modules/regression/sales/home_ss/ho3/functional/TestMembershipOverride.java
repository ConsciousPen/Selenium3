package aaa.modules.regression.sales.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.home_ss.helper.HelperCommon;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Tyrone Jemison
 * @name Test Membership Override
 * @scenario
 * [NEW BUSINESS]
 * -- AC1 --
 * GIVEN I'm an agent on the General tab in the AAA Products Owned section
 * WHEN I select 'Membership Override' option in the 'Current AAA Member' drop-down
 *      AND select the 'Override Type' (Term or Life), AND enter the 'Member Since Date'
 * THEN the quote or policy is eligible for the membership discount for the override type selected,
 *      AND the option on the Reports tab to order Membership report is no longer present
 *      AND the VRD displays the overridden membership details
 *      AND the policy can be bound.
 * -- AC2 --
 * GIVEN I'm an agent providing a membership override
 * WHEN I continue with a blank 'Member Since Date'
 * THEN a message displays 'Member Since Date' is required for override.
 * -- AC3 --
 * GIVEN I'm an agent on the General tab in the AAA Products Owned section
 * WHEN I'm attempting a membership override
 * THEN Membership Number is not required
 * -- AC4 --
 * -- AC5 --
 * [ENDORSEMENTS]
 * -- AC1 --
 * GIVEN I am endorsing a bound policy
 * WHEN the policy has a pre-existing overridden membership AND I replace the override with an ACTIVE membership number
 * THEN the option on the Reports tab to order Membership report is present AND the VRD displays the valid membership details
 *      AND the ms must be ordered to bind the policy.
 * -- AC2 --
 * GIVEN I am endorsing a bound policy
 * WHEN the policy has an ACTIVE membership AND I replace the ACTIVE membership with an overridden membership
 * THEN the membership number is not required AND 'member since date' is required AND the option on the Reports tab to order Membership report is no longer present
 *      AND the VRD displays the overridden membership details AND the policy can be bound.
 * -- AC3 --
 *  GIVEN I am endorsing a bound policy
 *  WHEN the policy has NO membership AND I override the membership
 *  THEN membership number is not required AND 'member since date' is required
 *          AND the option on the Reports tab to order Membership report is no longer present
 *          AND the VRD displays the overridden membership details AND the policy can be bound.
 * @runtime NOT RUN
 */
@StateList(states = Constants.States.AZ)
public class TestMembershipOverride extends HomeSSHO3BaseTest
{
    // Global Variables
    HelperCommon myHelper = new HelperCommon();

    /**
     * @author Tyrone Jemison
     * @param state
     * @steps
     * 1. Create TD to Hold Adjustments. Create Policy TD and adjust it.
     * 2. Grab Default GeneralTab TestData and Adjust it with new data, then add that to the overall test data.
     * 3. Create Customer and Policy using Membership Override Option and NO membership number. Bind Policy.
     * @runTime 5min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-9686")
    public void AC01AC03_testMembershipOverride_NB(@Optional("") String state) {

        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership").resolveLinks();
        TestData adjustingReportData = getTestSpecificTD("ReportsTab").resolveLinks();
        TestData adjustingAgentData = getTestSpecificTD("AgentInfo").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData applicantTabTestData = getPolicyTD().getTestData("ApplicantTab");
        TestData reportsTabTestData = getPolicyTD().getTestData("ReportsTab");

        reportsTabTestData.adjust("AAAMembershipReport", adjustingReportData);
        applicantTabTestData.adjust("AAAMembership", adjustingData);
        applicantTabTestData.adjust("AgentInfo", adjustingAgentData);

        defaultPolicyData.adjust("ApplicantTab", applicantTabTestData);
        defaultPolicyData.adjust("ReportsTab", reportsTabTestData);

        // Create Customer and Policy using Membership Override Option and NO membership number. Bind Policy.
        mainApp().open();
        createHOMESSPolicyAndVerifyOverrideRating(policy, defaultPolicyData);
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @steps
     * 1. Create TD to Hold Adjustments. Create Policy TD and adjust it.
     * 2. Grab Default GeneralTab TestData and Adjust it with new data, then add that to the overall test data.
     * 3. Create Customer and Policy using Membership Override Option and NO "Membership Since" Date.
     * 4. Verify "Member Since Date Required" Error.
     * @runTime 3min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-9686")
    public void AC02_testMembershipOverride_NB(@Optional("") String state) {

        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership_NoMemSinceDate").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData generalTabTestData = getPolicyTD().getTestData("ApplicantTab");
        generalTabTestData.adjust("AAAMembership", adjustingData);
        defaultPolicyData.adjust("ApplicantTab", generalTabTestData);

        // Create Customer and Policy using Membership Override Option and NO membership Since Date.
        // Verify "Member Since Date Required" Error.
        mainApp().open();
        createHomeSSOverrideMissingMemberSinceDate(defaultPolicyData, policy, "");
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @steps
     * 1. Create TD to Hold Adjustments. Create Policy TD and adjust it.
     * 2. Grab Default GeneralTab TestData and Adjust it with new data, then add that to the overall test data.
     * 3. Create Customer and Policy as a user other than "L41" or "using Membership Override Option and NO membership number. Bind Policy.
     * @runTime 2min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-9370")
    public void AC01_testMembershipOverride_NonPrivlidgedUser(@Optional("") String state) {
        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData generalTabTestData = getPolicyTD().getTestData("ApplicantTab");
        generalTabTestData.adjust("AAAMembership", adjustingData);
        defaultPolicyData.adjust("ApplicantTab", generalTabTestData);

        // Create Customer and Policy using Membership Override Option and NO membership number. Bind Policy.
		mainApp().open(getLoginTD().adjust("Groups", "I38"));
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, ApplicantTab.class, true);

        // Here, I make sure the failure occurred at the anticipated location.
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        assertThat(new ApplicantTab().getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER)).hasValue("Membership Override");

    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @steps
     * 1. Create new Customer and Policy. Create NB policy using Overridden Membership.
     * 2. Initiate Endorsement. Provide Active Membership Number.
     * 3. Order Membership Report.
     * 4. Complete Endorsement.
     * @runTime 7min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-9634")
    public void AC1_testMembershipOverride_Endorse(@Optional("UT") String state) {
        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership").resolveLinks();
        TestData adjustingReportData = getTestSpecificTD("ReportsTab").resolveLinks();
        TestData adjustingPPCData = getTestSpecificTD("PropertyInfoTab_ForPPC").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData reportsTabTestData = getPolicyTD().getTestData("ReportsTab");
        TestData generalTabTestData = getPolicyTD().getTestData("ApplicantTab");
        TestData propInfoTabTestData = getPolicyTD().getTestData("PropertyInfoTab");

        generalTabTestData.adjust("AAAMembership", adjustingData);
        reportsTabTestData.adjust("AAAMembershipReport", adjustingReportData);
        propInfoTabTestData.adjust("PublicProtectionClass", adjustingPPCData);

        defaultPolicyData.adjust("ApplicantTab", generalTabTestData);
        defaultPolicyData.adjust("ReportsTab", reportsTabTestData);
        defaultPolicyData.adjust("PropertyInfoTab", propInfoTabTestData);

        // Create Customer and Policy using Membership Override Option and NO membership number. Bind Policy.
        mainApp().open();
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        myHelper.doSameDayEndorsement(policyNumber, policy, getTestSpecificTD("Endorsement_AddActiveMember"));
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @steps
     * 1. Create new Customer and Policy. Create NB policy using Active Membership.
     * 2. Initiate Endorsement. Provide Membership Override.
     * 3. DO NOT Order Membership Report.
     * 4. Complete Endorsement.
     * @runTime 2min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-9634")
    public void AC2_testMembershipOverride_Endorse(@Optional("UT") String state) {
        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership_ActiveMember").resolveLinks();
        TestData adjustingReportData = getTestSpecificTD("ReportsTab_ActiveMember").resolveLinks();
        TestData adjustingPPCData = getTestSpecificTD("PropertyInfoTab_ForPPC").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData reportsTabTestData = getPolicyTD().getTestData("ReportsTab");
        TestData generalTabTestData = getPolicyTD().getTestData("ApplicantTab");
        TestData propInfoTabTestData = getPolicyTD().getTestData("PropertyInfoTab");

        generalTabTestData.adjust("AAAMembership", adjustingData);
        reportsTabTestData.adjust(adjustingReportData);
        propInfoTabTestData.adjust("PublicProtectionClass", adjustingPPCData);

        defaultPolicyData.adjust("ApplicantTab", generalTabTestData);
        defaultPolicyData.adjust("ReportsTab", reportsTabTestData);
        defaultPolicyData.adjust("PropertyInfoTab", propInfoTabTestData);

        // Create Customer and Policy using Membership Override Option and NO membership number. Bind Policy.
        mainApp().open();
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        myHelper.doSameDayEndorsement(policyNumber, policy, getTestSpecificTD("Endorsement_AddMemberOverride"));
        mainApp().close();
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @steps
     * 1. Create new Customer and Policy. Create NB policy with NO Membership.
     * 2. Initiate Endorsement. Provide Membership Override.
     * 3. DO NOT Order Membership Report.
     * 4. Complete Endorsement.
     * @runTime 2min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-9634")
    public void AC3_testMembershipOverride_Endorse(@Optional("") String state) {
        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership_NonMember").resolveLinks();
        TestData adjustingReportData = getTestSpecificTD("ReportsTab").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();

        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData generalTabTestData = getPolicyTD().getTestData("ApplicantTab");
        TestData reportsTabTestData = getPolicyTD().getTestData("ReportsTab");

        reportsTabTestData.adjust("AAAMembershipReport", adjustingReportData);
        generalTabTestData.adjust("AAAMembership", adjustingData);

        defaultPolicyData.adjust("ApplicantTab", generalTabTestData);
        defaultPolicyData.adjust("ReportsTab", reportsTabTestData);

        // Create Customer and Policy using Membership Override Option and NO membership number. Bind Policy.
        mainApp().open();
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        myHelper.doSameDayEndorsement(policyNumber, policy, getTestSpecificTD("Endorsement_AddMemberOverride"));
        mainApp().close();
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @steps
     * 1. Open App. Create a Customer and NB Policy.
     * 2. Close App. Advance JVM to NB+15.
     * 3. Execute Jobs.
     * 4. Advance JVM to NB+30.
     * 5. Execute Jobs.
     * 6. Open App. Search for prior created Policy.
     * 7. Validate Membership Discount Retained.
     * @RunTime 5min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6370")
    public void AC1_testMembershipOverride_NB15NB30Jobs(@Optional("") String state) {
        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership").resolveLinks();
        TestData adjustingReportData = getTestSpecificTD("ReportsTab").resolveLinks();
        TestData adjustingAgentData = getTestSpecificTD("AgentInfo").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData applicantTabTestData = getPolicyTD().getTestData("ApplicantTab");
        TestData reportsTabTestData = getPolicyTD().getTestData("ReportsTab");

        reportsTabTestData.adjust("AAAMembershipReport", adjustingReportData);
        applicantTabTestData.adjust("AAAMembership", adjustingData);
        applicantTabTestData.adjust("AgentInfo", adjustingAgentData);

        defaultPolicyData.adjust("ApplicantTab", applicantTabTestData);
        defaultPolicyData.adjust("ReportsTab", reportsTabTestData);

        // Create Customer and Policy using Membership Override Option and NO membership number. Bind Policy.
        mainApp().open();
        //myGenericHelper.createHOMESSPolicyAndVerifyOverrideRating(policy, defaultPolicyData);
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        mainApp().close();

        // Setup JVM Parameters
        LocalDateTime policyCreationDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime nb15TimePoint = policyCreationDate.plusDays(15L);
        LocalDateTime nb30TimePoint = policyCreationDate.plusDays(30L);

        // Advance JVM to NB+15
        TimeSetterUtil.getInstance().nextPhase(nb15TimePoint);
        JobUtils.executeJob(Jobs.membershipValidationJob);
        // Advance JVM to NB+30
        TimeSetterUtil.getInstance().nextPhase(nb30TimePoint);
        JobUtils.executeJob(Jobs.membershipValidationJob);

        // Create Customer and Policy using Membership Override Option and NO membership number. Bind Policy.
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        // Use an endorsement to gain access to the policy. Verify Discount is still there.
        policy.endorse().perform(getTestSpecificTD("Endorsement_DoNothing").adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Endorsement_DoNothing"), PremiumsAndCoveragesQuoteTab.class, true);

        // Verify Membership is Retained
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("Yes");
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @AC
     * GIVEN the system is running the membership batch order job at renewal time point 2,
     * WHEN a policy with a TERM membership override on the current term is encountered for processing,
     * THEN the membership override is discarded and the membership number is included in the request order file for
     *      validation if it pre-exists on the policy, or is in the BML response and the documents pertaining to discount
     *      removal are generated.
     * @RunTime 10min
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-10154")
    public void AC1_testMembershipOverride_Renewal(@Optional("AZ") String state) {
        Long membershipStage3TP = 63L;
        Long membershipStage4TP = 48L;
        Long membershipRenewRatingTP = 45L;

        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership_TERM").resolveLinks();
        TestData adjustingReportData = getTestSpecificTD("ReportsTab").resolveLinks();
        TestData adjustingAgentData = getTestSpecificTD("AgentInfo").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData reportsTabTestData = getPolicyTD().getTestData("ReportsTab");
        TestData applicantTabTestData = getPolicyTD().getTestData("ApplicantTab");

        applicantTabTestData.adjust("AAAMembership", adjustingData);
        reportsTabTestData.adjust("AAAMembershipReport", adjustingReportData);
        applicantTabTestData.adjust("AgentInfo", adjustingAgentData);

        defaultPolicyData.adjust("ApplicantTab", applicantTabTestData);
        defaultPolicyData.adjust("ReportsTab", reportsTabTestData);

        // Create Customer and Policy using Membership Override Option. Bind Policy.
        mainApp().open();
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        // Setup JVM Parameters
        LocalDateTime policyCreationDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime policyExpirationDate = policyCreationDate.plusYears(1L);
        LocalDateTime stage3TimePoint = policyExpirationDate.minusDays(membershipStage3TP);
        LocalDateTime stage4TimePoint = policyExpirationDate.minusDays(membershipStage4TP);
        LocalDateTime systemRatingTimePoint = policyExpirationDate.minusDays(membershipRenewRatingTP);
        mainApp().close();

        // Advance JVM to Stage3. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(stage3TimePoint);
        runMembershipValidationAndRenewalJobs();
        // Advance JVM to Stage4. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(stage4TimePoint);
        runMembershipValidationAndRenewalJobs();
        // Advance JVM to Final Time Point. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(systemRatingTimePoint);
        runMembershipValidationAndRenewalJobs();

        // Open Policy. Open Renewal Image.
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        policy.renew().start().submit();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_DoNothing"), PremiumsAndCoveragesQuoteTab.class, true);

        // Verify Membership is Dropped
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();

        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("No");
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("AAA Membership Discount")).isEqualToIgnoringCase("0.0");

        // If we don't close VRD, the test will fail attempting to close using the default methods.
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @AC
     * GIVEN the system is running the membership batch order job at renewal time point 2,
     * WHEN a policy with a LIFE membership override on the current term is encountered for processing,
     * THEN the membership override is discarded and the membership number is included in the request order file for
     *      validation if it pre-exists on the policy, or is in the BML response and the documents pertaining to discount
     *      removal are generated.
     * @RunTime 10min
     */
    @Parameters({"state"})
    //TODO: Test case disabled due to the error below. The test case passes when ran individually so more research is needed
    // Caused by: toolkit.exceptions.IstfException: HTTP Job ERROR: <--- Job 'Renewal_Offer_Generation_Part1' has timed out after 1200000 milliseconds
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, enabled = false)
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-10154")
    public void AC2_testMembershipOverride_Renewal(@Optional("AZ") String state) {
        Long membershipStage3TP = 63L;
        Long membershipStage4TP = 48L;
        Long membershipRenewRatingTP = 45L;

        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership_LIFE").resolveLinks();
        TestData adjustingReportData = getTestSpecificTD("ReportsTab").resolveLinks();
        TestData adjustingAgentData = getTestSpecificTD("AgentInfo").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData reportsTabTestData = getPolicyTD().getTestData("ReportsTab");
        TestData applicantTabTestData = getPolicyTD().getTestData("ApplicantTab");

        applicantTabTestData.adjust("AAAMembership", adjustingData);
        reportsTabTestData.adjust("AAAMembershipReport", adjustingReportData);
        applicantTabTestData.adjust("AgentInfo", adjustingAgentData);

        defaultPolicyData.adjust("ApplicantTab", applicantTabTestData);
        defaultPolicyData.adjust("ReportsTab", reportsTabTestData);

        // Create Customer and Policy using Membership Override Option. Bind Policy.
        mainApp().open();
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        // Setup JVM Parameters
        LocalDateTime policyCreationDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime policyExpirationDate = policyCreationDate.plusYears(1L);
        LocalDateTime stage3TimePoint = policyExpirationDate.minusDays(membershipStage3TP);
        LocalDateTime stage4TimePoint = policyExpirationDate.minusDays(membershipStage4TP);
        LocalDateTime systemRatingTimePoint = policyExpirationDate.minusDays(membershipRenewRatingTP);
        mainApp().close();

        // Advance JVM to Stage3. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(stage3TimePoint);
        runMembershipValidationAndRenewalJobs();
        // Advance JVM to Stage4. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(stage4TimePoint);
        runMembershipValidationAndRenewalJobs();
        // Advance JVM to Final Time Point. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(systemRatingTimePoint);
        runMembershipValidationAndRenewalJobs();

        // Open Policy. Open Renewal Image.
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        policy.renew().start().submit();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_DoNothing"), PremiumsAndCoveragesQuoteTab.class, true);

        // Verify Membership is Dropped
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();

        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("Yes");

        // If we don't close VRD, the test will fail attempting to close using the default methods.
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @AC
     * GIVEN I'm an agent in a revised renewal,
     * WHEN I override the membership for the revised renewal,
     * THEN the membership is not included in the batch order job and the membership override is retained.
     * @RunTime 12min
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-10154")
    public void AC3_testMembershipOverride_Renewal(@Optional("") String state) {
        Long membershipStage3TP = 63L;
        Long membershipStage4TP = 48L;
        Long membershipRenewRatingTP = 45L;

        // Create TD to Hold Adjustments. Create Default Policy TD.
        TestData adjustingData = getTestSpecificTD("AAAMembership_LIFE").resolveLinks();
        TestData adjustingReportData = getTestSpecificTD("ReportsTab").resolveLinks();
        TestData adjustingAgentData = getTestSpecificTD("AgentInfo").resolveLinks();
        TestData defaultPolicyData = getPolicyTD();
        // Grabbing Default GeneralTab TestData and Adjusting it with new data, then adding that to the overall test data.
        TestData reportsTabTestData = getPolicyTD().getTestData("ReportsTab");
        TestData applicantTabTestData = getPolicyTD().getTestData("ApplicantTab");

        applicantTabTestData.adjust("AAAMembership", adjustingData);
        reportsTabTestData.adjust("AAAMembershipReport", adjustingReportData);
        applicantTabTestData.adjust("AgentInfo", adjustingAgentData);

        defaultPolicyData.adjust("ApplicantTab", applicantTabTestData);
        defaultPolicyData.adjust("ReportsTab", reportsTabTestData);

        // Launch App
        mainApp().open();
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        // Setup JVM Parameters
        LocalDateTime policyCreationDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime policyExpirationDate = policyCreationDate.plusYears(1L);
        LocalDateTime revisedRenewalDate = policyExpirationDate.minusDays(100L);
        LocalDateTime stage3TimePoint = policyExpirationDate.minusDays(membershipStage3TP);
        LocalDateTime stage4TimePoint = policyExpirationDate.minusDays(membershipStage4TP);
        LocalDateTime systemRatingTimePoint = policyExpirationDate.minusDays(membershipRenewRatingTP);
        mainApp().close();

        // Advance JVM to Generate Renewal Image.
        TimeSetterUtil.getInstance().nextPhase(revisedRenewalDate);
        runMembershipValidationAndRenewalJobs();
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getTestSpecificTD("Renewal_AddMemberOverride"));
        mainApp().close();
        // Advance JVM to Stage3. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(stage3TimePoint);
        runMembershipValidationAndRenewalJobs();
        // Advance JVM to Stage4. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(stage4TimePoint);
        runMembershipValidationAndRenewalJobs();
        // Advance JVM to Final Time Point. Run Jobs
        TimeSetterUtil.getInstance().nextPhase(systemRatingTimePoint);
        runMembershipValidationAndRenewalJobs();

        // Open Policy. Open Renewal Image.
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        policy.renew().start().submit();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_DoNothing"), PremiumsAndCoveragesQuoteTab.class, true);

        // Verify Membership is Retained
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();

        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("Yes");

        // If we don't close VRD, the test will fail attempting to close using the default methods.
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
    }

    public void createHOMESSPolicyAndVerifyOverrideRating(IPolicy in_policy, TestData in_policyData) {
        createCustomerIndividual();
        in_policy.initiate();
        in_policy.getDefaultView().fillUpTo(in_policyData, PremiumsAndCoveragesQuoteTab.class, true);
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("Yes");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
        PremiumsAndCoveragesQuoteTab.btnContinue.click();
        in_policy.getDefaultView().fillFromTo(in_policyData, MortgageesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

    /** This creates a new customer and a new policy, however the membership validation is anticipated to fail and the error to be verified. */
    public void createHomeSSOverrideMissingMemberSinceDate(TestData in_policyData, IPolicy in_policy, String errorString) {
        createCustomerIndividual();
        in_policy.initiate();
        in_policy.getDefaultView().fillUpTo(in_policyData, ApplicantTab.class, true);

        assertThat(new ApplicantTab().getAAAMembershipAssetList().getAsset("Current AAA Member").isVisible()).isTrue();
    }

    public void runMembershipValidationAndRenewalJobs() {
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        JobUtils.executeJob(Jobs.membershipValidationJob);
    }
}
