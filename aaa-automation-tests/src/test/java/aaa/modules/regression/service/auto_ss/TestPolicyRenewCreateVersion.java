package aaa.modules.regression.service.auto_ss;

import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomAssertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author oreva
 * @name Renewal - Create Version
 * @scenario
 * 1. Create new or retrieve a customer.
 * 2. Create an Auto SS policy. 
 * 3. Create a renewal, it should be in Premium Calculated status.
 * 4. Navigate to Inquiry mode of renewal version 1 and get renewal premium amount.
 * 5. Create renewal version 2. 
 * 6. Change a coverage, re-calculate premium and get premium amount of version 2. 
 * 7. Navigate to transaction history page by clicking on 'Renewal Quote Version' button.
 * 8. Verify that renewal version 1 and version 2 and appropriate premium amounts are displaying in Transaction History table.
 */
public class TestPolicyRenewCreateVersion extends AutoSSBaseTest {
	
	@Parameters({"state"})
    @StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testPolicyRenewCreateVersion(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		policy.renew().performAndFill(getTestSpecificTD("TestData"));
		
		assertThat(NotesAndAlertsSummaryPage.alert).valueContains("This Policy is Pending Renewal");
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		assertThat(PolicySummaryPage.buttonRenewalQuoteVersion).isAbsent();
		
		PolicySummaryPage.buttonRenewals.click();
		assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell("Status").getValue()).isEqualTo("Premium Calculated");
		
		policy.policyInquiry().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		String premium_version1 = PremiumAndCoveragesTab.totalTermPremium.getValue();
		
		new PremiumAndCoveragesTab().createVersion();
		log.info("Create Version action is initiated");	
		new PremiumAndCoveragesTab().fillTab(getTestSpecificTD("TestData_version2"));
		String premium_version2 = PremiumAndCoveragesTab.totalTermPremium.getValue();
		
		assertThat(new Dollar(premium_version1)).isNotEqualTo(new Dollar(premium_version2));
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		
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
