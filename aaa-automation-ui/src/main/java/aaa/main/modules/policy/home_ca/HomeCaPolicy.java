/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.home_ca;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.modules.policy.home_ca.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.utils.EntityLogger;
import toolkit.datax.TestData;

/**
 * Concrete implementation for a specific entity type.
 * @category Generated
 */
public class HomeCaPolicy implements IPolicy {

	protected static Logger log = LoggerFactory.getLogger(HomeCaPolicy.class);

	private Workspace defaultView = new DefaultView();

	@Override
	public Workspace getDefaultView() {
		return defaultView;
	}

	@Override
	public void initiate() {
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
		new QuoteSummaryPage().initiateQuote(PolicyType.HOME_CA_HO3);
	}

	@Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, BindTab.class, true);
		BindTab.buttonSaveAndExit.click();

		log.info("CA QUOTE CREATED:  " + EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
	}

	@Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("CA POLICY CREATED: " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
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
	public void purchase(TestData td) {
		dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
		new PurchaseTab().fillTab(td).submitTab();
		log.info("CA POLICY CREATED: " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
	}

	@Override
	public void calculatePremium(TestData td) {
		dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
		new ReportsTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
	}

	@Override
	public void calculatePremiumAndPurchase(TestData td) {
		calculatePremium(td);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.UNDERWRITING_AND_APPROVAL.get());
		new UnderwritingAndApprovalTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
		new DocumentsTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
		new PurchaseTab().fillTab(td).submitTab();
	}

	@Override
	public void copyPolicy(TestData td) {
		policyCopy().perform(td);
		calculatePremiumAndPurchase(td);
		log.info("Copy Policy  " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
	}

	@Override
	public HomeCaPolicyActions.Endorse endorse() {
		return new HomeCaPolicyActions.Endorse();
	}

	@Override
	public PolicyActions.PriorTermEndorsement priorTermEndorsement() {
		return new HomeCaPolicyActions.PriorTermEndorsement();
	}

	@Override
	public HomeCaPolicyActions.Renew renew() {
		return new HomeCaPolicyActions.Renew();
	}

	@Override
	public PolicyActions.InitiateHOQuote initiateHOQuote() {
		return new HomeCaPolicyActions.InitiateHOQuote();
	}

	@Override
	public HomeCaPolicyActions.Cancel cancel() {
		return new HomeCaPolicyActions.Cancel();
	}

	@Override
	public HomeCaPolicyActions.CancelNotice cancelNotice() {
		return new HomeCaPolicyActions.CancelNotice();
	}

	@Override
	public HomeCaPolicyActions.ChangeBrokerRequest changeBrokerRequest() {
		return new HomeCaPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public HomeCaPolicyActions.ChangeReinstatementLapse changeReinstatementLapse() {
		return new HomeCaPolicyActions.ChangeReinstatementLapse();
	}

	@Override
	public HomeCaPolicyActions.ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		return new HomeCaPolicyActions.ChangeRenewalQuoteLapse();
	}

	@Override
	public HomeCaPolicyActions.CopyQuote copyQuote() {
		return new HomeCaPolicyActions.CopyQuote();
	}

	@Override
	public HomeCaPolicyActions.DataGather dataGather() {
		return new HomeCaPolicyActions.DataGather();
	}

	@Override
	public HomeCaPolicyActions.DeclineByCompanyQuote declineByCompanyQuote() {
		return new HomeCaPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public HomeCaPolicyActions.DeclineByCustomerQuote declineByCustomerQuote() {
		return new HomeCaPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public HomeCaPolicyActions.DeleteCancelNotice deleteCancelNotice() {
		return new HomeCaPolicyActions.DeleteCancelNotice();
	}

	@Override
	public HomeCaPolicyActions.DeletePendedTransaction deletePendedTransaction() {
		return new HomeCaPolicyActions.DeletePendedTransaction();
	}

	@Override
	public PolicyActions.DeletePendingRenwals deletePendingRenwals() {
		return new HomeCaPolicyActions.DeletePendingRenwals();
	}

	@Override
	public HomeCaPolicyActions.DoNotRenew doNotRenew() {
		return new HomeCaPolicyActions.DoNotRenew();
	}

	@Override
	public HomeCaPolicyActions.ManualRenew manualRenew() {
		return new HomeCaPolicyActions.ManualRenew();
	}

	@Override
	public HomeCaPolicyActions.NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new HomeCaPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PolicyActions.PendedEndorsementChange pendedEndorsementChange() {
		throw new UnsupportedOperationException("Action pendedEndorsementChange is not defined in entity \"HO_EP1\"");
	}

	@Override
	public HomeCaPolicyActions.PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		return new HomeCaPolicyActions.PolicyChangeRenewalLapse();
	}

	@Override
	public HomeCaPolicyActions.PolicyInquiry policyInquiry() {
		return new HomeCaPolicyActions.PolicyInquiry();
	}

	@Override
	public PolicyActions.PolicySpin policySpin() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"HO_EP1\"");
	}

	@Override
	public PolicyActions.PolicySplit policySplit() {
		throw new UnsupportedOperationException("Action policySplit is not defined in entity \"HO_EP1\"");
	}

	@Override
	public HomeCaPolicyActions.Propose propose() {
		return new HomeCaPolicyActions.Propose();
	}

	@Override
	public HomeCaPolicyActions.QuoteInquiry quoteInquiry() {
		return new HomeCaPolicyActions.QuoteInquiry();
	}

	@Override
	public HomeCaPolicyActions.Reinstate reinstate() {
		return new HomeCaPolicyActions.Reinstate();
	}

	@Override
	public HomeCaPolicyActions.RemoveDoNotRenew removeDoNotRenew() {
		return new HomeCaPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public HomeCaPolicyActions.RemoveManualRenew removeManualRenew() {
		return new HomeCaPolicyActions.RemoveManualRenew();
	}

	@Override
	public HomeCaPolicyActions.RemoveSuspendQuote removeSuspendQuote() {
		return new HomeCaPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public HomeCaPolicyActions.RescindCancellation rescindCancellation() {
		return new HomeCaPolicyActions.RescindCancellation();
	}

	@Override
	public HomeCaPolicyActions.RollBackEndorsement rollBackEndorsement() {
		return new HomeCaPolicyActions.RollBackEndorsement();
	}

	@Override
	public HomeCaPolicyActions.SuspendQuote suspendQuote() {
		return new HomeCaPolicyActions.SuspendQuote();
	}

	@Override
	public PolicyActions.Bind bind() {
		throw new NotImplementedException();
	}

	@Override
	public PolicyActions.PolicyCopy policyCopy() {
		return new HomeCaPolicyActions.PolicyCopy();
	}

	@Override
	public PolicyActions.PolicyDocGen policyDocGen() {
		return new HomeCaPolicyActions.PolicyDocGen();
	}

	@Override
	public PolicyActions.Rewrite rewrite() {
		return new HomeCaPolicyActions.Rewrite();
	}

	@Override
	public PolicyActions.QuoteDocGen quoteDocGen() {
		return new HomeCaPolicyActions.QuoteDocGen();
	}

	@Override
	public PolicyActions.RollOn rollOn() {return new HomeCaPolicyActions.RollOn();}

	@Override
	public PolicyActions.UpdateRulesOverride updateRulesOverride() {
		throw new NotImplementedException();
	}

	@Override
	public HomeCaPolicyActions.ManualRenewalWithOrWithoutLapse manualRenewalWithOrWithoutLapse() {
		return new HomeCaPolicyActions.ManualRenewalWithOrWithoutLapse();
	}

}
