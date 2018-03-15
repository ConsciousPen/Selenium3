/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.pup;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.modules.policy.pup.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.utils.EntityLogger;
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
		QuoteSummaryPage.buttonAddNewQuote.click();
		QuoteSummaryPage.SelectProduct.broadLineOfBusiness.setValue(QuoteSummaryPage.PERSONAL_LINES);
		QuoteSummaryPage.SelectProduct.product.setValue(PolicyType.PUP.getName());
		QuoteSummaryPage.SelectProduct.nextBtn.click();
	}

	@Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, BindTab.class, true);
		BindTab.buttonSaveAndExit.click();

		log.info("Created Quote " + EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
	}

	@Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("Created Policy " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
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
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
		PremiumAndCoveragesQuoteTab.btnCalculatePremium.click();
	}

	@Override
	public void calculatePremiumAndPurchase(TestData td) {
		calculatePremium(td);
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERWRITING_AND_APPROVAL.get());
		new UnderwritingAndApprovalTab().fillTab(td);
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
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
	public void purchase(TestData td) {
		dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		new BindTab().submitTab();
		new PurchaseTab().fillTab(td).submitTab();
		log.info("Purchased Quote " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
	}

	@Override
	public PolicyActions.Endorse endorse() {
		return new PupPolicyActions.Endorse();
	}

	@Override
	public PolicyActions.PriorTermEndorsement priorTermEndorsement() {
		return new PupPolicyActions.PriorTermEndorsement();
	}

	@Override
	public PolicyActions.Renew renew() {
		return new PupPolicyActions.Renew();
	}

	@Override
	public PolicyActions.Bind bind() {
		return new PupPolicyActions.Bind();
	}

	@Override
	public PolicyActions.Cancel cancel() {
		return new PupPolicyActions.Cancel();
	}

	@Override
	public PolicyActions.CancelNotice cancelNotice() {
		return new PupPolicyActions.CancelNotice();
	}

	@Override
	public PolicyActions.ChangeBrokerRequest changeBrokerRequest() {
		return new PupPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public PolicyActions.ChangeReinstatementLapse changeReinstatementLapse() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyActions.ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyActions.CopyQuote copyQuote() {
		return new PupPolicyActions.CopyQuote();
	}

	@Override
	public PolicyActions.DataGather dataGather() {
		return new PupPolicyActions.DataGather();
	}

	@Override
	public PolicyActions.DeclineByCompanyQuote declineByCompanyQuote() {
		return new PupPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public PolicyActions.DeclineByCustomerQuote declineByCustomerQuote() {
		return new PupPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public PolicyActions.DeleteCancelNotice deleteCancelNotice() {
		return new PupPolicyActions.DeleteCancelNotice();
	}

	@Override
	public PolicyActions.DeletePendedTransaction deletePendedTransaction() {
		return new PupPolicyActions.DeletePendingTransaction();
	}

	@Override
	public PolicyActions.DeletePendingRenwals deletePendingRenwals() {
		return new PupPolicyActions.DeletePendingRenwals();
	}

	@Override
	public PolicyActions.DoNotRenew doNotRenew() {
		return new PupPolicyActions.DoNotRenew();
	}

	@Override
	public PolicyActions.ManualRenew manualRenew() {
		return new PupPolicyActions.ManualRenew();
	}

	@Override
	public PolicyActions.NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new PupPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PolicyActions.PendedEndorsementChange pendedEndorsementChange() {
		return new PupPolicyActions.PendedEndorsementChange();
	}

	@Override
	public PolicyActions.PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		throw new UnsupportedOperationException("Action policySpin is not defined in entity \"Default Policy's Root Configuration\"");
	}

	@Override
	public PolicyActions.PolicyCopy policyCopy() {
		return new PupPolicyActions.PolicyCopy();
	}

	@Override
	public PolicyActions.PolicyInquiry policyInquiry() {
		return new PupPolicyActions.PolicyInquiry();
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
		return new PupPolicyActions.PolicyDocGenFlow();
	}

	@Override
	public PolicyActions.Propose propose() {
		return new PupPolicyActions.Propose();
	}

	@Override
	public PolicyActions.QuoteInquiry quoteInquiry() {
		return new PupPolicyActions.QuoteInquiry();
	}

	@Override
	public PolicyActions.Reinstate reinstate() {
		return new PupPolicyActions.Reinstate();
	}

	@Override
	public PolicyActions.RemoveDoNotRenew removeDoNotRenew() {
		return new PupPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public PolicyActions.RemoveManualRenew removeManualRenew() {
		return new PupPolicyActions.RemoveManualRenew();
	}

	@Override
	public PolicyActions.RemoveSuspendQuote removeSuspendQuote() {
		return new PupPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public PolicyActions.RescindCancellation rescindCancellation() {
		return new PupPolicyActions.RescindCancellation();
	}

	@Override
	public PolicyActions.RollBackEndorsement rollBackEndorsement() {
		return new PupPolicyActions.RollBackEndorsement();
	}

	@Override
	public PolicyActions.RollOn rollOn() {
		return new PupPolicyActions.RollOn();
	}

	@Override
	public PolicyActions.SuspendQuote suspendQuote() {
		return new PupPolicyActions.SuspendQuote();
	}

	@Override
	public PolicyActions.QuoteDocGen quoteDocGen() {
		return new PupPolicyActions.QuoteDocGenFlow();
	}

	@Override
	public PolicyActions.Rewrite rewrite() {
		return new PupPolicyActions.Rewrite();
	}

	@Override
	public PolicyActions.UpdateRulesOverride updateRulesOverride() {
		throw new NotImplementedException();
	}

	@Override
	public PolicyActions.ManualRenewalWithOrWithoutLapse manualRenewalWithOrWithoutLapse() {
		return new PupPolicyActions.ManualRenewalWithOrWithoutLapse();
	}
}
