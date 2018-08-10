/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.security.role.defaulttabs;

import aaa.admin.metadata.security.RoleMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;

public class GeneralRoleTab extends DefaultTab {
    public static Button buttonUpdate = new Button(By.id("roleForm:update_footer"));
    public static Button buttonReturn = new Button(By.xpath("//input[@value = 'Return' and not(@class = 'hidden') and not(contains(@style,'none'))]"));

    public static StaticElement labelAddedPriveleges = new StaticElement(RoleMetaData.GeneralRoleTab.ROLES_PRIVILEGES.getLocator());
    public static StaticElement labelRoleNameInquiry = new StaticElement(By.id("roleForm:role_name"));

    public GeneralRoleTab() {
        super(RoleMetaData.GeneralRoleTab.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
