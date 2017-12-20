/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.taxesfees.registry.fee;

import org.openqa.selenium.By;

import aaa.utils.EntityLogger;
import aaa.admin.modules.taxesfees.registry.IRegistry;
import aaa.admin.modules.taxesfees.registry.fee.views.DefaultView;
import aaa.admin.pages.taxesfees.TaxesFeesPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.AdminAppMainTabs;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;

public class FeeRegistry implements IRegistry {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    public void create(TestData td) {
        initiate();
        new Button(By.xpath("//input[@value = 'Add Fee']")).click();
        getDefaultView().fill(td.resolveLinks());
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.TAX_FEE_REGISTRY);
        new Button(By.xpath("//input[@value = 'Save']")).click();
        log.info("Created fee Registry " + entity);
    }

    @Override
    public void initiate() {
        navigate();
        new RadioGroup(By.id("headSearchForm:searchChoser")).setValue("Fee");
    }

    @Override
    public void search(TestData td) {
        TaxesFeesPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.TAXES_FEES.get());
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.TAXES_FEES_FEE_AND_TAX_REGISTRY.get());
    }
}
