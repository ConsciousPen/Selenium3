/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class ActionConstants {

    public static final String EDIT = "Edit";
    public static final String DELETE = "Delete";
    public static final String CHANGE = "Change";
    public static final String ELIMINATE = "ELIMINATE";
    public static final String CLEAR = "Clear";
    public static final String REMOVE = "Remove";
    public static final String COPY = "Copy";
    public static final String SET_CURRENT = "Set Current";
    public static final String ACTIONS = "Actions";
    public static final String GET = "Get";
    public static final String DISASSOCIATE = "Disassociate";
    public static final String VIEW = "View";

    private ActionConstants() {}

    public static final class BillingPaymentsAndOtherTransactionAction {
        public static final String WAIVE = "Waive";
        public static final String DECLINE = "Decline";
        public static final String TRANSFER = "Transfer";
        public static final String ISSUE = "Issue";
    }

    public static final class BillingAction {
        public static final String INQUIRY = "Inquiry";
        public static final String UPDATE = "Update";
        public static final String GENATE_ON_DEMAND = "Generate On Demand Document";
        public static final String HOLD = "Hold Policies";
        public static final String UPDATE_HOLD = "Update Hold";
        public static final String GENERATE_FUTURE_STATEMENT = "Generate Future Statement";
        public static final String REFUND = "Refund";
        public static final String MOVE_POLICIES = "Move Policies";
    }

    public static final class BillingBillsAndStatementsAction {
        public static final String DISCARD = "Discard";
        public static final String REGENERATE = "Re-generate";
    }

    public static final class BillingPendingTransactionAction {
        public static final String APPROVE = "Approve";
        public static final String REJECT = "Reject";
        public static final String CHANGE = "Change";
    }

    public static final class AccountAction {
        public static final String INQUIRY = " Inquiry";
        public static final String ADD_CUSTOMER = " Add Customer";
        public static final String UPDATE = " Update";
    }

    public static final class FeatureAction {
        public static final String CLOSE = "Close";
        public static final String OPEN = "Open";
    }

    public static final class ClaimAction {
        public static final String CLAIM_CLOSE = "Claim Close";
        public static final String CLAIM_INQUIRY = "Claim Inquiry";
        public static final String CLAIM_NOTIFICATION = "Claim Notification";
        public static final String CLAIM_OPEN = "Claim Open";
        public static final String CLAIM_REOPEN = "Claim Reopen";
        public static final String CLAIM_UPDATE = "Claim Update";
        public static final String GENERATE_ON_DEMAND = "Generate On Demand Document";
        public static final String POLICY_SUMMARY_INQUIRY = "Policy Summary Inquiry";
        public static final String SUBROGATION_OPEN = "Subrogation Open";
        public static final String SIU_POTENTIAL = "SIU Potential";
        public static final String SIU_REVIEW = "SIU Review";
        public static final String SIU_UPDATE = "SIU Update";
        public static final String SIU_CLEAR = "SIU Clear";
        public static final String SECURE_CLAIM = "Secure Claim";
    }

    public static final class ProductAction {
        public static final String ADDFROMCANCEL = "AddFromCancel";
        public static final String ADDPREMIUMWAIVER = "Add Premium Waiver";
        public static final String BIND = "Bind";
        public static final String CANCEL_NOTICE = "Cancel Notice";
        public static final String CANCELLATION = "Cancellation";
        public static final String CHANGE_BROKER = "Change Broker";
        public static final String CHANGE_PENDED_END_EFF_DATE = "Change pended End. Eff. Date";
        public static final String CHANGE_PENDED_ENDORSEMENT = "Change pended endorsement";
        public static final String CHANGE_REINSTATEMENT_LAPSE_PERIOD = "Change Reinstatement Lapse Period";
        public static final String CHANGE_RENEWAL_LAPSE_PERIOD = "Change Renewal Lapse Period";
        public static final String CHANGEREINSTATELAPSE = "changeReinstateLapse";
        public static final String CONTRACT_ACCEPTANCE = "Contract Acceptance";
        public static final String COPY_FROM_POLICY = "Copy from Policy";
        public static final String COPY_FROM_QUOTE = "Copy from Quote";
        public static final String COPY_POLICY = "Copy Policy";
        public static final String COPY_QUOTE = "Copy Quote";
        public static final String DATA_GATHER = "Data Gather";
        public static final String DECLINE_BY_COMPANY = "Decline by Company";
        public static final String DECLINE_BY_CUSTOMER = "Decline by Customer";
        public static final String DELETE_CANCEL_NOTICE = "Delete Cancel Notice";
        public static final String DELETE_PENDED_TRANSACTION = "Delete Pended Transaction";
        public static final String DO_NOT_RENEW = "Do Not Renew";
        public static final String ENDORSE = "Endorse";
        public static final String EXTENSION_RENEWAL = "Extension Renewal";
        public static final String GENERATE_ON_DEMAND_DOCUMENT = "Generate On Demand Document";
        public static final String GENERATE_ON_DEMAND_DOCUMENT_POLICY = "Generate On Demand Document  Policy";
        public static final String GENERATE_ON_DEMAND_DOCUMENT_QUOTE = "Generate On Demand Document  Quote";
        public static final String ISSUE = "Issue";
        public static final String ISSUE_AUDIT = "Issue Audit";
        public static final String ISSUE_REVISED_AUDIT = "Issue Revised Audit";
        public static final String MANUAL_RENEW = "Manual Renew";
        public static final String MANUAL_RENEW_FLAG = "Manual Renew Flag";
        public static final String NONPREMIUM_BEARING_ENDORSEMENT = "NonPremium Bearing Endorsement";
        public static final String POLICY_DOC_GEN = "Policy Doc Gen";
        public static final String POLICY_RENEWAL_CHANGE_LAPSE = "Policy Renewal Change Lapse";
        public static final String PREMIUM_ADJUSTMENT = "Premium Adjustment";
        public static final String PREMIUM_AUDIT = "Premium Audit";
        public static final String PREMIUM_AUDIT_REVIEW = "Premium Audit Review";
        public static final String PROPOSE = "Propose";
        public static final String OOS_PREMIUM_ADJUSTMENT = "OOS Premium Adjustment";
        public static final String QUOTE_RENEWAL_CHANGE_LAPSE = "Quote Renewal Change Lapse";
        public static final String REINSTATE = "Reinstate";
        public static final String REINSTATEMENT = "Reinstatement";
        public static final String REMOVE_CANCEL_NOTICE = "Remove Cancel Notice";
        public static final String REMOVE_DO_NOT_RENEW = "Remove Do Not Renew";
        public static final String REMOVE_MANUAL_RENEW = "Remove Manual Renew";
        public static final String REMOVE_MANUAL_RENEW_FLAG = "Remove Manual Renew Flag";
        public static final String REMOVE_SUSPENSE = "Remove Suspense";
        public static final String RENEW = "Renew";
        public static final String RESCIND_CANCELLATION = "Rescind Cancellation";
        public static final String RETRO_ENDORSEMENT = "Retro Endorsement";
        public static final String REWRITE = "Rewrite";
        public static final String ROLL_BACK_ENDORSEMENT = "Roll Back Endorsement";
        public static final String ROLL_ON_CHANGES = "Roll On Changes";
        public static final String SPIN = "spin";
        public static final String SPLIT = "split";
        public static final String SUSPEND_QUOTE = "Suspend Quote";
        public static final String UPDATE_INSURED_INTEREST_INFO = "Update Insured/Interest Info";
    }
}
