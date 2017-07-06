/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.pup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.PersonalUmbrellaTab;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.PolicyActions.Bind;
import aaa.main.modules.policy.PolicyActions.Cancel;
import aaa.main.modules.policy.PolicyActions.CancelNotice;
import aaa.main.modules.policy.PolicyActions.ChangeBrokerRequest;
import aaa.main.modules.policy.PolicyActions.ChangeReinstatementLapse;
import aaa.main.modules.policy.PolicyActions.ChangeRenewalQuoteLapse;
import aaa.main.modules.policy.PolicyActions.CopyQuote;
import aaa.main.modules.policy.PolicyActions.DataGather;
import aaa.main.modules.policy.PolicyActions.DeclineByCompanyQuote;
import aaa.main.modules.policy.PolicyActions.DeclineByCustomerQuote;
import aaa.main.modules.policy.PolicyActions.DeleteCancelNotice;
import aaa.main.modules.policy.PolicyActions.DeletePendedTransaction;
import aaa.main.modules.policy.PolicyActions.DoNotRenew;
import aaa.main.modules.policy.PolicyActions.Endorse;
import aaa.main.modules.policy.PolicyActions.ManualRenew;
import aaa.main.modules.policy.PolicyActions.NonPremiumBearingEndorsement;
import aaa.main.modules.policy.PolicyActions.PendedEndorsementChange;
import aaa.main.modules.policy.PolicyActions.PolicyChangeRenewalLapse;
import aaa.main.modules.policy.PolicyActions.PolicyCopy;
import aaa.main.modules.policy.PolicyActions.PolicyDocGen;
import aaa.main.modules.policy.PolicyActions.PolicyInquiry;
import aaa.main.modules.policy.PolicyActions.PolicySpin;
import aaa.main.modules.policy.PolicyActions.PolicySplit;
import aaa.main.modules.policy.PolicyActions.Propose;
import aaa.main.modules.policy.PolicyActions.QuoteDocGen;
import aaa.main.modules.policy.PolicyActions.QuoteInquiry;
import aaa.main.modules.policy.PolicyActions.Reinstate;
import aaa.main.modules.policy.PolicyActions.RemoveDoNotRenew;
import aaa.main.modules.policy.PolicyActions.RemoveManualRenew;
import aaa.main.modules.policy.PolicyActions.RemoveSuspendQuote;
import aaa.main.modules.policy.PolicyActions.Renew;
import aaa.main.modules.policy.PolicyActions.RescindCancellation;
import aaa.main.modules.policy.PolicyActions.RollBackEndorsement;
import aaa.main.modules.policy.PolicyActions.Rewrite;
import aaa.main.modules.policy.PolicyActions.RollOn;
import aaa.main.modules.policy.PolicyActions.SuspendQuote;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.modules.policy.pup.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
import toolkit.datax.TestData;

/**
 * Concrete implementation for a specific entity type.
 * 
 * @category Generated
 */
public class PupPolicy implements IPolicy {

	protected static Logger log = LoggerFactory.getLogger(PupPolicy.class);

	private Workspace defaultView = new DefaultView();

	@Override
	public Workspace getDefaultView() {
		return defaultView;
	}

