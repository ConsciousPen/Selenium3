/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.auto_ss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.enums.NavigationEnum.AutoSSTab;
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
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.views.DefaultView;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.QuoteSummaryPage;
import toolkit.datax.TestData;

/**
 * Concrete implementation for a specific entity type.
 * @category Generated
 */
public class AutoSSPolicy implements IPolicy {

    protected static Logger log = LoggerFactory.getLogger(AutoSSPolicy.class);

    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
        QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.AUTO_SS.getName());
        QuoteSummaryPage.buttonAddNewQuote.click();
    }

    @Override
	public void createQuote(TestData td) {
		initiate();
		getDefaultView().fillUpTo(td, BindTab.class, false);
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
    public void purchase(TestData td) {
        NavigationPage.toViewTab(AutoSSTab.BIND.get());
        new DocumentsAndBindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
        log.info("Purchased Quote " + EntityLogger.getEntityHeader(EntityType.POLICY));
    }

    @Override
    public void calculatePremium() {
        dataGather().start();
        NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.buttonCalculatePremium.click();
        PremiumAndCoveragesTab.buttonSaveAndExit.click();
    }
    
    @Override
    public void calculatePremiumAndPurchase(TestData td) {
        calculatePremium();
        NavigationPage.toViewTab(AutoCaTab.BIND.get());
        new DocumentsAndBindTab().submitTab();
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
        return new AutoSSPolicyActions.Endorse();
    }

    @Override
    public Renew renew() {
        return new AutoSSPolicyActions.Renew();
    }

    @Override
    public Bind bind() {
        return new AutoSSPolicyActions.Bind();
    }

    @Override
    public Cancel cancel() {
        return new AutoSSPolicyActions.Cancel();
    }

    @Override
    public CancelNotice cancelNotice() {
        return new AutoSSPolicyActions.CancelNotice();
    }

    @Override
    public ChangeBrokerRequest changeBrokerRequest() {
        return new AutoSSPolicyActions.ChangeBrokerRequest();
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
        return new AutoSSPolicyActions.CopyQuote();
    }

    @Override
    public DataGather dataGather() {
        return new AutoSSPolicyActions.DataGather();
    }

    @Override
    public DeclineByCompanyQuote declineByCompanyQuote() {
        return new AutoSSPolicyActions.DeclineByCompanyQuote();
    }

    @Override
    public DeclineByCustomerQuote declineByCustomerQuote() {
        return new AutoSSPolicyActions.DeclineByCustomerQuote();
    }

    @Override
    public DeleteCancelNotice deleteCancelNotice() {
        return new AutoSSPolicyActions.DeleteCancelNotice();
    }

    @Override
    public DeletePendedTransaction deletePendedTransaction() {
        return new AutoSSPolicyActions.DeletePendedTransaction();
    }

    @Override
    public DoNotRenew doNotRenew() {
        return new AutoSSPolicyActions.DoNotRenew();
    }

    @Override
    public ManualRenew manualRenew() {
        return new AutoSSPolicyActions.ManualRenew();
    }

    @Override
    public NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
        return new AutoSSPolicyActions.NonPremiumBearingEndorsement();
    }

    @Override
    public PendedEndorsementChange pendedEndorsementChange() {
        return new AutoSSPolicyActions.PendedEndorsementChange();
    }

    @Override
    public PolicyChangeRenewalLapse policyChangeRenewalLapse() {
        throw new UnsupportedOperationException("Action policyChangeRenewalLapse is not defined in entity \"Default Policy's Root Configuration\"");
    }

    @Override
    public PolicyCopy policyCopy() {
        return new AutoSSPolicyActions.PolicyCopy();
    }

    @Override
    public PolicyDocGen policyDocGen() {
        return new AutoSSPolicyActions.PolicyDocGen();
    }

    @Override
    public PolicyInquiry policyInquiry() {
        return new AutoSSPolicyActions.PolicyInquiry();
    }

    @Override
    public PolicySpin policySpin() {
        return new AutoSSPolicyActions.PolicySpin();
    }

    @Override
    public PolicySplit policySplit() {
        return new AutoSSPolicyActions.PolicySplit();
    }

    @Override
    public Rewrite rewrite() {
        return new AutoSSPolicyActions.Rewrite();
    }

    @Override
    public Propose propose() {
        return new AutoSSPolicyActions.Propose();
    }

    @Override
    public QuoteDocGen quoteDocGen() {
        return new AutoSSPolicyActions.QuoteDocGen();
    }

    @Override
    public QuoteInquiry quoteInquiry() {
        return new AutoSSPolicyActions.QuoteInquiry();
    }

    @Override
    public Reinstate reinstate() {
        return new AutoSSPolicyActions.Reinstate();
    }

    @Override
    public RemoveDoNotRenew removeDoNotRenew() {
        return new AutoSSPolicyActions.RemoveDoNotRenew();
    }

    @Override
    public RemoveManualRenew removeManualRenew() {
        return new AutoSSPolicyActions.RemoveManualRenew();
    }

    @Override
    public RemoveSuspendQuote removeSuspendQuote() {
        return new AutoSSPolicyActions.RemoveSuspendQuote();
    }

    @Override
    public RescindCancellation rescindCancellation() {
        return new AutoSSPolicyActions.RescindCancellation();
    }

    @Override
    public RollBackEndorsement rollBackEndorsement() {
        return new AutoSSPolicyActions.RollBackEndorsement();
    }

    @Override
    public RollOn rollOn() {
        return new AutoSSPolicyActions.RollOn();
    }

    @Override
    public SuspendQuote suspendQuote() {
        return new AutoSSPolicyActions.SuspendQuote();
    }

}
