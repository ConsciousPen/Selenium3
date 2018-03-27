/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.auto_ca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.utils.EntityLogger;
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
		QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.AUTO_CA_SELECT.getName());
		QuoteSummaryPage.buttonAddNewQuote.click();
	}

	@Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, DocumentsAndBindTab.class, false);
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		log.info("QUOTE CREATED: " + EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
	}

	@Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("POLICY CREATED: {}", EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
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
	public void createPriorTermEndorsement(TestData td) {
		priorTermEndorsement().performAndFill(td);
	}

	@Override
	public void calculatePremium(TestData td) {
		dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.MEMBERSHIP.get());
		new MembershipTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().fillTab(td);
	}

	@Override
	public void calculatePremiumAndPurchase(TestData td) {
		calculatePremium(td);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
		new DriverActivityReportsTab().fillTab(td);
		//TODO workaround for PAS-10786
		//NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		new DriverActivityReportsTab().submitTab();
		new DocumentsAndBindTab().fillTab(td).submitTab();
		new PurchaseTab().fillTab(td).submitTab();
	}

	@Override
	public void purchase(TestData td) {
		dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().fillTab(td).submitTab();
		new PurchaseTab().fillTab(td).submitTab();
		log.info("Purchased Quote " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
	}

	@Override
	public void copyPolicy(TestData td) {
		policyCopy().perform(td);
		calculatePremiumAndPurchase(td);
		log.info("Copy Policy " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
	}

	@Override
	public PolicyActions.Endorse endorse() {
		return new AutoCaPolicyActions.Endorse();
	}

	@Override
	public PolicyActions.PriorTermEndorsement priorTermEndorsement() {
		return new AutoCaPolicyActions.PriorTermEndorsement();
	}

	@Override
	public PolicyActions.Renew renew() {
		return new AutoCaPolicyActions.Renew();
	}

	@Override
	public PolicyActions.Cancel cancel() {
		return new AutoCaPolicyActions.Cancel();
	}

	@Override
	public PolicyActions.CancelNotice cancelNotice() {
		return new AutoCaPolicyActions.CancelNotice();
	}

	@Override
	public PolicyActions.ChangeBrokerRequest changeBrokerRequest() {
		return new AutoCaPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public PolicyActions.ChangeReinstatementLapse changeReinstatementLapse() {
		throw new UnsupportedOperationException("Action changeReinstatementLapse is not defined in entity \"AU_EP1\"");
	}

	@Override
	public PolicyActions.ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		throw new UnsupportedOperationException("Action changeRenewalQuoteLapse is not defined in entity \"AU_EP1\"");
	}

	@Override
	public PolicyActions.PolicyCopy policyCopy() {
		return new AutoCaPolicyActions.PolicyCopy();
	}

	@Override
	public PolicyActions.CopyQuote copyQuote() {
		return new AutoCaPolicyActions.CopyQuote();
	}

	@Override
	public PolicyActions.DataGather dataGather() {
		return new AutoCaPolicyActions.DataGather();
	}

	@Override
	public PolicyActions.DeclineByCompanyQuote declineByCompanyQuote() {
		return new AutoCaPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public PolicyActions.DeclineByCustomerQuote declineByCustomerQuote() {
		return new AutoCaPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public PolicyActions.DeleteCancelNotice deleteCancelNotice() {
		return new AutoCaPolicyActions.DeleteCancelNotice();
	}

	@Override
	public PolicyActions.DeletePendedTransaction deletePendedTransaction() {
		return new AutoCaPolicyActions.DeletePendedTransaction();
	}

	@Override
	public PolicyActions.DeletePendingRenwals deletePendingRenwals() {
		return new AutoCaPolicyActions.DeletePendingRenwals();
	}

	@Override
	public PolicyActions.DoNotRenew doNotRenew() {
		return new AutoCaPolicyActions.DoNotRenew();
	}

	@Override
	public PolicyActions.ManualRenew manualRenew() {
		return new AutoCaPolicyActions.ManualRenew();
	}

	@Override
	public PolicyActions.NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new AutoCaPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PolicyActions.PendedEndorsementChange pendedEndorsementChange() {
		return new AutoCaPolicyActions.PendedEndorsementChange();
	}

	@Override
	public PolicyActions.PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		throw new UnsupportedOperationException("Action policyChangeRenewalLapse is not defined in entity \"AU_EP1\"");
	}

	@Override
	public PolicyActions.PolicyInquiry policyInquiry() {
		return new AutoCaPolicyActions.PolicyInquiry();
	}

	@Override
	public PolicyActions.PolicySpin policySpin() {
		return new AutoCaPolicyActions.PolicySpin();
	}

	@Override
	public PolicyActions.PolicySplit policySplit() {
		return new AutoCaPolicyActions.PolicySplit();
	}

	@Override
	public PolicyActions.Propose propose() {
		return new AutoCaPolicyActions.Propose();
	}

	@Override
	public PolicyActions.QuoteInquiry quoteInquiry() {
		return new AutoCaPolicyActions.QuoteInquiry();
	}

	@Override
	public PolicyActions.Reinstate reinstate() {
		return new AutoCaPolicyActions.Reinstate();
	}

	@Override
	public PolicyActions.RemoveDoNotRenew removeDoNotRenew() {
		return new AutoCaPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public PolicyActions.RemoveManualRenew removeManualRenew() {
		return new AutoCaPolicyActions.RemoveManualRenew();
	}

	@Override
	public PolicyActions.RemoveSuspendQuote removeSuspendQuote() {
		return new AutoCaPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public PolicyActions.RescindCancellation rescindCancellation() {
		return new AutoCaPolicyActions.RescindCancellation();
	}

	@Override
	public PolicyActions.RollBackEndorsement rollBackEndorsement() {
		return new AutoCaPolicyActions.RollBackEndorsement();
	}

	@Override
	public PolicyActions.SuspendQuote suspendQuote() {
		return new AutoCaPolicyActions.SuspendQuote();
	}

	@Override
	public PolicyActions.UpdateRulesOverride updateRulesOverride() {
		return new AutoCaPolicyActions.UpdateRulesOverride();
	}

	@Override
	public PolicyActions.Bind bind() {
		return null;
	}

	@Override
	public PolicyActions.PolicyDocGen policyDocGen() {
		return new AutoCaPolicyActions.PolicyDocGen();
	}

	@Override
	public PolicyActions.Rewrite rewrite() {
		return new AutoCaPolicyActions.Rewrite();
	}

	@Override
	public PolicyActions.QuoteDocGen quoteDocGen() {
		return new AutoCaPolicyActions.QuoteDocGen();
	}

	@Override
	public PolicyActions.RollOn rollOn() {
		//return null;
		return new AutoCaPolicyActions.RollOn();
	}

	@Override
	public PolicyActions.ManualRenewalWithOrWithoutLapse manualRenewalWithOrWithoutLapse() {
		throw new UnsupportedOperationException("Action manualRenewalWithOrWithoutLapse is not defined for Auto Policy");
	}

}
