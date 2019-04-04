package aaa.modules.bct.renewal;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.datax.impl.SimpleDataProvider;

public class ManualRenewalTest extends BackwardCompatibilityBaseTest {
	/**
	 * @author Deloite
	 * @name Manual selection of Do Not Renew of a policy
	 * @scenario
	 * @param state
	 * Preconditions:
	 * Retrieve an active policy before R-35, User the authority to set a policy for non renewal
	 * Steps:
	 * User has the authority to select Do Not Renew from the Move To dropdown and click the Go button
	 * Check:
	 * User should be navigated to the Do Not Renew page
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, IN, MD, NJ, NV, OH, OK, OR, PA, VA})
	public void BCT_ONL_047_ManualRenewal(@Optional("") String state) {
		IPolicy policy = findAndOpenPolicy(getMethodName(), PolicyType.AUTO_SS);

		policy.doNotRenew().start();
		Page.dialogConfirmation.confirm();
		policy.doNotRenew().getView().fill(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DoNotRenew", "TestData"));
		policy.doNotRenew().submit();
		assertThat(PolicySummaryPage.labelDoNotRenew).isPresent();
	}
	/**
	 * @author Deloite
	 * @name Manual Renewal on or after R-35 Days
	 * @scenario
	 * @param state
	 * Preconditions:
	 * 1. Policy exists and is thirty-five (35) days or less from the policy expiration date
	 * 2. Renewal quote has been generated
	 * 3. System has generated renewal offer
	 * Steps:
	 * 1. User retrieves renewal image
	 * 2. User updates renewal quote manually
	 * 3. User navigates to bind tab
	 * 4. User clicks ‘Propose’ to manually propose the policy
	 * Check:
	 * Policy has successfully been proposed for renewal
	 */
	@Parameters({"state"})
	@Test
	public void BCT_ONL_055_ManualRenewal(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
	}
	/**
	 * @author Deloite
	 * @name Manual Renewal before R-35
	 * @scenario
	 * @param state
	 * Preconditions:
	 * Retrieve a Auto policy 36 or more days before Policy expiration date, which is not yet renewed.
	 * Steps:
	 * 1. Retrieve the policy and move to policy consolidated view and click on renew option
	 * 2. Enter all details and navigate to premium and coverage page and then click on calculate premium and save and exit
	 * 3. Enter all the mandatory details and click save and exit in the bind page.
	 * 4. Click on the pended renewal link and modify coverages, vehicle, driver information.
	 * Check:
	 * User should be navigated to general page
	 * 1. Premium is calculated and Policy Consolidated view is displayed. Hyperlink "Renewals" is enabled.
	 * 2. Activities and Progress Notes section saves the renewal creation timestamp and performer
	 * i)Consolidated view of policy's renewal term should be displayed.
	 * ii) Activities and Progress Notes section should contain an entry for (renewal image created) with a date and time stamp, and the user identified as the individual who performed the manual renewal activity.
	 * System should navigate into the pended renewal transaction and user should be able to modify the manually generated renewal image.
	 */
	@Parameters({"state"})
	@Test
	public void BCT_ONL_001_ManualRenewal(@Optional("") String state) {
		IPolicy policy = findAndOpenPolicy(getMethodName(), PolicyType.AUTO_SS);

		policy.renew().start();
		Tab.buttonOk.click();
		Page.dialogConfirmation.confirm();

		new PremiumAndCoveragesTab().calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
	}
	/**
	 * @author Deloite
	 * @name Validate Non Renewal is Removed
	 * @scenario
	 * @param state
	 * Preconditions:
	 * Retrieve a Auto policy 36 or more days before Policy expiration date, which is not yet renewed.
	 * Steps:
	 * 1. User has an authority level equal to one of the following levels: 1,2 or 4 and has authority to review or edit the Renewal Preview and/or Policy
	 * 2. Reviews policy qualification and Select "Remove Do Not Renew" from "Move To" Dropdown, Click "Go" in the Policy Consolidated Page and remove Non-Renewal trigger
	 * Check:
	 * The Do Not Renew Flag should be removed successfully
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, IN, MD, NJ, NV, OH, OK, OR, PA, VA})
	public void BCT_ONL_003_ManualRenewal(@Optional("") String state) {
		IPolicy policy = findAndOpenPolicy(getMethodName(), PolicyType.AUTO_SS);

		deletePendingRenewals(policy);
		policy.doNotRenew().perform(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DoNotRenew", "TestData"));
		assertThat(PolicySummaryPage.labelDoNotRenew).isPresent();

		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelDoNotRenew).isPresent(false);
	}
	/**
	 * @author Deloite
	 * @name Manual selection of Do Not Renew of a policy
	 * @scenario
	 * @param state
	 * Preconditions:
	 * Retrieve a Auto policy 36 or more days before Policy expiration date, which is not yet renewed.
	 * Steps:
	 * User has the authority to select Do Not Renew from the Move To dropdown and click the Go button
	 * Check:
	 * User should be navigated to the Do Not Renew page
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, IN, MD, NJ, NV, OH, OK, OR, PA, VA})
	public void BCT_ONL_004_ManualRenewal(@Optional("") String state) {
		IPolicy policy = findAndOpenPolicy(getMethodName(), PolicyType.AUTO_SS);

		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelDoNotRenew).isPresent(false);
	}
}
