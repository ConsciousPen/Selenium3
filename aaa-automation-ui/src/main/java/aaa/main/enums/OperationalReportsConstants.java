/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class OperationalReportsConstants {


    public static final String EUW_DETAIL = "PAS Earned / Unearned / Written (EUW) - Detail";

    private OperationalReportsConstants() {
    }

    public static final class DeliveryType {

        public static final String EMAIL = "E-Mail";
        public static final String DOWNLOAD = "Download";

        private DeliveryType(){}
    }

    public static final class BusinessUnit {
        public static final String CSAA_FIRE_CASUALTY_INSURANCE_COMPANY = "CSAA Fire & Casualty Insurance Company";
        public static final String CSAA_AFFINITY_INSURANCE_COMPANY = "CSAA Affinity Insurance Company";
        public static final String CSAA_GENERAL_INSURANCE_COMPANY = "CSAA General Insurance Company";
        public static final String CSAA_INTERINSURANCE_BUREAU = "CSAA Interinsurance Bureau";
    }

    public static final class RolesPriviliges {
        public static final String REPORTS_OPERATIONAL_EUW_SCHEDULE = "Reports:Operational:euwPremiumReport.xml:Schedule";
        public static final String REPORTS_OPERATIONAL_EUW_VIEW = "Reports:Operational:euwPremiumReport.xml:View";
    }

    public static final class EuwDetailOpReportTableHeaders {
        public static final String BUSINESS_UNIT = "Business Unit";
        public static final String POLICY_NUMBER = "Policy Number";
        public static final String POLICY_EFFECTIVE_DATE = "Policy Effective Date";
        public static final String POLICY_EXPIRATION_DATE = "Policy Expiration Date";
        public static final String POLICY_LENGTH_OF_TERM_DAYS = "Policy Length of Term (Days)";
        public static final String WRITTEN_PREMIUM = "Written Premium";
        public static final String UNEARNED_PREMIUM = "Unearned Premium";
        public static final String EARNED_PREMIUM_CHANGE_IN_UNEARNED = "Earned Premium (Change in Unearned)";
        public static final String CHANGE_IN_UNEARNED_MTD = "Change in Unearned (MTD)";
        public static final String CHANGE_IN_UNEARNED_YTD = "Change in Unearned (YTD)";

    }
}
