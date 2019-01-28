/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory.policy.defaulttabs;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFLink;
import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.pages.product.CommonProductFactoryPage;
import aaa.common.Tab;
import aaa.common.pages.Page;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.waiters.ElementWaiter;
import toolkit.webdriver.controls.waiters.LocatorWaiter;
import toolkit.webdriver.controls.waiters.Waiters;

public class ActionsTab extends PFDefaultTab {

    private static final String PROCESS_STATE_TMPL = "//h3[.='%s']";
    private static final String STATE_NAME_TMPL = "//h3[@aria-expanded='true']/following-sibling::form//a[.='%s']";
    private static final String ACTION_TMPL = "//form[@id='actions-add']//h4[.='%s']/ancestor::div[@class='pf-list-item']//label[//input]";

    PFButton btnAddAction = new PFButton(By.id("actions:add"));
    PFButton btnSubmitAddActions = new PFButton(By.id("actions-add:add"));
    PFButton btnDone = new PFButton(By.xpath("//button[.='Done']"));

    public ActionsTab() {
        super(ProductMetaData.ActionsTab.class);
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void setSectionValue(int index, TestData value) {
                if (!value.getKeys().isEmpty()) {
                    PFLink linkProcessState = getLinkProcessState(value.getValue("Process State"));
                    if (!Boolean.parseBoolean(linkProcessState.getAttribute("aria-expanded"))) {
                        linkProcessState.click();
                    }
                    getLinkStateName(value.getValue("State Name")).click(new ElementWaiter(btnDone));
                    btnAddAction.click(new ElementWaiter(btnSubmitAddActions));

                    for (String action : value.getList("Actions")) {
                        getCheckBoxAction(action).setValue(true);
                    }
                    btnSubmitAddActions.click(Waiters.SLEEP(1000).then(new ElementWaiter(btnDone)));
                    btnDone.click();
                }
            }

            @Override
            protected void selectSection(int index) {}

            @Override
            protected void addSection(int index, int size) {}
        };
    }

    public PFLink getLinkProcessState(String processState) {
        return new PFLink(By.xpath(String.format(PROCESS_STATE_TMPL, processState)));
    }

    public PFLink getLinkStateName(String stateName) {
        return new PFLink(By.xpath(String.format(STATE_NAME_TMPL, stateName)));
    }

    public PFCheckBox getCheckBoxAction(String action) {
        return new PFCheckBox(By.xpath(String.format(ACTION_TMPL, action)));
    }

    @Override
    public Tab submitTab() {
        if (btnSave.isPresent()) {
            btnSave.click();
        }
        CommonProductFactoryPage.activateNavigation();
        if (linkNextTab.isPresent()) {
            linkNextTab.click(new LocatorWaiter(By.xpath("//body[//form[@id='tree-search'] or //span[.='Choose root component']]")));
        }
        return this;
    }
}
