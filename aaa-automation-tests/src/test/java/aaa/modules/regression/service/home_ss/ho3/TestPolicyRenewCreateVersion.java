package aaa.modules.regression.service.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author oreva
 * @name Renewal - Create Version
 * @scenario
 * 1. Create new or retrieve a customer.
 * 2. Create a Home SS policy. 
 * 3. Create a renewal, it should be in Premium Calculated status.
 * 4. Navigate to Inquiry mode of renewal version 1 and get renewal premium amount.
 * 5. Create renewal version 2. 
 * 6. Change a coverage, re-calculate premium and get premium amount of version 2. 
 * 7. Navigate to transaction history page by clicking on 'Renewal Quote Version' button.
 * 8. Verify that renewal version 1 and version 2 and appropriate premium amounts are displaying in Transaction History table.
 */
public class TestPolicyRenewCreateVersion extends HomeSSHO3BaseTest {
	
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
	public void testPolicyRenewCreateVersion(@Optional("") String state) {
		
		if (getUserGroup().equals(UserGroups.B31.get()) || getUserGroup().equals(UserGroups.F35.get()) || 
				getUserGroup().equals(UserGroups.G36.get())) {
			//login with QA user, create policy and renewal
			mainApp().open(getLoginTD(UserGroups.QA));
			createCustomerIndividual();
			createPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			
			policy.renew().performAndFill(getTestSpecificTD("TestData"));
			assertThat(NotesAndAlertsSummaryPage.alert).valueContains("This Policy is Pending Renewal");
			assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
			mainApp().close();
			
			//re-login with B31 or F35 or G36 user
			mainApp().open(getLoginTD(Constants.UserGroups.valueOf(getUserGroup())));
			MainPage.QuickSearch.buttonSearchPlus.click();
			SearchPage.openPolicy(policyNumber);
			assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
			PolicySummaryPage.buttonRenewals.click();
			assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell("Status").getValue()).isEqualTo("Premium Calculated");
			
			if (getUserGroup().equals(UserGroups.B31.get())) {
				policy.policyInquiry().start();
				assertThat(Tab.buttonCreateVersion).as("Button 'Create Version' is present in renewal").isAbsent();
			}
			else {
				createRenewalVersion();
			}
		}
		else {
			mainApp().open();
			createCustomerIndividual();
			createPolicy();			
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			
			if (getUserGroup().equals(UserGroups.E34.get())) {
				policy.renew().start();
				NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES.get());
				NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
				new PremiumsAndCoveragesQuoteTab().calculatePremium();
				new PremiumsAndCoveragesQuoteTab().saveAndExit();
			}
			else {
				policy.renew().performAndFill(getTestSpecificTD("TestData"));
			}
			
			assertThat(NotesAndAlertsSummaryPage.alert).valueContains("This Policy is Pending Renewal");
			assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
			assertThat(PolicySummaryPage.buttonRenewalQuoteVersion).isAbsent();		
			PolicySummaryPage.buttonRenewals.click();
			assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell("Status").getValue()).isEqualTo("Premium Calculated");
			
			createRenewalVersion();
		}
	}
	
	private void createRenewalVersion() {
		policy.policyInquiry().start();
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		String premium_version1 = PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue();
		
		new PremiumsAndCoveragesQuoteTab().createVersion();
		log.info("Create Version action is initiated");	
		new PremiumsAndCoveragesQuoteTab().fillTab(getTestSpecificTD("TestData_version2"), true);
		String premium_version2 = PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue();
		
		assertThat(new Dollar(premium_version1)).isNotEqualTo(new Dollar(premium_version2));
		
		NavigationPage.toViewTab(HomeSSTab.BIND.get());
		new BindTab().submitTab();
		
		assertThat(PolicySummaryPage.buttonRenewalQuoteVersion).isEnabled();
		PolicySummaryPage.buttonRenewalQuoteVersion.click();
		
		Map<String, String> renewal_row1 = new HashMap<>();
		renewal_row1.put("#", "1");
		renewal_row1.put("Trans. Premium", premium_version1);
		
		Map<String, String> renewal_row2 = new HashMap<>();
		renewal_row2.put("#", "2");
		renewal_row2.put("Trans. Premium", premium_version2);
		
		assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(renewal_row1)).isPresent();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(renewal_row2)).isPresent();
		Tab.buttonCancel.click();
	}
}
