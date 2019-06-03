package aaa.modules.regression.sales.home_ss.ho3;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author Olga Reva
 * <b> Test Policy Holder discounts </b>
 * <p> Steps:
 * <p> 1. Find customer or create new if customer does not exist.
 * <p> 2. Initiate new HSS policy creation.
 * <p> 3. Fill all mandatory fields on all tabs, calculate premium.
 * <p> 4. Verify Young Home Owner discount:
 * <p> 		4a. Navigate to Applicant tab and enter so Date of birth that insured age will be 30 years.
 * <p> 		4b. Re-order reports.
 * <p> 		4c. Navigate to Premiums&Coverages Quote tab and re-calculate premium.
 * <p> 		4d. Verify Young Home owner discount applying and displaying in Discounts section and in Rating Details.
 * <p> 5. Verify Mature HomeOwner discount:
 * <p> 		5a. Navigate to Applicant tab and enter so Date of birth that insured age will be 55 years.
 * <p> 		5b. Re-order reports.
 * <p> 		5c. Navigate to Premiums&Coverages Quote tab and re-calculate premium.
 * <p> 		5d. Verify Mature Home owner discount applying and displaying in Discounts section and in Rating Details.
 * <p> 6. Verify no Policy Holder Category discounts apply:
 * <p> 		6a. Navigate to Applicant tab and enter so Date of birth that insured age will be 50 years.
 * <p> 		6b. Re-order reports.
 * <p> 		6c. Navigate to Premiums&Coverages Quote tab and re-calculate premium.
 * <p> 		6d. Verify Young and Mature HomeOwner discounts are not apply.
 * <p> 7. Verify AAA Employee discount.
 * <p> 		7a. Navigate to Applicant tab and set AAA Employee to 'Yes'.
 * <p> 		7b. Re-order reports.
 * <p> 		7c. Navigate to Premiums&Coverages Quote tab and re-calculate premium.
 * <p> 		7d. Verify AAA Employee discount applying and displaying in Discounts section and in Rating Details.
 * <p> 8. Save&Exit quote.
 * <p>
 *
 */
public class TestPolicyDiscountHolder extends HomeSSHO3BaseTest {	
	
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testPolicyHolderDiscount(@Optional("") String state) {
		mainApp().open();

		TestData td_YoungHomeOwner = getTestSpecificTD("TestData_YoungHomeOwner"); 
		TestData td_NoPolicyHolderDiscount = getTestSpecificTD("TestData_NoPolicyHolderDiscount"); 
		TestData td_MatureHomeowner = getTestSpecificTD("TestData_MatureHomeowner"); 
		TestData td_EmployeeDiscount = getTestSpecificTD("TestData_EmployeeDiscount"); 
        
		//getCopiedQuote(); 
		createCustomerIndividual();
		
		createQuote();
        
        policy.dataGather().start();
        
        //Policyholder: Young Homeowner discount applies
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        ApplicantTab applicantTab = new ApplicantTab(); 
        applicantTab.fillTab(td_YoungHomeOwner);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        ReportsTab reportsTab = new ReportsTab();
        reportsTab.fillTab(td_YoungHomeOwner);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
        premiumsTab.calculatePremium();

        CustomSoftAssertions.assertSoftly(softly -> {
            Map<String, String> youngHomeOwnerDiscount_dataRow = new HashMap<>();
            youngHomeOwnerDiscount_dataRow.put("Discount Category", "Policyholder");
            youngHomeOwnerDiscount_dataRow.put("Discounts Applied", "Young Homeowner");

            Map<String, String> matureHomeOwnerDiscount_dataRow = new HashMap<>();
            matureHomeOwnerDiscount_dataRow.put("Discount Category", "Policyholder");
            matureHomeOwnerDiscount_dataRow.put("Discounts Applied", "Mature Homeowner");

            Map<String, String> employeeDiscount_dataRow = new HashMap<>();
            employeeDiscount_dataRow.put("Discount Category", "Policyholder");
            employeeDiscount_dataRow.put("Discounts Applied", "AAA Employee");

            softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(youngHomeOwnerDiscount_dataRow)).exists();

            PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Policy holder Discount Category")).as("Incorrect value of Policy holder Discount Category in Rating Details").isNotEqualTo("0.0");

            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Young Homeowner discount")).as("Incorrect value of Young Homeowner discount in Rating Details").isNotEqualTo("0.0");
            PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();


            //Policyholder: Mature Homeowner discount applies
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
            applicantTab.fillTab(td_MatureHomeowner);

            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
            reportsTab.fillTab(td_MatureHomeowner);

            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
            premiumsTab.calculatePremium();

            if (getState().equals("NY")) {
                softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(matureHomeOwnerDiscount_dataRow)).isPresent(false);
                PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
                softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Policy holder Discount Category")).as("Incorrect value of Policy holder Discount Category in Rating Details").isEqualTo("0.0%");
                softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Mature Homeowner Discount")).as("Incorrect value of Mature Homeowner discount in Rating Details").isEqualTo("0.0");
                PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
            } else {
                softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(matureHomeOwnerDiscount_dataRow)).exists();

                PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
                softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Policy holder Discount Category")).as("Incorrect value of Policy holder Discount Category in Rating Details").isNotEqualTo("0.0");
                softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Mature Homeowner Discount")).as("Incorrect value of Mature Homeowner discount in Rating Details").isNotEqualTo("0.0");
                PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
            }

            //Policyholder: No discounts apply
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
            applicantTab.fillTab(td_NoPolicyHolderDiscount);

            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
            reportsTab.fillTab(td_NoPolicyHolderDiscount);

            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
            premiumsTab.calculatePremium();

            softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(youngHomeOwnerDiscount_dataRow)).isPresent(false);
            softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(matureHomeOwnerDiscount_dataRow)).isPresent(false);

            PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Policy holder Discount Category")).as("Incorrect value of Policy holder Discount Category in Rating Details").isEqualTo("0.0%");

            //CustomAssert.assertTrue("Incorrect value of Mature Homeowner discount in Rating Details",
            //		PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Mature Homeowner Discount").equals("0.0"));
            PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

            //Policyholder: AAA Employee discount applies
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
            applicantTab.fillTab(td_EmployeeDiscount);

            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
            premiumsTab.calculatePremium();

            softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(employeeDiscount_dataRow)).exists();

            PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Employee")).as("Incorrect value of 'Employee' in Rating Details").isEqualTo("Yes");

            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Employee discount")).as("Incorrect value of Employee discount in Rating Details").isNotEqualTo("0.0");
            PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

            PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
            log.info("TEST Policy Holder Discount: HSS Quote created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
        });
	}
}
