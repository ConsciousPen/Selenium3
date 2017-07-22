package aaa.modules.policy.templates;

import aaa.common.Tab;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.ProposeActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.webdriver.controls.TextBox;

public abstract class ExampleQuoteActionAbstract extends PolicyBaseTest {

	// private String quoteNum = new String();

	public void testQuoteCreation() {
		mainApp().open();

		createCustomerIndividual();
		createQuote();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
	}

	public void testQuotePropose() {
		mainApp().open();

		getCopiedQuote();

		log.info("TEST: Click Cancel button on Propose screen for Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.propose().start();
		Tab.buttonCancel.click();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

		log.info("TEST: Propose Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.propose().start();

		if (getPolicyType().equals(PolicyType.HOME_SS)) {
			policy.propose().getView().getTab(ProposeActionTab.class).getAssetList().getControl(HomeSSMetaData.ProposeActionTab.NOTES.getLabel()).verify.enabled();
			ProposeActionTab.message.verify
					.value("Please note that once you click \"OK\" the documents will be queued for generation " + "and will be available for viewing within the folder structure as soon as they have been successfully processed. This usually takes 3 to 5 minutes.");
			policy.propose().getView().getTab(ProposeActionTab.class).getAssetList().getControl(HomeSSMetaData.ProposeActionTab.NOTES.getLabel(), TextBox.class).setValue("Comment");
		}
		//Another if for other product

		policy.propose().submit();
		// Efolder.isDocumentExist("Applications and Proposals", "New
		// Business");

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PROPOSED);
	}

	public void testQuoteIssue() {
		mainApp().open();

		getCopiedQuote();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("TEST: Issue Quote #" + policyNumber);
		policy.purchase(tdPolicy.getTestData("DataGather", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	public void testPolicyCreation() {
		mainApp().open();

		createCustomerIndividual();
		createQuote();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
		log.info("TEST: Created Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

		policy.copyQuote().perform(tdPolicy.getTestData("CopyFromQuote", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
		log.info("TEST: Copied Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

		policy.dataGather().start();

		policy.getDefaultView().fill(getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}

	/*
	 * private String openQuote() { if (StringUtils.isNotBlank(quoteNum)) {
	 * 
	 * SearchPage.openQuote(quoteNum); } else { quoteNum =
	 * PolicySummaryPage.labelPolicyStatus.getValue(); } log.info(String.format(
	 * "TEST: Quote #%s is opened",
	 * PolicySummaryPage.labelPolicyNumber.getValue())); return quoteNum; }
	 */

}
