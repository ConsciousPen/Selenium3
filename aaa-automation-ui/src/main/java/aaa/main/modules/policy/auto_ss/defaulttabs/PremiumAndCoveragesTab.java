/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import static toolkit.verification.CustomAssertions.assertThat;
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
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import aaa.toolkit.webdriver.customcontrols.RatingDetailsTable;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Cell;
import toolkit.webdriver.controls.composite.table.Row;
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
	public static Table tableStateAndLocalTaxesSummary = new Table(By.xpath("//table[@id='policyDataGatherForm:taxTable' or @id='policyDataGatherForm:taxSurchargeSummaryTable']"));
	public static Table tableAAAPremiumSummary = new Table(By.id("policyDataGatherForm:AAAPremiumSummary"));
	public static Table tableTermPremiumbyVehicle = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table"));
	public static Table tablePolicyLevelLiabilityCoveragesPremium = new Table(By.xpath("//table[@id='policyDataGatherForm:policyTableTotalVehiclePremium']"));
	public static Table tableEValueMessages = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAEMemberDetailMVOComponent']//table"));
	public static Table autoPaySetupSavingMessage = new Table(By.id("policyDataGatherForm:installmentFeeAmountSavedPanel"));
	public static Table tableeMemberMessageGrid = new Table(By.id("policyDataGatherForm:eMemberMessageGrid"));
	public static Table tablePolicyLevelLiabilityCoverages = new Table(By.id("policyDataGatherForm:policy_vehicle_detail_coverage"));

	public static Button buttonViewCappingDetails = new Button(By.id("policyDataGatherForm:viewCappingDetails_Link_1"), Waiters.AJAX);
	public static Button buttonReturnToPremiumAndCoverages = new Button(By.id("cappingDetailsPopupPanel:cappingReturnTo"), Waiters.AJAX);
	public static Button buttonViewRatingDetails = new Button(By.id("policyDataGatherForm:viewRatingDetails_Link_1"), Waiters.AJAX);
	public static Button buttonContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);

	public static StaticElement totalTermPremium = new StaticElement(By.xpath("//span[@class='TOTAL_TERM_PREMIUM']"));
	public static StaticElement totalActualPremium = new StaticElement(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAPremiumSummary_body']/table/tbody/tr/td[2]/span"));
	public static StaticElement discountsAndSurcharges = new StaticElement(By.id("policyDataGatherForm:discountSurchargeSummaryTable"));
	public static StaticElement eValuePaperlessWarning = new StaticElement(By.id("policyDataGatherForm:eMemberDetails_electronicMemberDetailsEntity_electronicMemberOpt_error"));
	public static StaticElement euimHelpText = new StaticElement(By.xpath("//label[@id='policyDataGatherForm:policy_vehicle_detail_coverage:2:Coveragecd']/following-sibling::div//span"));
	public static StaticElement uimBIHelpText = new StaticElement(By.xpath("//label[@id='policyDataGatherForm:policy_vehicle_detail_coverage:3:Coveragecd']/following-sibling::div//span"));
	public static StaticElement uimPDHelpText = new StaticElement(By.xpath("//label[@id='policyDataGatherForm:policy_vehicle_detail_coverage:4:Coveragecd']/following-sibling::div//span"));

	public static Link linkPaymentPlan = new Link(By.id("policyDataGatherForm:paymentPlansTogglePanel:header"), Waiters.AJAX);
	public static Link linkViewApplicableFeeSchedule = new Link(By.id("policyDataGatherForm:installmentFeeDetails"), Waiters.AJAX);
	public static Table tablePaymentPlans = new Table(By.id("policyDataGatherForm:paymentPlansTable"));
	public static Table tableUnrestrictedPaymentPlans = new Table(By.id("policyDataGatherForm:unrestrictedPaymentPlansTable"));
	public static StaticElement labelPaymentPlanRestriction = new StaticElement(By.xpath("//*[@id='policyDataGatherForm:paymentPlansTogglePanel:content']/table[2]/tbody"));
	public static StaticElement labelInstallmentFees = new StaticElement(By.xpath("(//*[@id='policyDataGatherForm:paymentPlansTogglePanel:content']/table)[last()]/tbody"));

	public static ByT tableVehicleCoveragePremium = ByT.xpath("//table[@id='policyDataGatherForm:subtotalVehiclePremium_%s']");
	public static ByT tableVehicleCoverageDetails = ByT.xpath("//table[@id='policyDataGatherForm:vehicle_detail_%s']");

	public PremiumAndCoveragesTab() {
		super(AutoSSMetaData.PremiumAndCoveragesTab.class);
		assetList.applyConfiguration(COVERAGES_CONFIGURATION_NAME);
		assetList.getAsset(AutoSSMetaData.PremiumAndCoveragesTab.POLICY_LEVEL_PERSONAL_INJURY_PROTECTION_COVERAGES).applyConfiguration(COVERAGES_CONFIGURATION_NAME);
	}

	public AssetList getMoratoriumInformationAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.MORATORIUM_INFORMATION.getLabel(), AssetList.class);
	}

	public static Dollar getPreEndorsementPremium() {
		return new Dollar(tableAAAPremiumSummary.getRow(1).getCell(tableAAAPremiumSummary.getColumnsCount() - 2).getValue());
	}

	public static Dollar getActualPremium() {
		return new Dollar(tableAAAPremiumSummary.getRow(1).getCell(tableAAAPremiumSummary.getColumnsCount()).getValue());
	}

	public static Dollar getTotalTermPremium() {
		return new Dollar(totalTermPremium.getValue());
	}

	public static Dollar getStateAndLocalTaxesAndPremiumSurchargesPremium() {
		return new Dollar(tableStateAndLocalTaxesSummary.getFooter().getCell(tableStateAndLocalTaxesSummary.getColumnsCount()).getValue());
	}

	public TestData getRatingDetailsQuoteInfoData() {
		if (!tableRatingDetailsQuoteInfo.isPresent()) {
			RatingDetailsView.open();
		}

		Map<String, Object> map = new LinkedHashMap<>();
		List<String> keys = tableRatingDetailsQuoteInfo.getColumn(1).getValue();
		List<String> values = tableRatingDetailsQuoteInfo.getColumn(2).getValue();
		assertThat(keys.size()).as("Number of keys in table is not equal to number of values.").isEqualTo(values.size());

		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}

		keys = tableRatingDetailsQuoteInfo.getColumn(3).getValue();
		values = tableRatingDetailsQuoteInfo.getColumn(4).getValue();
		assertThat(keys.size()).as("Number of keys in table is not equal to number of values.").isEqualTo(values.size());
		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}

		return new SimpleDataProvider(map);
	}

	public TestData getRatingDetailsUnderwritingValueData() {

		Map<String, Object> map = new LinkedHashMap<>();
		List<String> keys = tableRatingDetailsUnderwriting.getColumn(1).getValue();
		List<String> values = tableRatingDetailsUnderwriting.getColumn(2).getValue();
		assertThat(keys.size()).as("Number of keys in table is not equal to number of values.").isEqualTo(values.size());

		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}

		keys = tableRatingDetailsUnderwriting.getColumn(4).getValue();
		values = tableRatingDetailsUnderwriting.getColumn(5).getValue();
		assertThat(keys.size()).as("Number of keys in table is not equal to number of values.").isEqualTo(values.size());
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
		List<String> keys = new ArrayList<>();
		List<String> _keys = new ArrayList<>();
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
			List<String> values = new ArrayList<>();
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

	public TestData getFormsData() {
		TestData td;
		Map<String, String> forms = new LinkedHashMap<>();
		for (int row = 1; row <= tableFormsSummary.getRowsCount(); row++) {
			forms.put(tableFormsSummary.getRow(row).getCell(1).getValue(), tableFormsSummary.getRow(row).getCell(2).getValue());
		}
		return new SimpleDataProvider(forms);
	}

	public Dollar getPolicyLevelLiabilityCoveragesPremium() {
		return new Dollar(tablePolicyLevelLiabilityCoveragesPremium.getRow(1).getCell(3).getValue());
	}

	public static RatingDetailsTable tableCappedPolicyPremium = new RatingDetailsTable
			("//div[@id='cappingDetailsPopupPanel:vehicleCapPanel_body']//table");

	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(td);
		if (td.getTestData(getMetaKey()) != null && !td.getTestData(getMetaKey()).containsKey(AutoSSMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM.getLabel())) {
			hideHeader();
			btnCalculatePremium().click();
			showHeader();
		}
		return this;
	}

	public JavaScriptButton btnCalculatePremium() {
		return getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM.getLabel(), JavaScriptButton.class);
	}

	public void calculatePremium() {
		if (!btnCalculatePremium().isPresent()) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		}
		btnCalculatePremium().click();
	}

	@Override
	public Tab submitTab() {
		if (RatingDetailsView.buttonRatingDetailsOk.isPresent() && RatingDetailsView.buttonRatingDetailsOk.isVisible()) {
			RatingDetailsView.buttonRatingDetailsOk.click();
		}
		buttonContinue.click();
		return this;
	}

	public Dollar getVehicleCoveragePremiumByVehicle(int index) {
		String xpathForVehicle = "//table[@id='policyDataGatherForm:subtotalVehiclePremium_%s']";
		String xpathForVehicleFormatted = String.format(xpathForVehicle, index);
		Table vehcilePremiumTable = new Table(By.xpath(xpathForVehicleFormatted));
		return new Dollar(vehcilePremiumTable.getRow(1).getCell(3).getValue());
	}

	public String getVehicleCoverageDetailsValueByVehicle(int index, String coverageName) {
		Table vehicleCoverageDetailsTable = new Table(tableVehicleCoverageDetails.format(index));
		Row coverageRow = vehicleCoverageDetailsTable.getRowContains(1, coverageName);
		Cell cell = coverageRow.getCell(2);
		String result;
		if (cell.controls.comboBoxes.getFirst().isPresent()) {
			result = cell.controls.comboBoxes.getFirst().getValue();
		} else if (cell.controls.textBoxes.getFirst().isPresent()) {
			result = cell.controls.textBoxes.getFirst().getValue();
		} else {
			result = cell.getValue();
		}
		return result;
	}

	public void setVehicleCoverageDetailsValueByVehicle(int index, String coverageName, String value) {
		Table vehicleCoverageDetailsTable = new Table(tableVehicleCoverageDetails.format(index));
		Row coverageRow = vehicleCoverageDetailsTable.getRowContains(1, coverageName);
		Cell cell = coverageRow.getCell(2);
		if (cell.controls.comboBoxes.getFirst().isPresent()) {
			cell.controls.comboBoxes.getFirst().setValueContains(value);
		} else if (cell.controls.textBoxes.getFirst().isPresent()) {
			cell.controls.textBoxes.getFirst().setValue(value);
		} else {
			cell.controls.radioGroups.getFirst().setValue(value);
		}

	}



	public void setPolicyCoverageDetailsValue(String coverageName, String value) {
		Row coverageRow = tablePolicyLevelLiabilityCoverages.getRowContains(1, coverageName);
		Cell cell = coverageRow.getCell(2);
		if (cell.controls.comboBoxes.getFirst().isPresent()) {
			cell.controls.comboBoxes.getFirst().setValueContains(value);
		} else if (cell.controls.textBoxes.getFirst().isPresent()) {
			cell.controls.textBoxes.getFirst().setValue(value);
		} else {
			cell.controls.radioGroups.getFirst().setValue(value);
		}
	}

	public String getPolicyCoverageDetailsTermPremium(String coverageName) {
		Row coverageRow = tablePolicyLevelLiabilityCoverages.getRowContains(1, coverageName);
		Cell cell = coverageRow.getCell(3);
		return cell.getValue();
	}

	public Dollar getPolicyCoveragePremium() {
		return new Dollar(tablePolicyLevelLiabilityCoverages.getRow(1).getCell(3).getValue());
	}

	public String getPolicyCoverageDetailsValue(String coverageName) {
		Row coverageRow = tablePolicyLevelLiabilityCoverages.getRowContains(1, coverageName);
		Cell cell = coverageRow.getCell(2);
		String result;
		if (cell.controls.comboBoxes.getFirst().isPresent()) {
			result = cell.controls.comboBoxes.getFirst().getValue();
		} else if (cell.controls.textBoxes.getFirst().isPresent()) {
			result = cell.controls.textBoxes.getFirst().getValue();
		} else {
			result = cell.getValue();
		}
		return result;
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

				List<String> tempValues = new ArrayList<>();
				tempValues.addAll(values);
				tempValues.removeIf("No Coverage"::equals);
				tempValues.removeIf("Unstacked"::equals);
				tempValues.removeIf("Yes"::equals);
				if (tempValues.stream().allMatch(String::isEmpty)) {
					continue; // skip column with only "No Coverage"
				}

				assertThat(keys.size()).as("Number of keys in table is not equal to number of values.").isEqualTo(values.size());

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
