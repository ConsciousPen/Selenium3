/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFLink;
import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.metadata.product.ProductMetaData.ProductProductFactoryCopy;
import aaa.admin.metadata.product.ProductMetaData.ProductProductFactorySearch;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.HomeTab;
import aaa.admin.pages.product.CommonProductFactoryPage;
import aaa.admin.pages.product.ProductProductFactoryPage;
import aaa.common.AbstractAction;
import aaa.main.enums.ProductConstants;
import toolkit.datax.TestData;

public class ProductFactoryActions {

    public abstract static class Copy extends AbstractAction {
        private static Object lock = new Object();

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method is not supported for this action. Use start(TestData testData) instead.");
        }

        public AbstractAction start(TestData td) {
            ProductProductFactoryPage.search(td);
            ProductProductFactoryPage.tableProducts.getRow(ProductConstants.ProductTable.PRODUCT_CODE, td.getValue(ProductProductFactorySearch.class.getSimpleName(), "Product Code")).getCell(
                    ProductConstants.ProductTable.COPY_PRODUCT).controls.links
                    .getFirst().click();
			new DialogAssetList(AbstractDialog.POPUP_PARENT_LOCATOR, ProductProductFactoryCopy.class).fill(td);
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            synchronized (lock) {
                start(td);
                getView().fill(td);
                submit();
                return this;
            }
        }

        @Override
        public AbstractAction submit() {
            CommonProductFactoryPage.activateNavigation();
            new PFLink(By.xpath("//a[contains(@id,'nav:routes') and .='Home']")).click();
            getView().getTab(HomeTab.class).getAssetList().getAsset(ProductMetaData.HomeTab.BUTTON_ACTIVATE.getLabel(), PFButton.class).click();
            PFButton buttonActivateAnyWay = new PFButton(By.id("lookupErrorForm:deploy-anyway"));
            if (buttonActivateAnyWay.isPresent() && buttonActivateAnyWay.isVisible()) {
                buttonActivateAnyWay.click();
            }
            CommonProductFactoryPage.activateNavigation();
            new PFButton(By.xpath("//div[@role='button' and @class='product-wrapper waves-effect']")).click();
            new PFLink(By.id("nav:back")).click();
            return this;
        }
    }

    public abstract static class Update extends AbstractAction {

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method is not supported for this action. Use start(TestData testData) instead.");
        }

        public AbstractAction start(TestData td) {
            ProductProductFactoryPage.search(td);
            ProductProductFactoryPage.tableProducts.getRow(ProductConstants.ProductTable.PRODUCT_CODE, td.getValue(ProductProductFactorySearch.class.getSimpleName(), "Product Code"))
                    .getCell(ProductConstants.ProductTable.PRODUCT_NAME).controls.links.getFirst().click();
			new DialogAssetList(AbstractDialog.POPUP_PARENT_LOCATOR, ProductProductFactoryCopy.class).fill(td);
            //activateNavigation();
            PFButton buttonDeactivate = getView().getTab(HomeTab.class).getAssetList().getAsset(ProductMetaData.HomeTab.BUTTON_DEACTIVATE.getLabel(), PFButton.class);
            if (buttonDeactivate.isPresent()) {
                buttonDeactivate.click();
            }
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            start(td);
            getView().fill(td);
            submit();
            return this;
        }

        @Override
        public AbstractAction submit() {
            CommonProductFactoryPage.activateNavigation();

            new PFLink(By.xpath("//a[contains(@id,'nav:routes') and .='Home']")).click();
            getView().getTab(HomeTab.class).getAssetList().getAsset(ProductMetaData.HomeTab.BUTTON_ACTIVATE.getLabel(), PFButton.class).click();
            PFButton buttonActivateAnyWay = new PFButton(By.id("lookupErrorForm:deploy-anyway"));
            if (buttonActivateAnyWay.isPresent() && buttonActivateAnyWay.isVisible()) {
                buttonActivateAnyWay.click();
            }
            CommonProductFactoryPage.activateNavigation();
            new PFButton(By.xpath("//div[@role='button' and @class='product-wrapper waves-effect']")).click();
            new PFLink(By.id("nav:back")).click();
            return this;
        }
    }

    public abstract static class Inquiry extends AbstractAction {

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method is not supported for this action. Use start(TestData testData) instead.");
        }

        public AbstractAction start(TestData td) {
            ProductProductFactoryPage.search(td);
            ProductProductFactoryPage.tableProducts.getRow(ProductConstants.ProductTable.PRODUCT_CODE, td.getValue(ProductProductFactorySearch.class.getSimpleName(), "Product Code"))
                    .getCell(ProductConstants.ProductTable.PRODUCT_NAME).controls.links.getFirst().click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            start(td);
            getView().fill(td);
            submit();
            return this;
        }

        @Override
        public AbstractAction submit() {
            CommonProductFactoryPage.activateNavigation();
            new PFButton(By.xpath("//div[@role='button' and @class='product-wrapper waves-effect']")).click();
            new PFLink(By.id("nav:back")).click();
            return this;
        }
    }
}
