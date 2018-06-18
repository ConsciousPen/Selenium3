/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import java.util.LinkedHashMap;
import org.apache.commons.lang.StringUtils;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoSSMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class RatingDetailReportsTab extends Tab {

	private static final Object lock = new Object();

	public RatingDetailReportsTab() {
		super(AutoSSMetaData.RatingDetailReportsTab.class);
	}

	@Override
	public Tab submitTab() {
		if (getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).isPresent()
				&& StringUtils.isBlank(getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
				.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue())) {

			getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
					.getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ACTION.getLabel()).controls.links.get(1).click();
			getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getAsset(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG)
					.setValue(createTestData());
		}
		buttonNext.click();
		return this;
	}

	@Override
	public Tab fillTab(TestData td) {
		synchronized (lock) {
			super.fillTab(td);
		}
		return this;
	}

	public TestData getInsuranceScoreOverrideData(String score) {
		TestData editInsuranceScoreDialogData = DataProviderFactory.dataOf(
				AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.NEW_SCORE.getLabel(), score,
				AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.REASON_FOR_OVERRIDE.getLabel(), "Fair Credit Reporting Act Dispute",
				AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.BTN_SAVE.getLabel(), "click");
		return DataProviderFactory.dataOf(
				AutoSSMetaData.RatingDetailReportsTab.InsuranceScoreOverrideRow.ACTION.getLabel(), "Override Score",
				AutoSSMetaData.RatingDetailReportsTab.InsuranceScoreOverrideRow.EDIT_INSURANCE_SCORE.getLabel(), editInsuranceScoreDialogData);
	}

	private TestData createTestData() {
		LinkedHashMap<String, String> data = new LinkedHashMap<>();
		data.put(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), "$<today-1y:MM/dd/yyyy>");
		data.put(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.BTN_OK.getLabel(), "true");
		return new SimpleDataProvider(data);
	}
}
