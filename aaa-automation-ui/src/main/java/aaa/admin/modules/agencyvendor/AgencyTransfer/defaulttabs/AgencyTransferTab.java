/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.agencyvendor.AgencyTransfer.defaulttabs;

import aaa.admin.metadata.agencyvendor.AgencyTransferMetaData;
import aaa.admin.pages.agencyvendor.AgencyTransferPage;
import aaa.common.DefaultTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ListBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class AgencyTransferTab extends DefaultTab {

	protected Logger log = LoggerFactory.getLogger(AgencyTransferTab.class);

	public AgencyTransferTab() {
		super(AgencyTransferMetaData.AgencyTransferTab.class);
		assetList = new AssetList(By.xpath("//*"), metaDataClass);
	}
	public static Button Search = new Button(By.id("borManagementForm:searchButton"));
	public static Button Clear = new Button(By.id("borManagementForm:clearButton"));
	public static Button addTransfer = new Button(By.id("borManagementForm:addButton"));
	public static TextBox transferId = new TextBox(By.id("borManagementForm:searchCriteria_transferNumber"));
	public static TextBox transferEffectiveDate = new TextBox(By.id("borTransferManagementForm:borTransferInfo_effectiveDateInputDate"));
	public static ListBox transferStatus = new ListBox(By.id("borManagementForm:searchCriteria_status"));
	public static Button intraAgency = new Button(By.id("borTransferManagementForm:borTransferInfo_type:1"));
	public static Button interAgency = new Button(By.id("borTransferManagementForm:borTransferInfo_type:0"));
	public static Button renewal = new Button(By.id("borTransferManagementForm:borTransferInfo_effectiveUpon:0"));
	public static Button other = new Button(By.id("borTransferManagementForm:borTransferInfo_effectiveUpon:1"));
	public static Button commissionImpactedNo= new Button(By.id("borTransferManagementForm:borTransferInfo_impactsComission:1"));
	public static Button locationName= new Button(By.name("borTransferManagementForm:changeSourceProducerCd"));
	public static ListBox reason = new ListBox(By.id("borTransferManagementForm:borTransferInfo_reasonCode"));
	public static ListBox targetInsuranceAgent = new ListBox(By.id("borTransferManagementForm:borTransferTarget_targetSubproducerCd"));
	public static Button submit = new Button(By.id("borTransferManagementForm:submit_footer"));

	/**
	 * Go to the admin -> Agency Transfer -> Add transfer
	 */

	public void createBortObject() {
		NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.AGENCY_VENDOR.get());
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.AGENCY_VENDOR_AGENCY_TRANSFER.get());
		AgencyTransferPage.buttonAddNewTransfer.click();
	}

}
