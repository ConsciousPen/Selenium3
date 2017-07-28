/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.home_ss;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.EntityLogger;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ProductOfferingTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.modules.policy.home_ss.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.modules.policy.home_ss.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
import toolkit.datax.TestData;

/**
 * Concrete implementation for a specific entity type.
 * 
 * @category Generated
 */
public class HomeSSPolicy implements IPolicy {

	protected static Logger log = LoggerFactory.getLogger(HomeSSPolicy.class);

	private Workspace defaultView = new DefaultView();

	@Override
	public Workspace getDefaultView() {
		return defaultView;
	}

	@Override
	public void initiate() {
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
		QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.HOME_SS_HO3.getName());
		QuoteSummaryPage.buttonAddNewQuote.click();
	}

	@Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, BindTab.class, false);
		BindTab.buttonSaveAndExit.click();

		log.info("QUOTE CREATED: " + EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
	}

	@Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("POLICY CREATED: " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
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
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().submitTab();
		new PurchaseTab().fillTab(td).submitTab();
		log.info("Purchased Quote " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
	}

	@Override
	public void calculatePremium(TestData td) {
		dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		new ReportsTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		new ProductOfferingTab().btnCalculatePremium.click();
	}

	@Override
	public void calculatePremiumAndPurchase(TestData td) {
	    calculatePremium(td);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		new UnderwritingAndApprovalTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
		new DocumentsTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
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
		return new HomeSSPolicyActions.Endorse();
	}

	@Override
	public PolicyActions.Renew renew() {
		return new HomeSSPolicyActions.Renew();
	}

	@Override
	public PolicyActions.Bind bind() {
		return new HomeSSPolicyActions.Bind();
	}

	@Override
	public PolicyActions.Cancel cancel() {
		return new HomeSSPolicyActions.Cancel();
	}

	@Override
	public PolicyActions.CancelNotice cancelNotice() {
		return new HomeSSPolicyActions.CancelNotice();
	}

	@Override
	public PolicyActions.ChangeBrokerRequest changeBrokerRequest() {
		return new HomeSSPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public PolicyActions.ChangeReinstatementLapse changeReinstatementLapse() {
		return new HomeSSPolicyActions.ChangeReinstatementLapse();
	}

	@Override
	public PolicyActions.ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		return new HomeSSPolicyActions.ChangeRenewalQuoteLapse();
	}

	@Override
	public PolicyActions.CopyQuote copyQuote() {
		return new HomeSSPolicyActions.CopyQuote();
	}

	@Override
	public PolicyActions.DataGather dataGather() {
		return new HomeSSPolicyActions.DataGather();
	}

	@Override
	public PolicyActions.DeclineByCompanyQuote declineByCompanyQuote() {
		return new HomeSSPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public PolicyActions.DeclineByCustomerQuote declineByCustomerQuote() {
		return new HomeSSPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public PolicyActions.DeleteCancelNotice deleteCancelNotice() {
		return new HomeSSPolicyActions.DeleteCancelNotice();
	}

	@Override
	public PolicyActions.DeletePendedTransaction deletePendedTransaction() {
		return new HomeSSPolicyActions.DeletePendedTransaction();
	}
	
	@Override
	public PolicyActions.DeletePendingRenwals deletePendingRenwals() {
		return new HomeSSPolicyActions.DeletePendingRenwals();
	}

	@Override
	public PolicyActions.DoNotRenew doNotRenew() {
		return new HomeSSPolicyActions.DoNotRenew();
	}

	@Override
	public PolicyActions.ManualRenew manualRenew() {
		return new HomeSSPolicyActions.ManualRenew();
	}

	@Override
	public PolicyActions.NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new HomeSSPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PolicyActions.PendedEndorsementChange pendedEndorsementChange() {
		return new HomeSSPolicyActions.PendedEndorsementChange();
	}

	@Override
	public PolicyActions.PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		return new HomeSSPolicyActions.PolicyChangeRenewalLapse();
	}

	@Override
	public PolicyActions.PolicyCopy policyCopy() {
		return new HomeSSPolicyActions.PolicyCopy();
	}

	@Override
	public PolicyActions.PolicyInquiry policyInquiry() {
		return new HomeSSPolicyActions.PolicyInquiry();
	}

	@Override
	public PolicyActions.PolicySpin policySpin() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyActions.PolicySplit policySplit() {
		throw new UnsupportedOperationException("Action policySplit is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyActions.PolicyDocGen policyDocGen() {
		return new HomeSSPolicyActions.PolicyDocGenFlow();
	}

	@Override
	public PolicyActions.Propose propose() {
		return new HomeSSPolicyActions.Propose();
	}

	@Override
	public PolicyActions.QuoteInquiry quoteInquiry() {
		return new HomeSSPolicyActions.QuoteInquiry();
	}

	@Override
	public PolicyActions.Reinstate reinstate() {
		return new HomeSSPolicyActions.Reinstate();
	}

	@Override
	public PolicyActions.RemoveDoNotRenew removeDoNotRenew() {
		return new HomeSSPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public PolicyActions.RemoveManualRenew removeManualRenew() {
		return new HomeSSPolicyActions.RemoveManualRenew();
	}

	@Override
	public PolicyActions.RemoveSuspendQuote removeSuspendQuote() {
		return new HomeSSPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public PolicyActions.RescindCancellation rescindCancellation() {
		return new HomeSSPolicyActions.RescindCancellation();
	}

	@Override
	public PolicyActions.RollBackEndorsement rollBackEndorsement() {
		return new HomeSSPolicyActions.RollBackEndorsement();
	}

	@Override
	public PolicyActions.RollOn rollOn() {
		return new HomeSSPolicyActions.RollOn();
	}

	@Override
	public PolicyActions.SuspendQuote suspendQuote() {
		return new HomeSSPolicyActions.SuspendQuote();
	}

	@Override
	public PolicyActions.QuoteDocGen quoteDocGen() {
		return new HomeSSPolicyActions.QuoteDocGenFlow();
	}

	@Override
	public PolicyActions.Rewrite rewrite() {
		return new HomeSSPolicyActions.Rewrite();
	}

	@Override
	public PolicyActions.UpdateRulesOverride updateRulesOverride() {
		throw new NotImplementedException();
	}
}
