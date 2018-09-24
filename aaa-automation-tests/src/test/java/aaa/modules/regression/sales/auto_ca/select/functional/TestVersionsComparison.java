package aaa.modules.regression.sales.auto_ca.select.functional;

import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.google.common.collect.ArrayListMultimap;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.modules.regression.sales.template.functional.TestComparisonConflictAbstract;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestVersionsComparison extends TestComparisonConflictAbstract {

	@Override
	protected ArrayListMultimap<String, String> getUIFieldsToTDMapping() {
		return ArrayListMultimap.create(VersionsComparisonConstants.UI_FIELDS_TO_TD_MAPPING);
	}

	@Override
	protected ArrayListMultimap<String, String> getPredefinedExpectedValues() {
		return ArrayListMultimap.create(VersionsComparisonConstants.PREDEFINED_EXPECTED_VALUES);
	}

	@Override
	protected Map<String, String> getComparisonPageDifferentValues() {
		return VersionsComparisonConstants.COMPARISON_PAGE_DIFFERENT_VALUES;
	}

	@Override
	protected Tab getGeneralTab() { return new GeneralTab(); }

	@Override
	protected Tab getDocumentsAndBindTab() { return new DocumentsAndBindTab(); }

	@Override
	protected void navigateToGeneralTab() { NavigationPage.toViewTab(NavigationEnum.AutoCaTab.GENERAL.get()); }

	@Override
	protected TestData getTdPolicy() {
		return testDataManager.policy.get(getPolicyType());
	}

	@Override
	protected PolicyType getPolicyType() { return PolicyType.AUTO_CA_SELECT; }

	//Named Insured Information section

	private TestData getTDNamedInsuredInformationVersion1() {
		return getTestSpecificTD("TestData_NamedInsuredInformation_Version1");
	}

	private TestData getTDNamedInsuredInformationVersion2() {
		return getTestSpecificTD("TestData_NamedInsuredInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13526"})
	public void pas13526_dataGatherComparisonNamedInsuredInformation(@Optional("CA") String state) {
		dataGatherComparison(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsComparisonConstants.DATA_GATHER_NAMED_INSURED_INFORMATION, "GeneralTab", "NamedInsuredInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13526"})
	public void pas13526_endorsementsComparisonNamedInsuredInformation(@Optional("CA") String state) {
		endorsementComparison(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_NAMED_INSURED_INFORMATION, "GeneralTab", "NamedInsuredInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13526"})
	public void pas13526_renewalComparisonNamedInsuredInformation(@Optional("CA") String state) {
		renewalComparison(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_NAMED_INSURED_INFORMATION, "GeneralTab", "NamedInsuredInformation");
	}

	//AAA Product Owned section

	private TestData getTDAAAProductOwnedVersion1() {
		return getTestSpecificTD("TestData_AAAProductOwned_Version1");
	}

	private TestData getTDAAAProductOwnedVersion2() {
		return getTestSpecificTD("TestData_AAAProductOwned_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13529"})
	public void pas13529_dataGatherComparisonAAAProductOwned(@Optional("CA") String state) {
		dataGatherComparison(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsComparisonConstants.AAA_PRODUCT_OWNED,"GeneralTab", "AAAProductOwned");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13529"})
	public void pas13529_endorsementsComparisonAAAProductOwned(@Optional("CA") String state) {
		endorsementComparison(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsComparisonConstants.AAA_PRODUCT_OWNED, "GeneralTab", "AAAProductOwned");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13529"})
	public void pas13529_renewalComparisonAAAProductOwned(@Optional("CA") String state) {
		renewalComparison(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsComparisonConstants.AAA_PRODUCT_OWNED, "GeneralTab", "AAAProductOwned");
	}

	//Contact Information section

	private TestData getTDContactInformationVersion1() {
		return getTestSpecificTD("TestData_ContactInformation_Version1");
	}

	private TestData getTDContactInformationVersion2() {
		return getTestSpecificTD("TestData_ContactInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13531"})
	public void pas13531_dataGatherComparisonContactInformation(@Optional("CA") String state) {
		dataGatherComparison(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsComparisonConstants.CONTACT_INFORMATION, "GeneralTab", "ContactInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13531"})
	public void pas13531_endorsementsComparisonContactInformation(@Optional("CA") String state) {
		endorsementComparison(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsComparisonConstants.CONTACT_INFORMATION, "GeneralTab", "ContactInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13531"})
	public void pas13531_renewalComparisonContactInformation(@Optional("CA") String state) {
		renewalComparison(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsComparisonConstants.CONTACT_INFORMATION, "GeneralTab", "ContactInformation");
	}

	//Current Carrier Information

	private TestData getTDCurrentCarrierInformationVersion1() {
		return getTestSpecificTD("TestData_CurrentCarrierInformation_Version1");
	}

	private TestData getTDCurrentCarrierInformationVersion2() {
		return getTestSpecificTD("TestData_CurrentCarrierInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13542"})
	public void pas13542_dataGatherComparisonCurrentCarrierInformation(@Optional("CA") String state) {
		dataGatherComparison(getTDCurrentCarrierInformationVersion1(), getTDCurrentCarrierInformationVersion2(), VersionsComparisonConstants.CURRENT_CARRIER_INFORMATION, "GeneralTab", "CurrentCarrierInformation");
	}

	//test for Endorsement comparison not needed, section can't be changed during Endorsement

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13542"})
	public void pas13542_renewalComparisonCurrentCarrierInformation(@Optional("CA") String state) {
		renewalComparison(getTDCurrentCarrierInformationVersion1(), getTDCurrentCarrierInformationVersion2(), VersionsComparisonConstants.CURRENT_CARRIER_INFORMATION, "GeneralTab", "CurrentCarrierInformation");
	}

	//Policy Information section

	private TestData getTDPolicyInformationVersion1() {
		return getTestSpecificTD("TestData_PolicyInformation_Version1");
	}

	private TestData getTDPolicyInformationVersion2() {
		return getTestSpecificTD("TestData_PolicyInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13589"})
	public void pas13589_dataGatherComparisonPolicyInformation(@Optional("CA") String state) {
		dataGatherComparison(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsComparisonConstants.POLICY_INFORMATION, "GeneralTab", "PolicyInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13589"})
	public void pas13589_endorsementsComparisonPolicyInformation(@Optional("CA") String state) {
		endorsementComparison(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsComparisonConstants.POLICY_INFORMATION, "GeneralTab", "PolicyInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13589"})
	public void pas13589_renewalComparisonPolicyInformation(@Optional("CA") String state) {
		renewalComparison(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsComparisonConstants.POLICY_INFORMATION, "GeneralTab", "PolicyInformation");
	}

	//Third Party Designee Information section

	private TestData getTDThirdPartyDesigneeVersion1() {
		return getTestSpecificTD("TestData_ThirdPartyDesigneeInformation_Version1");
	}

	private TestData getTDThirdPartyDesigneeVersion2() {
		return getTestSpecificTD("TestData_ThirdPartyDesigneeInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13832"})
	public void pas13832_dataGatherComparisonThirdPartyDesignee(@Optional("CA") String state) {
		dataGatherComparison(getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsComparisonConstants.THIRD_PARTY_DESIGNEE, "GeneralTab", "ThirdPartyDesigneeInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13832"})
	public void pas13832_endorsementsComparisonThirdPartyDesignee(@Optional("CA") String state) {
		endorsementComparison(getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsComparisonConstants.THIRD_PARTY_DESIGNEE, "GeneralTab", "ThirdPartyDesigneeInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-13832"})
	public void pas13832_renewalComparisonThirdPartyDesignee(@Optional("CA") String state) {
		renewalComparison(getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsComparisonConstants.THIRD_PARTY_DESIGNEE, "GeneralTab", "ThirdPartyDesigneeInformation");
	}

	//Driver Information section

	private TestData getTDDriverInformationVersion1() {
		return getTestSpecificTD("TestData_DriverInformation_Version1");
	}

	private TestData getTDDriverInformationRenewalVersion1() {
		return getTestSpecificTD("TestData_DriverInformation_Renewal_Version1");
	}

	private TestData getTDDriverInformationVersion2() {
		return getTestSpecificTD("TestData_DriverInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14147", "PAS-14146"})
	public void pas14147_dataGatherComparisonDriverInformation(@Optional("CA") String state) {
		dataGatherComparison(getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsComparisonConstants.DATA_GATHER_DRIVER_INFORMATION, "DriverTab", "DriverInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14147", "PAS-14146"})
	public void pas14147_endorsementsComparisonDriverInformation(@Optional("CA") String state) {
		endorsementComparison(getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_DRIVER_INFORMATION, "DriverTab", "DriverInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14147", "PAS-14146"})
	public void pas14147_renewalComparisonDriverInformation(@Optional("CA") String state) {
		renewalComparison(getTDDriverInformationRenewalVersion1(), getTDDriverInformationVersion2(), VersionsComparisonConstants.RENEWAL_DRIVER_INFORMATION, "DriverTab", "DriverInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14145","PAS-14144","PAS-14160","PAS-14159"})
	public void pas14145_dataGatherComparisonVehicleInformation(@Optional("CA") String state) {
		dataGatherComparison(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsComparisonConstants.DATA_GATHER_VEHICLE_INFORMATION, "VehicleTab", "VehicleInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14145","PAS-14144","PAS-14160","PAS-14159"})
	public void pas14145_endorsementsComparisonVehicleInformation(@Optional("CA") String state) {
		endorsementComparison(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_VEHICLE_INFORMATION, "VehicleTab", "VehicleInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14145","PAS-14144","PAS-14160","PAS-14159"})
	public void pas14145_renewalComparisonVehicleInformation(@Optional("CA") String state) {
		renewalComparison(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_VEHICLE_INFORMATION, "VehicleTab", "VehicleInformation");
	}
}