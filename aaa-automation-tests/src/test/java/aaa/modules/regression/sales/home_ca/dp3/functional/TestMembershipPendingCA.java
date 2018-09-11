package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaDP3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMembershipPendingCA extends HomeCaDP3BaseTest {
    private BindTab bindTab = new BindTab();
    private ErrorTab errorTab = new ErrorTab();
    /**
     * @author Robert Boles
     * @name Test Align Current AAA Member for CA products (Auto and Property) with SS - PAS-17784
     * @scenario
     * 1. Create Customer.
     * 2. Create CA HDP Quote
     * 3. Current AAA member will have the option with Membership "Membership Pending"
     * 4. No warning message (Pending membership will be validated and removed at NB+30, if not found Active) shows
     * 5. Navigate to Premium & Coverages tab and Calculate Premium
     * 6. Premium will reduce and AAA Membership discount applies in Discounts section
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17784: Align Current AAA Member for CA products (Auto and Property) with SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-17784")
    public void pas17784_AC1_CA_HO6_Pending_Option(@Optional("") String state) {
        TestData testData = getPolicyTD();
        // keypathTabSection Result: "ApplicantTab|AAAMembership"
        String keypathTabSection = TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
                HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

        //Make keypath to reports tab and hide ordering the report for AAA Mmembership
        String keypathReportsSection = TestData.makeKeyPath(ReportsTab.class.getSimpleName(),
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

        // Create the Quote and save policy number //
        mainApp().open();
        createCustomerIndividual();
        createQuote(testData);

        premiumAndDiscountCheckAAANo();
        Dollar premiumCheckAAANo = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
        log.info(premiumCheckAAANo.toString());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        new ApplicantTab().getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Membership Pending");
        premiumAndDiscountCheckAAAPending();
        Dollar premiumCheckAAAPending = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
        log.info(premiumCheckAAAPending.toString());
        assertThat(premiumCheckAAAPending.lessThan(premiumCheckAAANo)).isTrue();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        bindTab.btnPurchase.click();
        errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CSA25636985);
    }

    private void premiumAndDiscountCheckAAANo() {
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        CustomAssertions.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied")).hasValue("New Policy");
    }

    private void premiumAndDiscountCheckAAAPending() {
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
        //CustomAssertions.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied")).hasValue("New Policy, AAA Membership");
    }
}
