package aaa.modules.regression.conversions.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.regression.conversions.template.ManualConversionTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

@StateList(states = {Constants.States.KY, Constants.States.WV})

public class TestCancelledConversionRewrite extends ManualConversionTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

    /**
     * * @author Dominykas Razgunas
     *
     *@name Test Paperless Preferences properties and Inquiry mode
     *@scenario
     *1. Create customer.
     *2. Create Auto SS NY Conversion Policy.
     *3. Propose and Issue Policy.
     *4. Cancel Policy on its effective date.
     *5. Rewrite Policy.
     *6. Initiate Data gather on rewritten quote.
     *7. Navigate to P&C.
     *8. Calculate premium.
	 * 9. Check that no Error is fired
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT}, description = "Test Premium Calculation for a rewritten conversion policy KY/WV")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-25298")
    public void pas25298_calculatePremiumForRewrittenConversionPolicy(@Optional("WV") String state) {

    	// Days should be the same as in manualRenewalEntryToActivePolicy so that cancelation would work on effective date of the policy
		LocalDateTime effDate = TimeSetterUtil.getInstance().getPhaseStartTime().plusDays(45);
		manualRenewalEntryToActivePolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData").adjust(TestData.makeKeyPath("CancellationActionTab", "Cancel Date"), effDate.format(DateTimeUtils.MM_DD_YYYY)));
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		new RatingDetailReportsTab().fillTab(getPolicyTD());
		new PremiumAndCoveragesTab().calculatePremium();
		assertThat(new PremiumAndCoveragesTab().btnCalculatePremium()).isPresent();
    }
}