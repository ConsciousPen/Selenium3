/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.agencyvendor.brand;

import aaa.admin.metadata.agencyvendor.BrandMetaData;
import aaa.admin.modules.agencyvendor.AgencyVendorActions;
import aaa.admin.modules.agencyvendor.brand.defaulttabs.BrandTab;
import aaa.admin.modules.agencyvendor.brand.views.DefaultView;
import aaa.admin.pages.agencyvendor.BrandPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.main.enums.AdminConstants;
import toolkit.datax.TestData;

public final class BrandActions {
    private BrandActions() {};

    public static class BrandUpdate extends AgencyVendorActions.Update {

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        @Override
        public AbstractAction start(int rowNumber) {
            BrandPage.tableBrands.getRow(rowNumber).getCell(AdminConstants.AdminBrandsTable.ACTION).controls.links.getFirst().click();
            return this;
        }

        public AbstractAction start(String brandName) {
            BrandPage.tableBrands.getRow(AdminConstants.AdminBrandsTable.NAME, brandName).getCell(AdminConstants.AdminBrandsTable.ACTION).controls.links.getFirst().click();
            return this;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            BrandPage.assetListAddBrand.getControl(BrandMetaData.BrandTab.AddBrandDialog.BUTTON_UPDATE.getLabel()).getWebElement().click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        public AbstractAction perform(TestData td, String brandName) {
            start(brandName);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }
    }

    public static class BrandTypeUpdate extends AgencyVendorActions.Update {

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        public AbstractAction start(String brandTypeName) {
            BrandPage.tableBrandType.getRow(AdminConstants.AdminBrandsTypeTable.NAME, brandTypeName).getCell(AdminConstants.AdminBrandsTypeTable.ACTION).controls.links.getFirst().click();
            return this;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(String brandTypeName) instead.");
        }

        @Override
        public AbstractAction submit() {
            BrandTab.buttonUpdate.click();
            return this;
        }

        public AbstractAction perform(TestData td, String brandTypeName) {
            start(brandTypeName);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String brandTypeName) instead.");
        }
    }
}
