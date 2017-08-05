/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class ProductConstants {

    private ProductConstants() {}

    public static final class PolicyStatus {
        public static final String BOUND = "Bound";
        public static final String CANCELLATION_PENDING = "Cancellation Pending";
        public static final String COMPANY_DECLINED = "Company Declined";
        public static final String CUSTOMER_DECLINED = "Customer Declined";
	    public static final String DATA_GATHERING = "Gathering Info";
        public static final String ISSUED = "Issued";
        public static final String PENDING_OUT_OF_SEQUENCE_COMPLETION = "Pending out of sequence completion";
        public static final String POLICY_ACTIVE = "Policy Active";
        public static final String POLICY_EXPIRED = "Policy Expired";
        public static final String POLICY_CANCELLED = "Policy Cancelled";
        public static final String POLICY_PENDING = "Policy Pending";
        public static final String PREMIUM_CALCULATED = "Premium Calculated";
        public static final String PROPOSED = "Proposed";
        public static final String SUSPENDED = "Suspended";
    }

    public static final class ProductFlag {
        public static final String NONE = "";
        public static final String CANCEL_NOTICE = "Cancel Notice";
    }

    public static final class PolicyType {
        public static final String AUTO_BASE = "Auto (Base)";
        public static final String AUTO_PRECONFIGURED = "Auto (Preconfigured)";
        public static final String HOME = "Home";
        public static final String HOME_PRECONFIGURED = "Home (Preconfigured)";
    }

   
    public static final class PolicyLineOfBusiness {
        public static final String AUTOMOBILE = "Automobile";
        public static final String HOMEOWNERS = "Homeowners";
    }

   
    public static final class TransactionHistoryType {
        public static final String ANNIVERSARY_RENEWAL = "Anniversary Renewal";
        public static final String ANNIVERSARY_RENEWAL_WITH_LAPSE = "Anniversary Renewal with Lapse";
        public static final String ANNIVERSARY_RENEWAL_WITH_LAPSE_REMOVED = "Anniversary Renewal with Lapse Removed";
        public static final String BACKED_OFF_ANNIVERSARY_RENEWAL = "Backed Off Anniversary Renewal";
        public static final String BACKED_OFF_ANNIVERSARY_RENEWAL_WITH_LAPSE = "Backed Off Anniversary Renewal with Lapse";
        public static final String BACKED_OFF_ENDORSEMENT = "Backed Off Endorsement";
        public static final String BACKED_OFF_REINSTATEMENT = "Backed Off Reinstatement";
        public static final String BACKED_OFF_REINSTATEMENT_WITH_LAPSE = "Backed Off Reinstatement with Lapse";
        public static final String CANCEL_NOTICE = "Cancel Notice";
        public static final String CANCELLATION = "Cancellation";
        public static final String CANCELLATION_AUDIT = "Cancellation Audit";
        public static final String CHANGE_AGENCY = "Change Agency";
        public static final String ENDORSEMENT = "Endorsement";
        public static final String FINAL_AUDIT = "Final Audit";
        public static final String FLAT_CANCELLATION = "Flat Cancellation";
        public static final String ISSUE = "Issue";
        public static final String MANUAL_RENEWAL_FLAG = "Manual Renewal Flag";
        public static final String NON_PREMIUM_ENDORSEMENT = "NonPremium Endorsement";
        public static final String OOS_ENDORSEMENT = "OOS Endorsement";
        public static final String REINSTATEMENT = "Reinstatement";
        public static final String REINSTATEMENT_WITH_LAPSE = "Reinstatement with Lapse";
        public static final String REINSTATEMENT_WITH_LAPSE_REMOVED = "Reinstatement with Lapse Removed";
        public static final String REMOVE_CANCEL_NOTICE = "Remove Cancel Notice";
        public static final String REMOVE_MANUAL_RENEWAL_FLAG = "Remove Manual Renewal Flag";
        public static final String REVERSE_CANCELLATION_AUDIT = "Reverse Cancellation Audit";
        public static final String REVERSE_REVISED_CANCELLATION_AUDIT = "Reverse Revised Cancellation Audit";
        public static final String REVISED_CANCELLATION_AUDIT = "Revised Cancellation Audit";
        public static final String REVISED_FINAL_AUDIT = "Revised Final Audit";
        public static final String ROLL_BACK = "Roll Back";
        public static final String ROLLED_ON_ANNIVERSARY_RENEWAL = "Rolled On Anniversary Renewal";
    }

    public static final class ReinstatementReason {
        public static final String LAPSE_PAYMENT_RECEIVED_WITHIN_THE_LAPSE_PERIOD = "Lapse: payment received within the lapse period";
        public static final String LAPSE_LAPSE_ALLOWED_FOR_CUSTOMER_RETENTION = "Lapse: lapse allowed for customer retention";
        public static final String WITHOUT_LAPSE_PAYMENT_RECEIVED_WITHIN_GRACE_PERIOD = "Without Lapse: payment received within grace period";
        public static final String RESCIND_CANCELLATION = "Rescind Cancellation";
    }

    public static final class ReinstatementLapseChangeReason {
        public static final String LAPSE_CHANGED_ERROR_ON_ORIGINAL_LAPSE = "Lapse Changed: error on original lapse";
    }

    public static final class PremiumAuditPolicyPremiumAuditTransactions {
        public static final String AP_RP = "AP/RP";
        public static final String AUDIT = "Audit #";
        public static final String AUDIT_TRANSACTION_TYPE = "Audit Transaction Type";
        public static final String AUDIT_PROCESS_DATE = "Audit Process Date";
        public static final String COMMISIONS = "Commission";
        public static final String FEE = "Fee";
        public static final String FINAL_AP_RP = "Final AP/RP";
        public static final String TAX = "Tax";
    }

    public static final class PremiumAuditMethod {
        public static final String AUDIT_COMPLETED = "Audit Completed";
        public static final String AUDIT_REVISED = "Audit Revised";
        public static final String MAIL_AUDIT = "Mail Audit";
        public static final String MAIN_AUDIT = "Main Audit";
        public static final String PHYSICAL_AUDIT = "Physical Audit";
        public static final String PHONE_AUDIT = "Phone Audit";
    }

    public static final class PremiumAuditStage {
        public static final String IDENTIFIED = "Identified";
        public static final String IN_REVIEW = "In Review";
        public static final String NOT_YET_IDENTIFIED = "Not Yet Identified";
    }

    public static final class ProductTable {
        public static final String PRODUCT_NAME = "Product Name";
        public static final String PRODUCT_CODE = "Product Code";
        public static final String COPY_PRODUCT = "Copy Product";
    }
}
