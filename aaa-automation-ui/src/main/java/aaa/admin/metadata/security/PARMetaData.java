/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.security;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.AdvancedSelector;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class PARMetaData {

    public static final class GeneralPARTab extends MetaData {
        public static final AssetDescriptor<TextBox> ROLE_CODE = declare("Role Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ROLE_NAME = declare("Role Name", TextBox.class);
        public static final AssetDescriptor<AdvancedSelector> PRODUCTS = declare("Products", AdvancedSelector.class, By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='roleForm:select_products']]"));
        public static final AssetDescriptor<AdvancedSelector> PRIVILEGES = declare("Privileges", AdvancedSelector.class,
                By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='roleForm:select_privileges']]"));
    }

    public static final class SearchByField extends MetaData {
        public static final AssetDescriptor<TextBox> PRODUCT_ACCESS_ROLE_NAME = declare("Product Access Role Name", TextBox.class);
        public static final AssetDescriptor<TextBox> PRODUCT_ACCESS_ROLE_CODE = declare("Product Access Role Code", TextBox.class);
    }
}
