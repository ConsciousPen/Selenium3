package aaa.modules.regression.sales.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


/**
 * <p> Created by lkazarnovskiy on 8/8/2017.
 * <p> <p>
 * <p> 1. Create new or open existent Customer.
 * <p> 2. Start HSS quote creation.
 * <p> 3. Fill General, Applicant, Property Info tabs.
 * <p> 4. Order reports.
 * <p> 5. Navigate to Premiums and Coverages tab, Endorsement subtab.
 * <p> 6.  Add HS 09 88 Endorsement.
 * <p> 7. Click continue button.    VERIFY: There is HS 09 88 Endorsement in the Endorsement Forms table on the Premium page
 * <p> 8. Navigate to Premiums and Coverages tab, Endorsement subtab.
 * <p> 9.  Remove HS 09 88 Endorsement,
 * <p> 10. Click continue button.   VERIFY: There is NO HS 09 88 Endorsement in the Endorsement Forms table on the Premium page.
 * <p> 11. Navigate to Premiums and Coverages tab.
 * <p> 12. Calculate premium.
 * <p> 13. Fill Underwriting and Approval, Documents tabs
 * <p> 14. Issue policy.
 */
public class TestPolicyRemoveForm extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void hssPolicyRemoveForm(@Optional("") String state) {

		TestData td = getPolicyTD();
		TestData tdEndorsement = getTestSpecificTD("TestData");
		TestData tdEndorsementDelete = getTestSpecificTD("TestData_Delete");

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(td.adjust(tdEndorsement), PremiumsAndCoveragesQuoteTab.class);

		String expectedEndorsementName = String.format("HS 09 88 01 12 Additional Insured - Special Event; Effective %1$s - %2$s",
				tdEndorsement.getTestData("EndorsementTab").getTestData("HS 09 88").getValue("Effective date"),
				tdEndorsement.getTestData("EndorsementTab").getTestData("HS 09 88").getValue("Expiration date"));

		assertThat(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", expectedEndorsementName)).isPresent();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
		policy.getDefaultView().getTab(EndorsementTab.class).fillTab(tdEndorsementDelete).submitTab();

		assertThat(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", expectedEndorsementName)).isPresent(false);

		policy.getDefaultView().fillFromTo(td, PremiumsAndCoveragesQuoteTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}
