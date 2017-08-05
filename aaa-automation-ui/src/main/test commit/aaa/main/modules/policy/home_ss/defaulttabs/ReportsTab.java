/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace. Tab classes from the default
 * workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB
 * LABEL>ActionTab (to prevent duplication). Modify this class if tab filling
 * procedure has to be customized, extra asset list to be added, custom testdata
 * key to be defined, etc.
 *
 * @category Generated
 */
public class ReportsTab extends Tab {
	public Table tblAAAMembershipReport = new Table(By.xpath("//table[@id='policyDataGatherForm:membershipReports']"));
	public Table tblInsuranceScoreReport = new Table(By.xpath("//table[@id='policyDataGatherForm:creditReports']"));
	public Table tblInsuranceScoreOverride = new Table(By.xpath("//table[@id='policyDataGatherForm:creditScoreOverride']"));
	public Table tblFirelineReport = new Table(By.xpath("//table[@id='policyDataGatherForm:firelineReportTable']"));
	public Table tblPublicProtectionClass = new Table(By.xpath("//table[@id='policyDataGatherForm:ppcReportTable']"));
	public Table tblClueReport = new Table(By.xpath("//table[@id='policyDataGatherForm:orderClueReports']"));
	public Table tblISO360Report = new Table(By.xpath("//table[@id='policyDataGatherForm:iso360ReportTable']"));
	public Table tblRiskMeterReport = new Table(By.xpath("//table[@id='policyDataGatherForm:riskMeterReportTable']"));

	public ReportsTab() {
		super(HomeSSMetaData.ReportsTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}
}
