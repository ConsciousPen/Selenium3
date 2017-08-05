/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.agencyvendor;

import org.openqa.selenium.By;

import aaa.admin.metadata.agencyvendor.AgencyMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class BankingDetailsPage extends AdminPage {

    public static AssetList assetListBankingDetails = new AssetList(By.id("brokerAgencyInfoForm:settlementMethodDetails"), AgencyMetaData.BankingDetailsTab.class);
}
