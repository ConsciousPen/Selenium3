/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class CustomerConstants {
	public static final String ACCOUNT_HAS_MORE_THAN_ONE_CUSTOMER = "Account has more than one Customer. Select one from the list below.";
    public static final String ACTION = "Action";
    public static final String ADDRESS = "Address";
    public static final String ADDRESS_LINE_1 = "Address Line 1";
    public static final String ADDRESS_LINE_1_IS_REQUIRED_FOR_CUSTOMER = "Address Line 1 is required for customer";
    public static final String ADDRESS_LINE_2 = "Address Line 2";
    public static final String ADDRESS_LINE_3 = "Address Line 3";
    public static final String ADDRESS_VALIDATED = "Address Validated?";
    public static final String ADDRRESS_TYPE = "Address Type";
    public static final String ARE_YOU_SURE_YOU_WANT_CHANGE_RELATIONSHIP_TYPE =
            "Are you sure you want to change Relationship Type? All related Relationship information will be lost. Press 'Yes' to continue.";
    public static final String ARE_YOU_SURE_YOU_WANT_TO_CONTINUE = "Are you sure you want to continue? All unsaved data will be lost.";
    public static final String ARE_YOU_SURE_YOU_WANT_TO_REMOVE_ROW = "Are you sure you want to remove the row?";
    public static final String ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THIS_SECTION = "Are you sure you want to remove this section?";
    public static final String ATTENTION = "Attention";
    public static final String CHANNEL = "Channel";
    public static final String CHAT = "Chat";
    public static final String CHAT_ID = "Chat ID";
    public static final String CHAT_ID_IS_REQUIRED = "Chat ID is required";
    public static final String CHAT_TYPE = "Chat Type";
    public static final String CITY = "City";
    public static final String CITY_IS_REQUIRED_FOR_CUSTOMER = "City is required for customer";
    public static final String COMMENT = "Comment";
    public static final String CONTACT_DETAILS = "Contact Details";
    public static final String CONTACT_METHOD = "Contact Method";
    public static final String CONTACT_TYPE = "Contact Type";
    public static final String COUNTRY = "Country";
    public static final String DATE_BUSINESS_STARTED = "Date business started";
    public static final String DATE_CANNOT_BE_BLANK = "Date cannot be blank";
    public static final String DATE_OF_BIRTH = "Date of Birth";
    public static final String DESCRIPTION = "Description";
    public static final String DESCRIPTION_SIZE_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_255_SYMBOLS = "Description size should be less than or equal to 255 symbols";
    public static final String DO_NOT_SOLICIT = "Do not Solicit?";
    public static final String DO_YOU_WANT_TO_DELETE_LEAD_OBJECT_NOW = "This Relationship is a Lead in Unqualified status. Do you want to delete Lead object now?";
    public static final String DO_YOU_WANT_TO_STARTE_LEAD_QUALIFICATION = "Do you want to start Lead qualification?";
    public static final String EIN = "EIN";
    public static final String EIN_IS_REQUIRED = "EIN is required";
    public static final String EMAIL = "Email";
    public static final String EMAIL_ADDRESS = "Email Address";
    public static final String EMAIL_TYPE = "Email Type";
    public static final String FILL_IN_OR_REMOVE_SIC_CLASSIFICATION = "Fill in or remove SIC Classification";
    public static final String FIRST_NAME = "First Name";
    public static final String FIRST_NAME_IS_REQUIRED_FOR_RELATIONSHIP = "First Name is required for relationship";
    public static final String GENDER = "Gender";
    public static final String INQUIRY = "Inquiry";
    public static final String INTEGER_PART_OF_VALUE_CANNOT_EXCEED_17_DIGITS = "%s: Integer-part of value cannot exceed 17 digits";
    public static final String INVALID = "Invalid";
    public static final String IN_QUALIFICATION = "In Qualification";
    public static final String LAST_NAME = "Last Name";
    public static final String LAST_NAME_IS_REQUIRED_FOR_RELATIONSHIP = "Last Name is required for relationship";
    public static final String LEAD_IN_INVALID_STATUS_WILL_BE_UNAVAILABLE_FOR_FURTHER_UPDATES = "Lead in Invalid status will be unavailable for further updates. Are you sure you want to proceed?";
    public static final String LEAD_STATUS = "Lead Status";
    public static final String LIKELIHOOD = "Likelihood";
    public static final String LINE_OF_BUSINESS = "Line of Business";
    public static final String MAKE_INVALID = "Make Invalid";
    public static final String MARITAL_STATUS = "Marital Status";
    public static final String MIDDLE_NAME = "Middle Name";
    public static final String MOVE_CUSTOMER = "Move Customer";
    public static final String NAME_DBA = "Name - DBA";
    public static final String NAME_LEGAL = "Name - Legal";
    public static final String NAME_LEGAL_IS_REQUIRED = "Name - Legal is required";
    public static final String NON_INDIVIDUAL_TYPE = "Non-Individual Type";
    public static final String NON_INDIVIDUAL_TYPE_IS_REQUIRED_FOR_CUSTOMER = "Non-Individual Type is required";
    public static final String PHONE = "Phone";
    public static final String PHONE_NUMBER = "Phone Number";
    public static final String PHONE_TYPE = "Phone Type";
    public static final String POTENTIAL = "Potential";
    public static final String PREFERRED = "Preferred?";
    public static final String PRODUCT_NAME = "Product Name";
    public static final String QUALIFIED = "Qualified";
    public static final String QUALIFY = "Qualify";
    public static final String RELATIONSHIP_TO_CUSTOMER = "Relationship to Customer";
    public static final String RELATIONSHIP_TYPE_IS_MANDATORY = "Relationship type is mandatory";
    public static final String SIC_CLASSIFICATION = "SIC Classification";
    public static final String SOCIAL_NET = "Social Net";
    public static final String START_QUALIFICATION = "Start Qualification";
    public static final String STATE = "State/Province";
    public static final String STATE_IS_REQUIRED_FOR_CUSTOMER = "State is required for customer";
    public static final String STATUS_QUALIFIED_ALLOWS_TO_START_QUOTATION = "Status 'Qualified' allows to start quotation. Do you want to proceed with status change?";
    public static final String SUCH_CONTACT_DETAILS_INFORMATION_ALREADY_EXISTS = "Such contact details information already exists. Please check entered information";
    public static final String SUFFIX = "Suffix";
    public static final String TAX = "Tax Identification";
    public static final String TITLE = "Title";
    public static final String TYPE = "Type";
    public static final String UNQUALIFIED = "Unqualified";
    public static final String UPDATE = "Update";
    public static final String WEB_ADDRESS = "Web URL";
    public static final String WEB_TYPE = "Web URL Type";
    public static final String WEB_URL = "Web URL";
    public static final String WEB_URL_IS_REQUIRED = "Web URL is required";
    public static final String YOU_SHOULD_CHOOSE_AT_LEAST_ONE_PRODUCT = "You should choose at least one product";
    public static final String ZIP_CODE = "Zip/Post Code";
    public static final String ZIP_CODE_IS_REQUIRED_FOR_CUSTOMER = "Zip Code is required for customer";
    public static final String GENERATE_ON_DEMAND_DOCUMENT = "Generate On Demand Document";
    public static final String ARCHIVED = "Archived";

    private CustomerConstants() {}

    public static final class CustomerContactsTable {
        public static final String CONTACT_TYPE = "Contact Type";
        public static final String ACTION = "Action";
    }

    public static final class CustomerInformationTable {
        public static final String NAME_DBA = "Name - DBA";
    }

    public static final class AdditionalNamesTable {
        public static final int FIRST_ROW = 1;
        public static final String SALUTATION = "Salutation";
        public static final String FIRST_NAME = "First Name";
        public static final String MIDDLE_NAME = "Middle Name";
        public static final String LAST_NAME = "Last Name";
        public static final String SUFFIX = "Suffix";
        public static final String DESIGNATION = "Designation";
        public static final String NAME_DBA = "Name-DBA";
    }

    public static final class CustomerQuotesTable {
        public static final String QUOTE = "Quote";
    }

    public static final class CustomerOpportunityTable {
        public static final String STATUS = "Status";
        public static final String ID = "ID";
        public static final String LIKELIHOOD = "Likelihood";
    }

    public static final class CustomerCommunicationsTable {
        public static final String ENTITY_REFERENCE_ID = "Entity Reference ID";
        public static final String CHANNEL = "Channel";
        public static final String ACTION = "Action";
    }

    public static final class CustomerBusinessEntitiesTable {
        public static final String DATE_BUSINESS_STARTED = "Date Business started";
    }

    public static final class CustomerDivisionsTable {
        public static final String BILLING_METHOD = "Billing Method";
    }

    public static final class CustomerEmploymentCensusTable {
        public static final String PARTICIPANT_ID = "Participant Id";
        public static final String NAME = "Name";
    }

    public static final class CustomerStudentCensusTable {
        public static final String PARTICIPANT_ID = "Participant Id";
        public static final String NAME = "Name";
    }

    public static final class CustomerMembershipCensusTable {
        public static final String PARTICIPANT_ID = "Participant Id";
        public static final String NAME = "Name";
    }

    public static final class CustomerNewProductDetailsTable {
        public static final String POLICY_TYPE = "Policy Type";
    }

    public static final class CustomerProductsTable {
        public static final String ACTION = "Action";
    }
}
