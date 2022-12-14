package aaa.modules.regression.sales.auto_ca.choice;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

public class TestPolicyGoodStudentDiscount extends AutoCaChoiceBaseTest {
	protected TestData tdPolicy;
	private String origPolicyNum;
	private String policyNum1;
	private String policyNum2;
	private String policyNum3;
	private String policyNum4;
	private String policyNum5;

	/**
	 * @author Olga Reva
	 * <b> Validation of Good Student discount for New Quote in the Premium & Coverages Discount section and Rating Details </b>
	 * <p> Steps:
	 * <p> 1. Verify Good Student Discount at new quote creation.
	 * <p> 1.1. Initiate a new Auto Quote with the Authority to create a Quote. Provide the required information to create a quote.
	 * <p> 1.2. Select the following field values for at least one driver:
	 * <p> 	Quote# 1: Driver Type = Available for Rating, Age = 24, Marital status = Separated, Occupation = Homemaker, Most Recent GPA = College Graduate;
	 * <p> 	Quote# 2: Driver Type = Available for Rating, Age = 23, Marital status = Single, Occupation = Farmer, Most Recent GPA = College Graduate;
	 * <p> 	Quote# 3: Driver Type = Available for Rating, Age = 16, Marital status = Single, Occupation = Student, Most Recent GPA = B Student;
	 * <p> 	Quote# 4: Driver Type = Available for Rating, Age = 25, Marital status = Divorced, Occupation = Student, Most Recent GPA = Pass;
	 * <p> 	Quote# 5: Driver Type = Available for Rating, Age = 24, Marital status = Separated, Occupation = Homemaker, Most Recent GPA = College Graduate,
	 * <p> 				Total Years Driving Experience = 10, DSR points >1, Has chargeable suspension within last 3 years;
	 * <p> 1.3. Calculate premium.
	 * <p> 1.4. Verify Good Student discount is applied to the driver and display Premium and Coverages Discount section.
	 * <p> 1.5. Verify Good Student discount is applied to the driver and display as 'Yes' in Rating Details.
	 * <p> 1.6. Bind the quote.
	 *
	 * <p> 2. Verify Good Student Discount at endorsement.
	 * <p> 2.1. Retrieve Policy# 2 and initiate an endorsement for it.
	 * <p> 2.2. Navigate to Driver tab and change Marital Status to "Married".
	 * <p> 2.3. Calculate premium.
	 * <p> 2.4. Verify Good Student discount is removed and doesn't display on Premium and Coverages Discount section.
	 * <p> 2.5. Verify Good Student discount is removed and display as 'No' in Rating Details.
	 * <p> 2.6. Bind endorsement.
	 *
	 * <p> 3. Verify Good Student Discount at Renewal image.
	 * <p> 3.1. Retrieve for Policies# 1, 3-5 renewal image in status Premium calculated in Iquiry mode.
	 * <p> 3.2. Navigate to Premium & Coverages tab.
	 * <p> 3.3. Policies# 1, 3, 5: Verify Good Student discount is applied to the driver and display Premium and Coverages Discount section.
	 * <p> 3.4. Policies# 1, 3, 5: Verify Good Student discount is applied to the driver and display as 'Yes' in Rating Details.
	 * <p> 3.5. Policy# 4: Verify Good Student discount isn't applied to the driver and displaying as 'No' in Rating Details.
	 */

	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
	public void testValidationGoodStudentDiscount(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());

		TestData td_quote1 = getTestSpecificTD("TestData_1");
		TestData td_quote2 = getTestSpecificTD("TestData_2");
		TestData td_quote3 = getTestSpecificTD("TestData_3");
		TestData td_quote4 = getTestSpecificTD("TestData_4");
		TestData td_quote5 = getTestSpecificTD("TestData_5");
		TestData td_activity = getTestSpecificTD("TestData_Activity");
		TestData td_endorse = getTestSpecificTD("TestData_Endorsement");

		mainApp().open();
		createCustomerIndividual();

		origPolicyNum = createPolicy();

		policyNum1 = createPolicyAndVerifyGoodStudentDiscount(td_quote1, true);
		log.info("TEST: Policy 1 is created with #" + policyNum1);
		policyNum2 = createPolicyAndVerifyGoodStudentDiscount(td_quote2, true);
		log.info("TEST: Policy 2 is created with #" + policyNum2);
		policyNum3 = createPolicyAndVerifyGoodStudentDiscount(td_quote3, true);
		log.info("TEST: Policy 3 is created with #" + policyNum3);
		policyNum4 = createPolicyAndVerifyGoodStudentDiscount(td_quote4, true);
		log.info("TEST: Policy 4 is created with #" + policyNum4);
		policyNum5 = createPolicyAndVerifyGoodStudentDiscount(td_quote5, td_activity, true);
		log.info("TEST: Policy 5 is created with #" + policyNum5);

