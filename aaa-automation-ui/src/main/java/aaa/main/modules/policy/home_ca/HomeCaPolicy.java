/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.home_ca;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.HomeCaTab;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.PolicyActions.Bind;
import aaa.main.modules.policy.PolicyActions.PendedEndorsementChange;
import aaa.main.modules.policy.PolicyActions.PolicyCopy;
import aaa.main.modules.policy.PolicyActions.PolicyDocGen;
import aaa.main.modules.policy.PolicyActions.PolicySpin;
import aaa.main.modules.policy.PolicyActions.PolicySplit;
import aaa.main.modules.policy.PolicyActions.QuoteDocGen;
import aaa.main.modules.policy.PolicyActions.Rewrite;
import aaa.main.modules.policy.PolicyActions.RollOn;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.Cancel;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.CancelNotice;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.ChangeBrokerRequest;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.ChangeReinstatementLapse;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.ChangeRenewalQuoteLapse;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.CopyQuote;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.DataGather;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.DeclineByCompanyQuote;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.DeclineByCustomerQuote;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.DeleteCancelNotice;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.DeletePendedTransaction;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.DoNotRenew;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.Endorse;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.ManualRenew;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.NonPremiumBearingEndorsement;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.PolicyChangeRenewalLapse;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.PolicyInquiry;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.Propose;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.QuoteInquiry;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.Reinstate;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.RemoveDoNotRenew;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.RemoveManualRenew;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.RemoveSuspendQuote;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.Renew;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.RescindCancellation;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.RollBackEndorsement;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions.SuspendQuote;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.modules.policy.home_ca.views.DefaultView;
import aaa.main.pages.summary.QuoteSummaryPage;
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
        QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.HOME_CA.getName());
        QuoteSummaryPage.buttonAddNewQuote.click();
    }

    @Override
    public void createQuote(TestData td) {
    	initiate();
		getDefaultView().fillUpTo(td, BindTab.class, false);
		BindTab.buttonSaveAndExit.click();

        log.info("CA QUOTE CREATED:  " + EntityLogger.getEntityHeader(EntityType.QUOTE));
    }

    @Override
	public void createPolicy(TestData td) {
		initiate();
		getDefaultView().fill(td);
		log.info("CA POLICY CREATED: " + EntityLogger.getEntityHeader(EntityType.POLICY));
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
        NavigationPage.toViewTab(HomeCaTab.BIND.get());
        new BindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
        log.info("CA POLICY CREATED: " + EntityLogger.getEntityHeader(EntityType.POLICY));
    }

    @Override
    public void calculatePremium() {
        dataGather().start();
        NavigationPage.toViewTab(HomeCaTab.REPORTS.get());
        new ReportsTab().orderAllReports();
        NavigationPage.toViewTab(HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
    }
    
    @Override
    public void calculatePremiumAndPurchase(TestData td) {
        calculatePremium();
        NavigationPage.toViewTab(HomeCaTab.UNDERWRITING_AND_APPROVAL.get());
        new UnderwritingAndApprovalTab().fillTab(td);
        NavigationPage.toViewTab(HomeCaTab.BIND.get());
        new BindTab().submitTab();
        new PurchaseTab().fillTab(td).submitTab();
    }

    @Override
    public void copyPolicy(TestData td) {
        policyCopy().perform(td);
        calculatePremiumAndPurchase(td);
        log.info("Copy Policy  " + EntityLogger.getEntityHeader(EntityType.POLICY));
    }

    @Override
    public void copyQuote(TestData td) {
        copyQuote().perform(td);
        calculatePremium();
        log.info("Copy Quote  " + EntityLogger.getEntityHeader(EntityType.POLICY));
    }

    @Override
    public Endorse endorse() {
        return new Endorse();
    }

    @Override
    public Renew renew() {
        return new Renew();
    }

    @Override
    public Cancel cancel() {
        return new Cancel();
    }

    @Override
    public CancelNotice cancelNotice() {
        return new CancelNotice();
    }

    @Override
    public ChangeBrokerRequest changeBrokerRequest() {
        return new ChangeBrokerRequest();
    }

    @Override
    public ChangeReinstatementLapse changeReinstatementLapse() {
        return new ChangeReinstatementLapse();
    }

    @Override
    public ChangeRenewalQuoteLapse changeRenewalQuoteLapse() {
        return new ChangeRenewalQuoteLapse();
    }

    @Override
    public CopyQuote copyQuote() {
        return new CopyQuote();
    }

    @Override
    public DataGather dataGather() {
        return new DataGather();
    }

    @Override
    public DeclineByCompanyQuote declineByCompanyQuote() {
        return new DeclineByCompanyQuote();
    }

    @Override
    public DeclineByCustomerQuote declineByCustomerQuote() {
        return new DeclineByCustomerQuote();
    }

    @Override
    public DeleteCancelNotice deleteCancelNotice() {
        return new DeleteCancelNotice();
    }

    @Override
    public DeletePendedTransaction deletePendedTransaction() {
        return new DeletePendedTransaction();
    }

    @Override
    public DoNotRenew doNotRenew() {
        return new DoNotRenew();
    }

    @Override
    public ManualRenew manualRenew() {
        return new ManualRenew();
    }

    @Override
    public NonPremiumBearingEndorsement nonPremiumBearingEndorsement() {
        return new NonPremiumBearingEndorsement();
    }

    @Override
    public PendedEndorsementChange pendedEndorsementChange() {
        throw new UnsupportedOperationException("Action pendedEndorsementChange is not defined in entity \"HO_EP1\"");
    }

    @Override
    public PolicyChangeRenewalLapse policyChangeRenewalLapse() {
        return new PolicyChangeRenewalLapse();
    }

    @Override
    public PolicyInquiry policyInquiry() {
        return new PolicyInquiry();
    }

    @Override
    public PolicySpin policySpin() {
        throw new UnsupportedOperationException("Action policySpin is not defined in entity \"HO_EP1\"");
    }

    @Override
    public PolicySplit policySplit() {
        throw new UnsupportedOperationException("Action policySplit is not defined in entity \"HO_EP1\"");
    }

    @Override
    public Propose propose() {
        return new Propose();
    }

    @Override
    public QuoteInquiry quoteInquiry() {
        return new QuoteInquiry();
    }

    @Override
    public Reinstate reinstate() {
        return new Reinstate();
    }

    @Override
    public RemoveDoNotRenew removeDoNotRenew() {
        return new RemoveDoNotRenew();
    }

    @Override
    public RemoveManualRenew removeManualRenew() {
        return new RemoveManualRenew();
    }

    @Override
    public RemoveSuspendQuote removeSuspendQuote() {
        return new RemoveSuspendQuote();
    }

    @Override
    public RescindCancellation rescindCancellation() {
        return new RescindCancellation();
    }

    @Override
    public RollBackEndorsement rollBackEndorsement() {
        return new RollBackEndorsement();
    }

    @Override
    public SuspendQuote suspendQuote() {
        return new SuspendQuote();
    }

    @Override
    public Bind bind() {
        throw new NotImplementedException();
    }

    @Override
    public PolicyCopy policyCopy() {
        return new HomeCaPolicyActions.PolicyCopy();
    }

    @Override
    public PolicyDocGen policyDocGen() {
        throw new NotImplementedException();
    }

    @Override
    public Rewrite rewrite() {
        return new HomeCaPolicyActions.Rewrite();
    }

    @Override
    public QuoteDocGen quoteDocGen() {
        throw new NotImplementedException();
    }

    @Override
    public RollOn rollOn() {
        throw new NotImplementedException();
    }
}
