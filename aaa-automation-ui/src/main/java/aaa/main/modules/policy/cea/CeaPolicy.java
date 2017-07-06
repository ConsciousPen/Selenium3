/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.cea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.CaliforniaEarthquakeTab;
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
import aaa.main.modules.policy.cea.defaulttabs.BindTab;
import aaa.main.modules.policy.cea.defaulttabs.PremiumCoveragesTab;
import aaa.main.modules.policy.cea.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.cea.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
import toolkit.datax.TestData;

/**
 * Concrete implementation for a specific entity type.
 * 
 * @category Generated
 */
public class CeaPolicy implements IPolicy {

	protected static Logger log = LoggerFactory.getLogger(CeaPolicy.class);

	private Workspace defaultView = new DefaultView();

	@Override
	public Workspace getDefaultView() {
		return defaultView;
	}

	@Override
	public void initiate() {
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
		QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.CEA.getName());
		QuoteSummaryPage.buttonAddNewQuote.click();
	}

	@Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, BindTab.class, false);
		BindTab.buttonSaveAndExit.click();
		log.info("Created " + EntityLogger.getEntityHeader(EntityType.QUOTE));
	}

	@Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("Created " + EntityLogger.getEntityHeader(EntityType.POLICY));
	}

	public void createQuoteFromCa(TestData td) {
		getDefaultView().fillUpTo(td, BindTab.class, false);
		BindTab.buttonSaveAndExit.click();
		log.info("Created " + EntityLogger.getEntityHeader(EntityType.QUOTE));
	}

	public void createPolicyFromCa(TestData td) {
		getDefaultView().fill(td);
		log.info("Created " + EntityLogger.getEntityHeader(EntityType.POLICY));
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
    public void purchase(TestData td) {
        dataGather().start();
        NavigationPage.toViewTab(CaliforniaEarthquakeTab.BIND.get());
        new BindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
        log.info("Issue " + EntityLogger.getEntityHeader(EntityType.POLICY));
    }

	@Override
	public void calculatePremium() {
		dataGather().start();
		NavigationPage.toViewTab(CaliforniaEarthquakeTab.PREMIUMS_AND_COVERAGES.get());
		PremiumCoveragesTab.btnCalculatePremium.click();
	}
	
	@Override
    public void calculatePremiumAndPurchase(TestData td) {
        calculatePremium();
        NavigationPage.toViewTab(CaliforniaEarthquakeTab.BIND.get());
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
	public Endorse endorse() {
		return new CeaPolicyActions.Endorse();
	}

	@Override
	public Renew renew() {
		return new CeaPolicyActions.Renew();
	}

	@Override
	public Bind bind() {
		return new CeaPolicyActions.Bind();
	}

	@Override
	public Cancel cancel() {
		return new CeaPolicyActions.Cancel();
	}

	@Override
	public CancelNotice cancelNotice() {
		return new CeaPolicyActions.CancelNotice();
	}

	@Override
	public ChangeBrokerRequest changeBrokerRequest() {
		return new CeaPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public ChangeReinstatementLapse changeReinstatementLapse() {
		throw new UnsupportedOperationException("Action changeReinstatementLapse is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		throw new UnsupportedOperationException("Action changeRenewalQuoteLapse is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public CopyQuote copyQuote() {
		return new CeaPolicyActions.CopyQuote();
	}

	@Override
	public DataGather dataGather() {
		return new CeaPolicyActions.DataGather();
	}

	@Override
	public DeclineByCompanyQuote declineByCompanyQuote() {
		return new CeaPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public DeclineByCustomerQuote declineByCustomerQuote() {
		return new CeaPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public DeleteCancelNotice deleteCancelNotice() {
		return new CeaPolicyActions.DeleteCancelNotice();
	}

	@Override
	public DeletePendedTransaction deletePendedTransaction() {
		return new CeaPolicyActions.DeletePendedTransaction();
	}

	@Override
	public DoNotRenew doNotRenew() {
		return new CeaPolicyActions.DoNotRenew();
	}

	@Override
	public ManualRenew manualRenew() {
		return new CeaPolicyActions.ManualRenew();
	}

	@Override
	public NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new CeaPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PendedEndorsementChange pendedEndorsementChange() {
		return new CeaPolicyActions.PendedEndorsementChange();
	}

	@Override
	public PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		throw new UnsupportedOperationException("Action policyChangeRenewalLapse is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyCopy policyCopy() {
		return new CeaPolicyActions.PolicyCopy();
	}

	@Override
	public PolicyDocGen policyDocGen() {
		return new CeaPolicyActions.PolicyDocGen();
	}

	@Override
	public PolicyInquiry policyInquiry() {
		return new CeaPolicyActions.PolicyInquiry();
	}

	@Override
	public PolicySpin policySpin() {
		return new CeaPolicyActions.PolicySpin();
	}

	@Override
	public PolicySplit policySplit() {
		return new CeaPolicyActions.PolicySplit();
	}

	@Override
	public Rewrite rewrite() {
		return new CeaPolicyActions.Rewrite();
	}

	@Override
	public Propose propose() {
		return new CeaPolicyActions.Propose();
	}

	@Override
	public QuoteDocGen quoteDocGen() {
		return new CeaPolicyActions.QuoteDocGen();
	}

	@Override
	public QuoteInquiry quoteInquiry() {
		return new CeaPolicyActions.QuoteInquiry();
	}

	@Override
	public Reinstate reinstate() {
		return new CeaPolicyActions.Reinstate();
	}

	@Override
	public RemoveDoNotRenew removeDoNotRenew() {
		return new CeaPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public RemoveManualRenew removeManualRenew() {
		return new CeaPolicyActions.RemoveManualRenew();
	}

	@Override
	public RemoveSuspendQuote removeSuspendQuote() {
		return new CeaPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public RescindCancellation rescindCancellation() {
		return new CeaPolicyActions.RescindCancellation();
	}

	@Override
	public RollBackEndorsement rollBackEndorsement() {
		return new CeaPolicyActions.RollBackEndorsement();
	}

	@Override
	public RollOn rollOn() {
		return new CeaPolicyActions.RollOn();
	}

	@Override
	public SuspendQuote suspendQuote() {
		return new CeaPolicyActions.SuspendQuote();
	}
}