		verifyGoodStudentDiscountOnEndorsement(policyNum2, td_endorse, false);

		verifyGoodStudentDiscountOnRenewal(policyNum1, true);
		verifyGoodStudentDiscountOnRenewal(policyNum3, true);
		verifyGoodStudentDiscountOnRenewal(policyNum4, false);
		verifyGoodStudentDiscountOnRenewal(policyNum5, true);
	}

	private String createPolicyAndVerifyGoodStudentDiscount(TestData td_quote, boolean isDiscountApplied) {
		SearchPage.openPolicy(origPolicyNum);
		policy.policyCopy().perform(td_quote);
		policy.dataGather().start();

		policy.getDefaultView().fillFromTo(td_quote, GeneralTab.class, MembershipTab.class, true);

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().fillTab(td_quote);
		new PremiumAndCoveragesTab().calculatePremium();

		String discountApplied;
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
			discountApplied = "Yes";
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
			discountApplied = "None";
		}

		PremiumAndCoveragesTab.RatingDetailsView.open();
		new PremiumAndCoveragesTab().getRatingDetailsDriversData().forEach(i -> CustomAssertions.assertThat(i.getValue("Good Student")).isEqualTo(discountApplied));
		 PremiumAndCoveragesTab.RatingDetailsView.close();
		new PremiumAndCoveragesTab().submitTab();

		policy.getDefaultView().fillFromTo(td_quote, DriverActivityReportsTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	private String createPolicyAndVerifyGoodStudentDiscount(TestData td_quote, TestData td_activity, boolean isDiscountApplied) {
		SearchPage.openPolicy(origPolicyNum);
		policy.policyCopy().perform(td_quote);
		policy.dataGather().start();

		policy.getDefaultView().fillFromTo(td_quote, GeneralTab.class, MembershipTab.class, true);

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().fillTab(td_quote);
		new PremiumAndCoveragesTab().calculatePremium();
		new PremiumAndCoveragesTab().submitTab();
		new DriverActivityReportsTab().fillTab(td_quote).submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
		new DriverTab().fillTab(td_activity);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().fillTab(td_quote);
		new PremiumAndCoveragesTab().calculatePremium();

		String discountApplied;
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
			discountApplied = "Yes";
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
			discountApplied = "None";
		}

		PremiumAndCoveragesTab.RatingDetailsView.open();
		new PremiumAndCoveragesTab().getRatingDetailsDriversData().forEach(i -> CustomAssertions.assertThat(i.getValue("Good Student")).isEqualTo(discountApplied));
		 PremiumAndCoveragesTab.RatingDetailsView.close();

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().fillTab(td_quote).submitTab();
		new PurchaseTab().fillTab(td_quote).submitTab();

		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	private void verifyGoodStudentDiscountOnEndorsement(String policyNum, TestData td, boolean isDiscountApplied) {
		SearchPage.openPolicy(policyNum);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus30Days"));

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
		new DriverTab().fillTab(td).submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();

		String discountApplied;
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
			discountApplied = "Yes";
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
			discountApplied = "None";
		}

		PremiumAndCoveragesTab.RatingDetailsView.open();
		Map<String, String> goodStudentDiscount = new HashMap<>();
		goodStudentDiscount.put("", "Good Student");
		goodStudentDiscount.put("Proposed Changes", discountApplied);

		CustomAssertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsDrivers.getRowContains(goodStudentDiscount)).isPresent();
		 PremiumAndCoveragesTab.RatingDetailsView.close();

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();

		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Endorsement is created for policy with #" + policyNum2);
	}

	private void verifyGoodStudentDiscountOnRenewal(String policyNum, boolean isDiscountApplied) {
		SearchPage.openPolicy(policyNum);
		log.info("TEST: Verifying Good Student discount for renewal of " + policyNum);

		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();

		String discountApplied;
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
			discountApplied = "Yes";
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
			discountApplied = "None";
		}
		PremiumAndCoveragesTab.RatingDetailsView.open();
		new PremiumAndCoveragesTab().getRatingDetailsDriversData().forEach(i -> CustomAssertions.assertThat(i.getValue("Good Student")).isEqualTo(discountApplied));
		 PremiumAndCoveragesTab.RatingDetailsView.close();
		new PremiumAndCoveragesTab().saveAndExit();

		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals.isEnabled()).isTrue();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		if (isDiscountApplied) {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Good Student Discount");
		}
		else {
			CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Good Student Discount");
		}
		PremiumAndCoveragesTab.RatingDetailsView.open();
		new PremiumAndCoveragesTab().getRatingDetailsDriversData().forEach(i -> CustomAssertions.assertThat(i.getValue("Good Student")).isEqualTo(discountApplied));
		 PremiumAndCoveragesTab.RatingDetailsView.close();
		new PremiumAndCoveragesTab().cancel();
	}
}

