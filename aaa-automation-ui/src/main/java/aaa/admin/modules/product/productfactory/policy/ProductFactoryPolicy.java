/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory.policy;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFLink;
import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.modules.product.productfactory.IProductFactory;
import aaa.admin.modules.product.productfactory.ProductFactoryActions.Copy;
import aaa.admin.modules.product.productfactory.ProductFactoryActions.Update;
import aaa.admin.modules.product.productfactory.policy.ProductFactoryPolicyActions.Inquiry;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.HomeTab;
import aaa.admin.modules.product.productfactory.policy.views.DefaultView;
import aaa.admin.pages.product.CommonProductFactoryPage;
import aaa.admin.pages.product.ProductProductFactoryPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.waiters.ElementWaiter;
import toolkit.webdriver.controls.waiters.Waiters;

public class ProductFactoryPolicy implements IProductFactory {
	DialogAssetList dialogAssetlistSelectProductType = new DialogAssetList(AbstractDialog.POPUP_PARENT_LOCATOR, ProductMetaData.ProductProductFactorySelectProductType.class);
    Link linkNavigateHome = new Link(By.xpath("//a[contains(@id,'nav:routes') and .='Home']"));
    PFButton buttonActivateAnyWay = new PFButton(By.id("lookupErrorForm:deploy-anyway"));
    PFButton buttonMenuSwitcher = new PFButton(By.xpath("//div[@role='button' and @class='product-wrapper waves-effect']"));
    PFLink linkNavigateBack = new PFLink(By.id("nav:back"));

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.PRODUCT.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.PRODUCT_PRODUCT_FACTORY.get());
    }

    @Override
    public void initiate(TestData td) {
        navigate();
        ProductProductFactoryPage.buttonAddNewProduct.click();
        dialogAssetlistSelectProductType.fill(td);
    }

    @Override
    public void search(TestData td) {
        ProductProductFactoryPage.search(td);
    }

    @Override
    public Workspace getDefaultView() {
        return new DefaultView();
    }

    @Override
    public Copy copy() {
        return new ProductFactoryPolicyActions.Copy();
    }

    @Override
    public void create(TestData td) {
        initiate(td);
        getDefaultView().fill(td);

        CommonProductFactoryPage.activateNavigation();
        PFButton buttonActivate = getDefaultView().getTab(HomeTab.class).getAssetList().getAsset(
                ProductMetaData.HomeTab.BUTTON_ACTIVATE.getLabel(), PFButton.class);
        linkNavigateHome.click(new ElementWaiter(buttonActivate));
        buttonActivate.click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));

        if (buttonActivateAnyWay.isPresent() && buttonActivateAnyWay.isVisible()) {
            buttonActivateAnyWay.click();
        }

        CommonProductFactoryPage.activateNavigation();

        buttonMenuSwitcher.waitForAccessible(5000);
        buttonMenuSwitcher.click();
        linkNavigateBack.click();
    }

    @Override
    public Update update() {
        return new ProductFactoryPolicyActions.Update();
    }

    @Override
    public Inquiry inquiry() {
        return new ProductFactoryPolicyActions.Inquiry();
    }
}
