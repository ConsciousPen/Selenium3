/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.security.profile.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.security.ProfileMetaData;
import aaa.common.DefaultTab;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class GeneralProfileTab extends DefaultTab {
    public AssetList assetListSearchForm = new AssetList(By.id("brokerSearchFromsearchBrokerCd"), ProfileMetaData.GeneralProfileTab.AddAgencyMetaData.class);

    public static Table tableAgencyLocation = new Table(By.id("userProfileForm:userAgencyLocations"));
    public static StaticElement errorAgencyLocation = new StaticElement(By.id("userProfileForm:agency_code_for_bls_error"));
    public static StaticElement errorDifferentPassword = new StaticElement(By.className("error_message"));

    public static Table tableAgencySearchResult = new Table(By.id("brokerSearchFromsearchBrokerCd:body_brokerSearchResultssearchBrokerCd"));
    public static StaticElement labelAgencyNotFound = new StaticElement(
            By.xpath("//span[@id='brokerSearchFromsearchBrokerCd:brokerSearchMessagesearchBrokerCd']/table/tbody/tr/td[@class='section_header']"));

    public static StaticElement labelAddedRoles = new StaticElement(By.id("userProfileForm:allowRoles"));
    public static StaticElement labelAddedPARoles = new StaticElement(By.id("userProfileForm:parSelected"));
    public static StaticElement labelAddedManagers = new StaticElement(By.xpath("//span[@id='userProfileForm:managers']"));
    public static StaticElement labelAddedUserSubordinates = new StaticElement(By.xpath("//span[@id='userProfileForm:subordinates']"));

    public static Button buttonReturn = new Button(By.xpath("//input[@value = 'Return' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
    public static Button buttonUpdate = new Button(By.xpath("//input[(@value = 'Update' or @value = 'UPDATEE') and not(@class = 'hidden') and not(contains(@style,'none'))]"));

    public GeneralProfileTab() {
        super(ProfileMetaData.GeneralProfileTab.class);
    }
}
