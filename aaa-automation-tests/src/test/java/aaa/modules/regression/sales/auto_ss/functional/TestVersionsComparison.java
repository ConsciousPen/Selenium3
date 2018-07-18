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

public class TestVersionsComparison extends TestComparisonConflictAbstract {

	@Override
	protected ArrayListMultimap<String, String> getAttributesToTDMapping() {
		return ArrayListMultimap.create(TestVersionsComparisonConstants.attributesToTDMapping);
	}

	@Override
	protected ArrayListMultimap<String, String> getPredefinedExpectedValues() {
		return ArrayListMultimap.create(TestVersionsComparisonConstants.predefinedExpectedValues);
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

	protected TestData getTDNamedInsuredInformationVersion1() {
		return getTestSpecificTD("TestData_NamedInsuredInformation_Version1");
	}

	protected TestData getTDNamedInsuredInformationVersion2() {
		return getTestSpecificTD("TestData_NamedInsuredInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12266"})
	public void pas12266_dataGatherComparisonNamedInsuredInformation(@Optional("AZ") String state) {
		dataGatherComparison(state, getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), TestVersionsComparisonConstants.dataGatherNamedInsuredInformation, "GeneralTab", "NamedInsuredInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12266"})
	public void pas12266_endorsementsComparisonNamedInsuredInformation(@Optional("AZ") String state) {
		endorsementsComparison(state, getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), TestVersionsComparisonConstants.endorsementRenewalNamedInsuredInformation, "GeneralTab", "NamedInsuredInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12266"})
	public void pas12266_renewalComparisonNamedInsuredInformation(@Optional("AZ") String state) {
		renewalComparison(state, getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), TestVersionsComparisonConstants.endorsementRenewalNamedInsuredInformation, "GeneralTab", "NamedInsuredInformation");
	}

	//AAA Product Owned section

	protected TestData getTDAAAProductOwnedVersion1() {
		return getTestSpecificTD("TestData_AAAProductOwned_Version1");
	}

	protected TestData getTDAAAProductOwnedVersion2() {
		return getTestSpecificTD("TestData_AAAProductOwned_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12883"})
	public void pas12883_dataGatherComparisonAAAProductOwned(@Optional("AZ") String state) {
		dataGatherComparison(state, getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), TestVersionsComparisonConstants.aaaProductOwned, "GeneralTab", "AAAProductOwned");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12883"})
	public void pas12883_endorsementsComparisonAAAProductOwned(@Optional("AZ") String state) {
		endorsementsComparison(state, getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), TestVersionsComparisonConstants.aaaProductOwned, "GeneralTab", "AAAProductOwned");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12883"})
	public void pas12883_renewalComparisonAAAProductOwned(@Optional("AZ") String state) {
		renewalComparison(state, getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), TestVersionsComparisonConstants.aaaProductOwned, "GeneralTab", "AAAProductOwned");
	}

	//Vehicle Information section

	protected TestData getTDVehicleInformationVersion1() {
		return getTestSpecificTD("TestData_VehicleInformation_Version1");
	}

	protected TestData getTDVehicleInformationVersion2() {
		return getTestSpecificTD("TestData_VehicleInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12469"})
	public void pas12469_dataGatherComparisonVehicleInformation(@Optional("AZ") String state) {
		dataGatherComparison(state, getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), TestVersionsComparisonConstants.vehicleInformation, "VehicleTab", "VehicleInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12469"})
	public void pas12469_endorsementsComparisonVehicleInformation(@Optional("AZ") String state) {
		endorsementsComparison(state, getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), TestVersionsComparisonConstants.vehicleInformation, "VehicleTab", "VehicleInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12469"})
	public void pas12469_renewalComparisonVehicleInformation(@Optional("AZ") String state) {
		renewalComparison(state, getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), TestVersionsComparisonConstants.vehicleInformation, "VehicleTab", "VehicleInformation");
	}

}