/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory.policy.defaulttabs;

import java.util.List;
import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFLink;
import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.pages.product.CommonProductFactoryPage;
import aaa.common.Tab;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class WorkspacesTab extends PFDefaultTab {
    private static final String LINK_XPATH_TMPL = "//h3[.='%s']";

    public WorkspacesTab() {
        super(ProductMetaData.WorkspacesTab.class);
        assetList = new MultiAssetList(By.xpath("//*"), metaDataClass) {

            @Override
            protected void setRawValue(List<TestData> value) {
                int size = value.size();
                for (int i = 0; i < size; i++) {
                    beforeSection(value.get(i));
                    setSectionValue(i, value.get(i));
                }
            }

            private void beforeSection(TestData testData) {
                if (testData.containsKey("Path")) {
                    List<String> path = testData.getList("Path");
                    new PFLink(By.xpath(String.format(LINK_XPATH_TMPL, path.get(path.size() - 1)))).click();
                }
            }

            @Override
            protected void addSection(int index, int size) {}

            @Override
            protected void selectSection(int index) {}
        };
    }

    @Override
    public Tab submitTab() {
        if (btnSave.isPresent()) {
            btnSave.click();
        }
        CommonProductFactoryPage.activateNavigation();
        if (linkNextTab.isPresent()) {
            linkNextTab.click();
        }
        return this;
    }
}
