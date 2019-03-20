/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import java.util.LinkedHashMap;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Cell;
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
	private static final Object lock = new Object();
	public Table tblAAAMembershipReport = new Table(By.xpath("//table[@id='policyDataGatherForm:membershipReports']"));
	public Table tblInsuranceScoreReport = new Table(By.xpath("//table[@id='policyDataGatherForm:creditReports']"));
	public Table tblInsuranceScoreOverride = new Table(By.xpath("//table[@id='policyDataGatherForm:creditScoreOverride']"));
	public Table tblFirelineReport = new Table(By.xpath("//table[@id='policyDataGatherForm:firelineReportTable']"));
	public Table tblPublicProtectionClass = new Table(By.xpath("//table[@id='policyDataGatherForm:ppcReportTable']"));
	public Table tblClueReport = new Table(By.xpath("//table[@id='policyDataGatherForm:orderClueReports']"));
	public Table tblISO360Report = new Table(By.xpath("//table[@id='policyDataGatherForm:iso360ReportTable']"));
	public Table tblRiskMeterReport = new Table(By.xpath("//table[@id='policyDataGatherForm:riskMeterReportTable']"));
	public StaticElement lblAdversalyImpactedMessage = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:warningMessage']"));
	public StaticElement lblELCMessage = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:ELCMessageText']"));
	public StaticElement lblFirelineMessage = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:firelineValidationErrorMessage']"));
	protected static Logger log = LoggerFactory.getLogger(ReportsTab.class);

	public ReportsTab() {
		super(HomeSSMetaData.ReportsTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}

	@Override
	public Tab fillTab(TestData td) {
		synchronized (lock) {
			super.fillTab(td);
			if (td != null && td.containsKey(getMetaKey()) && getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).isPresent()
					&& StringUtils.isBlank(getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue())) {

				getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
						.getCell(8).controls.links.get(1).click();
				getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getAsset(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG)
						.setValue(createTestData());
			}
		}
		return this;
	}

	public void reorderReports() {
		RadioGroup agentAgreement = getAssetList().getAsset(HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class);
		if (agentAgreement.isPresent()) {
			agentAgreement.setValue("I Agree");
		}
		reOrderReports(tblAAAMembershipReport);
		reOrderReports(tblFirelineReport);
		reOrderReports(tblPublicProtectionClass);
		reOrderReports(tblClueReport);
		reOrderReports(tblInsuranceScoreReport);
	}

	protected void reOrderReports(Table reportTable) {
		if (reportTable.isPresent()) {
			for (int i = 1; i <= reportTable.getRowsCount(); i++) {
				Cell cell = reportTable.getRow(i).getCell("Report");
				Link report = cell.controls.links.get("Re-order report') or contains(.,'Re-order report");
				if (report.isPresent() && !report.getAttribute("class").equals("link_disabled")) {
					new JavaScriptButton(report.getLocator()).click();  // very small shortcutted and with .. workaround to avoid "other element received click"
				}
			}
		}
	}

	private TestData createTestData() {
		LinkedHashMap<String, Object> data = new LinkedHashMap<>();
		data.put(HomeSSMetaData.ReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), "$<today-1y:MM/dd/yyyy>");
		data.put(HomeSSMetaData.ReportsTab.AddMemberSinceDialog.MEMBERSHIP_EXPIRATION_DATE.getLabel(), "$<today+1y:MM/dd/yyyy>");
		data.put(HomeSSMetaData.ReportsTab.AddMemberSinceDialog.BTN_OK.getLabel(), "true");
		return new SimpleDataProvider(data);
	}
}
