/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.security;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.AdvancedSelector;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class RoleMetaData {

    public static final class GeneralRoleTab extends MetaData {
    	public static final AssetDescriptor<StaticElement> CHANNEL = declare("Channel", StaticElement.class);
        public static final AssetDescriptor<TextBox> ROLE_NAME = declare("Role Name", TextBox.class);
        public static final AssetDescriptor<TextBox> CATEGORY = declare("Category", TextBox.class);
        public static final AssetDescriptor<AdvancedSelector> PRIVILEGES = declare("Privileges", AdvancedSelector.class,
                By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='roleForm:select_privileges']]"));
        public static final AssetDescriptor<StaticElement> ROLES_PRIVILEGES = declare("Role's Privileges", StaticElement.class, By.id("roleForm:privileges"));
    }

    public static final class SearchByField extends MetaData {
        public static final AssetDescriptor<TextBox> ROLE_NAME = declare("Role Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> BUSINESS_DOMAIN = declare("Business Domain", ComboBox.class);
    }
}
