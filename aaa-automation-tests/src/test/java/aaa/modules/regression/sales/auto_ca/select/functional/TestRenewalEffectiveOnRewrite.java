package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static toolkit.verification.CustomAssertions.assertThat;

@StateList(states = {Constants.States.CA})
public class TestRenewalEffectiveOnRewrite extends AutoCaSelectBaseTest {

	/**
	 * @author Dominykas Razgunas
	 * @name Test Renewal Effective On Rewrite
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Auto policy
	 * 3. Cancel policy
	 * 4. Change system date+10 days
	 * 5. Rewrite Policy
	 * 6. Renew Policy
	 * 7. Check that Renewal Effective and Base Date is equal to rewritten policies expiration and not original policies
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.HIGH, Groups.FUNCTIONAL, Groups.TIMEPOINT}, description = "Test Renewal Effective On Rewrite")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-22502")
	public void pas22502_TestRenewalEffectiveOnRewrite(@Optional("CA") String state) {

		LocalDateTime policyEffective = TimeSetterUtil.getInstance().getCurrentTime();
		String policyNumber = openAppAndCreatePolicy();

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		TimeSetterUtil.getInstance().nextPhase(policyEffective.plusDays(10));

		searchForPolicy(policyNumber);
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		String renewalEffective = PolicySummaryPage.getExpirationDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		policy.dataGather().start();
		policy.getDefaultView().fill(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"));

		policy.renew().perform();
		assertThat(new GeneralTab().getPolicyInfoAssetList().getAsset(AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)).hasValue(renewalEffective);
	}
}