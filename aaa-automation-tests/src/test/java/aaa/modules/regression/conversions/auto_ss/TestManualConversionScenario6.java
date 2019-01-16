package aaa.modules.regression.conversions.auto_ss;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

/**
 * @author Tatsiana Saltsevich
 * @name Manual Hybrid Conversion Docs Verification ("D-T-AU-SS-MT-957-CNV")
 * Hybrid CONVERSION
 * Do not generate pre-renewal notice (AAPRN1MT) for those policies in Customer/Company Declined status
 **/

public class TestManualConversionScenario6 extends AutoSSBaseTest {
	@Parameters({"state"})
	@StateList(states = Constants.States.MT)
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS)
	public void manualConversionDocsScenario6(@Optional("MT") String state) {
		LocalDateTime renewalDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(55);
		TestData policyTd = getConversionPolicyDefaultTD().adjust(DocumentsAndBindTab.class.getSimpleName(), getTestSpecificTD("DocumentsAndBindTab"));
		//1. Login with user role = E34 having privilege 'Initiate Renewal Entry' and retrieve the customer created above.
		mainApp().open(getLoginTD(Constants.UserGroups.L41));
		createCustomerIndividual();
		//2. (R-55) Select the action "Initiate Renewal Entry" from 'Select Action:' dropdown box on Customer UI and click on the Go button.
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalDate);
		//3. Open the renewal image in Data Gathering mode. Enter mandatory data on all pages.
		policy.getDefaultView().fill(policyTd);
		Tab.buttonBack.click();
		//6. Navigate to policy consolidated view, click on the renewal image Button
		String policyNum = PolicySummaryPage.getPolicyNumber();
		PolicySummaryPage.buttonRenewals.click();
		//Status of the Renewal Image is in 'Premium calculated'
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		//8. On the above created renewal image, process 'Customer decline'
		policy.declineByCustomerQuote().perform(getPolicyTD("DeclineByCustomer", "TestData_Plus1Year"));
		PolicySummaryPage.buttonRenewals.click();
		//Status of the renewal image = Customer declined
		CustomAssertions.assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell(4)).hasValue(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);

		//(R-50) Run the batch jobs: AAAPreRenewalNoticeAsyncJob, aaaDocGen Job
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPreRenewalLetterGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//'Pre Renewal Notice' AAPRN1MT is NOT generated in renewal E-folder
		DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AAPRN1MT, policyNum, PRE_RENEWAL, false);
	}
}
