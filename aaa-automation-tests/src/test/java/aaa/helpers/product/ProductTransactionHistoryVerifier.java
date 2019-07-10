/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.product;

import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.table.Table;

public class ProductTransactionHistoryVerifier extends TableVerifier {

	public ProductTransactionHistoryVerifier() {}

	public ProductTransactionHistoryVerifier(ETCSCoreSoftAssertions softly) {
        this.softly = softly;
	}

	public ProductTransactionHistoryVerifier setType(String valueTransactionHistoryType) {
        setValue("Type", valueTransactionHistoryType);
        return this;
    }

    public ProductTransactionHistoryVerifier setTransactionDate(LocalDateTime value) {
        setValue("Tran. Premium", value.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public ProductTransactionHistoryVerifier setEffectiveDate(LocalDateTime value) {
        setValue("Effective Date", value.format(DateTimeUtils.MM_DD_YYYY));
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
