/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.auto_ss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.modules.policy.auto_ss.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.utils.EntityLogger;
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
		getDefaultView().fillUpTo(td, DocumentsAndBindTab.class, false);
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

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
	public void createPriorTermEndorsement(TestData td) {
		priorTermEndorsement().performAndFill(td);
	}

	@Override
    public void purchase(TestData td) {
    	dataGather().start();
	    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
	    new DocumentsAndBindTab().fillTab(td).submitTab();
	    new PurchaseTab().fillTab(td).submitTab();
	    log.info("Purchased Quote " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
    }

    @Override
    public void calculatePremium(TestData td) {
        dataGather().start();
        //TODO workaround for QC 44220 Failed to rate policy QAZSS953305611,1528211031,quote
        //no error if general tab is opened before premium calculation
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
	    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
	    new RatingDetailReportsTab().fillTab(td);
	    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
    }

    @Override
    public void calculatePremiumAndPurchase(TestData td) {
        calculatePremium(td);
	    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
	    new DriverActivityReportsTab().fillTab(td);
	    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
	    new DocumentsAndBindTab().fillTab(td).submitTab();
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
        return new AutoSSPolicyActions.Endorse();
    }

	@Override
	public PolicyActions.PriorTermEndorsement priorTermEndorsement() {
		return new AutoSSPolicyActions.PriorTermEndorsement();
	}

	@Override
    public PolicyActions.Renew renew() {
        return new AutoSSPolicyActions.Renew();
    }

    @Override
    public PolicyActions.Bind bind() {
        return new AutoSSPolicyActions.Bind();
    }

    @Override
    public PolicyActions.Cancel cancel() {
        return new AutoSSPolicyActions.Cancel();
    }

    @Override
    public PolicyActions.CancelNotice cancelNotice() {
        return new AutoSSPolicyActions.CancelNotice();
    }

    @Override
    public PolicyActions.ChangeBrokerRequest changeBrokerRequest() {
        return new AutoSSPolicyActions.ChangeBrokerRequest();
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
        return new AutoSSPolicyActions.CopyQuote();
    }

    @Override
    public PolicyActions.DataGather dataGather() {
        return new AutoSSPolicyActions.DataGather();
    }

    @Override
    public PolicyActions.DeclineByCompanyQuote declineByCompanyQuote() {
        return new AutoSSPolicyActions.DeclineByCompanyQuote();
    }

    @Override
    public PolicyActions.DeclineByCustomerQuote declineByCustomerQuote() {
        return new AutoSSPolicyActions.DeclineByCustomerQuote();
    }

    @Override
    public PolicyActions.DeleteCancelNotice deleteCancelNotice() {
        return new AutoSSPolicyActions.DeleteCancelNotice();
    }

    @Override
    public PolicyActions.DeletePendedTransaction deletePendedTransaction() {
        return new AutoSSPolicyActions.DeletePendedTransaction();
    }
    
    @Override
	public PolicyActions.DeletePendingRenwals deletePendingRenwals() {
		return new AutoSSPolicyActions.DeletePendingRenwals();
	}

    @Override
    public PolicyActions.DoNotRenew doNotRenew() {
        return new AutoSSPolicyActions.DoNotRenew();
    }

    @Override
    public PolicyActions.ManualRenew manualRenew() {
        return new AutoSSPolicyActions.ManualRenew();
    }

    @Override
    public PolicyActions.NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
        return new AutoSSPolicyActions.NonPremiumBearingEndorsement();
    }

    @Override
    public PolicyActions.PendedEndorsementChange pendedEndorsementChange() {
        return new AutoSSPolicyActions.PendedEndorsementChange();
    }

    @Override
    public PolicyActions.PolicyChangeRenewalLapse policyChangeRenewalLapse() {
        throw new UnsupportedOperationException("Action policyChangeRenewalLapse is not defined in entity \"Default Policy's Root Configuration\"");
    }

    @Override
    public PolicyActions.PolicyCopy policyCopy() {
        return new AutoSSPolicyActions.PolicyCopy();
    }

    @Override
    public PolicyActions.PolicyDocGen policyDocGen() {
        return new AutoSSPolicyActions.PolicyDocGen();
    }

    @Override
    public PolicyActions.PolicyInquiry policyInquiry() {
        return new AutoSSPolicyActions.PolicyInquiry();
    }

    @Override
    public PolicyActions.PolicySpin policySpin() {
        return new AutoSSPolicyActions.PolicySpin();
    }

    @Override
    public PolicyActions.PolicySplit policySplit() {
        return new AutoSSPolicyActions.PolicySplit();
    }

    @Override
    public PolicyActions.Rewrite rewrite() {
        return new AutoSSPolicyActions.Rewrite();
    }

    @Override
    public PolicyActions.Propose propose() {
        return new AutoSSPolicyActions.Propose();
    }

    @Override
    public PolicyActions.QuoteDocGen quoteDocGen() {
        return new AutoSSPolicyActions.QuoteDocGen();
    }

    @Override
    public PolicyActions.QuoteInquiry quoteInquiry() {
        return new AutoSSPolicyActions.QuoteInquiry();
    }

    @Override
    public PolicyActions.Reinstate reinstate() {
        return new AutoSSPolicyActions.Reinstate();
    }

    @Override
    public PolicyActions.RemoveDoNotRenew removeDoNotRenew() {
        return new AutoSSPolicyActions.RemoveDoNotRenew();
    }

    @Override
    public PolicyActions.RemoveManualRenew removeManualRenew() {
        return new AutoSSPolicyActions.RemoveManualRenew();
    }

    @Override
    public PolicyActions.RemoveSuspendQuote removeSuspendQuote() {
        return new AutoSSPolicyActions.RemoveSuspendQuote();
    }

    @Override
    public PolicyActions.RescindCancellation rescindCancellation() {
        return new AutoSSPolicyActions.RescindCancellation();
    }

    @Override
    public PolicyActions.RollBackEndorsement rollBackEndorsement() {
        return new AutoSSPolicyActions.RollBackEndorsement();
    }

    @Override
    public PolicyActions.RollOn rollOn() {
        return new AutoSSPolicyActions.RollOn();
    }

    @Override
    public PolicyActions.SuspendQuote suspendQuote() {
        return new AutoSSPolicyActions.SuspendQuote();
    }

	@Override
	public PolicyActions.UpdateRulesOverride updateRulesOverride() {
		return new AutoSSPolicyActions.UpdateRulesOverride();
	}

    @Override
    public PolicyActions.ManualRenewalWithOrWithoutLapse manualRenewalWithOrWithoutLapse() {
        throw new UnsupportedOperationException("Action manualRenewalWithOrWithoutLapse is not defined for Auto Policy");
    }
}
