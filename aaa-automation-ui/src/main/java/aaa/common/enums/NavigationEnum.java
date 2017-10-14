/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.enums;

public class NavigationEnum {

	 public enum AppMainTabs {
	        BILLING("Billing"),
	        CASE("Case"),
	        CUSTOMER("Customer"),
	        MY_WORK("My Work"),
	        POLICY("Policy"),
	        QUOTE("Quote"),
	        REPORTS("Reports");

	        String id;

	        AppMainTabs(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum AdminAppMainTabs {
	        AGENCY_VENDOR("Agency/Vendor"),
	        BILLING("Billing"),
	        CEM("CEM"),
	        CMS("CMS"),
	        COMMISSION("Commission"),
	        CRM("CRM"),
	        GENERAL("General"),
	        PRODUCT("Product"),
	        REPORTS("Reports"),
	        SECURITY("Security"),
	        TAXES_FEES("Taxes/Fees"),
	        WORK_FLOW("Work Flow"),
	        ADMINISTRATION("Administration");

	        String id;

	        AdminAppMainTabs(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum AdminAppLeftMenu {
	        AGENCY_VENDOR_AGENCY("Agency", ""),
	        AGENCY_VENDOR_AGENCY_TRANSFER("Agency Transfer", ""),
	        AGENCY_VENDOR_BRAND("Brand", ""),
	        AGENCY_VENDOR_CARRIER_INFO("Carrier Info", ""),
	        AGENCY_VENDOR_FEE_GROUP("Fee Group", ""),
	        AGENCY_VENDOR_FEE_REGISTRY("Fee Registry", ""),
	        AGENCY_VENDOR_FEE_STRATEGY("Fee Strategy", ""),
	        AGENCY_VENDOR_PARTNER("Partner", ""),
	        AGENCY_VENDOR_TAX_REGISTRY("Tax Registry", ""),
	        AGENCY_VENDOR_VENDOR("Vendor", ""),
	        BILLING_BILLING_CYCLE("Billing Cycle", ""),
	        BILLING_GLOBAL("Global", ""),
	        BILLING_RULES("Rules", ""),
	        CEM_CAMPAIGNS("Campaigns", ""),
	        CEM_CEM_CONFIGURATION("CEM Configuration", " "),
	        CEM_GROUPS_INFORMATION("Groups Information", ""),
	        CEM_MAJOR_LARGE_ACCOUNT("Major/Large Account", ""),
	        CMS_EFOLDER_MGMT("Efolder Mgmt", ""),
	        CMS_PRINT_MONITOR("Print Monitor", ""),
	        CMS_TEMPLATE_MGMT("Template Mgmt", ""),
	        COMMISSION_BULK_ADJUSTMENT("Bulk Adjustment", ""),
	        COMMISSION_BULK_STRATEGY("Bulk Strategy", ""),
	        COMMISSION_COMMISSION_BONUS("Commission Bonus", ""),
	        COMMISSION_COMMISSION_CLAWBACK("Commission Clawback", ""),
	        COMMISSION_COMMISSION_GROUP("Commission Group", ""),
	        COMMISSION_COMMISSION_TEMPLATE("Commission Template", ""),
	        COMMISSION_COMMISSION_HOLD("Commission Hold", ""),
	        COMMISSION_COMMISSION_REFERRAL("Commission Referral", ""),
	        CRM_CUSTOMER_CORE_ADMIN("Customer Core Admin", ""),
	        GB_COMMISSION_COMMISSION_STRATEGY("GB Commission Strategy", ""),
	        GENERAL_ASYNC_TASKS("Async Tasks", ""),
	        GENERAL_BULLETIN("Bulletin", ""),
	        GENERAL_CATASTROPHE_REGISTRY("Catastrophe Registry", ""),
	        GENERAL_DB_LOOKUPS("DB Lookups", ""),
	        GENERAL_NOTES("Notes", ""),
	        GENERAL_NUMBER_RANGES("# Ranges", ""),
	        GENERAL_SCHEDULER("Scheduler", ""),
	        PC_COMMISSION_COMMISSION_STRATEGY("P&C Commission Strategy", ""),
	        PRODUCT_AUTOMATED_PROCESSING("Automated Processing", ""),
	        PRODUCT_ENDORSEMENT_FORM_RELATIONSHIP("Endorsement Form Relationship", ""),
	        PRODUCT_MORATORIUM("Moratorium", ""),
	        PRODUCT_POLICY_PLAN("Policy Plan", ""),
	        PRODUCT_PRODUCT_FACTORY("Product Factory", ""),
	        PRODUCT_SCHEME("Scheme", ""),
	        PRODUCT_SHORT_RATE_CANCELLATION("Short Rate Cancellation", ""),
	        SECURITY_AUTHORITY("Authority", ""),
	        SECURITY_GROUP("Group", ""),
	        SECURITY_PRODUCT_ACCESS_ROLE("Product Access Role", ""),
	        SECURITY_PROFILE("Profile", "profile-main-flow"),
	        SECURITY_ROLE("Role", "role-flow"),
	        TAXES_FEES_FEE_AND_TAX_REGISTRY("Fee and Tax Registry", ""),
	        TAXES_FEES_FEE_AND_TAX_STRATEGY("Fee and Tax Strategy", ""),
	        TAXES_FEES_FEE_GROUP("Fee Group", ""),
	        WORK_FLOW_PROCESS_MANAGEMENT("Process Management", ""),
			UPLOAD_TO_VIN_TABLE("Upload to VIN table", ""),
			GENERATE_PRODUCT_SCHEMA("Generate Product Schema", "");

	        String id;
	        String flow;

	        AdminAppLeftMenu(String id, String flow) {
	            this.id = id;
	            this.flow = flow;
	        }

	        public String getFlow() {
	            return flow;
	        }

		    public String get() {
			    return id;
		    }
	    }

	    public enum AdminAppSubTabs {
	        AGENCY_VENDOR_CARRIER_INFO_BANKING_DETAILS("Banking Details"),
	        AGENCY_VENDOR_CARRIER_INFO_COMPANIES("Companies"),
	        AGENCY_VENDOR_CARRIER_INFO_CORPORATE_INFO("Corporate Info"),
	        BILLING_BILLING_CYCLE_BILLS("Bills"),
	        BILLING_BILLING_CYCLE_CALENDARS("Calendars"),
	        BILLING_BILLING_CYCLE_CANCELLATIONS("Cancellations"),
	        BILLING_BILLING_CYCLE_EFT_RECURRING("EFT/Recurring"),
	        BILLING_BILLING_CYCLE_WRITE_OFF("Write-Off"),
	        BILLING_GLOBAL_AUTOMATIC_BILLING_ACCOUNT("Automatic Billing Account"),
	        BILLING_GLOBAL_BILLING_HOLD_MORATORIUM_REASONS("Billing Hold & Moratorium Reasons"),
	        BILLING_GLOBAL_CROSS_POLICY_PAYMENT_TRANSFERS("Cross Policy Payment Transfers"),
	        BILLING_GLOBAL_DECLINE_PAYMENT_REASON_TYPE("Decline Payment Reason & Type"),
	        BILLING_GLOBAL_DOWN_PAYMENT_REASON("Down Payment Reason"),
	        BILLING_GLOBAL_OTHER_TRANSACTIONS("Other Transactions"),
	        BILLING_GLOBAL_PAYMENT_TIME("Payment Time"),
	        BILLING_GLOBAL_TRANSFER_PAYMENT_REASON_TYPE("Transfer Payment Reason & Type"),
	        BILLING_RULES_BILLING_FEES("Billing Fees"),
	        BILLING_RULES_NO_APPROVAL_REQUIRED_MAX("No Approval Required Max"),
	        BILLING_RULES_OFF_CYCLE("Off-Cycle"),
	        BILLING_RULES_OUTSTANDING_AMOUNT("Outstanding Amount"),
	        BILLING_RULES_PAYMENT_DEFICIENCY("Payment Deficiency"),
	        BILLING_RULES_PAYMENT_PLANS("Payment Plans"),
	        BILLING_RULES_PAYMENT_TOLERANCE("Payment Tolerance"),
	        BILLING_RULES_REFUND_APPROVAL_WAIT_DAYS("Refund Approval & Wait Days"),
	        BILLING_RULES_RENEWAL_HOLD_("Renewal Hold "),
	        BILLING_RULES_RENEWAL_PARAMETERS("Renewal Parameters"),
	        BILLING_RULES_SKIPPED_BILLS("Skipped Bills"),
	        BILLING_RULES_WRITE_OFF("Write-Off"),
	        CMS_EFOLDER_MGMT_DOCUMENT_TYPES("Document Types"),
	        CMS_EFOLDER_MGMT_EFOLDER_TREE("Efolder Tree"),
	        CMS_EFOLDER_MGMT_PRODUCT_SUPPORT("Product Support"),
	        CMS_PRINT_MONITOR_PRINT_DOCS("Print Docs"),
	        CMS_PRINT_MONITOR_PRINT_JOBS("Print Jobs"),
	        CMS_PRINT_MONITOR_PRINT_REQS("Print Reqs"),
	        CMS_TEMPLATE_MGMT_DOC_PACKAGES("Doc Packages"),
	        CMS_TEMPLATE_MGMT_DOC_TEMPLATES("Doc Templates"),
	        CMS_TEMPLATE_MGMT_FILES("Files");

	        String id;

	        AdminAppSubTabs(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum AgencyVendorTab {
	        AGENCY_INFO("Agency Info"),
	        BANKING_DETAILS("Banking Details"),
	        CHILDREN("Children"),
	        COMMISSION("Commission"),
	        CONTACT_INFO("Contact Info"),
	        SUPPORT_TEAM("Support Team"),
	        USERS("Users");

	        String id;

	        AgencyVendorTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum AutoCaTab {
	    	PREFILL("Prefill"),
	    	GENERAL("General"),
	    	DRIVER("Driver"),
	    	MEMBERSHIP("Membership"),
	    	VEHICLE("Vehicle"),
	    	ASSIGNMENT("Assignment"),
	    	FORMS("Forms"),
	        PREMIUM_AND_COVERAGES("Premium & Coverages"),
	        DRIVER_ACTIVITY_REPORTS("Driver Activity Reports"),
	        DOCUMENTS_AND_BIND("Documents & Bind");

	        String id;

	        AutoCaTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum AutoSSTab {
	    	PREFILL("Prefill"),
	    	GENERAL("General"),
	    	DRIVER("Driver"),
	    	RATING_DETAIL_REPORTS("Rating Detail Reports"),
	    	VEHICLE("Vehicle"),
	    	ASSIGNMENT("Assignment"),
	    	FORMS("Forms"),
	        PREMIUM_AND_COVERAGES("Premium & Coverages"),
	        DRIVER_ACTIVITY_REPORTS("Driver Activity Reports"),
	        DOCUMENTS_AND_BIND("Documents & Bind");

	        String id;

	        AutoSSTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum CaliforniaEarthquakeTab {
	        GENERAL("General"),
	        PROPERTY_INFO("Property Info"),
	        PREMIUMS_AND_COVERAGES("Premiums & Coverages"),
	        MORTGAGEE_AND_ADDITIONAL_INTERESTS("Mortgagee & Additional Interests"),
	        DOCUMENTS("Documents"),
	        BIND("Bind");

	        String id;

	        CaliforniaEarthquakeTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum HomeCaTab {
	        GENERAL("General"),
	        APPLICANT("Applicant"),
	        PROPERTY_INFO("Property Info"),
	        REPORTS("Reports"),
		    ENDORSEMENT("Endorsement"),
		    PREMIUMS_AND_COVERAGES("Premium & Coverages"),
		    PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING("Product Offering"),
		    PREMIUMS_AND_COVERAGES_ENDORSEMENT("Endorsement"),
		    PREMIUMS_AND_COVERAGES_ENDORSEMENT_OTHER_ENDORSEMENTS("Other Endorsements"),
		    PREMIUMS_AND_COVERAGES_ENDORSEMENT_SCHEDULED_PERSONAL_PROPERTY("Scheduled Personal Property"),
		    PREMIUMS_AND_COVERAGES_SCHEDULED_PP("Scheduled Personal Property"),
		    PREMIUMS_AND_COVERAGES_QUOTE("Quote"),
		    MORTGAGEE_AND_ADDITIONAL_INTERESTS("Mortgagee & Additional Interests"),
	        UNDERWRITING_AND_APPROVAL("Underwriting & Approval"),
	        DOCUMENTS("Documents"),
	        BIND("Bind");

	        String id;

	        HomeCaTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum HomeSSTab {
	        GENERAL("General"),
	        APPLICANT("Applicant"),
            PROPERTY_INFO("Property Info"),
            REPORTS("Reports"),
            ENDORSEMENT("Endorsement"),
            PREMIUMS_AND_COVERAGES("Premiums & Coverages"),
            PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING("Product Offering"),
            PREMIUMS_AND_COVERAGES_ENDORSEMENT("Endorsement"),
		    PREMIUMS_AND_COVERAGES_ENDORSEMENT_OTHER_ENDORSEMENTS("Other Endorsements"),
		    PREMIUMS_AND_COVERAGES_ENDORSEMENT_SCHEDULED_PERSONAL_PROPERTY("Scheduled Personal Property"),
		    PREMIUMS_AND_COVERAGES_QUOTE("Quote"),
		    MORTGAGEE_AND_ADDITIONAL_INTERESTS("Mortgagee & Additional interests"),
            UNDERWRITING_AND_APPROVAL("Underwriting & Approval"),
            DOCUMENTS("Documents"),
            BIND("Bind");

	        String id;

	        HomeSSTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum PersonalUmbrellaTab {
	        PREFILL("Prefill"),
	        GENERAL("General"),
	        UNDERLYING_RISKS("Underlying Risks"),
	        UNDERLYING_RISKS_PROPERTY("Property"),
	        UNDERLYING_RISKS_AUTO("Auto"),
	        UNDERLYING_RISKS_OTHER_VEHICLES("Other Vehicles"),
	        UNDERLYING_RISKS_ALL_RESIDENTS("All Residents"),
	        CLAIM("Claims"),
	        ENDORSEMENTS("Endorsements"),
	        PREMIUM_AND_COVERAGES("Premium & Coverages"),
	        PREMIUM_AND_COVERAGES_QUOTE("Quote"),
	        UNDERWRITING_AND_APPROVAL("Underwriting & Approval"),
	        DOCUMENTS("Documents"),
	        BIND("Bind");

	        String id;

	        PersonalUmbrellaTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    /**
	     * Use NavigationPage.toMainTab() method
	     */
	    public enum CustomerSummaryTab {
	    	CUSTOMER("Customer Info"),
	        ACCOUNT("Account"),
	        CAMPAIGN("Campaign"),
	        COMMUNICATION("Communication");
	        
	        String id;

	        CustomerSummaryTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }
	    
	    /**
	     * Use NavigationPage.toViewSubTab() method
	     */
	    public enum CustomerSummarySecondaryTab {
	    	PORTFOLIO("Portfolio"),
		    CONTACTS_RELATIONSHIPS("Contacts & Relationships");
	
	        String id;
	
	        CustomerSummarySecondaryTab(String id) {
	            this.id = id;
	        }
	
	        public String get() {
	            return id;
	        }
	    }

	    public enum CustomerTab {
	        EMPLOYEE_INFO("Employee Info"),
	        GENERAL("General"),
	        RELATIONSHIP("Relationship");

	        String id;

	        CustomerTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum QuoteProposalTab {
	        PROPOSAL("Proposal"),
	        QUOTES_SELECTION("Quotes Selection");

	        String id;

	        QuoteProposalTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum AccountTab {
	        ACCOUNT_INFO("Acct. Info"),
	        CONTACTS("Designated Contacts"),
	        GROUPS("Affinity Groups");

	        String id;

	        AccountTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }

	    public enum ReportsTab {
	        BUSINESS_ACTIVITIES("Business Activities"),
	        OPERATIONAL_REPORTS("Operational Reports"),
	        DASHBOARD("Dashboard"),
	        TEMPLATES("Templates");

	        String id;

	        ReportsTab(String id) {
	            this.id = id;
	        }

	        public String get() {
	            return id;
	        }
	    }
}
