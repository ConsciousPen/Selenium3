/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.cea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.EntityLogger;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.PolicyType;
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
		log.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
	}

	@Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
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
	    NavigationPage.toViewTab(NavigationEnum.CaliforniaEarthquakeTab.BIND.get());
	    new BindTab().submitTab();
	    new PurchaseTab().fillTab(td).submitTab();
	    log.info("Issue " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
    }

	@Override
	public void calculatePremium(TestData td) {
		dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.CaliforniaEarthquakeTab.PREMIUMS_AND_COVERAGES.get());
		PremiumCoveragesTab.btnCalculatePremium.click();
	}

	@Override
    public void calculatePremiumAndPurchase(TestData td) {
        calculatePremium(td);
		NavigationPage.toViewTab(NavigationEnum.CaliforniaEarthquakeTab.BIND.get());
		new BindTab().submitTab();
		new PurchaseTab().fillTab(td).submitTab();
    }

	@Override
	public void copyPolicy(TestData td) {
		policyCopy().perform(td);
		calculatePremiumAndPurchase(td);
		log.info("Copy Policy " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
	}

	@Override
	public PolicyActions.Endorse endorse() {
		return new CeaPolicyActions.Endorse();
	}

	@Override
	public PolicyActions.Renew renew() {
		return new CeaPolicyActions.Renew();
	}

	@Override
	public PolicyActions.Bind bind() {
		return new CeaPolicyActions.Bind();
	}

	@Override
	public PolicyActions.Cancel cancel() {
		return new CeaPolicyActions.Cancel();
	}

	@Override
	public PolicyActions.CancelNotice cancelNotice() {
		return new CeaPolicyActions.CancelNotice();
	}

	@Override
	public PolicyActions.ChangeBrokerRequest changeBrokerRequest() {
		return new CeaPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public PolicyActions.ChangeReinstatementLapse changeReinstatementLapse() {
		throw new UnsupportedOperationException("Action changeReinstatementLapse is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyActions.ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		throw new UnsupportedOperationException("Action changeRenewalQuoteLapse is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyActions.CopyQuote copyQuote() {
		return new CeaPolicyActions.CopyQuote();
	}

	@Override
	public PolicyActions.DataGather dataGather() {
		return new CeaPolicyActions.DataGather();
	}

	@Override
	public PolicyActions.DeclineByCompanyQuote declineByCompanyQuote() {
		return new CeaPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public PolicyActions.DeclineByCustomerQuote declineByCustomerQuote() {
		return new CeaPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public PolicyActions.DeleteCancelNotice deleteCancelNotice() {
		return new CeaPolicyActions.DeleteCancelNotice();
	}

	@Override
	public PolicyActions.DeletePendedTransaction deletePendedTransaction() {
		return new CeaPolicyActions.DeletePendedTransaction();
	}

	@Override
	public PolicyActions.DoNotRenew doNotRenew() {
		return new CeaPolicyActions.DoNotRenew();
	}

	@Override
	public PolicyActions.ManualRenew manualRenew() {
		return new CeaPolicyActions.ManualRenew();
	}

	@Override
	public PolicyActions.NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new CeaPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PolicyActions.PendedEndorsementChange pendedEndorsementChange() {
		return new CeaPolicyActions.PendedEndorsementChange();
	}

	@Override
	public PolicyActions.PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		throw new UnsupportedOperationException("Action policyChangeRenewalLapse is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyActions.PolicyCopy policyCopy() {
		throw new UnsupportedOperationException("Action 'Copy from Policy' is not supported by California Earthquake product");
	}

	@Override
	public PolicyActions.PolicyDocGen policyDocGen() {
		return new CeaPolicyActions.PolicyDocGen();
	}

	@Override
	public PolicyActions.PolicyInquiry policyInquiry() {
		return new CeaPolicyActions.PolicyInquiry();
	}

	@Override
	public PolicyActions.PolicySpin policySpin() {
		return new CeaPolicyActions.PolicySpin();
	}

	@Override
	public PolicyActions.PolicySplit policySplit() {
		return new CeaPolicyActions.PolicySplit();
	}

	@Override
	public PolicyActions.Rewrite rewrite() {
		return new CeaPolicyActions.Rewrite();
	}

	@Override
	public PolicyActions.Propose propose() {
		return new CeaPolicyActions.Propose();
	}

	@Override
	public PolicyActions.QuoteDocGen quoteDocGen() {
		return new CeaPolicyActions.QuoteDocGen();
	}

	@Override
	public PolicyActions.QuoteInquiry quoteInquiry() {
		return new CeaPolicyActions.QuoteInquiry();
	}

	@Override
	public PolicyActions.Reinstate reinstate() {
		return new CeaPolicyActions.Reinstate();
	}

	@Override
	public PolicyActions.RemoveDoNotRenew removeDoNotRenew() {
		return new CeaPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public PolicyActions.RemoveManualRenew removeManualRenew() {
		return new CeaPolicyActions.RemoveManualRenew();
	}

	@Override
	public PolicyActions.RemoveSuspendQuote removeSuspendQuote() {
		return new CeaPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public PolicyActions.RescindCancellation rescindCancellation() {
		return new CeaPolicyActions.RescindCancellation();
	}

	@Override
	public PolicyActions.RollBackEndorsement rollBackEndorsement() {
		return new CeaPolicyActions.RollBackEndorsement();
	}

	@Override
	public PolicyActions.RollOn rollOn() {
		return new CeaPolicyActions.RollOn();
	}

	@Override
	public PolicyActions.SuspendQuote suspendQuote() {
		return new CeaPolicyActions.SuspendQuote();
	}

	@Override
	public PolicyActions.UpdateRulesOverride updateRulesOverride() {
		throw new UnsupportedOperationException("Action is not implemented yet");
	}

	public void createQuoteFromCa(TestData td) {
		getDefaultView().fillUpTo(td, BindTab.class, false);
		BindTab.buttonSaveAndExit.click();
		log.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
	}

	public void createPolicyFromCa(TestData td) {
		getDefaultView().fill(td);
		log.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
	}

}
