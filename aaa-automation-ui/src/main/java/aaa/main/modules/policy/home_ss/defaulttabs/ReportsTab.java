/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.table.Cell;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

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
	public ReportsTab() {
		super(HomeSSMetaData.ReportsTab.class);
	}

	public Table tblAAAMembershipReport = new Table(By.xpath("//table[@id='policyDataGatherForm:membershipReports']"));
	public Table tblInsuranceScoreReport = new Table(By.xpath("//table[@id='policyDataGatherForm:creditReports']"));
	public Table tblInsuranceScoreOverride = new Table(By.xpath("//table[@id='policyDataGatherForm:creditScoreOverride']"));
	public Table tblFirelineReport = new Table(By.xpath("//table[@id='policyDataGatherForm:firelineReportTable']"));
	public Table tblPublicProtectionClass = new Table(By.xpath("//table[@id='policyDataGatherForm:ppcReportTable']"));
	public Table tblClueReport = new Table(By.xpath("//table[@id='policyDataGatherForm:orderClueReports']"));
	public Table tblISO360Report = new Table(By.xpath("//table[@id='policyDataGatherForm:iso360ReportTable']"));
	public Table tblRiskMeterReport = new Table(By.xpath("//table[@id='policyDataGatherForm:riskMeterReportTable']"));

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}

	@Override
	public Tab fillTab(TestData td) {
		//assetList.fill(td);
		orderAllReports();
		
		return this;
	} 

	protected void orderReports(Table reportTable) {
		if (reportTable.isPresent()) {
			for (int i = 1; i <= reportTable.getRowsCount(); i++) {
				Cell cell = reportTable.getRow(i).getCell("Report");
				Link report = cell.controls.links.get("Order Report') or contains(.,'Order report");
				if (report.isPresent() && !report.getAttribute("class").equals("link_disabled")) {
					report.click(Waiters.AJAX);
					// cell.controls.links.get(1).waitForAccessible(10000);
				}
			}
		}
	}
	
	public void orderAllReports() {
	    RadioGroup customerAgreement = getAssetList().getControl(HomeSSMetaData.ReportsTab.CUSTOMER_AGREEMENT.getLabel(), RadioGroup.class);
        RadioGroup agentAgreement = getAssetList().getControl(HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class);
        if (agentAgreement.isPresent())
            agentAgreement.setValue("I Agree");
        if (customerAgreement.isPresent())
            customerAgreement.setValue("Customer agrees");
	    orderReports(tblAAAMembershipReport);
        orderReports(tblInsuranceScoreReport);
        /*if (BaseTest.getState().equals("VA") || BaseTest.getState().equals("NJ") || BaseTest.getState().equals("DE") || BaseTest.getState().equals("CT") || BaseTest.getState().equals("MD")) {
            orderReports(tblRiskMeterReport, Waiters.AJAX);
        }*/
        orderReports(tblFirelineReport);
        orderReports(tblPublicProtectionClass);
        orderReports(tblClueReport);
	}
	
	protected void reOrderReports(Table reportTable){
		if (reportTable.isPresent()) {
			for (int i = 1; i <= reportTable.getRowsCount(); i++) {
				Cell cell = reportTable.getRow(i).getCell("Report");
				Link report = cell.controls.links.get("Re-order report') or contains(.,'Re-order report");
				if (report.isPresent() && !report.getAttribute("class").equals("link_disabled")) {
					report.click(Waiters.AJAX);
					// cell.controls.links.get(1).waitForAccessible(10000);
				}
			}
		}
	}
	
	public void reorderReports(){
		//Set Yes to all named insured
		for (int i = 1; i <= tblInsuranceScoreReport.getRowsCount(); i++) {
			RadioGroup rgConfirm = tblInsuranceScoreReport.getRow(i).getCell("Order Insurance Score").controls.radioGroups.get(1, Waiters.AJAX);
			if (rgConfirm.isEnabled())
				rgConfirm.setValue("Yes", Waiters.AJAX);
		}
		
		RadioGroup customerAgreement = getAssetList().getControl(HomeSSMetaData.ReportsTab.CUSTOMER_AGREEMENT.getLabel(), RadioGroup.class);
        RadioGroup agentAgreement = getAssetList().getControl(HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class);
        if (agentAgreement.isPresent())
            agentAgreement.setValue("I Agree");
        if (customerAgreement.isPresent())
            customerAgreement.setValue("Customer agrees");
        reOrderReports(tblAAAMembershipReport);
        reOrderReports(tblInsuranceScoreReport);
        reOrderReports(tblFirelineReport);
        reOrderReports(tblPublicProtectionClass);
        reOrderReports(tblClueReport);
	}	
	
	
	
	
}
