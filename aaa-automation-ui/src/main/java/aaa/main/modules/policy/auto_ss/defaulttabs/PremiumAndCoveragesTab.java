/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoSSMetaData;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 *
 * @category Generated
 */
public class PremiumAndCoveragesTab extends Tab {
	public static Table tableRatingDetailsQuoteInfo = new Table(By.id("ratingDetailsPopupForm:policy_summary"));
	public static Table tableRatingDetailsUnderwriting = new Table(By.id("ratingDetailsPopupForm:underwriting_summary"));
	public static Table tableRatingDetailsDrivers = new Table(By.id("ratingDetailsPopupForm:driver_summary"));
	public static Table tableRatingDetailsVehicles = new Table(By.id("ratingDetailsPopupForm:vehicle_summary"));
	public static Table tableRatingDetailsActivities = new Table(By.id("ratingDetailsPopupForm:incident_summary"));
	public static Table tableDiscounts = new Table(By.id("policyDataGatherForm:discountSurchargeSummaryTable"));
	public static Table tableFormsSummary = new Table(By.id("policyDataGatherForm:formSummaryTable"));
	public static Table tablefeesSummary = new Table(By.id("policyDataGatherForm:feesSummaryTable"));
	public static Table tableInstallmentFeeDetails = new Table(By.id("policyDataGatherForm:installmentFeeDetailsTable"));
	public static Table tableAAAPremiumSummary = new Table(By.id("policyDataGatherForm:AAAPremiumSummary"));
	public static Table tableTermPremiumbyVehicle = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table"));
	public static Table tablePolicyLevelLiabilityCoveragesPremium = new Table(By.xpath("//table[@id='policyDataGatherForm:policyTableTotalVehiclePremium']"));
	public static Table tableEValueMessages = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAEMemberDetailMVOComponent']//table"));
	public static Table autoPaySetupSavingMessage = new Table(By.id("policyDataGatherForm:installmentFeeAmountSavedPanel"));

