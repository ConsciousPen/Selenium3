package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum.SearchBy;
import aaa.main.enums.SearchEnum.SearchFor;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.CancelNoticeActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.TextBox;

/**
 * @author Ryan Yu
 * @name Test Cancellation Notice with Cancellation
 * @scenario
 * 1. Create Customer
 * 2. Create Policy
 * 3. Cancel Notice for Policy
 * 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * 5. Shift Time to Days Of Notice + Cancellation Date
 * 6. Run 'aaaCancellationConfirmationAsyncJob'
 * 7. Verify Policy status is 'Policy Cancelled'
 * 8. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 * @details
 */
public class PolicyCancelNoticeWithCancellation extends PolicyBaseTest {
	protected String policyNumber;
	protected int daysOfNotice;

	protected void TC01_CreatePolicyAndCancelNotice() {
		mainApp().open();

		policyNumber = getCopiedPolicy();

		log.info("Cancel Notice for Policy #" + policyNumber);
		policy.cancelNotice().start();
		policy.cancelNotice().getView().fill(getPolicyTD("CancelNotice", "TestData"));
		if (getPolicyType().equals(PolicyType.HOME_SS_HO3)){
			daysOfNotice = Integer.parseInt(policy.cancelNotice().getView().getTab(CancelNoticeActionTab.class).getAssetList().getAsset(HomeSSMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE.getLabel(), TextBox.class).getValue());
		}
		if (getPolicyType().equals(PolicyType.PUP)){
			daysOfNotice = Integer.parseInt(policy.cancelNotice().getView().getTab(aaa.main.modules.policy.pup.actiontabs.CancelNoticeActionTab.class).getAssetList().getAsset(PersonalUmbrellaMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE.getLabel(), TextBox.class).getValue());
		}
		if (getPolicyType().equals(PolicyType.AUTO_SS)){
			daysOfNotice = Integer.parseInt(policy.cancelNotice().getView().getTab(aaa.main.modules.policy.auto_ss.actiontabs.CancelNoticeActionTab.class).getAssetList().getAsset(AutoSSMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE.getLabel(), TextBox.class).getValue());
		}
		policy.cancelNotice().submit();
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
	}

	protected void TC02_CancellationPolicy() {
		log.info("TEST: Cancellation Policy #" + policyNumber);
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(DateTimeUtils.getCurrentDateTime().plusDays(daysOfNotice));
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);

		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);

		mainApp().open();
		SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, policyNumber);

		assertThat(PolicySummaryPage.labelCancelNotice).isPresent(false);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}
}
