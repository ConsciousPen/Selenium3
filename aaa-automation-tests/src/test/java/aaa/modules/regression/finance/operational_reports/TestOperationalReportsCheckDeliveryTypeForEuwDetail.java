package aaa.modules.regression.finance.operational_reports;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Test;
import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.OperationalReportsConstants;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;

public class TestOperationalReportsCheckDeliveryTypeForEuwDetail extends BaseTest {

	private OperationalReportsTab orTab = new OperationalReportsTab();

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
	 * TC Steps:
	 * 1. Navigate Reports -> Operational Reports;
	 * 2. Fill Category, Type, Name;
	 * 3. Check Delivery Type radio buttons 'Download', 'E-Mail' are displayed;
	 * 4. Select Email radio button;
	 * 5. Check 'E-mail Address' Selection and 'Additional E-mail Address' field appeared;
	 * 6. Click 'Report' button;
	 * 7. Verify, that errors 'Field is required' for 'E-mail Address' Selection and 'Additional E-mail Address'
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-15536")
	public void pas15536_testOperationalReportsCheckDeliveryTypeForEuwDetail() {
		opReportApp().open();

		orTab.fillTab(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));

		//Check Delivery Type radio buttons 'Download', 'E-Mail' are displayed;
		assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.DELIVERY_TYPE))
				.containsAllOptions(OperationalReportsConstants.DeliveryType.EMAIL, OperationalReportsConstants.DeliveryType.DOWNLOAD);

		orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.DELIVERY_TYPE).setValue(OperationalReportsConstants.DeliveryType.EMAIL);

		//Check 'E-mail Address' Selection and 'Additional E-mail Address' field appeared;
		assertSoftly(softly -> {
			softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.EMAIL_ADDRESS)).isPresent();
			softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.ADDITIONAL_EMAIL)).isPresent();
		});

		OperationalReportsTab.buttonGenerateReport.click();

		//Verify, that errors 'Field is required' for 'E-mail Address' Selection and 'Additional E-mail Address'
		assertSoftly(softly -> {
			softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.EMAIL_ADDRESS)
					.getWarning().toString()).contains("Field is required");
			softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.ADDITIONAL_EMAIL)
					.getWarning().toString()).contains("Field is required");
		});
	}
}
