/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class ClaimConstants {

    private ClaimConstants() {}

    public static final class ClaimStatus {
        public static final String NOTIFICATION = "Notification";
        public static final String INITIAL = "Initial";
        public static final String OPEN = "Open";
        public static final String REOPENED = "Reopened";
        public static final String CLOSED = "Closed";
        public static final String SIU_POTENTIAL = "SIU Potential";
        public static final String SIU_REVIEW = "SIU Review";
    }

    public static final class ClaimPolicyStatus {
        public static final String ACTIVE = "Active";
        public static final String NO_POLICY = "No Policy";
    }

    public static final class ClaimVersionType {
        public static final String UPDATED = "Updated";
        public static final String FNOL = "FNOL";
    }

    public static final class ClaimReason {
        public static final String UPDATED = "Claim Updated";
        public static final String CREATED = "Claim Created";
    }

    public static final class SpecialHandlingStatus {
        public static final String SUBROGATION = "Subrogation";
        public static final String SECURE_CLAIM = "Secure Claim";
    }

    public static final class FeatureStatus {
        public static final String OPEN = "Open";
        public static final String CLOSED = "Closed";
        public static final String PENDING = "Pending";
    }

    public static final class ClaimCommercialDamage {
        public static final String AUTO = "Auto";
        public static final String BODILY_INJURY = "Bodily Injury";
        public static final String BUSINESS_INTERRUPTION = "Business Interruption";
        public static final String BUSINESS_PERSONAL_PROPERTY = "Business Personal Property";
        public static final String FINANCIAL = "Financial";
        public static final String OTHER_PROPERTY = "Other Property";
        public static final String PERSONAL_ADVERTISING_INJURY = "Personal & Advertising Injury";
        public static final String PROPERTY = "Property";
    }

    public static final class ClaimAutoDamage {
        public static final String VEHICLE = "Vehicle";
        public static final String INJURY = "Injury";
        public static final String OTHER_PROPERTY = "Other Property";
    }

    public static final class ClaimHomeDamage {
        public static final String HOME = "Home";
        public static final String INJURY = "Injury";
        public static final String OTHER_PROPERTY = "Other Property";
    }

    public static final class ClaimHomeBaseDamage {
        public static final String INJURY = "Injury";
        public static final String OTHER_PROPERTY = "Other Property";
    }

    public static final class ClaimDamage {
        public static final String PROPERTY = "Property";
        public static final String HOME = "Home";
        public static final String VEHICLE = "Vehicle";
        public static final String INJURY = "Injury";
        public static final String OTHER_PROPERTY = "Other Property";
    }

    public static final class FeatureReserveHistoryTransaction {
        public static final String ADD_FEATURE_RESERVE = "Add Feature Reserve";
        public static final String UPDATE_FEATURE_RESERVE = "Update Feature Reserve";
    }

    public static final class FeatureReserveHistoryTransactionComment {
        public static final String FEATURE_CLOSED = "Feature Closed.";
    }

    public static final class FeatureReserveType {
        public static final String RECOVERY = "Recovery";
    }

    public static final class PaymentsAndRecoveriesTransactionStatus {
        public static final String ISSUED = "Issued";
        public static final String DECLINED = "Declined";
        public static final String CLEARED = "Cleared";
        public static final String VOIDED = "Voided";
        public static final String APPROVED = "Approved";
        public static final String PENDING = "Pending";
        public static final String STOP_REQUESTED = "Stop Requested";
        public static final String STOPPED = "Stopped";
    }

    public static final class PaymentsAndRecoveriesNote {
        public static final String NONE = "";
        public static final String FINAL = "Final";
        public static final String SUPPLEMENTAL = "Supplemental";
    }

    public static final class ClaimResponsiblePartiesTable {
        public static final String NAME = "Name";
    }

    public static final class ClaimSummaryOfPaymentsAndRecoveriesTable {
        public static final String TRANSACTION_STATUS = "Transaction Status";
        public static final String TOTAL_PAYMENT_AMOUNT = "Total Payment Amount";
        public static final String TOTAL_RECOVERY_AMOUNT = "Total Recovery Amount";
        public static final String TOTAL_REMAINING_AMOUNT = "Total Remaining Amount";
        public static final String PAYMENT_NUMBER = "Payment Number";
    }

    public static final class ClaimCreationResultTable {
        public static final String CLAIM_SEQ = "Claim Seq. #";
        public static final String CLAIM = "Claim #";
        public static final String CREATION_STATUS = "Creation Status";
    }

    public static final class ClaimAllDamagesTable {
        public static final String DAMAGE_TYPE = "Damage Type";
        public static final String DAMAGE_DESCRIPTION = "Damage Description";
        public static final String DAMAGE_NUMBER = "Damage Number";
        public static final String DAMAGE = "Damage";
    }

    public static final class ClaimAllFeaturesTable {
        public static final String FEATURE_NUMBER = "Feature Number";
        public static final String TOTAL_INCURREDS = "Total Incurreds";
        public static final String CLAIMANT = "Claimant";
        public static final String EMPTY = "";
    }

    public static final class ClaimFeaturePaymentsTable {
        public static final String TRANSACTION_STATUS = "Transaction Status";
        public static final String TOTAL_PAYMENT_AMOUNT = "Total Payment Amount";
    }

    public static final class ClaimSummaryOfPaymentSeriesTable {
        public static final String STATUS = "Status";
    }

    public static final class ClaimFeatureReserveHistoryTable {
        public static final String RESERVE_TYPE = "Reserve Type";
        public static final String OLD_RESERVE = "Old Reserve";
        public static final String NEW_RESERVE = "New Reserve";
        public static final String TRANSACTION = "Transaction";
    }

    public static final class ClaimHistoryTable {
        public static final String DATE = "Date";
    }

    public static final class ClaimDamageFeaturesTable {
        public static final String FEATURE_NUMBER = "Feature Number";
        public static final String TOTAL_INCURRED = "Total incurred";
        public static final String COVERAGE = "Coverage";
        public static final String FEATURE_STATUS = "Feature Status";
    }

    public static final class ClaimLossEventTable {
        public static final String CAUSE_OF_LOSS = "Cause of Loss";
        public static final String DESCRIPTION_OF_LOSS_EVENT = "Description of Loss Event";
        public static final String CLAIM_FILE_OWNER = "Claim File Owner";
        public static final String LOSS_LOCATION = "Loss Location";
    }

    public static final class ClaimPartiesTable {
        public static final String PARTY_NAME = "Party Name";
        public static final String ZIP_POSTAL_CODE = "Zip/Postal Code";
        public static final String CITY = "City";
        public static final String ADDRESS_LINE = "Address Line 1";
        public static final String STATE_PROVINCE = "State/Province";
        public static final String ROLE = "Role";
    }

    public static final class ClaimAccessControlListTable {
        public static final String NAME = "Name";
        public static final String TYPE = "Type";
        public static final String ACTION = "Action";
    }

    public static final class ClaimDamageInfoTable {
        public static final String DAMAGE_TYPE = "Damage Type";
        public static final String PARTY_TYPE = "Party Type";
        public static final String DAMAGE = "Damage";
        public static final String DAMAGE_DESCRIPTION = "Damage Description";
    }

    public static final class ClaimDamagePartiesTable {
        public static final String NAME = "Name";
        public static final String ROLE = "Role";
        public static final String ADDRESS = "Address";
        public static final String EMAIL = "Email";
    }

    public static final class ClaimDamageReserveHistoryTable {
        public static final String RESERVE_TYPE = "Reserve Type";
        public static final String NEW_RESERVE = "New Reserve";
        public static final String OLD_RESERVE = "Old Reserve";
        public static final String UPDATE_AMOUNT = "Update Amount";
        public static final String TRANSACTION = "Transaction";
    }

    public static final class ClaimVendorsTable {
        public static final String COMPANY_NAME = "Company Name";
        public static final String SERVICE_TYPE = "Service Type";
    }

    public static final class ClaimRiskItemTreeTable {
        public static final String INSURABLE_RISK = "Insurable Risk";
        public static final String DEDUCTIBLE_AMOUNT = "Deductible Amount";
        public static final String COVERAGE = "Coverage";
        public static final String COLLISION_DEDUCTIBLE = "Collision Deductible";
        public static final String HURRICANE_COVERAGE = "Hurricane Coverage";
        public static final String LIMIT_LEVEL = "Limit Level";
        public static final String LIMIT_AMOUNT = "Limit Amount";
    }

    public static final class ClaimDamageReservesTable {
        public static final String RESERVE = "Reserve";
        public static final String UNALLOCATED_AMOUNT = "Unallocated Amount";
        public static final String ALLOCATED_AMOUNT = "Allocated Amount";
        public static final String TOTAL_AMOUNT = "Total Amount";
    }

    public static final class ClaimListTable {
        public static final String CLAIM = "Claim #";
    }

    public static final class ClaimPolicyPartiesTable {
        public static final String FULL_NAME = "Full Name";
        public static final String ROLE_TYPE = "Role Type";
        public static final String PARTY_NAME = "Party Name";
    }

    public static final class ClaimsRatingDetails {
        public static  final  String NUMBER_OF_YEARS_CLAIMS_FREE = "Number of years claims free";
        public static final String PRIOR_CLAIMS = "Prior claims";
        public static final String PRIOR_CLAIMS_POINTS = "Prior claims points";
        public static final String AAA_CLAIMS = "AAA claims";
        public static final String AAA_CLAIMS_POINTS = "AAA claims points";
        public static final String POINTS = "Points";
        public static final String DATE = "Date";
        public static final String CLAIM = "Claim";
        public static final String CLAIM_1 = "Claim 1";
        public static final String CLAIM_2 = "Claim 2";
        public static final String CLAIM_3 = "Claim 3";
        public static final String CLAIM_4 = "Claim 4";
    }

    public static final class CauseOfLoss {
        public static final String HAIL = "Hail";
        public static final String POINTS = "Points";
        public static final String FIRE = "Fire";
        public static final String WATER = "Water";
        public static final String WIND = "Wind";
        public static final String THEFT = "Theft";
        public static final String LIABILITY = "Liability";
    }

    public static final class LossFor {
        public static final String APPLICANT = "Applicant";
        public static final String PROPERTY = "Property";
        public static final String APPLICANT_PROPERTY = "Applicant and Property";
        public static final String NEITHER = "Neither";
    }
}