/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.home_ss;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.HomeSSTab;
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
import aaa.main.modules.policy.PolicyActions.UpdateRulesOverride;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
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
		QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.HOME_SS.getName());
		QuoteSummaryPage.buttonAddNewQuote.click();
	}

	@Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, BindTab.class, false);
		BindTab.buttonSaveAndExit.click();

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
	public void purchase(TestData td) {
	    dataGather().start();
	    NavigationPage.toViewTab(HomeSSTab.BIND.get());
        new BindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
		log.info("Purchased Quote " + EntityLogger.getEntityHeader(EntityType.POLICY));
	}

	@Override
	public void calculatePremium(TestData td) {
		dataGather().start();
		NavigationPage.toViewTab(HomeSSTab.REPORTS.get());
		new ReportsTab().fillTab(td);
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		new ProductOfferingTab().btnCalculatePremium.click();
	}
	
	@Override
	public void calculatePremiumAndPurchase(TestData td) {
	    calculatePremium(td);
	    NavigationPage.toViewTab(HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
        new UnderwritingAndApprovalTab().fillTab(td);
	    NavigationPage.toViewTab(HomeSSTab.BIND.get());
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
	public Endorse endorse() {
		return new HomeSSPolicyActions.Endorse();
	}

	@Override
	public Renew renew() {
		return new HomeSSPolicyActions.Renew();
	}

	@Override
	public Bind bind() {
		return new HomeSSPolicyActions.Bind();
	}

	@Override
	public Cancel cancel() {
		return new HomeSSPolicyActions.Cancel();
	}

	@Override
	public CancelNotice cancelNotice() {
		return new HomeSSPolicyActions.CancelNotice();
	}

	@Override
	public ChangeBrokerRequest changeBrokerRequest() {
		return new HomeSSPolicyActions.ChangeBrokerRequest();
	}

	@Override
	public ChangeReinstatementLapse changeReinstatementLapse() {
		return new HomeSSPolicyActions.ChangeReinstatementLapse();
	}

	@Override
	public ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
		return new HomeSSPolicyActions.ChangeRenewalQuoteLapse();
	}

	@Override
	public CopyQuote copyQuote() {
		return new HomeSSPolicyActions.CopyQuote();
	}

	@Override
	public DataGather dataGather() {
		return new HomeSSPolicyActions.DataGather();
	}

	@Override
	public DeclineByCompanyQuote declineByCompanyQuote() {
		return new HomeSSPolicyActions.DeclineByCompanyQuote();
	}

	@Override
	public DeclineByCustomerQuote declineByCustomerQuote() {
		return new HomeSSPolicyActions.DeclineByCustomerQuote();
	}

	@Override
	public DeleteCancelNotice deleteCancelNotice() {
		return new HomeSSPolicyActions.DeleteCancelNotice();
	}

	@Override
	public DeletePendedTransaction deletePendedTransaction() {
		return new HomeSSPolicyActions.DeletePendedTransaction();
	}

	@Override
	public DoNotRenew doNotRenew() {
		return new HomeSSPolicyActions.DoNotRenew();
	}

	@Override
	public ManualRenew manualRenew() {
		return new HomeSSPolicyActions.ManualRenew();
	}

	@Override
	public NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
		return new HomeSSPolicyActions.NonPremiumBearingEndorsement();
	}

	@Override
	public PendedEndorsementChange pendedEndorsementChange() {
		return new HomeSSPolicyActions.PendedEndorsementChange();
	}

	@Override
	public PolicyChangeRenewalLapse policyChangeRenewalLapse() {
		return new HomeSSPolicyActions.PolicyChangeRenewalLapse();
	}

	@Override
	public PolicyCopy policyCopy() {
		return new HomeSSPolicyActions.PolicyCopy();
	}

	@Override
	public PolicyInquiry policyInquiry() {
		return new HomeSSPolicyActions.PolicyInquiry();
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
		return new HomeSSPolicyActions.PolicyDocGenFlow();
	}

	@Override
	public Propose propose() {
		return new HomeSSPolicyActions.Propose();
	}

	@Override
	public QuoteInquiry quoteInquiry() {
		return new HomeSSPolicyActions.QuoteInquiry();
	}

	@Override
	public Reinstate reinstate() {
		return new HomeSSPolicyActions.Reinstate();
	}

	@Override
	public RemoveDoNotRenew removeDoNotRenew() {
		return new HomeSSPolicyActions.RemoveDoNotRenew();
	}

	@Override
	public RemoveManualRenew removeManualRenew() {
		return new HomeSSPolicyActions.RemoveManualRenew();
	}

	@Override
	public RemoveSuspendQuote removeSuspendQuote() {
		return new HomeSSPolicyActions.RemoveSuspendQuote();
	}

	@Override
	public RescindCancellation rescindCancellation() {
		return new HomeSSPolicyActions.RescindCancellation();
	}

	@Override
	public RollBackEndorsement rollBackEndorsement() {
		return new HomeSSPolicyActions.RollBackEndorsement();
	}

	@Override
	public RollOn rollOn() {
		return new HomeSSPolicyActions.RollOn();
	}

	@Override
	public SuspendQuote suspendQuote() {
		return new HomeSSPolicyActions.SuspendQuote();
	}

	@Override
	public QuoteDocGen quoteDocGen() {
		return new HomeSSPolicyActions.QuoteDocGenFlow();
	}

	@Override
	public Rewrite rewrite() {
		return new HomeSSPolicyActions.Rewrite();
	}
	
    @Override
    public UpdateRulesOverride updateRulesOverride() {
    	throw new NotImplementedException();
    }
}
