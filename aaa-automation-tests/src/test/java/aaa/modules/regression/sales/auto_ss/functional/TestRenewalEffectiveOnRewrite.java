package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(statesExcept = {Constants.States.CA})
public class TestRenewalEffectiveOnRewrite extends AutoSSBaseTest {

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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22502")
	public void pas22502_TestRenewalEffectiveOnRewrite(@Optional("") String state) {
        ErrorTab errorTab = new ErrorTab();

		String policyNumber = openAppAndCreatePolicy();
        LocalDateTime policyEffective = PolicySummaryPage.getEffectiveDate();

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		TimeSetterUtil.getInstance().nextPhase(policyEffective.plusDays(10));

		searchForPolicy(policyNumber);
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		String renewalEffective = PolicySummaryPage.getExpirationDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		policy.dataGather().start();
		policy.getDefaultView().fillUpTo(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"), DocumentsAndBindTab.class, true);
		new DocumentsAndBindTab().submitTab();

		// Need to override error during the end of December when the cancellation date and base dates have different years
		if (errorTab.tableErrors.isPresent()) {
		    errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_10022001);
		    errorTab.override();
            new DocumentsAndBindTab().submitTab();
        }

        new PurchaseTab().fillTab(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy")).submitTab();
		policy.renew().perform();
		assertThat(new GeneralTab().getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)).hasValue(renewalEffective);
	}
}