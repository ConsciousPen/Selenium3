package aaa.modules.policy.home_ss_ho3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


/**
 * Created by lkazarnovskiy on 8/8/2017.
 * <p>
 * 1. Create new or open existent Customer.
 * 2. Start HSS quote creation.
 * 3. Fill General, Applicant, Property Info tabs.
 * 4. Order reports.
 * 5. Navigate to Premiums and Coverages tab, Endorsement subtab.
 * 6.  Add HS 09 88 Endorsement.
 * 7. Click continue button.    VERIFY: There is HS 09 88 Endorsement in the Endorsement Forms table on the Premium page
 * 8. Navigate to Premiums and Coverages tab, Endorsement subtab.
 * 9.  Remove HS 09 88 Endorsement,
 * 10. Click continue button.   VERIFY: There is NO HS 09 88 Endorsement in the Endorsement Forms table on the Premium page.
 * 11. Navigate to Premiums and Coverages tab.
 * 12. Calculate premium.
 * 13. Fill Underwriting and Approval, Documents tabs
 * 14. Issue policy.
 */
public class TestPolicyRemoveForm extends HomeSSHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void hssPolicyRemoveForm() {

		TestData td = getPolicyTD();
		TestData tdEndorsement = getPolicyTD("TestPolicyRemoveForm", "TestData");
		TestData tdEndorsementDelete = getPolicyTD("TestPolicyRemoveForm", "TestData_Delete");

		String expectedEndorsementName = String.format("HS 09 88 01 12 Additional Insured - Special Event; Effective %1$s - %2$s",
				tdEndorsement.getTestData("EndorsementTab").getTestData("HS 09 88").getValue("Effective date"),
				tdEndorsement.getTestData("EndorsementTab").getTestData("HS 09 88").getValue("Expiration date"));

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(td.adjust(tdEndorsement), PremiumsAndCoveragesQuoteTab.class);

		PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", expectedEndorsementName).verify.present();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
		policy.getDefaultView().getTab(EndorsementTab.class).fillTab(tdEndorsementDelete).submitTab();

		PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", expectedEndorsementName).verify.present(false);

		policy.getDefaultView().fillFromTo(td, PremiumsAndCoveragesQuoteTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}
