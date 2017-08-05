/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionreferral;

import aaa.admin.modules.commission.commissionreferral.CommissionReferralActions.DeleteCommissionReferral;
import aaa.admin.modules.commission.commissionreferral.CommissionReferralActions.EditCommissionReferral;
import aaa.admin.modules.commission.commissionreferral.CommissionReferralActions.ExpireCommissionReferral;
import aaa.admin.modules.commission.commissionreferral.views.DefaultView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class CommissionReferral implements ICommissionReferral {
    private Workspace defaultView = new DefaultView();

    @Override
    public void search(TestData td) {
        CommissionPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.COMMISSION_COMMISSION_REFERRAL.get());
    }

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        log.info("Created Commission Referral " + td);
    }

    @Override
    public void initiate() {
        navigate();
        CommissionPage.buttonAddCommissionReferral.click();
    }

    @Override
    public EditCommissionReferral edit() {
        return new EditCommissionReferral();
    }

    @Override
    public ExpireCommissionReferral expire() {
        return new ExpireCommissionReferral();
    }

    @Override
    public DeleteCommissionReferral delete() {
        return new DeleteCommissionReferral();
    }
}