	public static Button buttonCalculatePremium = new Button(By.id("policyDataGatherForm:premiumRecalc"));
	public static Button buttonViewRatingDetails = new Button(By.id("policyDataGatherForm:viewRatingDetails_Link_1"));
	public static Button buttonContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);
	public static Button buttonRatingDetailsOk = new Button(By.id("ratingDetailsPopupButton:ratingDetailsPopupCancel"));

	public static StaticElement totalTermPremium = new StaticElement(By.xpath("//span[@class='TOTAL_TERM_PREMIUM']"));
	public static StaticElement totalActualPremium = new StaticElement(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAPremiumSummary_body']/table/tbody/tr/td[2]/span"));
	public static StaticElement discountsAndSurcharges = new StaticElement(By.id("policyDataGatherForm:discountSurchargeSummaryTable"));

	public static Link linkPaymentPlan = new Link(By.id("policyDataGatherForm:paymentPlansTogglePanel:header"), Waiters.AJAX);
	public static Link linkViewApplicableFeeSchedule = new Link(By.id("policyDataGatherForm:installmentFeeDetails"), Waiters.AJAX);

	public static ByT tableVehicleCoveragePremium = ByT.xpath("//table[@id='policyDataGatherForm:subtotalVehiclePremium_%s']");

	public PremiumAndCoveragesTab() {
		super(AutoSSMetaData.PremiumAndCoveragesTab.class);
	}

	public static Dollar getPreEndorsementPremium() {
		return new Dollar(tableAAAPremiumSummary.getRow(1).getCell(tableAAAPremiumSummary.getColumnsCount() - 2).getValue());
	}

	public static Dollar getActualPremium() {
		return new Dollar(tableAAAPremiumSummary.getRow(1).getCell(tableAAAPremiumSummary.getColumnsCount()).getValue());
	}

	public TestData getRatingDetailsQuoteInfoData() {
		if (!tableRatingDetailsQuoteInfo.isPresent()) {
			buttonViewRatingDetails.click();
		}

		Map<String, Object> map = new LinkedHashMap<>();
		List<String> keys = tableRatingDetailsQuoteInfo.getColumn(1).getValue();
		List<String> values = tableRatingDetailsQuoteInfo.getColumn(2).getValue();
		CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());

		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}

		keys = tableRatingDetailsQuoteInfo.getColumn(3).getValue();
		values = tableRatingDetailsQuoteInfo.getColumn(4).getValue();
		CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());
		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}

		return new SimpleDataProvider(map);
	}

	public TestData getRatingDetailsUnderwritingValueData() {

		Map<String, Object> map = new LinkedHashMap<>();
		List<String> keys = tableRatingDetailsUnderwriting.getColumn(1).getValue();
		List<String> values = tableRatingDetailsUnderwriting.getColumn(2).getValue();
		CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());

		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}

		keys = tableRatingDetailsUnderwriting.getColumn(4).getValue();
		values = tableRatingDetailsUnderwriting.getColumn(5).getValue();
		CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());
		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}

		return new SimpleDataProvider(map);
	}

	public List<TestData> getRatingDetailsDriversData() {
		ByT pagePattern = ByT.xpath("//div[@id='ratingDetailsPopupForm:driverPanel_body']//center//td[@class='pageText']//*[text()='%s']");
		return getTestDataFromTable(tableRatingDetailsDrivers, pagePattern);
	}

	public List<TestData> getRatingDetailsVehiclesData() {
		ByT pagePattern = ByT.xpath("//div[@id='ratingDetailsPopupForm:vehiclePanel']//center//td[@class='pageText']//*[text()='%s']");
		return getTestDataFromTable(tableRatingDetailsVehicles, pagePattern);
	}

	public List<TestData> getTermPremiumByVehicleData() {
		List<TestData> testDataList = new ArrayList<>();
		Map<String, Object> map = new LinkedHashMap<>();
		List<String> keys = new ArrayList<String>();
		List<String> _keys = new ArrayList<String>();
		_keys.addAll(new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[1]//table[2]")).getColumn(1).getValue());
		_keys.addAll(new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[1]//table[3]")).getColumn(1).getValue());
		_keys.addAll(new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[1]//table[4]")).getColumn(1).getValue());
		for (String key : _keys) {
			if (key.contains("\n")) {
				keys.add(key.substring(0, key.indexOf("\n")).trim());
			} else {
				keys.add(key);
			}
		}
		for (int column = 1; column <= tableTermPremiumbyVehicle.getRow(1).getCellsCount(); column++) {
			if (tableTermPremiumbyVehicle.getColumn(column).getValue().stream().allMatch(String::isEmpty)) {
				continue; // empty column means absent vehicle
			}
			List<String> values = new ArrayList<String>();
			if (column == 1) {
				values.addAll(new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[1]//table[2]")).getColumn(2).getValue());
				values.addAll(new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[1]//table[3]")).getColumn(2).getValue());
				values.addAll(new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[1]//table[4]")).getColumn(2).getValue());
			} else {
				values.addAll(new Table(By.xpath(String.format("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[%s]//table[2]", column)))
						.getColumn(1).getValue());
				values.addAll(new Table(By.xpath(String.format("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[%s]//table[3]", column)))
						.getColumn(1).getValue());
				values.addAll(new Table(By.xpath(String.format("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[%s]//table[4]", column)))
						.getColumn(1).getValue());
			}
			for (int i = 0; i < keys.size(); i++) {
				map.put(keys.get(i), values.get(i));
			}

			testDataList.add(new SimpleDataProvider(map));
			map.replaceAll((k, v) -> null);
		}
		return testDataList;
	}

	public Dollar getPolicyLevelLiabilityCoveragesPremium() {
		Dollar policyLevelLiabilityCoveragesPremium = new Dollar(tablePolicyLevelLiabilityCoveragesPremium.getRow(1).getCell(3).getValue());
		return policyLevelLiabilityCoveragesPremium;
	}

	public static void calculatePremium() {
		if (!buttonCalculatePremium.isPresent()) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		}
		buttonCalculatePremium.click();
	}

	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(td);
		if (td.getTestData(getMetaKey()) != null && !td.getTestData(getMetaKey()).containsKey(AutoSSMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM.getLabel())) {
			hideHeader();
			buttonCalculatePremium.click();
			showHeader();
		}
		return this;
	}

	@Override
	public Tab submitTab() {
		if (buttonRatingDetailsOk.isPresent() && buttonRatingDetailsOk.isVisible()) {
			buttonRatingDetailsOk.click();
		}
		buttonContinue.click();
		return this;
	}

	public Dollar getVehicleCoveragePremiumByVehicle(int index) {
		String xpathForVehicle = "//table[@id='policyDataGatherForm:subtotalVehiclePremium_%s']";
		String xpathForVehicleFormatted = String.format(xpathForVehicle, index);
		Table VehcilePremiumTable = new Table(By.xpath(xpathForVehicleFormatted));
		Dollar policyLevelLiabilityCoveragesPremium = new Dollar(VehcilePremiumTable.getRow(1).getCell(3).getValue());
		return policyLevelLiabilityCoveragesPremium;
	}

	public Dollar getVehicleCoveragePremiumByVehicle1(int index) {
		Table vehiclePremiumTable = new Table(tableVehicleCoveragePremium.format(index));
		Dollar policyLevelLiabilityCoveragesPremium = new Dollar(vehiclePremiumTable.getRow(1).getCell(3).getValue());
		return policyLevelLiabilityCoveragesPremium;
	}

	private List<TestData> getTestDataFromTable(Table table, ByT pagePattern) {
		List<TestData> testDataList = new ArrayList<>();

		if (!table.isPresent()) {
			buttonViewRatingDetails.click();
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

				List<String> _values = new ArrayList<String>();
				_values.addAll(values);
				_values.removeIf(s -> "No Coverage".equals(s));
				_values.removeIf(s -> "Unstacked".equals(s));
				_values.removeIf(s -> "Yes".equals(s));
				if (_values.stream().allMatch(String::isEmpty)) {
					continue; // skip column with only "No Coverage"
				}

				CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());

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
}
