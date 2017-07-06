/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.productstrategy;

import org.openqa.selenium.By;

import aaa.admin.modules.product.productstrategy.views.DefaultView;
import aaa.admin.pages.product.ProductAutomatedProcessingPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;

public class ProductStrategy implements IProductStrategy {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        new Button(By.id("strategySearchForm:addButton")).click();
    }

    @Override
    public void search(TestData td) {
        ProductAutomatedProcessingPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.PRODUCT.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.PRODUCT_AUTOMATED_PROCESSING.get());
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        log.info("Created Product Strategy " + td);
    }
}
