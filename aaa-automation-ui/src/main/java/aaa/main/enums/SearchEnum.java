/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.enums;

public class SearchEnum {

    public enum SearchBy {
        POLICY_QUOTE("Policy/Quote #"),
        PRODUCT_ID("Product ID"),
        AGENT_OF_RECORD("Agent of Record"),
        AGENT("Agent #"),
        ACCOUNT("Account #"),
        BILLING_ACCOUNT("Billing Account #"),
        FIRST_NAME("First Name"),
        LAST_NAME("Last Name"),
        CITY("City"),
        STATE("State"),
        ZIP_CODE("Zip Code"),
        PHONE("Phone #"),
        CUSTOMER("Customer #"),
        AGENCY_NAME("Agency Name"),
        AGENCY("Agency #"),
        UNDERWRITING_COMPANY("Underwriting Company #"),
        SSN("SSN");

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

        final String id;

        SearchFor(String id) {
            this.id = id;
        }

        public String get() {
            return id;
        }
    }
}
