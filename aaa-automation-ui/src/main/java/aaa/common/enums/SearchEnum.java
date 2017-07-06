/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.enums;

public class SearchEnum {

    public enum SearchBy {
        POLICY_QUOTE("Policy/Quote #"),
        CLAIM("Claim #"),
        DATE_OF_LOSS("Date of Loss"),
        FIRST_NAME("First Name"),
        LAST_NAME("Last Name"),
        ADDRESS_LINE_1("Address Line 1"),
        ADDRESS_LINE_2("Address Line 2"),
        ZIP_CODE("Zip Code"),
        CITY("City"),
        STATE_PROVINCE("State/Province"),
        ACCOUNT("Account #"),
        CUSTOMER("Customer #"),
        POLICY_TITLE("Policy Title"),
        BILLING_ACCOUNT("Billing Account #"),
        BROKER_NAME("Broker Name"),
        BROKER("Broker #");

        String id;

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
        QUOTE("Quote"),
        POLICY("Policy"),
        BILLING("Billing"),
        CLAIM("Claim"),
        NEW_CLAIM("NewClaim"),
        PARTY("Party");

        String id;

        SearchFor(String id) {
            this.id = id;
        }

        public String get() {
            return id;
        }
    }
}
