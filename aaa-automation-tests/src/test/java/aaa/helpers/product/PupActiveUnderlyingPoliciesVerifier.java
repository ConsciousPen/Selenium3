/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.product;

import aaa.helpers.TableVerifier;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import toolkit.webdriver.controls.composite.table.Table;

public class PupActiveUnderlyingPoliciesVerifier extends TableVerifier {

    public PupActiveUnderlyingPoliciesVerifier setPolicyNumber(String policyNum) {
        setValue("Policy Number", policyNum);
        return this;
    }

    @Override
    protected Table getTable() {
        return new PrefillTab().tblActivePoliciesList;
    }

    @Override
    protected String getTableName() {
        return "Active underlying policies";
    }
}
