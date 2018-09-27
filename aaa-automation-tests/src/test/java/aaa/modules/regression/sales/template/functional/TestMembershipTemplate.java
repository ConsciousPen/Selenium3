package aaa.modules.regression.sales.template.functional;

import aaa.admin.modules.administration.generateproductschema.defaulttabs.CacheManager;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.*;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

import java.time.LocalDateTime;
import java.util.Optional;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestMembershipTemplate extends PolicyBaseTest {

    private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
    private ErrorTab errorTab = new ErrorTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private ApplicantTab applicantTab = new ApplicantTab();

    protected void pas16457_validateMembershipNB15() {

        //Create and bind policy
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        switch (getPolicyType().getShortName())
        {
            case "AutoSS": {
                TestData tdAuto = getAdjustedTestData_Auto();
                AutoSSPolicy(tdAuto);
                break;
            }

            case "HomeSS_HO3": {
                TestData tdHome = getAdjustedTestData_Home();
                homeSSPolicy(tdHome);
                break;
            }

            case "HomeCA_HO3": {
                TestData tdHome = getAdjustedTestData_Home();
                homeCAPolicy(tdHome);
                break;
            }

            default: {
                TestData tdAuto = getAdjustedTestData_Auto();
                AutoCASpecificPolicy(tdAuto);
            }
        }

        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        //Update membership number in DB
        Optional<AAAMembershipQueries.AAAMembershipStatus> membershipStatus = AAAMembershipQueries.getAAAMembershipStatusFromSQL(policyNumber);
        assertThat(membershipStatus).isNotNull().isEqualTo(AAAMembershipQueries.AAAMembershipStatus.Error);

        AAAMembershipQueries.updateAAAMembershipNumberInSQL(policyNumber, "4290023796712001");
        AAAMembershipQueries.updatePriorAAAMembershipNumberInSQL(policyNumber, "4290023796712001");

        adminApp().open();
        new CacheManager().goClearCacheManagerTable();
        mainApp().reopen();

        //Run NB15 and NB30 jobs and verify that the Membership Status is 'Active' in DB
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(15));
        JobUtils.executeJob(Jobs.membershipValidationJob);

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(30));
        JobUtils.executeJob(Jobs.membershipValidationJob);

        membershipStatus = AAAMembershipQueries.getAAAMembershipStatusFromSQL(policyNumber);
        assertThat(membershipStatus).isNotNull().isEqualTo(AAAMembershipQueries.AAAMembershipStatus.ACTIVE);
    }

    protected void AutoCASpecificPolicy(TestData td) {
        //TODO Finish Auto CA Policy in which There is a No Hit on Membership
        //Member Since Dialog is currently broken 8/1/2018
        policy.getDefaultView().fillUpTo(td, DriverActivityReportsTab.class, true);
        errorTab.overrideAllErrors();
        errorTab.override();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
        policy.getDefaultView().fillFromTo(td, DriverActivityReportsTab.class, DocumentsAndBindTab.class, false);
        new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");


    }

    protected void AutoSSPolicy(TestData td) {

        //Fill up to the rating details report then fill out the Member Since Date dialog
        policy.getDefaultView().fillUpTo(td, RatingDetailReportsTab.class, true);
        ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
                .getCell(7).click();
        new RatingDetailReportsTab().getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT)
                .getAsset(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG.getLabel(), AssetList.class)
                .getAsset(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), TextBox.class).setValue("11/14/2016");
        Page.dialogConfirmation.confirm();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get()); //Continue button disappears for a moment
        policy.getDefaultView().fillFromTo(td, VehicleTab.class, DriverActivityReportsTab.class);
        errorTab.overrideAllErrors();
        errorTab.override();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
        policy.getDefaultView().fillFromTo(td, DriverActivityReportsTab.class, DocumentsAndBindTab.class, false);

        new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");
        validateErrors(td);
    }

    protected void homeSSPolicy(TestData td) {

        try {
            policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        } catch (Exception e) {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get()); //continue button disappears
            policy.getDefaultView().fillFromTo(td, PropertyInfoTab.class, BindTab.class);
        }

        validateErrors(td);

    }

    protected void homeCAPolicy(TestData td) {

        policy.getDefaultView().fillUpTo(td, aaa.main.modules.policy.home_ca.defaulttabs.BindTab.class, true);
        validateErrors(td);
    }

    private TestData getAdjustedTestData_Home() {
        TestData testData = getPolicyTD();

        TestData testDataApplicantTab = testData.getTestData(HomeSSMetaData.GeneralTab.class.getSimpleName());

        TestData testDataAAAProductsOwned = testDataApplicantTab.getTestData(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel())
                .adjust(HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "2111111111111110");

        TestData applicantTabAdjusted = testDataApplicantTab
                .adjust(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), testDataAAAProductsOwned);

        testData.adjust(HomeSSMetaData.GeneralTab.class.getSimpleName(), applicantTabAdjusted).resolveLinks();
        return testData;
    }

    private TestData getAdjustedTestData_Auto() {
        TestData testData = getPolicyTD();

        TestData testDataGeneralTab = testData.getTestData(AutoSSMetaData.GeneralTab.class.getSimpleName());

        TestData testDataAAAProductsOwned = testDataGeneralTab.getTestData(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel())
                .adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), "2111111111111110");

        TestData generalTabAdjusted = testDataGeneralTab
                .adjust(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), testDataAAAProductsOwned);

        testData.adjust(AutoSSMetaData.GeneralTab.class.getSimpleName(), generalTabAdjusted).resolveLinks();
        return testData;
    }

    private void validateErrors(TestData td){
        documentsAndBindTab.submitTab();
        errorTab.overrideAllErrors();
        errorTab.override();
        documentsAndBindTab.submitTab();
        purchaseTab.fillTab(td);
        purchaseTab.submitTab();
    }

    /**
     * Navigate the quote to PnC tab - assert discounts present when Current AAA Member = No
     * @param  policyType Uses policy Type to differentiate between product variation
     */
    protected void premiumAndDiscountCheckAAANo(String policyType) {
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        if (policyType.equals("HomeCA_HO6")) {
            assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied").getValue()).isEqualTo("New Policy");
        } else if (policyType.equals("HomeCA_HO3")){
            assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied").getValue()).isEqualTo("New home");
        } else {
            assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts).isAbsent();
        }
    }

    /**
     * Navigate the quote to PnC tab - assert discounts present when Current AAA Member = Pending
     * @param  policyType Uses policy Type to differentiate between product variation
     */
    protected void premiumAndDiscountCheckAAAPending(String policyType) {
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
        switch (policyType) {
            case "HomeCA_HO6": {
                assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied").getValue()).isEqualTo("New Policy, AAA Membership");
                break;
            }
            case "HomeCA_DP3": {
                assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts).isAbsent();
                break;
            }
            case "HomeCA_HO3": {
                assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied").getValue()).isEqualTo("New home, AAA Membership");
                break;
            }
            case "HomeCA_HO4": {
                assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied").getValue()).isEqualTo("AAA Membership");
                break;
            }
        }
    }

    /**
     * Create quote using default test data but adjusted to set Current AAA Member to No
     */
    protected void setKeyPathsAndGenerateQuote() {
        TestData testData = getPolicyTD();
        // keypathTabSection Result: "ApplicantTab|AAAMembership"
        String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab.class.getSimpleName(),
                HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

        //Make keypath to reports tab and hide ordering the report for AAA Membership
        String keypathReportsSection = TestData.makeKeyPath(aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab.class.getSimpleName(),
                HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());

        // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
        String keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

        // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
        String keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());

        testData.adjust(keypathCurrentMember, "No")
                .mask(keypathMemberNum)
                .mask(keypathReportsSection);

        // Create the Quote //
        mainApp().open();
        createCustomerIndividual();
        createQuote(testData);
    }

    /**
     * This method sets Current AAA Membership to pending and asserts that the policy cannot be bound with pending selected
     */
    public void pendingMembershipValidations_AC1_3() {
        String policyType = getPolicyType().getShortName();

        //Set Current AAA Member to "No" and save premiums / assert discounts
        premiumAndDiscountCheckAAANo(policyType);

        Dollar premiumCheckAAANo = aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        new aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab().getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Membership Pending");

        //Set Current AAA Member to "Membership Pending" and save premiums / assert discounts
        premiumAndDiscountCheckAAAPending(policyType);
        Dollar premiumCheckAAAPending = aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
        if (policyType.equals("HomeCA_DP3")) {
            assertThat(premiumCheckAAAPending.equals(premiumCheckAAANo)).isTrue();
        } else {
            assertThat(premiumCheckAAAPending.lessThan(premiumCheckAAANo)).isTrue();
        }
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        new BindTab().btnPurchase.click();
        errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CSA25636985);
        errorTab.cancel();
    }

    /**
     * Create policy - add endorsement - assert Current AAA Members cannot be set to pending.
     */
    public void addEndorsementAndCheckForMSPending() {
        mainApp().open();
        createCustomerIndividual();
        createPolicy();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        membershipStatusCheckApplicantTab();
    }

    /**
     * Create policy - create renewal image at renewal TP1 - assert Current AAA Members cannot be set to pending.
     */
    public void generateRenewalImageAndCheckForMSPending() {

        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);

        log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);

        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        PolicySummaryPage.buttonRenewals.click();

        PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.comboBoxes.getFirst().setValue("Data Gathering");
        PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.buttons.get("Go").click();
        PolicySummaryPage.buttonOk.click();
        PolicySummaryPage.buttonOkPopup.click();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        membershipStatusCheckApplicantTab();
    }

    /**
     * Validate Current AAA Member cannot be set to Membership Pending based upon policyType
     */
    public void membershipStatusCheckApplicantTab() {
        String policyType = getPolicyType().getShortName();
        String membershipValue = "Membership Pending";
        switch (policyType) {
            case "HomeCA_HO6": {
                assertThat(applicantTab.getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).getAllValues().contains(membershipValue)).isFalse();
                break;
            }
            case "HomeCA_DP3": {
                assertThat(applicantTab.getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).getAllValues().contains(membershipValue)).isFalse();
                break;
            }
            case "HomeCA_HO3": {
                assertThat(applicantTab.getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).getAllValues().contains(membershipValue)).isFalse();
                break;
            }
            case "HomeCA_HO4": {
                assertThat(applicantTab.getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).getAllValues().contains(membershipValue)).isFalse();
                break;
            }
            case "HomeSS_DP3" : {
                assertThat(applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).getAllValues().contains(membershipValue)).isFalse();
            }
        }
    }
}
