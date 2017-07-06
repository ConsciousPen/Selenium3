/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.auto_ca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.AutoCaTab;
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
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
import toolkit.datax.TestData;

/**
 * Concrete implementation for a specific entity type.
 * 
 * @category Generated
 */
public class AutoCaPolicy implements IPolicy {

	protected static Logger log = LoggerFactory.getLogger(AutoCaPolicy.class);

	private Workspace defaultView = new DefaultView();

	@Override
	public Workspace getDefaultView() {
		return defaultView;
	}

	@Override
	public void initiate() {
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
		QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.AUTO_CA.getName());
		QuoteSummaryPage.buttonAddNewQuote.click();
	}

	@Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, DocumentsAndBindTab.class, false);
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		log.info("QUOTE CREATED: " + EntityLogger.getEntityHeader(EntityType.QUOTE));
	}

	@Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("POLICY CREATED: " + EntityLogger.getEntityHeader(EntityType.POLICY));
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
		NavigationPage.toViewTab(AutoCaTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonCalculatePremium.click();
	}
	
	@Override
    public void calculatePremiumAndPurchase(TestData td) {
        calculatePremium();
        NavigationPage.toViewTab(AutoCaTab.BIND.get());
        new DocumentsAndBindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
    }
	
	@Override
    public void purchase(TestData td) {
        dataGather().start();
        NavigationPage.toViewTab(AutoCaTab.BIND.get());
        new DocumentsAndBindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
        log.info("Purchased Quote " + EntityLogger.getEntityHeader(EntityType.POLICY));
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
	public Endorse endorse() {
		return new AutoCaPolicyActions.Endorse();
	}

	@Override
	public Renew renew() {
		return new AutoCaPolicyActions.Renew();
	}

	@Override
	public Cancel cancel() {
		return new AutoCaPolicyActions.Cancel();
	}

	@Override
	public CancelNotice cancelNotice() {
		return new AutoCaPolicyActions.CancelNotice();
	}

	@Override
	public ChangeBrokerRequest changeBrokerRequest() {
		return new AutoCaPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public ChangeReinstatementLapse changeReinstatementLapse() {
		throw new UnsupportedOperationException("Action changeReinstatementLapse is not defined in entity \"AU_EP1\"");
	}

	@Override
	public ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		throw new UnsupportedOperationException("Action changeRenewalQuoteLapse is not defined in entity \"AU_EP1\"");
	}

	@Override
	public PolicyCopy policyCopy() {
		return new AutoCaPolicyActions.PolicyCopy();
	}

	@Override
	public CopyQuote copyQuote() {
		return new AutoCaPolicyActions.CopyQuote();
	}

	@Override
	public DataGather dataGather() {
		return new AutoCaPolicyActions.DataGather();
	}

	@Override
	public DeclineByCompanyQuote declineByCompanyQuote() {
		return new AutoCaPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public DeclineByCustomerQuote declineByCustomerQuote() {
		return new AutoCaPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public DeleteCancelNotice deleteCancelNotice() {
		return new AutoCaPolicyActions.DeleteCancelNotice();
	}

	@Override
	public DeletePendedTransaction deletePendedTransaction() {
		return new AutoCaPolicyActions.DeletePendedTransaction();
	}

	@Override
	public DoNotRenew doNotRenew() {
		return new AutoCaPolicyActions.DoNotRenew();
	}

	@Override
	public ManualRenew manualRenew() {
		return new AutoCaPolicyActions.ManualRenew();
	}

	@Override
	public NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new AutoCaPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PendedEndorsementChange pendedEndorsementChange() {
		return new AutoCaPolicyActions.PendedEndorsementChange();
	}

	@Override
	public PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		throw new UnsupportedOperationException("Action policyChangeRenewalLapse is not defined in entity \"AU_EP1\"");
	}

	@Override
	public PolicyInquiry policyInquiry() {
		return new AutoCaPolicyActions.PolicyInquiry();
	}

	@Override
	public PolicySpin policySpin() {
		return new AutoCaPolicyActions.PolicySpin();
	}

	@Override
	public PolicySplit policySplit() {
		return new AutoCaPolicyActions.PolicySplit();
	}

	@Override
	public Propose propose() {
		return new AutoCaPolicyActions.Propose();
	}

	@Override
	public QuoteInquiry quoteInquiry() {
		return new AutoCaPolicyActions.QuoteInquiry();
	}

	@Override
	public Reinstate reinstate() {
		return new AutoCaPolicyActions.Reinstate();
	}

	@Override
	public RemoveDoNotRenew removeDoNotRenew() {
		return new AutoCaPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public RemoveManualRenew removeManualRenew() {
		return new AutoCaPolicyActions.RemoveManualRenew();
	}

	@Override
	public RemoveSuspendQuote removeSuspendQuote() {
		return new AutoCaPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public RescindCancellation rescindCancellation() {
		return new AutoCaPolicyActions.RescindCancellation();
	}

	@Override
	public RollBackEndorsement rollBackEndorsement() {
		return new AutoCaPolicyActions.RollBackEndorsement();
	}

	@Override
	public SuspendQuote suspendQuote() {
		return new AutoCaPolicyActions.SuspendQuote();
	}

	@Override
	public Bind bind() {
		return null;
	}

	@Override
	public PolicyDocGen policyDocGen() {
		return null;
	}

	@Override
	public Rewrite rewrite() {
		return new AutoCaPolicyActions.Rewrite();
	}

	@Override
	public QuoteDocGen quoteDocGen() {
		return null;
	}

	@Override
	public RollOn rollOn() {
		return null;
	}
}
