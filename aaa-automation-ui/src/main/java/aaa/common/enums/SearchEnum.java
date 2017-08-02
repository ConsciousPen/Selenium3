/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.enums;

public class SearchEnum {

    public enum SearchBy {
        POLICY_QUOTE("Policy/Quote #"),
        PRODUCT_ID("Product ID"),
        AGENT_OF_RECORD("Agent of Record"),
        AGENT("Agent #"),
        /*CLAIM("Claim #"),
        DATE_OF_LOSS("Date of Loss"),*/
        ACCOUNT("Account #"),
        BILLING_ACCOUNT("Billing Account #"),
        FIRST_NAME("First Name"),
        LAST_NAME("Last Name"),
        /*ADDRESS_LINE_1("Address Line 1"),
        ADDRESS_LINE_2("Address Line 2"),*/
        CITY("City"),
        STATE("State"),
        //STATE_PROVINCE("State/Province"),
        ZIP_CODE("Zip Code"),
        PHONE("Phone #"),
        CUSTOMER("Customer #"),
        AGENCY_NAME("Agency Name"),
        AGENCY("Agency #"),
        UNDERWRITING_COMPANY("Underwriting Company #"),
        SSN("SSN");
        /*POLICY_TITLE("Policy Title"),
        BROKER_NAME("Broker Name"),
        BROKER("Broker #");*/

        final String id;

        SearchBy(String id) {
            this.id = id;
        }

        public String get() {
            return id;
        }
    }

    public enum SearchFor {
        ACCOUNT("Account"),
        CUSTOMER("Customer"),
        BILLING("Billing"),
        POLICY("Policy"),
        QUOTE("Quote");
        /*CLAIM("Claim"),
        NEW_CLAIM("NewClaim"),
        PARTY("Party");*/

        final String id;

        SearchFor(String id) {
            this.id = id;
        }

        public String get() {
            return id;
        }
    }
}
