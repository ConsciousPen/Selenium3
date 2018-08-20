package aaa.modules.regression.sales.auto_ss.functional;

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
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.regression.sales.template.functional.TestComparisonConflictAbstract;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.AZ)
public class TestVersionsConflict extends TestComparisonConflictAbstract {

	@Override
	protected ArrayListMultimap<String, String> getUIFieldsToTDMapping() {
		return ArrayListMultimap.create(VersionsConflictConstants.uiFieldsToTDMapping);
	}

	@Override
	protected ArrayListMultimap<String, String> getPredefinedExpectedValues() {
		return ArrayListMultimap.create(VersionsConflictConstants.predefinedExpectedValues);
	}

	@Override
	protected Map<String, String> getComparisonPageDifferentValues() {
		return VersionsComparisonConstants.comparisonPageDifferentValues;
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
		ooseConflict(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.namedInsuredInformation,  VersionsConflictConstants.namedInsuredInformationVersion2, VersionsConflictConstants.namedInsuredInformationVersion1,  "GeneralTab", "NamedInsuredInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_ooseConflictAutomaticNamedInsuredInformation(@Optional("AZ") String state) {
		ooseConflict(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.namedInsuredInformation,  VersionsConflictConstants.namedInsuredInformationVersion2, VersionsConflictConstants.namedInsuredInformationVersion1,  "GeneralTab", "NamedInsuredInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_renewalMergeNamedInsuredInformation(@Optional("AZ") String state) {
		renewalMerge(getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsConflictConstants.namedInsuredInformation,  VersionsConflictConstants.namedInsuredInformationVersion1,  "GeneralTab", "NamedInsuredInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-17141"})
	public void pas17141_ooseConflictManualAAAProductOwned(@Optional("AZ") String state) {
		ooseConflict(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.aaaProductOwnedManual,  VersionsConflictConstants.aaaProductOwnedVersion2, VersionsConflictConstants.aaaProductOwnedVersion1,  "GeneralTab", "AAAProductOwned", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-17141"})
	public void pas17141_ooseConflictAutomaticAAAProductOwned(@Optional("AZ") String state) {
		ooseConflict(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.aaaProductOwnedAutomatic,  VersionsConflictConstants.aaaProductOwnedVersion2, VersionsConflictConstants.aaaProductOwnedVersion1,  "GeneralTab", "AAAProductOwned", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-17141"})
	public void pas17141_renewalMergeAAAProductOwned(@Optional("AZ") String state) {
		renewalMerge(getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsConflictConstants.aaaProductOwnedAutomatic,  VersionsConflictConstants.aaaProductOwnedVersion1,  "GeneralTab", "AAAProductOwned");
	}

	//Contact Information section

	private TestData getTDContactInformationVersion1() {
		return getTestSpecificTD("TestData_ContactInformation_Version1");
	}

	private TestData getTDContactInformationVersion2() { return getTestSpecificTD("TestData_ContactInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_ooseConflictManualContactInformation(@Optional("AZ") String state) {
		ooseConflict(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.contactInformation,  VersionsConflictConstants.contactInformationVersion2, VersionsConflictConstants.contactInformationVersion1,  "GeneralTab", "ContactInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_ooseConflictAutomaticContactInformation(@Optional("AZ") String state) {
		ooseConflict(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.contactInformation,  VersionsConflictConstants.contactInformationVersion2, VersionsConflictConstants.contactInformationVersion1,  "GeneralTab", "ContactInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_renewalMergeContactInformation(@Optional("AZ") String state) {
		renewalMerge(getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsConflictConstants.contactInformation,  VersionsConflictConstants.contactInformationVersion1,  "GeneralTab", "ContactInformation");
	}

	//Current Carrier section

	private TestData getTDCurrentCarrierInformationVersion1() {
		return getTestSpecificTD("TestData_CurrentCarrierInformation_Version1");
	}

	private TestData getTDCurrentCarrierInformationVersion2() { return getTestSpecificTD("TestData_CurrentCarrierInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_renewalMergeCurrentCarrierInformation(@Optional("AZ") String state) {
		renewalMerge(getTDCurrentCarrierInformationVersion1(), getTDCurrentCarrierInformationVersion2(), VersionsConflictConstants.currentCarrierInformation,  VersionsConflictConstants.currentCarrierInfrmationVersion1,  "GeneralTab", "CurrentCarrierInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-16047"})
	public void pas16047_ooseConflictManualPolicyInformation(@Optional("AZ") String state) {
		ooseConflict(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.policyInformationManual,  VersionsConflictConstants.policyInformationVersion2, VersionsConflictConstants.policyInformationVersion1,  "GeneralTab", "PolicyInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-16047"})
	public void pas16047_ooseConflictAutomaticPolicyInformation(@Optional("AZ") String state) {
		ooseConflict(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.policyInformationAutomatic,  VersionsConflictConstants.policyInformationVersion2, VersionsConflictConstants.policyInformationVersion1,  "GeneralTab", "PolicyInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-16047"})
	public void pas16047_renewalMergePolicyInformation(@Optional("AZ") String state) {
		renewalMerge(getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsConflictConstants.policyInformationAutomatic,  VersionsConflictConstants.policyInformationVersion1,  "GeneralTab", "PolicyInformation");
	}

	//Driver Information section

	private TestData getTDDriverInformationVersion1() {
		return getTestSpecificTD("TestData_DriverInformation_Version1");
	}

	private TestData getTDDriverInformationVersion2() { return getTestSpecificTD("TestData_DriverInformation_Version2"); }

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_ooseConflictManualDriverInformation(@Optional("AZ") String state) {
		ooseConflict(getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.driverInformation,  VersionsConflictConstants.driverInformationVersion2, VersionsConflictConstants.driverInformationVersion1,  "DriverTab", "DriverInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_ooseConflictAutomaticDriverInformation(@Optional("AZ") String state) {
		ooseConflict(getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.driverInformation,  VersionsConflictConstants.driverInformationVersion2, VersionsConflictConstants.driverInformationVersion1,  "DriverTab", "DriverInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-13513"})
	public void pas13513_renewalMergeDriverInformation(@Optional("AZ") String state) {
		renewalMerge(getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsConflictConstants.driverInformation,  VersionsConflictConstants.driverInformationVersion1,  "DriverTab", "DriverInformation");
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
		ooseConflict(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.vehicleInformationManual,  VersionsConflictConstants.vehicleInformationVersion2, VersionsConflictConstants.vehicleInformationVersion1,  "VehicleTab", "VehicleInformation", false);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-14121"})
	public void pas14121_ooseConflictAutomaticVehicleInformation(@Optional("AZ") String state) {
		ooseConflict(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.vehicleInformationAutomatic,  VersionsConflictConstants.vehicleInformationVersion2, VersionsConflictConstants.vehicleInformationVersion1,  "VehicleTab", "VehicleInformation", true);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-14121"})
	public void pas14121_renewalMergeVehicleInformation(@Optional("AZ") String state) {
		renewalMerge(getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsConflictConstants.vehicleInformationAutomatic,  VersionsConflictConstants.vehicleInformationVersion1,  "VehicleTab", "VehicleInformation");
	}
}