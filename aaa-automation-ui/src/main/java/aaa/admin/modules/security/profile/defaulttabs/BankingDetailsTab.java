/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.security.profile.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.security.ProfileMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import toolkit.webdriver.controls.StaticElement;

public class BankingDetailsTab extends DefaultTab {

    public static StaticElement errorExpirationDate = new StaticElement(By.id("userProfileForm:paymentEFT_effectiveTerm_expiration_error"));

    public BankingDetailsTab() {
        super(ProfileMetaData.BankingDetailsTab.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
