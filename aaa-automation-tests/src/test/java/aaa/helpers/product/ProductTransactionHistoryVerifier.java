/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.product;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.utils.datetime.DateTime;
import toolkit.webdriver.controls.composite.table.Table;

public class ProductTransactionHistoryVerifier extends TableVerifier {

    public ProductTransactionHistoryVerifier setType(String valueTransactionHistoryType) {
        setValue("Type", valueTransactionHistoryType);
        return this;
    }

    public ProductTransactionHistoryVerifier setTransactionDate(DateTime value) {
        setValue("Tran. Premium", value.toString(DateTime.MM_DD_YYYY));
        return this;
    }

    public ProductTransactionHistoryVerifier setEffectiveDate(DateTime value) {
        setValue("Effective Date", value.toString(DateTime.MM_DD_YYYY));
        return this;
    }

    public ProductTransactionHistoryVerifier setReason(String value) {
        setValue("Reason", value);
        return this;
    }

    public ProductTransactionHistoryVerifier setTransactionPremium(Dollar value) {
        setValue("Tran. Premium", value.toString());
        return this;
    }

    public ProductTransactionHistoryVerifier setEndingPremium(Dollar value) {
        setValue("Ending Premium", value.toString());
        return this;
    }

    @Override
    protected Table getTable() {
        return PolicySummaryPage.tableTransactionHistory;
    }

    @Override
    protected String getTableName() {
        return "Transaction History";
    }
}
