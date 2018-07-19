package aaa.modules.regression.sales.auto_ss.functional;

import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.google.common.collect.ArrayListMultimap;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.regression.sales.template.functional.TestComparisonConflictAbstract;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


public class TestVersionsConflict extends TestComparisonConflictAbstract {

	@Override
	protected ArrayListMultimap<String, String> getUIFieldsToTDMapping() {
		return ArrayListMultimap.create(TestVersionsConflictConstants.uiFieldsToTDMapping);
	}

	@Override
	protected ArrayListMultimap<String, String> getPredefinedExpectedValues() {
		return ArrayListMultimap.create(TestVersionsConflictConstants.predefinedExpectedValues);
	}

	@Override
	protected Map<String, String> getComparisonPageDifferentValues() {
		return TestVersionsComparisonConstants.comparisonPageDifferentValues;
	}

	@Override
	protected Tab getGeneralTab() { return new GeneralTab(); }

	@Override
	protected Tab getDocumentsAndBindTab() { return new DocumentsAndBindTab(); }

	@Override
	protected void navigateToGeneralTab() { NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); }

	@Override
	protected TestData getTdPolicy() {
		return testDataManager.policy.get(getPolicyType());
	}

	@Override
	protected PolicyType getPolicyType() { return PolicyType.AUTO_SS; }

	//Named Insured Information section

	private TestData getTDNamedInsuredInformationVersion1() {
		return getTestSpecificTD("TestData_NamedInsuredInformation_Version1");
	}

	private TestData getTDNamedInsuredInformationVersion2() {
		return getTestSpecificTD("TestData_NamedInsuredInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_ooseConflictManualNamedInsuredInformation(@Optional("AZ") String state) {
		ooseConflict(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), TestVersionsConflictConstants.namedInsuredInformation,  TestVersionsConflictConstants.namedInsuredInformationVersion2, TestVersionsConflictConstants.namedInsuredInformationVersion1,  "GeneralTab", "NamedInsuredInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_ooseConflictAutomaticNamedInsuredInformation(@Optional("AZ") String state) {
		ooseConflict(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), TestVersionsConflictConstants.namedInsuredInformation,  TestVersionsConflictConstants.namedInsuredInformationVersion2, TestVersionsConflictConstants.namedInsuredInformationVersion1,  "GeneralTab", "NamedInsuredInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_renewalMergeNamedInsuredInformation(@Optional("AZ") String state) {
		renewalMerge(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), TestVersionsConflictConstants.namedInsuredInformation,  TestVersionsConflictConstants.namedInsuredInformationVersion1,  "GeneralTab", "NamedInsuredInformation");
	}

	//Vehicle Information section

	private TestData getTDVehicleInformationVersion1() {
		return getTestSpecificTD("TestData_VehicleInformation_Version1");
	}

	private TestData getTDVehicleInformationVersion2() {
		return getTestSpecificTD("TestData_VehicleInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-14121"})
	public void pas14121_ooseConflictManualVehicleInformation(@Optional("AZ") String state) {
		ooseConflict(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), TestVersionsConflictConstants.vehicleInformation,  TestVersionsConflictConstants.vehicleInformationVersion2, TestVersionsConflictConstants.vehicleInformationVersion1,  "VehicleTab", "VehicleInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-14121"})
	public void pas14121_ooseConflictAutomaticVehicleInformation(@Optional("AZ") String state) {
		ooseConflict(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), TestVersionsConflictConstants.vehicleInformation,  TestVersionsConflictConstants.vehicleInformationVersion2, TestVersionsConflictConstants.vehicleInformationVersion1,  "VehicleTab", "VehicleInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-14121"})
	public void pas14121_renewalMergeVehicleInformation(@Optional("AZ") String state) {
		renewalMerge(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), TestVersionsConflictConstants.vehicleInformation,  TestVersionsConflictConstants.vehicleInformationVersion1,  "VehicleTab", "VehicleInformation");
	}
}