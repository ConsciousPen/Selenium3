/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.product;

import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.webdriver.controls.composite.table.Table;

public class ProductEndorsementsVerifier extends TableVerifier {

    public ProductEndorsementsVerifier setStatus(String policyStatus) {
        setValue("Status", policyStatus.toString());
        return this;
    }

    @Override
    protected Table getTable() {
        return PolicySummaryPage.tableEndorsements;
    }

    @Override
    protected String getTableName() {
        return "Endorsements";
    }
}
