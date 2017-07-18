/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy;

import aaa.common.Workspace;
import toolkit.datax.TestData;

/**
 * Interface for a specific entity type.
 * Modify this class if action set for a specific entity type has to be altered.
 * @category Generated
 */
public interface IPolicy {

    Workspace getDefaultView();
    
	 /**
     * Initiate product/entity creation (Select product name at the quote tab and click Add New Quote button)
     */
    void initiate();

    /**
     * Initiate quote creation, fill all mandatory fields and save quote in premium calculated status
     * @param td - TestData for quote creation (at least all mandatory fields should be filled to navigate till Purchase action)
     */
    void createQuote(TestData td);

    /**
     * Fill tabs with provided data and purchase policy.
     * @param td - TestData for policy creation and purchase
     */
    void createPolicy(TestData td);

    /**
     * Perform Renew action, fill policy renew data, confirm purchase of renewal
     * @param td - TestData for renew action and renewed policy data. Purchase tab is not shown so shouldn't present in TestData
     **/
    void createRenewal(TestData td);

    /**
     * Fill Endorsement action tab, confirm endorsement, fill policy endorsement, confirm purchase of endorsement
     * @param td - TestData for Endorsement and data gather for endorsement. Purchase tab is not shown so shouldn't present in TestData
     **/
    void createEndorsement(TestData td);

    /**
     * Open Data Gathering mode, order reports, Calculate premium, stay in data gathering mode
     * @param td TODO
     */
    void calculatePremium(TestData td);
    
    /**
     * Open Data Gathering mode, order reports, Calculate premium, Purchase policy
     * @param td - purchase screen data
     */
    void calculatePremiumAndPurchase(TestData td);

    /**
     * Perform 'Copy from Policy' action and purchase new quote 
     * @param td - data for Copy action, filling tabs such as Underwriting & Approval if needed, purchase data
     **/
    void copyPolicy(TestData td);

    /**
     * Open Data Gathering mode, navigate to Bind tab and purchase quote (quote should be in Premium Calculated status and have been ready for purchasing)
     * @param td - purchase data 
     **/
    void purchase(TestData td);
    
    
    //Policy Actions:
    PolicyActions.Endorse endorse();

    PolicyActions.Renew renew();

    PolicyActions.Cancel cancel();

    PolicyActions.CancelNotice cancelNotice();

    PolicyActions.ChangeBrokerRequest changeBrokerRequest();

    PolicyActions.ChangeReinstatementLapse changeReinstatementLapse();

    PolicyActions.PolicyCopy policyCopy();

    PolicyActions.CopyQuote copyQuote();

    PolicyActions.DataGather dataGather();

    PolicyActions.DeclineByCompanyQuote declineByCompanyQuote();

    PolicyActions.DeclineByCustomerQuote declineByCustomerQuote();

    PolicyActions.DeleteCancelNotice deleteCancelNotice();

    PolicyActions.DeletePendedTransaction deletePendedTransaction();

    PolicyActions.DoNotRenew doNotRenew();

    PolicyActions.ManualRenew manualRenew();

    PolicyActions.PendedEndorsementChange pendedEndorsementChange();

    PolicyActions.PolicyChangeRenewalLapse policyChangeRenewalLapse();

    PolicyActions.PolicyInquiry policyInquiry();

    PolicyActions.QuoteInquiry quoteInquiry();

    PolicyActions.Reinstate reinstate();

    PolicyActions.RemoveDoNotRenew removeDoNotRenew();

    PolicyActions.RemoveManualRenew removeManualRenew();

    PolicyActions.Bind bind();

    PolicyActions.PolicyDocGen policyDocGen();
    
    PolicyActions.QuoteDocGen quoteDocGen();

    PolicyActions.Rewrite rewrite();
    
    PolicyActions.RollBackEndorsement rollBackEndorsement();

    PolicyActions.RollOn rollOn();
    
    PolicyActions.PolicySpin policySpin();
    
    PolicyActions.PolicySplit policySplit();
    
    PolicyActions.SuspendQuote suspendQuote();
    
    PolicyActions.Propose propose();
    
    PolicyActions.RescindCancellation rescindCancellation();
    
    PolicyActions.NonPremiumBearingEndorsement nonPremiumBearingEndorsement();
    
    PolicyActions.UpdateRulesOverride updateRulesOverride();
    
    //TODO check if used in AAA and delete
    PolicyActions.RemoveSuspendQuote removeSuspendQuote();
    PolicyActions.ChangeRenewalQuoteLapse changeRenewalQuoteLapse();
}
