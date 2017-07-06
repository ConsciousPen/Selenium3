/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy;

import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions.DeleteCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions.ExpireCommissionStrategy;
import aaa.admin.pages.commission.CommissionPage;
import toolkit.datax.TestData;

public abstract class CommissionStrategy implements ICommissionStrategy {

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        CommissionPage.buttonSaveCommissionStrategy.click();
        log.info("Created Commission Strategy " + td);
    }

    @Override
    public ExpireCommissionStrategy expire() {
        return new CommissionStrategyActions.ExpireCommissionStrategy();
    }

    @Override
    public void search(TestData td) {
        CommissionPage.search(td);
    }

    @Override
    public DeleteCommissionStrategy delete() {
        return new DeleteCommissionStrategy();
    }
}
