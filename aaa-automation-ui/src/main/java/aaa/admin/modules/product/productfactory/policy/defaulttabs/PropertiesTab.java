/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory.policy.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.pages.product.CommonProductFactoryPage;
import aaa.common.Tab;
import toolkit.webdriver.controls.waiters.LocatorWaiter;

public class PropertiesTab extends PFDefaultTab {

    public PropertiesTab() {
        super(ProductMetaData.PropertiesTab.class);
    }

    @Override
    public Tab submitTab() {
        CommonProductFactoryPage.activateNavigation();
        if (linkNextTab.isPresent()) {
            linkNextTab.click(new LocatorWaiter(By.xpath("//span[.='Actions']")));
        }
        return this;
    }
}
