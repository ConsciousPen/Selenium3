/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.taxesfees.feegroup;

import org.openqa.selenium.By;

import aaa.utils.EntityLogger;
import aaa.admin.modules.taxesfees.feegroup.defaulttabs.AddFeeGroupTab;
import aaa.admin.modules.taxesfees.feegroup.views.DefaultView;
import aaa.admin.pages.taxesfees.TaxesFeesPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.AdminAppMainTabs;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;

public class FeeGroup implements IFeeGroup {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        new Button(By.id("roleSearchForm:add-role")).click();
    }

    @Override
    public void search(TestData td) {
        TaxesFeesPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.SECURITY.get());
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.SECURITY_ROLE.get());
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.FEE_GROUP);
        AddFeeGroupTab.buttonSave.click();
        log.info("Created " + entity);
    }
}
