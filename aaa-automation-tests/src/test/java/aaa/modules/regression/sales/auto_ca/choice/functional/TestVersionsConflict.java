package aaa.modules.regression.sales.auto_ca.choice.functional;

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
	protected PolicyType getPolicyType() { return PolicyType.AUTO_CA_CHOICE; }

	private TestData getTDNBPolicy() {
		return getTestSpecificTD("TestData_NB_Policy");
	}

	//Named Insured Information section

	private TestData getTDNamedInsuredInformationVersion1() {
		return getTestSpecificTD("TestData_NamedInsuredInformation_Version1");
	}

	private TestData getTDNamedInsuredInformationVersion2() {
		return getTestSpecificTD("TestData_NamedInsuredInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-14826"})
	public void pas14826_ooseConflictManualNamedInsuredInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.NAMED_INSURED_INFORMATION,  VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_2, VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_1,  "GeneralTab", "NamedInsuredInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-14826"})
	public void pas14826_ooseConflictAutomaticNamedInsuredInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.NAMED_INSURED_INFORMATION,  VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_2, VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_1,  "GeneralTab", "NamedInsuredInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-14826"})
	public void pas14826_renewalMergeNamedInsuredInformation(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.NAMED_INSURED_INFORMATION,  VersionsConflictConstants.NAMED_INSURED_INFORMATION_VERSION_1,  "GeneralTab", "NamedInsuredInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17148"})
	public void pas17148_ooseConflictManualAAAProductOwned(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.AAA_PRODUCT_OWNED_MANUAL,  VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_2, VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_1,  "GeneralTab", "AAAProductOwned", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17148"})
	public void pas17148_ooseConflictAutomaticAAAProductOwned(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.AAA_PRODUCT_OWNED_AUTOMATIC,  VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_2, VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_1,  "GeneralTab", "AAAProductOwned", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17148"})
	public void pas17148_renewalMergeAAAProductOwned(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.AAA_PRODUCT_OWNED_AUTOMATIC,  VersionsConflictConstants.AAA_PRODUCT_OWNED_VERSION_1,  "GeneralTab", "AAAProductOwned");
	}

	//Contact Information section

	private TestData getTDContactInformationVersion1() {
		return getTestSpecificTD("TestData_ContactInformation_Version1");
	}

	private TestData getTDContactInformationVersion2() { return getTestSpecificTD("TestData_ContactInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17149"})
	public void pas17149_ooseConflictManualContactInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.CONTACT_INFORMATION,  VersionsConflictConstants.CONTACT_INFORMATION_VERSION_2, VersionsConflictConstants.CONTACT_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17149"})
	public void pas17149_ooseConflictAutomaticContactInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.CONTACT_INFORMATION,  VersionsConflictConstants.CONTACT_INFORMATION_VERSION_2, VersionsConflictConstants.CONTACT_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17149"})
	public void pas17149_renewalMergeContactInformation(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.CONTACT_INFORMATION,  VersionsConflictConstants.CONTACT_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation");
	}

	//Current Carrier section

	private TestData getTDCurrentCarrierInformationVersion1() {
		return getTestSpecificTD("TestData_CurrentCarrierInformation_Version1");
	}

	private TestData getTDCurrentCarrierInformationVersion2() { return getTestSpecificTD("TestData_CurrentCarrierInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17150"})
	public void pas17150_renewalMergeCurrentCarrierInformation(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getTDCurrentCarrierInformationVersion1(), getTDCurrentCarrierInformationVersion2(), VersionsConflictConstants.CURRENT_CARRIER_INFORMATION,  VersionsConflictConstants.CURRENT_CARRIER_INFORMATION_VERSION_1,  "GeneralTab", "CurrentCarrierInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-16049", "PAS-16058"})
	public void pas16049_ooseConflictManualPolicyInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.POLICY_INFORMATION_MANUAL,  VersionsConflictConstants.POLICY_INFORMATION_VERSION_2, VersionsConflictConstants.POLICY_INFORMATION_VERSION_1,  "GeneralTab", "PolicyInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-16049", "PAS-16058"})
	public void pas16049_ooseConflictAutomaticPolicyInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.POLICY_INFORMATION_AUTOMATIC,  VersionsConflictConstants.POLICY_INFORMATION_VERSION_2, VersionsConflictConstants.POLICY_INFORMATION_VERSION_1,  "GeneralTab", "PolicyInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-16049", "PAS-16058"})
	public void pas16049_renewalMergePolicyInformation(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.POLICY_INFORMATION_AUTOMATIC,  VersionsConflictConstants.POLICY_INFORMATION_VERSION_1,  "GeneralTab", "PolicyInformation");
	}

	//Third Party Designee Information section

	private TestData getTDThirdPartyDesigneeVersion1() {
		return getTestSpecificTD("TestData_ThirdPartyDesigneeInformation_Version1");
	}

	private TestData getTDThirdPartyDesigneeVersion2() {
		return getTestSpecificTD("TestData_ThirdPartyDesigneeInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16563"})
	public void pas16563_ooseConflictManualThirdPartyDesignee(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsConflictConstants.THIRD_PARTY_DESIGNEE,  VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_2, VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_1,  "GeneralTab", "ThirdPartyDesigneeInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16563"})
	public void pas16563_ooseConflictAutomaticThirdPartyDesignee(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsConflictConstants.THIRD_PARTY_DESIGNEE,  VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_2, VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_1,  "GeneralTab", "ThirdPartyDesigneeInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-16563"})
	public void pas16563_renewalMergeThirdPartyDesignee(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getTDThirdPartyDesigneeVersion1(), getTDThirdPartyDesigneeVersion2(), VersionsConflictConstants.THIRD_PARTY_DESIGNEE,  VersionsConflictConstants.THIRD_PARTY_DESIGNEE_VERSION_1,  "GeneralTab", "ThirdPartyDesigneeInformation");
	}

	//Driver Information section

	private TestData getTDDriverInformationVersion1() {
		return getTestSpecificTD("TestData_DriverInformation_Version1");
	}

	private TestData getTDDriverInformationVersion2() { return getTestSpecificTD("TestData_DriverInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17151"})
	public void pas17151_ooseConflictManualDriverInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.DRIVER_INFORMATION_MANUAL,  VersionsConflictConstants.DRIVER_INFORMATION_VERSION_2, VersionsConflictConstants.DRIVER_INFORMATION_VERSION_1,  "DriverTab", "DriverInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17151"})
	public void pas17151_ooseConflictAutomaticDriverInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.DRIVER_INFORMATION_AUTOMATIC,  VersionsConflictConstants.DRIVER_INFORMATION_VERSION_2, VersionsConflictConstants.DRIVER_INFORMATION_VERSION_1,  "DriverTab", "DriverInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-17151"})
	public void pas17151_renewalMergeDriverInformation(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.DRIVER_INFORMATION_AUTOMATIC,  VersionsConflictConstants.DRIVER_INFORMATION_VERSION_1,  "DriverTab", "DriverInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-15242", "PAS-14204", "PAS-14203", "PAS-14265", "PAS-17154", "PAS-17153"})
	public void pas15242_ooseConflictManualVehicleInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.VEHICLE_INFORMATION_MANUAL,  VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_2, VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_1,  "VehicleTab", "VehicleInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-15242", "PAS-14204", "PAS-14203", "PAS-14265", "PAS-17154", "PAS-17153"})
	public void pas15242_ooseConflictAutomaticVehicleInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.VEHICLE_INFORMATION_AUTOMATIC,  VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_2, VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_1,  "VehicleTab", "VehicleInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-15242", "PAS-14204", "PAS-14203", "PAS-14265", "PAS-17154", "PAS-17153"})
	public void pas15242_renewalMergeVehicleInformation(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.VEHICLE_INFORMATION_AUTOMATIC,  VersionsConflictConstants.VEHICLE_INFORMATION_VERSION_1,  "VehicleTab", "VehicleInformation");
	}

	//Add Components Section

	private TestData getAddNamedInsuredInformationVersion1() {
		return getTestSpecificTD("TestData_AddNamedInsuredInformation_Version1");
	}

	private TestData getAddNamedInsuredInformationVersion2() {
		return getTestSpecificTD("TestData_AddNamedInsuredInformation_Version2");
	}

	private TestData getAddDriverAndActivityInformationVersion1() {
		return getTestSpecificTD("TestData_AddDriverInformation_Version1");
	}

	private TestData getAddDriverAndActivityInformationVersion2() {
		return getTestSpecificTD("TestData_AddDriverInformation_Version2");
	}

	private TestData getAddVehicleInformationVersion1() {
		return getTestSpecificTD("TestData_AddVehicleInformation_Version1");
	}

	private TestData getAddVehicleInformationVersion2() {
		return getTestSpecificTD("TestData_AddVehicleInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-19838", "PAS-19839", "PAS-19840", "PAS-19841"})
	public void pas19839_ooseConflictManualAddNamedInsuredInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getAddNamedInsuredInformationVersion1(), getAddNamedInsuredInformationVersion2(), VersionsConflictConstants.ADD_NAMED_INSURED_INFORMATION_MANUAL,  VersionsConflictConstants.ADD_NAMED_INSURED_INFORMATION_VERSION_2, VersionsConflictConstants.ADD_NAMED_INSURED_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})

	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-19838", "PAS-19839", "PAS-19840", "PAS-19841"})
	public void pas19839_ooseConflictAutomaticAddVehicleInformation(@Optional("CA") String state) {
		ooseConflict(getTDNBPolicy(), getAddVehicleInformationVersion1(), getAddVehicleInformationVersion2(), VersionsConflictConstants.ADD_VEHICLE_INFORMATION_AUTOMATIC,  VersionsConflictConstants.ADD_VEHICLE_INFORMATION_VERSION_2, VersionsConflictConstants.ADD_VEHICLE_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-19838", "PAS-19839", "PAS-19840", "PAS-19841"})
	public void pas19839_renewalMergeAddDriverAndActivityInformation(@Optional("CA") String state) {
		renewalMerge(getTDNBPolicy(), getAddDriverAndActivityInformationVersion1(), getAddDriverAndActivityInformationVersion2(), VersionsConflictConstants.ADD_DRIVER_INFORMATION_AUTOMATIC,  VersionsConflictConstants.ADD_DRIVER_INFORMATION_VERSION_1,  "GeneralTab", "ContactInformation");
	}

	//Assignment section

	private TestData getTDNewBusinessPolicy() {
		return getTestSpecificTD("TestData_Multiple_Drivers_NB_Policy"); }

	private TestData getTDAssignmentVersion1() {
		return getTestSpecificTD("TestData_Assignment_Version1");
	}

	private TestData getTDAssignmentVersion2() { return getTestSpecificTD("TestData_Assignment_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-25188"})
	public void pas25188_ooseConflictManualAssignment(@Optional("CA") String state) {
		ooseConflict(getTDNewBusinessPolicy(), getTDAssignmentVersion1(), getTDAssignmentVersion2(), VersionsConflictConstants.ASSIGNMENT,  VersionsConflictConstants.ASSIGNMENT_VERSION_2, VersionsConflictConstants.ASSIGNMENT_VERSION_1,  "AssignmentTab", "DriverVehicleRelationshipTable", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-25188"})
	public void pas25188_ooseConflicAutomaticAssignment(@Optional("CA") String state) {
		ooseConflict(getTDNewBusinessPolicy(), getTDAssignmentVersion1(), getTDAssignmentVersion2(), VersionsConflictConstants.ASSIGNMENT,  VersionsConflictConstants.ASSIGNMENT_VERSION_2, VersionsConflictConstants.ASSIGNMENT_VERSION_1,  "AssignmentTab", "DriverVehicleRelationshipTable", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-25188"})
	public void pas25188_renewalMergeAssignment(@Optional("CA") String state) {
		renewalMerge(getTDNewBusinessPolicy(), getTDAssignmentVersion1(), getTDAssignmentVersion2(), VersionsConflictConstants.ASSIGNMENT,  VersionsConflictConstants.ASSIGNMENT_VERSION_1,  "AssignmentTab", "DriverVehicleRelationshipTable");
	}

	private TestData getNBPolicyForMultipleDriversVehicles() {
		return getTestSpecificTD("TestData_Multiple_Drivers_Vehicles_NB_Policy");
	}

	private TestData getRemoveComponentVersion1() {
		return getTestSpecificTD("TestData_Plus20Days");
	}

	private TestData getRemoveComponentVersion2() {
		return getTestSpecificTD("TestData_Plus10Days");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-19838", "PAS-19839", "PAS-19840", "PAS-19841"})
	public void pas19839_ooseConflictAutomaticRemoveVehicleAndDriver(@Optional("CA") String state) {
		ooseConflictRemoveComponent(getNBPolicyForMultipleDriversVehicles(), getRemoveComponentVersion1(), getRemoveComponentVersion2(), VersionsConflictConstants.REMOVE_DRIVER_VEHICLE_INFORMATION,  VersionsConflictConstants.REMOVE_DRIVER_VEHICLE_INFORMATION_VERSION_2, VersionsConflictConstants.REMOVE_DRIVER_VEHICLE_INFORMATION_VERSION_1,  "EndorsementActionTab", "", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-19838", "PAS-19839", "PAS-19840", "PAS-19841"})
	public void pas19839_ooseConflictManualRemoveVehicleAndDriver(@Optional("CA") String state) {
		ooseConflictRemoveComponent(getNBPolicyForMultipleDriversVehicles(), getRemoveComponentVersion1(), getRemoveComponentVersion2(), VersionsConflictConstants.REMOVE_DRIVER_VEHICLE_INFORMATION,  VersionsConflictConstants.REMOVE_DRIVER_VEHICLE_INFORMATION_VERSION_2, VersionsConflictConstants.REMOVE_DRIVER_VEHICLE_INFORMATION_VERSION_1,  "EndorsementActionTab", "", false);
	}

	private TestData getNBPolicyForMultipleNamedInsureds() {
		return getTestSpecificTD("TestData_Multiple_Named_Insureds_NB_Policy");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-19838", "PAS-19839", "PAS-19840", "PAS-19841"})
	public void pas19839_renewalMergeAutomaticRemoveNamedInsured(@Optional("CA") String state) {
		renewalMergeRemoveComponent(getNBPolicyForMultipleNamedInsureds(), getRemoveComponentVersion1(), getRemoveComponentVersion2(), VersionsConflictConstants.REMOVE_NAMED_INSURED,  VersionsConflictConstants.REMOVE_NAMED_INSUREDS_RENEWAL_VERSION_1,  "EndorsementActionTab", "");
	}

	/**
	 * @name Scenario1: When a driver is assigned to a vehicle in endorsement, and that driver is removed in OOSE
	 * Then removed driver comes in conflict screen and when the user choses to remove the driver, then the assignment of the vehicle falls back to its previous value
	 * @scenario
	 * 1. Create a policy with 2 drivers, 1 vehicle. Assign Vehicle1 to Driver1
	 * 2. Do an endorsement at Today+20 days, Delete Driver 2
	 * 3. Do an OOSE at Today+10 days, Assign Vehicle1 to Driver2
	 * 4. On Conflict screen, we expect to see removal of Driver2 in conflict.
	 * 5. Choose to remove Driver2, and ensure that Vehicle1 is assigned back to Driver1
	 * @details
	 */

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-27125"})
	public void pas27125_ooseConflictManualAtomicMergeAssignment_Scenario1(@Optional("CA") String state) {
		ooseConflictAtomicMerge(getNBPolicyForAtomicMergeScenario1(), getAtomicMergeScenario1_Version1(), getAtomicMergeScenario1_Version2(), VersionsConflictConstants.ATOMIC_MERGE_SCENARIO1, VersionsConflictConstants.ATOMIC_MERGE_SCENARIO1_VERSION1, "AssignmentTab", "DriverVehicleRelationshipTable", false, false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-27125"})
	public void pas27125_ooseConflictAutoAtomicMergeAssignment_Scenario1(@Optional("CA") String state) {
		ooseConflictAtomicMerge(getNBPolicyForAtomicMergeScenario1(), getAtomicMergeScenario1_Version1(), getAtomicMergeScenario1_Version2(), VersionsConflictConstants.ATOMIC_MERGE_SCENARIO1, VersionsConflictConstants.ATOMIC_MERGE_SCENARIO1_VERSION1, "AssignmentTab", "DriverVehicleRelationshipTable", true, false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-27125"})
	public void pas27125_renewalMergeAtomicMergeAssignment_Scenario1(@Optional("CA") String state) {
		renewalMergeAtomicMerge(getNBPolicyForAtomicMergeScenario1(), getAtomicMergeScenario1_Version1(), getAtomicMergeScenario1_Version2(), VersionsConflictConstants.ATOMIC_MERGE_SCENARIO1, VersionsConflictConstants.ATOMIC_MERGE_SCENARIO1_RENEWAL, "AssignmentTab", "DriverVehicleRelationshipTable", true, false);
	}

	/**
	 * @name Scenario2: When a driver is assigned to a vehicle in endorsement, and that driver is removed in OOSE
	 * Then removed driver comes in conflict screen and when the user choses to remove the driver, then the assignment of the vehicle falls back to its previous value
	 * @scenario
	 * 1. Create a policy with 2 drivers, 1 vehicle. Assign Vehicle1 to Driver2
	 * 2. Do an endorsement at Today+20 days, Delete Driver 2, assign vehicle1 to driver1
	 * 3. Do an OOSE at Today+10 days, Assign Vehicle1 to Driver2
	 * 4. On Conflict screen, we expect to see removal of Driver2 in conflict.
	 * 5. Choose to remove Driver2, and ensure that Vehicle1 is assigned back to Driver1
	 * @details
	 */

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-27125"})
	public void pas27125_ooseConflictManualAtomicMergeAssignment_Scenario2(@Optional("CA") String state) {
		ooseConflictAtomicMerge(getNBPolicyForAtomicMergeScenario2(), getAtomicMergeScenario2_Version1(), getAtomicMergeScenario2_Version2(), VersionsConflictConstants.ATOMIC_MERGE_SCENARIO2, VersionsConflictConstants.ATOMIC_MERGE_SCENARIO2_VERSION1, "AssignmentTab", "DriverVehicleRelationshipTable", false, true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-27125"})
	public void pas27125_ooseConflictAutoAtomicMergeAssignment_Scenario2(@Optional("CA") String state) {
		ooseConflictAtomicMerge(getNBPolicyForAtomicMergeScenario2(), getAtomicMergeScenario2_Version1(), getAtomicMergeScenario2_Version2(), VersionsConflictConstants.ATOMIC_MERGE_SCENARIO2, VersionsConflictConstants.ATOMIC_MERGE_SCENARIO2_VERSION1, "AssignmentTab", "DriverVehicleRelationshipTable", true, false);
	}

	/**
	 * @name Scenario3: When there is a conflict and user tried to remove drivers which are assigned to vehicles
	 * Then roll on changes button is disabled and user is forced to go data gather state to fix vehicle assignment
	 * @scenario
	 * 1. Create a policy with 3 drivers, 3 vehicle. Assign Vehicle1 to Driver1, vehicle2 to driver2, vehicle3 to driver3
	 * 2. Do an endorsement at Today+20 days, Delete Driver 2, assign vehicle1 to driver1
	 * 3. Do an OOSE at Today+10 days, Assign Vehicle1 to Driver2
	 * 4. On Conflict screen, we expect to see removal of Driver2 in conflict.
	 * 5. Choose to remove Driver2, and ensure that Vehicle1 is assigned back to Driver1
	 * @details
	 */

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-27125"})
	public void pas27125_ooseConflictManualAtomicMergeAssignment_Scenario3(@Optional("CA") String state) {
		ooseConflictAtomicMerge_Scenario3(getNBPolicyForAtomicMergeScenario3(), getRemoveComponentVersion1(), getRemoveComponentVersion2(), VersionsConflictConstants.ATOMIC_MERGE_SCENARIO3, VersionsConflictConstants.REMOVE_DRIVER_VEHICLE_INFORMATION, false);
	}

	/**
	 * @name Scenario4: When a vehicle driver assignment in conflict and the two driver are removed
	 * Then the removal of drivers is also shown on conflict screen and upon the selections made driver assignment is dynamically made
	 * @scenario
	 * 1. Create a policy with 3 drivers, 3 vehicle. Assign Vehicle1 to Driver1, vehicle2 to driver2, vehicle3 to driver3
	 * 2. Do an endorsement at Today+20 days, Delete Driver 2, assign vehicle1 to driver3
	 * 3. Do an OOSE at Today+10 days, Delete Driver 3, Assign Vehicle1 to Driver2
	 * 4. On Conflict screen, we expect to see removal of Driver2 and Driver3 in conflict.
	 * 5. Upon the user decision to choose to remove Driver2/Driver3, assignment of driver to vehicle1 is decided
	 * @details
	 */

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-27125"})
	public void pas27125_ooseConflictManualAtomicMergeAssignment_Scenario4(@Optional("CA") String state) {
		ooseConflictAtomicMerge_Scenario4(getNBPolicyForAtomicMergeScenario4(), getAtomicMergeScenario4_Version1(), getAtomicMergeScenario4_Version2(), VersionsConflictConstants.ATOMIC_MERGE_MANUAL_SCENARIO4, VersionsConflictConstants.ATOMIC_MERGE_SCENARIO4_VERSION1, "AssignmentTab", "DriverVehicleRelationshipTable", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-27125"})
	public void pas27125_ooseConflictAutoAtomicMergeAssignment_Scenario4(@Optional("CA") String state) {
		ooseConflictAtomicMerge_Scenario4(getNBPolicyForAtomicMergeScenario4(), getAtomicMergeScenario4_Version1(), getAtomicMergeScenario4_Version2(), VersionsConflictConstants.ATOMIC_MERGE_AUTOMATIC_SCENARIO4, VersionsConflictConstants.ATOMIC_MERGE_SCENARIO4_VERSION1, "AssignmentTab", "DriverVehicleRelationshipTable", true);
	}

	private TestData getNBPolicyForAtomicMergeScenario1() {
		return getTestSpecificTD("TestData_AtomicMerge_Scenario1_NB_Policy");
	}

	private TestData getAtomicMergeScenario1_Version1() {
		return getTestSpecificTD("TestData_AtomicMerge_Scenario1_Version1");
	}

	private TestData getAtomicMergeScenario1_Version2() {
		return getTestSpecificTD("TestData_AtomicMerge_Scenario1_Version2");
	}

	private TestData getNBPolicyForAtomicMergeScenario2() {
		return getTestSpecificTD("TestData_AtomicMerge_Scenario2_NB_Policy");
	}

	private TestData getAtomicMergeScenario2_Version1() {
		return getTestSpecificTD("TestData_AtomicMerge_Scenario2_Version1");
	}

	private TestData getAtomicMergeScenario2_Version2() {
		return getTestSpecificTD("TestData_AtomicMerge_Scenario2_Version2");
	}

	private TestData getNBPolicyForAtomicMergeScenario3() {
		return getTestSpecificTD("TestData_Multiple_Drivers_Vehicles_NB_Policy");
	}

	private TestData getNBPolicyForAtomicMergeScenario4() {
		return getTestSpecificTD("TestData_Multiple_Drivers_NB_Policy");
	}

	private TestData getAtomicMergeScenario4_Version1() {
		return getTestSpecificTD("TestData_AtomicMerge_Scenario4_Version1");
	}

	private TestData getAtomicMergeScenario4_Version2() {
		return getTestSpecificTD("TestData_AtomicMerge_Scenario4_Version2");
	}
}