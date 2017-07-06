/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.agencyvendor.brand;

import aaa.EntityLogger;
import aaa.admin.modules.agencyvendor.AgencyVendor;
import aaa.admin.modules.agencyvendor.brand.BrandActions.BrandUpdate;
import aaa.admin.modules.agencyvendor.brand.defaulttabs.BrandTab;
import aaa.admin.modules.agencyvendor.brand.views.DefaultView;
import aaa.admin.pages.agencyvendor.BrandPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class Brand extends AgencyVendor {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        BrandPage.buttonAddBrand.click();
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.BRAND);
        BrandTab.buttonSave.click();
        log.info("Created Brand " + entity);
    }

    @Override
    public BrandUpdate update() {
        return new BrandUpdate();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.AGENCY_VENDOR.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.AGENCY_VENDOR_BRAND.get());
    }
}
