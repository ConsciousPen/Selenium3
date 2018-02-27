package aaa.modules.regression.sales.template;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.Tab;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.actiontabs.GenerateProposalActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

public abstract class QuoteActionAbstract extends PolicyBaseTest {

	public void testQuoteCreation() {
		mainApp().open();

		createCustomerIndividual();
		createQuote();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
	}

	public void testQuoteIssue() {
		mainApp().open();

		createCustomerIndividual();
		createQuote();

		log.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.purchase(getPolicyTD("DataGather", "TestData"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	public void testQuotePropose() {
		mainApp().open();

		createCustomerIndividual();
		createQuote();

		log.info("TEST: Click Cancel button on Propose screen for Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.propose().start();
		Tab.buttonCancel.click();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

		log.info("TEST: Propose Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.propose().start();
		if (getPolicyType().equals(PolicyType.HOME_CA_HO3)) {
			policy.propose().getView().getTab(GenerateProposalActionTab.class).getAssetList().getAsset(HomeSSMetaData.ProposeActionTab.NOTES.getLabel()).verify.enabled();
			GenerateProposalActionTab.message.verify.value("Please note that once you click \"OK\" the documents will be queued for generation " + "and will be available for viewing within the folder structure as soon as they have been successfully processed. This usually takes 3 to 5 minutes.");
		}

		if (getPolicyType().equals(PolicyType.HOME_SS_HO3)) {
			policy.propose().getView().getTab(aaa.main.modules.policy.home_ss.actiontabs.ProposeActionTab.class).getAssetList().getAsset(HomeSSMetaData.ProposeActionTab.NOTES.getLabel()).verify.enabled();
			aaa.main.modules.policy.home_ss.actiontabs.ProposeActionTab.message.verify.value("Please note that once you click \"OK\" the documents will be queued for generation " + "and will be available for viewing within the folder structure as soon as they have been successfully processed. This usually takes 3 to 5 minutes.");
		}
		
		if (getPolicyType().equals(PolicyType.AUTO_SS)) {
			policy.propose().getView().getTab(aaa.main.modules.policy.auto_ss.actiontabs.ProposeActionTab.class).getAssetList().getAsset(HomeSSMetaData.ProposeActionTab.NOTES.getLabel()).verify.enabled();
			aaa.main.modules.policy.auto_ss.actiontabs.ProposeActionTab.message.verify.value("Please note that once you click \"OK\" the documents will be queued for generation " + "and will be available for viewing within the folder structure as soon as they have been successfully processed. This usually takes 3 to 5 minutes.");
		}
		
		policy.propose().submit();
		// Efolder.isDocumentExist("Applications and Proposals",
		// "New Business");

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PROPOSED);
	}

	 public void testQuoteDeclineByCustomer() {
	       mainApp().open();
	       
	       createCustomerIndividual();
		   createQuote();

	       log.info("TEST: Decline by Customer Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

	       policy.declineByCustomerQuote().perform(getPolicyTD("DeclineByCustomer", "TestData"));
	        
	       PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
	  }
	  

	  public void testQuoteDeclineByCompany() {
	       mainApp().open();
	    	
	       createCustomerIndividual();
	       createQuote();

	       log.info("TEST: Decline by Company Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
	       policy.declineByCompanyQuote().perform(getPolicyTD("DeclineByCompany", "TestData"));


	       PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.COMPANY_DECLINED);

	   }
	      
	   
	   public void testQuoteCopy() {
	       mainApp().open();
	    	
	       createCustomerIndividual();
	       createQuote();

	       log.info("TEST: Copy quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
	        
	       PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

		   policy.copyQuote().perform(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromQuote", "TestData"));

	       assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);

	   }


}
