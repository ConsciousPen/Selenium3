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
public class TestVersionsConflict extends TestComparisonConflictAbstract {

	@Override
	protected ArrayListMultimap<String, String> getUIFieldsToTDMapping() {
		return ArrayListMultimap.create(VersionsConflictConstants.UI_FIELDS_TO_TD_MAPPING);
	}

	@Override
	protected ArrayListMultimap<String, String> getPredefinedExpectedValues() {
		return ArrayListMultimap.create(VersionsConflictConstants.PREDEFINED_EXPECTED_VALUES);
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14826"})
	public void pas14826_ooseConflictManualNamedInsuredInformation(@Optional("CA") String state) {
		ooseConflict(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.NAMED_INSURED_INFORMATION,  VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_2, VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_1,  "GeneralTab", "NamedInsuredInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14826"})
	public void pas14826_ooseConflictAutomaticNamedInsuredInformation(@Optional("CA") String state) {
		ooseConflict(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.NAMED_INSURED_INFORMATION,  VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_2, VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_1,  "GeneralTab", "NamedInsuredInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-14826"})
	public void pas14826_renewalMergeNamedInsuredInformation(@Optional("CA") String state) {
		renewalMerge(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.NAMED_INSURED_INFORMATION,  VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_1,  "GeneralTab", "NamedInsuredInformation");
	}

	//AAA Products Owned  section

	private TestData getTDAAAProductOwnedVersion1() {
		return getTestSpecificTD("TestData_AAAProductOwned_Version1");
	}

	private TestData getTDAAAProductOwnedVersion2() {
		return getTestSpecificTD("TestData_AAAProductOwned_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17148"})
	public void pas17148_ooseConflictManualAAAProductOwned(@Optional("CA") String state) {
		ooseConflict(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.AAA_PRODUCT_OWNED_MANUAL,  VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_2, VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_1,  "GeneralTab", "AAAProductOwned", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17148"})
	public void pas17148_ooseConflictAutomaticAAAProductOwned(@Optional("CA") String state) {
		ooseConflict(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.AAA_PRODUCT_OWNED_AUTOMATIC,  VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_2, VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_1,  "GeneralTab", "AAAProductOwned", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17148"})
	public void pas17148_renewalMergeAAAProductOwned(@Optional("CA") String state) {
		renewalMerge(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.AAA_PRODUCT_OWNED_AUTOMATIC_RENEWAL,  VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_1,  "GeneralTab", "AAAProductOwned");
	}

	//Contact Information section

	private TestData getTDContactInformationVersion1() {
		return getTestSpecificTD("TestData_ContactInformation_Version1");
	}

	private TestData getTDContactInformationVersion2() { return getTestSpecificTD("TestData_ContactInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17149"})
	public void pas17149_ooseConflictManualContactInformation(@Optional("CA") String state) {
		ooseConflict(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.CONTACT_INFORMATION,  VersionsConflictConstants.CONTACT_INFORMATION_VERSION_2, VersionsConflictConstants.CONTACT_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17149"})
	public void pas17149_ooseConflictAutomaticContactInformation(@Optional("CA") String state) {
		ooseConflict(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.CONTACT_INFORMATION,  VersionsConflictConstants.CONTACT_INFORMATION_VERSION_2, VersionsConflictConstants.CONTACT_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17149"})
	public void pas17149_renewalMergeContactInformation(@Optional("CA") String state) {
		renewalMerge(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.CONTACT_INFORMATION,  VersionsConflictConstants.CONTACT_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation");
	}

	//Current Carrier section

	private TestData getTDCurrentCarrierInformationVersion1() {
		return getTestSpecificTD("TestData_CurrentCarrierInformation_Version1");
	}

	private TestData getTDCurrentCarrierInformationVersion2() { return getTestSpecificTD("TestData_CurrentCarrierInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17150"})
	public void pas17150_renewalMergeCurrentCarrierInformation(@Optional("CA") String state) {
		renewalMerge(getTDCurrentCarrierInformationVersion1(), getTDCurrentCarrierInformationVersion2(), VersionsConflictConstants.CURRENT_CARRIER_INFORMATION,  VersionsConflictConstants.CURRENT_CARRIER_INFORMATION_VERSION_1,  "GeneralTab", "CurrentCarrierInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16049", "PAS-16058"})
	public void pas16049_ooseConflictManualPolicyInformation(@Optional("CA") String state) {
		ooseConflict(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.POLICY_INFORMATION_MANUAL,  VersionsConflictConstants.POLICY_INFORMATION_VERSION_2, VersionsConflictConstants.POLICY_INFORMATION_VERSION_1,  "GeneralTab", "PolicyInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16049", "PAS-16058"})
	public void pas16049_ooseConflictAutomaticPolicyInformation(@Optional("CA") String state) {
		ooseConflict(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.POLICY_INFORMATION_AUTOMATIC,  VersionsConflictConstants.POLICY_INFORMATION_VERSION_2, VersionsConflictConstants.POLICY_INFORMATION_VERSION_1,  "GeneralTab", "PolicyInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16049", "PAS-16058"})
	public void pas16049_renewalMergePolicyInformation(@Optional("CA") String state) {
		renewalMerge(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.POLICY_INFORMATION_AUTOMATIC,  VersionsConflictConstants.POLICY_INFORMATION_VERSION_1,  "GeneralTab", "PolicyInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16563"})
	public void pas16563_ooseConflictManualThirdPartyDesignee(@Optional("CA") String state) {
		ooseConflict(getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsConflictConstants.THIRD_PARTY_DESIGNEE,  VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_2, VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_1,  "GeneralTab", "ThirdPartyDesigneeInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16563"})
	public void pas16563_ooseConflictAutomaticThirdPartyDesignee(@Optional("CA") String state) {
		ooseConflict(getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsConflictConstants.THIRD_PARTY_DESIGNEE,  VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_2, VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_1,  "GeneralTab", "ThirdPartyDesigneeInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16563"})
	public void pas16563_renewalMergeThirdPartyDesignee(@Optional("CA") String state) {
		renewalMerge(getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsConflictConstants.THIRD_PARTY_DESIGNEE,  VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_1,  "GeneralTab", "ThirdPartyDesigneeInformation");
	}

	//Driver Information section

	private TestData getTDDriverInformationVersion1() {
		return getTestSpecificTD("TestData_DriverInformation_Version1");
	}

	private TestData getTDDriverInformationVersion2() { return getTestSpecificTD("TestData_DriverInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17151"})
	public void pas17151_ooseConflictManualDriverInformation(@Optional("CA") String state) {
		ooseConflict(getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.DRIVER_INFORMATION_MANUAL,  VersionsConflictConstants.DRIVER_INFORMATION_VERSION_2, VersionsConflictConstants.DRIVER_INFORMATION_VERSION_1,  "DriverTab", "DriverInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17151"})
	public void pas17151_ooseConflictAutomaticDriverInformation(@Optional("CA") String state) {
		ooseConflict(getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.DRIVER_INFORMATION_AUTOMATIC,  VersionsConflictConstants.DRIVER_INFORMATION_VERSION_2, VersionsConflictConstants.DRIVER_INFORMATION_VERSION_1,  "DriverTab", "DriverInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-17151"})
	public void pas17151_renewalMergeDriverInformation(@Optional("CA") String state) {
		renewalMerge(getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.DRIVER_INFORMATION_AUTOMATIC,  VersionsConflictConstants.DRIVER_INFORMATION_VERSION_1,  "DriverTab", "DriverInformation");
	}

	//Vehicle Information section

	private TestData getTDVehicleInformationVersion1() {
		return getTestSpecificTD("TestData_VehicleInformation_Version1");
	}

	private TestData getTDVehicleInformationRenewalVersion1() {
		return getTestSpecificTD("TestData_VehicleInformationRenewal_Version1");
	}

	private TestData getTDVehicleInformationVersion2() {
		return getTestSpecificTD("TestData_VehicleInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-15242", "PAS-14204", "PAS-14203", "PAS-14265", "PAS-17154", "PAS-17153"})
	public void pas15242_ooseConflictManualVehicleInformation(@Optional("CA") String state) {
		ooseConflict(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.VEHICLE_INFORMATION_MANUAL,  VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_2, VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_1,  "VehicleTab", "VehicleInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-15242", "PAS-14204", "PAS-14203", "PAS-14265", "PAS-17154", "PAS-17153"})
	public void pas15242_ooseConflictAutomaticVehicleInformation(@Optional("CA") String state) {
		ooseConflict(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.VEHICLE_INFORMATION_AUTOMATIC,  VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_2, VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_1,  "VehicleTab", "VehicleInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-15242", "PAS-14204", "PAS-14203", "PAS-14265", "PAS-17154", "PAS-17153"})
	public void pas15242_renewalMergeVehicleInformation(@Optional("CA") String state) {
		renewalMerge(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.VEHICLE_INFORMATION_AUTOMATIC,  VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_1,  "VehicleTab", "VehicleInformation");
	}
}