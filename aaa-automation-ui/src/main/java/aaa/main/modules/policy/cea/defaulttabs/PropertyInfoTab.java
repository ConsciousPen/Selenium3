/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.cea.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.CaliforniaEarthquakeMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class PropertyInfoTab extends Tab {
    public PropertyInfoTab() {
        super(CaliforniaEarthquakeMetaData.PropertyInfoTab.class);
    }
    public Button btnContinue = new Button(By.id("policyDataGatherForm:next_footer"), Waiters.AJAX);

    @Override
    public Tab submitTab() {
    	btnContinue.click();
        return this;
    }
}
