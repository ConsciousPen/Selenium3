package aaa.modules.regression.finance.operational_reports;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;
import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.OperationalReportsConstants;
import aaa.modules.BaseTest;
import aaa.toolkit.webdriver.customcontrols.AdvancedSelectorOR;
import toolkit.utils.TestInfo;

public class TestOperationalReportsBusinessUnitParameter extends BaseTest {

	private OperationalReportsTab orTab = new OperationalReportsTab();

	/**
	 * @author Reda Kazlauskiene
	 * Objectives: check that “Business Unit” values are displayed and selectable
	 * TC Steps:
	 * 1. Navigate Reports -> Operational Reports;
	 * 2. Fill Category, Type
	 * 3. Click Button “Business Unit”
	 * 4. check that “Business Unit” values are displayed and selectable
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-15535")
	public void pas15535_testOperationalReportsBusinessUnitParameter() {

		opReportApp().open();
		orTab.fillTab(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));

		//Check "Business Unit” values;
		AdvancedSelectorOR businessUnit = orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.BUSINESS_UNIT);

		assertThat(businessUnit.getAdvancedSelectorText().getValue()).isEqualTo("All");

		businessUnit.setValue(Arrays.asList(OperationalReportsConstants.BusinessUnit.CSAA_AFFINITY_INSURANCE_COMPANY,
				OperationalReportsConstants.BusinessUnit.CSAA_FIRE_CASUALTY_INSURANCE_COMPANY));

		assertThat(businessUnit.getAdvancedSelectorText().getValue()).contains(Arrays.asList(OperationalReportsConstants.BusinessUnit.CSAA_AFFINITY_INSURANCE_COMPANY,
				OperationalReportsConstants.BusinessUnit.CSAA_FIRE_CASUALTY_INSURANCE_COMPANY));

		businessUnit.setValue(Arrays.asList("ALL"));

		assertThat(businessUnit.getAdvancedSelectorText().getValue()).contains(Arrays.asList(OperationalReportsConstants.BusinessUnit.CSAA_AFFINITY_INSURANCE_COMPANY,
				OperationalReportsConstants.BusinessUnit.CSAA_FIRE_CASUALTY_INSURANCE_COMPANY, OperationalReportsConstants.BusinessUnit.CSAA_GENERAL_INSURANCE_COMPANY,
				OperationalReportsConstants.BusinessUnit.CSAA_INTERINSURANCE_BUREAU));

		businessUnit.setValue(new ArrayList<>());

		assertThat(businessUnit.getAdvancedSelectorText().getValue()).isEqualTo("All");
	}
}