	@Override
	public void initiate() {
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
		QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.PUP.getName());
		QuoteSummaryPage.buttonAddNewQuote.click();
	}

	@Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, BindTab.class, false);
		BindTab.buttonSaveAndExit.click();

		log.info("Created Quote " + EntityLogger.getEntityHeader(EntityType.QUOTE));
	}

	@Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("Created Policy " + EntityLogger.getEntityHeader(EntityType.POLICY));
	}

	@Override
	public void createRenewal(TestData td) {
	    renew().performAndFill(td);
	}

	@Override
	public void createEndorsement(TestData td) {
	    endorse().performAndFill(td);
	}
	
	@Override
	public void calculatePremium() {
		dataGather().start();
		NavigationPage.toViewTab(PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesQuoteTab.btnCalculatePremium.click();
	}
	
    @Override
    public void calculatePremiumAndPurchase(TestData td) {
        calculatePremium();
        NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERWRITING_AND_APPROVAL.get());
        new UnderwritingAndApprovalTab().fillTab(td);
        NavigationPage.toViewTab(PersonalUmbrellaTab.BIND.get());
        new BindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
    }

	@Override
	public void copyPolicy(TestData td) {
		policyCopy().perform(td);
		calculatePremiumAndPurchase(td);
		log.info("Copy Policy " + EntityLogger.getEntityHeader(EntityType.POLICY));
	}

	@Override
	public void copyQuote(TestData td) {
		copyQuote().perform(td);
		calculatePremium();
		log.info("Copy Quote " + EntityLogger.getEntityHeader(EntityType.POLICY));
	}

	@Override
	public void purchase(TestData td) {
	    dataGather().start();
        NavigationPage.toViewTab(PersonalUmbrellaTab.BIND.get());
        new BindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
        log.info("Purchased Quote " + EntityLogger.getEntityHeader(EntityType.POLICY));
	}

	@Override
	public Endorse endorse() {
		return new PupPolicyActions.Endorse();
	}

	@Override
	public Renew renew() {
		return new PupPolicyActions.Renew();
	}

	@Override
	public Bind bind() {
		return new PupPolicyActions.Bind();
	}

	@Override
	public Cancel cancel() {
		return new PupPolicyActions.Cancel();
	}

	@Override
	public CancelNotice cancelNotice() {
		return new PupPolicyActions.CancelNotice();
	}

	@Override
	public ChangeBrokerRequest changeBrokerRequest() {
		return new PupPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public ChangeReinstatementLapse changeReinstatementLapse() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public CopyQuote copyQuote() {
		return new PupPolicyActions.CopyQuote();
	}

	@Override
	public DataGather dataGather() {
		return new PupPolicyActions.DataGather();
	}

	@Override
	public DeclineByCompanyQuote declineByCompanyQuote() {
		return new PupPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public DeclineByCustomerQuote declineByCustomerQuote() {
		return new PupPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public DeleteCancelNotice deleteCancelNotice() {
		return new PupPolicyActions.DeleteCancelNotice();
	}

	@Override
	public DeletePendedTransaction deletePendedTransaction() {
		return new PupPolicyActions.DeletePendingTransaction();
	}

	@Override
	public DoNotRenew doNotRenew() {
		return new PupPolicyActions.DoNotRenew();
	}

	@Override
	public ManualRenew manualRenew() {
		return new PupPolicyActions.ManualRenew();
	}

	@Override
	public NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new PupPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PendedEndorsementChange pendedEndorsementChange() {
		return new PupPolicyActions.PendedEndorsementChange();
	}

	@Override
	public PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyCopy policyCopy() {
		return new PupPolicyActions.PolicyCopy();
	}

	@Override
	public PolicyInquiry policyInquiry() {
		return new PupPolicyActions.PolicyInquiry();
	}

	@Override
	public PolicySpin policySpin() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicySplit policySplit() {
		throw new UnsupportedOperationException("Action policySplit is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyDocGen policyDocGen() {
		return new PupPolicyActions.PolicyDocGenFlow();
	}

	@Override
	public Propose propose() {
		return new PupPolicyActions.Propose();
	}

	@Override
	public QuoteInquiry quoteInquiry() {
		return new PupPolicyActions.QuoteInquiry();
	}

	@Override
	public Reinstate reinstate() {
		return new PupPolicyActions.Reinstate();
	}

	@Override
	public RemoveDoNotRenew removeDoNotRenew() {
		return new PupPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public RemoveManualRenew removeManualRenew() {
		return new PupPolicyActions.RemoveManualRenew();
	}

	@Override
	public RemoveSuspendQuote removeSuspendQuote() {
		return new PupPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public RescindCancellation rescindCancellation() {
		return new PupPolicyActions.RescindCancellation();
	}

	@Override
	public RollBackEndorsement rollBackEndorsement() {
		return new PupPolicyActions.RollBackEndorsement();
	}

	@Override
	public RollOn rollOn() {
		return new PupPolicyActions.RollOn();
	}

	@Override
	public SuspendQuote suspendQuote() {
		return new PupPolicyActions.SuspendQuote();
	}

	@Override
	public QuoteDocGen quoteDocGen() {
		return new PupPolicyActions.QuoteDocGenFlow();
	}

	@Override
	public Rewrite rewrite() {
		return new PupPolicyActions.Rewrite();
	}
}
