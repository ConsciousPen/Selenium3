/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import aaa.toolkit.webdriver.customcontrols.RatingDetailsTable;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

//import toolkit.verification.CustomAssert;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 *
 * @category Generated
 */
public class PremiumAndCoveragesTab extends Tab {

	public static StaticElement labelProductInquiry = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:sedit_AAAProductOverride_policyFormCd']"));
	public static StaticElement labelProductMessageInquiry = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:componentRegion_AAAProductOverride']"));
	public static StaticElement totalTermPremium = new StaticElement(By.xpath("//span[@class='TOTAL_TERM_PREMIUM']"));
	public static Link buttonViewRatingDetails = new Link(By.id("policyDataGatherForm:viewRatingDetails_Link"));
	public static Table tableRatingDetailsQuoteInfo = new Table(By.id("ratingDetailsPopupForm:policy_summary"));
	public static Table tableRatingDetailsVehicles = new Table(By.id("ratingDetailsPopupForm:vehicle_summary"));
	public static Table tableRatingDetailsDrivers = new Table(By.id("ratingDetailsPopupForm:driver_summary"));
	public static Table tableDiscounts = new Table(By.id("policyDataGatherForm:discountSurchargeSummaryTable"));

	// -- old controls
	public static Table tablePremiumSummary = new Table(By.id("policyDataGatherForm:AAAPremiumSummary"));
	public static Button buttonCommissionOverride = new Button(By.id("policyDataGatherForm:commissionOverrideButton"));
	public Button btnContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);

	public static Link linkPaymentPlan = new Link(By.id("policyDataGatherForm:paymentPlansTogglePanel:header"), Waiters.AJAX);
	public static Link linkViewApplicableFeeSchedule = new Link(By.id("policyDataGatherForm:installmentFeeDetails"), Waiters.AJAX);

	// --
	public PremiumAndCoveragesTab() {
		super(AutoCaMetaData.PremiumAndCoveragesTab.class);
		assetList.applyConfiguration(COVERAGES_CONFIGURATION_NAME);
	}

	public static Dollar getPolicyTermPremium() {
		return new Dollar(tablePremiumSummary.getRow(1).getCell(4).getValue());
	}

	public List<TestData> getRatingDetailsDriversData() {
		ByT pagePattern = ByT.xpath("//div[@id='ratingDetailsPopupForm:driverPanel_body']//center//td[@class='pageText']//*[text()='%s']");
		return getTestDataFromTable(tableRatingDetailsDrivers, pagePattern);
	}

	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(td);
		if (td.getTestData(getMetaKey()) != null && !td.getTestData(getMetaKey()).containsKey(AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM.getLabel())) {
			hideHeader();
			btnCalculatePremium().click();
			showHeader();
		}
		return this;
	}

	@Override
	public Tab submitTab() {
		btnContinue.click();
		return this;
	}

	public void calculatePremium() {
		if (!btnCalculatePremium().isPresent()) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		}
		btnCalculatePremium().click();
	}

	public JavaScriptButton btnCalculatePremium() {
		return getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM.getLabel(), JavaScriptButton.class);
	}

	private List<TestData> getTestDataFromTable(Table table, ByT pagePattern) {
		List<TestData> testDataList = new ArrayList<>();

		if (!table.isPresent()) {
			RatingDetailsView.open();
		}

		Map<String, Object> map = new LinkedHashMap<>();
		List<String> keys = table.getColumn(1).getValue();

		int pageNumber = 1;
		while (new Link(pagePattern.format(pageNumber)).isPresent()) {
			new Link(pagePattern.format(pageNumber)).click();

			for (int column = 2; column <= table.getColumnsCount(); column++) {
				List<String> values = table.getColumn(column).getValue();
				if (values.stream().allMatch(String::isEmpty)) {
					continue; // empty column means absent vehicle
				}

				List<String> _values = new ArrayList<>();
				_values.addAll(values);
				_values.removeIf(s -> "No Coverage".equals(s));
				_values.removeIf(s -> "Unstacked".equals(s));
				_values.removeIf(s -> "Yes".equals(s));
				if (_values.stream().allMatch(String::isEmpty)) {
					continue; // skip column with only "No Coverage"
				}

				//CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());
				Assertions.assertThat(keys.size()).as("Number of keys in table is not equal to number of values.").isEqualTo(values.size());

				for (int i = 0; i < keys.size(); i++) {
					map.put(keys.get(i), values.get(i));
				}

				testDataList.add(new SimpleDataProvider(map));
				map.replaceAll((k, v) -> null);
			}
			pageNumber++;
		}
		return testDataList;
	}

	public static class RatingDetailsView {
		public static RatingDetailsTable tableVehicleSummary = new RatingDetailsTable("//div[@id='ratingDetailsPopup_container']//table[@id='ratingDetailsPopupForm:vehicle_summary']");
		public static RatingDetailsTable tableDriverSummary = new RatingDetailsTable("//div[@id='ratingDetailsPopup_container']//table[@id='ratingDetailsPopupForm:driver_summary']");

		public static Button buttonRatingDetailsOk = new Button(By.id("ratingDetailsPopupButton:ratingDetailsPopupCancel"), Waiters.AJAX);

		public static void open() {
			buttonViewRatingDetails.click();
		}

		public static void close() {
			buttonRatingDetailsOk.click();
		}
	}
}
