package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;

import com.exigen.ipb.etcsa.utils.Dollar;

public class TestPolicyDiscounts extends HomeCaHO3BaseTest {

    /**
      * @author Jurij Kuznecov
      * @name Test CAH Policy Discounts
      * @scenario 
      * 1.  Create new or open existent Customer
      * 2.  Start CAH quote creation
      * 3.  Fill all mandatory fields till 'Premiums and Coverages' tab and calculate premium
      * 4.  Navigate to 'Applicant' tab and add additional active AAA Policy (Auto)
      * 5.  Calculate premium
      * 6.  Check MultiPolicy Discount applied
      * 7.  Issue policy, verify policy status is 'Active' and premium is calculated according to MultyPolicy discount
      * 8.  Start Endorsement action
      * 9.  Navigate to 'Applicant' tab and add additional active AAA Policy (PUP)
      * 10. Calculate premium
      * 11. Check MultiPolicy Discount applied
      * 12. Bind policy, verify policy status is 'Active' and premium is calculated according to MultyPolicy discount
      */

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testPolicyDiscounts(@Optional("CA") String state) {
        ApplicantTab applicantTab = new ApplicantTab();

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class, true);

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        applicantTab.fillTab(getTestSpecificTD("TestData_AddAuto"));
        applicantTab.submitTab();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();

        Dollar premiumAutoMultiPolice = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
        assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied")).valueContains("Multi-policy");

        PremiumsAndCoveragesQuoteTab.btnContinue.click();
        policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.getTotalPremiumSummaryForProperty().verify.equals(premiumAutoMultiPolice);

        new HomeCaPolicyActions.Endorse().perform(getPolicyTD("Endorsement", "TestData"));

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        applicantTab.fillTab(getTestSpecificTD("TestData_AddPUP"));
        applicantTab.submitTab();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();

        Dollar premiumPUPMultiPolice = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
        assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied")).valueContains("Multi-policy");

        PremiumsAndCoveragesQuoteTab.btnContinue.click();
        policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, BindTab.class, true);
        new BindTab().submitTab();

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.getTotalPremiumSummaryForProperty().verify.equals(premiumPUPMultiPolice);
    }
}
