/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class PolicyConstants {

    private PolicyConstants() {}

    public static final class PremiumWaiverErrorMessages {
        public static final String PREMIUM_WAIVER_START_DATE_IS_REQUIRED = "'Premium Waiver Start Date' is required";
        public static final String PREMIUM_WAIVER_START_DATE_DOES_NOT_FALL = "Premium Waiver Start Date does not fall in between " +
                "the Effective Date of the first Certificate Policy term and Expiration Date of the last Certificate Policy term.";
        public static final String PREMIUM_WAIVER_START_DATE_CANNOT_BE_APPLIED = "Premium Waiver Start Date cannot be applied on the " +
                "former Premium Waiver effective days.";
        public static final String PREMIUM_WAIVER_START_DATE_CANNOT_BE_AFTER_POLICY_CANCELLATION_DATE = "Premium Waiver Start Date cannot" +
                " be after the Policy Cancellation Date";
        public static final String PREMIUM_WAIVER_START_DATE_MUST_BE_FALL_IN_THE_CERTIFICATE_TERM = "Premium Waiver Start Date must fall in the" +
                " Certificate Policy term where status is Policy Active, Policy Pending or Cancellation Pending";

        public static final String PREMIUM_WAIVER_END_DATE_DOES_NOT_FALL = "Premium Waiver End Date does not fall in between the Effective" +
                " Date of the first Certificate Policy term and Expiration Date of the last Certificate Policy term.";
        public static final String PREMIUM_WAIVER_END_DATE_IS_REQUIRED = "'Premium Waiver End Date' is required";
        public static final String PREMIUM_WAIVER_END_DATE_IS_CANNOT_BE_PRIOR_THE_START_DATE = "Premium Waiver End Date cannot be prior the Premium Waiver " +
                "Start Date";
        public static final String REMOVE_WAIVER_END_DATE_CANNOT_BE_AFTER_POLICY_CANCELLATION_DATE = "Premium Waiver End Date cannot be after" +
                " the Policy Cancellation Date";
        public static final String PREMIUM_WAIVER_END_DATE_MUST_BE_FALL_IN_THE_CERTIFICATE_TERM =
                "Premium Waiver End Date must fall in the Certificate Policy term where status is Policy Active, Policy Pending or Cancellation Pending";
    }

    public static final class PremiumWaiverConfirmationPopUp {
        public static final String ARE_YOU_SURE_WANT_TO_ADD_PREMIUM_WAIVER = "Are you sure you want to add Premium Waiver to Certificate Policy?";
        public static final String ARE_YOU_SURE_WANT_TO_REMOVE_PREMIUM_WAIVER = "Are you sure you want to remove Premium Waiver from Certificate Policy?";
    }

    public static final class PolicyCertificatePoliciesTable {
        public static final String POLICY_NUMBER = "Policy Number";
    }

    public static final class PoliciesTable {
        public static final String POLICY = "Policy #";
    }

    public static final class PolicyErrorsTable {
        public static final String MESSAGE = "Message";
    }

    public static final class PolicyTransactionHistoryTable {
        public static final String TYPE = "Type";
        public static final String TRAN_PREMIUM = "Tran. Premium";
        public static final String TRANSACTION_DATE = "Transaction Date";
        public static final String REASON = "Reason";
        public static final String VERSION_STATE = "Version State";
        public static final String EFFECTIVE_DATE = "Effective Date";
        public static final String ENDING_PREMIUM = "Ending Premium";
        public static final String PERFORMER = "Performer";
        public static final String HASHTAG = "#";
    }

    public static final class PolicyHistoryTable {
        public static final String ACTUAL_EXPOSURE_AMOUNT = "Actual Exposure Amount";
        public static final String CHANGE_IN_EXPOSURE = "Change in Exposure";
    }

    public static final class PolicyCoverageSummaryTable {
        public static final String COVERAGE_NAME = "Coverage Name";
        public static final String PLAN = "Plan";
        public static final String CONTRIBUTION = "Contribution %";
        public static final String PARTICIPANTS = "Participants";
        public static final String VOLUME = "Volume";
        public static final String PAYOR = "Payor";
        public static final String PAYMENT_MODE = "Payment Mode";
        public static final String MODAL_PREMIUM = "Modal Premium";
        public static final String ANNUAL_PREMIUM = "Annual Premium";
    }

    public static final class PolicyCoveragePremiumSummaryTable {
        public static final String BILLABLE_PREMIUM = "Billable Premium";
    }

    public static final class PolicyGeneralInformationTable {
        public static final String BROKERAGE = "Brokerage";
        public static final String AGENCY_PRODUCER = "Agency / Producer";
        public static final String EFFECTIVE_DATE = "Effective Date";
        public static final String EXPIRATION_DATE = "Expiration Date";
    }

    public static final class PolicyClassInformationTable {
        public static final String CLASS = "Class";
        public static final String TRANSACTION_TYPE = "Transaction Type";
        public static final String AUDIT_PROCESS_DATE = "Audit Process Date";
        public static final String ORIGINAL_WRITTEN_PREMIUM = "Original Written Premium";
        public static final String ACTUAL_EXPOSURE_AMOUNT = "Actual Exposure Amount";
        public static final String CHANGE_IN_EXPOSURE = "Change in Exposure";
        public static final String AP_RP = "AP/RP";
        public static final String EARNED_PREMIUM = "Earned Premium";
    }

    public static final class PolicyDriversTable {
        public static final String NAME = "Name";
    }

    public static final class PolicyVehiclesTable {
        public static final String MAKE = "Make";
        public static final String YEAR = "Year";
    }

    public static final class PolicyRenewalsTable {
        public static final String PREMIUM = "Premium";
    }

    public static final class PolicyCoveragesTable {
        public static final String COVERAGE_NAME = "Coverage Name";
        public static final String PLAN = "Plan";
    }

    public static final class PolicyPremiumSummaryTable {
        public static final String TAXES = "Taxes";
        public static final String FEES = "Fees";
        public static final String BASE_PREMIUM = "Base Premium";
        public static final String FORM_PREMIUM = "Form Premium";
        public static final String BILLABLE_PREMIUM = "Billable Premium";
        public static final String AP_RP = "AP/RP";
        public static final String TERM_PREMIUM = "Term Premium";
        public static final String ACTION = "Action";
    }

    public static final class PolicyFormsTable {
        public static final String FORM_ID = "Form ID";
    }

    public static final class PolicyGroupCoveragesTable {
        public static final String COVERAGE = "Coverage";
        public static final String COVERAGE_NAME = "Coverage Name";
    }

    public static final class PolicyRiskItemsTable {
        public static final String ACTION = "Action";
    }

    public static final class PolicyInsuredInformationTable {
        public static final String NAME = "Name";
        public static final String CONTACT_NAME = "Contact Name";
    }

    public static final class PolicyPlanTable {
        public static final String PLAN = "Plan";
    }

    public static final class PolicyPremiumAuditTable {
        public static final String AUDIT_STAGE = "Audit Stage";
        public static final String AUDIT_METHOD = "Audit Method";
        public static final String AUDIT = "Audit #";
        public static final String AUDIT_PERIOD = "Audit Period";
    }

    public static final class PolicyTTTCoverageTable {
        public static final String ACTUAL_PREMIUM = "Actual Premium";
        public static final String TAXES = "Taxes";
        public static final String BILLABLE_PREMIUM = "Billable Premium";
        public static final String AP_RP = "AP/RP";
    }

    public static final class PolicyTTTLocationTable {
        public static final String LOCATION = "Location";
        public static final String AP_RP = "AP/RP";
        public static final String TAXES = "Taxes";
        public static final String BILLABLE_PREMIUM = "Billable Premium";
    }

    public static final class PolicyTTTVehicleTable {
        public static final String VEHICLE = "Vehicle";
        public static final String ACTUAL_PREMIUM = "Actual Premium";
        public static final String BILLABLE_PREMIUM = "Billable Premium";
        public static final String AP_RP = "AP/RP";
        public static final String TAXES = "Taxes";
    }

    public static final class PolicyIncludedAndSelectedEndorsementsTable {
        public static final String FORM_ID = "Form ID";
        public static final String NAME = "Name";
    }

    public static final class PolicyEndorsementFormsTable {
        public static final String DESCRIPTION = "Description";
    }
}
