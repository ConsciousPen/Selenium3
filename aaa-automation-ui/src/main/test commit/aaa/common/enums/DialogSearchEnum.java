/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.enums;

public class DialogSearchEnum {

	public enum SearchResultsTableColumn {
        BILLING_ACCOUNT_NUMBER("Billing Account #"),
        POLICY_NUMBER("Policy #"),
        FIRST_NAME("First Name"),
        LAST_NAME("Last Name");

        String id;

        SearchResultsTableColumn(String id) {
            this.id = id;
        }

        public String get() {
            return id;
        }
    }

    public enum SearchBy {
        BILLING_ACCOUNT("Billing Account #"),
        POLICY("Policy #"),
        FIRST_NAME("First Name"),
        LAST_NAME("Last Name"),
        MORTGAGEE("Mortgagee"),
        AGENCY("Agency"),
        AGENCY_NAME("Agency Name"),
        ASSIGN_ADJUSTER("Assign Adjuster"),
        COMMISSION_GROUP_NAME("Commission Group Name");

        String id;

        SearchBy(String id) {
            this.id = id;
        }

        public String get() {
            return id;
        }
    }
}
