/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.taxesfees.strategy.fee;

import org.openqa.selenium.By;

import aaa.utils.EntityLogger;
import aaa.admin.modules.taxesfees.strategy.IStrategy;
import aaa.admin.modules.taxesfees.strategy.fee.views.DefaultView;
import aaa.admin.pages.taxesfees.TaxesFeesPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.AdminAppMainTabs;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;

public class FeeStrategy implements IStrategy {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
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

    @Override
    public void create(TestData td) {
        initiate();
        new Button(By.id("searchForm:addTaxBtn")).click();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.TAX_FEE_STRATEGY);
        new Button(By.id("taxStrategyForm:okBtnStrategy")).click();
        log.info("Created Fee Strategy " + entity);
    }
}
