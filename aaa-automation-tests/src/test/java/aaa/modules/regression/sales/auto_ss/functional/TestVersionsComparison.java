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
	protected void navigateToGeneralTab() { NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get()); }

	@Override
	protected TestData getTdPolicy() {
		return testDataManager.policy.get(getPolicyType());
	}

	@Override
	protected PolicyType getPolicyType() { return PolicyType.AUTO_SS; }

	private TestData getTDNBQuote() {
		return getTestSpecificTD("TestData_NB_Quote");
	}

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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12266"})
	public void pas12266_dataGatherComparisonNamedInsuredInformation(@Optional("AZ") String state) {
		dataGatherComparison(getTDNBQuote(), getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsComparisonConstants.DATA_GATHER_NAMED_INSURED_INFORMATION, "GeneralTab", "NamedInsuredInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12266"})
	public void pas12266_endorsementsComparisonNamedInsuredInformation(@Optional("AZ") String state) {
		endorsementComparison(getTDNBPolicy(), getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_NAMED_INSURED_INFORMATION, "GeneralTab", "NamedInsuredInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12266"})
	public void pas12266_renewalComparisonNamedInsuredInformation(@Optional("AZ") String state) {
		renewalComparison(getTDNBPolicy(), getTDNamedInsuredInformationVersion1(), getTDNamedInsuredInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_NAMED_INSURED_INFORMATION, "GeneralTab", "NamedInsuredInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12883"})
	public void pas12883_dataGatherComparisonAAAProductOwned(@Optional("AZ") String state) {
		dataGatherComparison(getTDNBQuote(), getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsComparisonConstants.AAA_PRODUCT_OWNED,"GeneralTab", "AAAProductOwned");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12883"})
	public void pas12883_endorsementsComparisonAAAProductOwned(@Optional("AZ") String state) {
		endorsementComparison(getTDNBPolicy(), getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsComparisonConstants.AAA_PRODUCT_OWNED, "GeneralTab", "AAAProductOwned");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12883"})
	public void pas12883_renewalComparisonAAAProductOwned(@Optional("AZ") String state) {
		renewalComparison(getTDNBPolicy(), getTDAAAProductOwnedVersion1(), getTDAAAProductOwnedVersion2(), VersionsComparisonConstants.AAA_PRODUCT_OWNED, "GeneralTab", "AAAProductOwned");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12884"})
	public void pas12884_dataGatherComparisonContactInformation(@Optional("AZ") String state) {
		dataGatherComparison(getTDNBQuote(), getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsComparisonConstants.CONTACT_INFORMATION, "GeneralTab", "ContactInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12884"})
	public void pas12884_endorsementsComparisonContactInformation(@Optional("AZ") String state) {
		endorsementComparison(getTDNBPolicy(), getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsComparisonConstants.CONTACT_INFORMATION, "GeneralTab", "ContactInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12884"})
	public void pas12884_renewalComparisonContactInformation(@Optional("AZ") String state) {
		renewalComparison(getTDNBPolicy(), getTDContactInformationVersion1(), getTDContactInformationVersion2(), VersionsComparisonConstants.CONTACT_INFORMATION, "GeneralTab", "ContactInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12885"})
	public void pas12885_dataGatherComparisonCurrentCarrierInformation(@Optional("AZ") String state) {
		dataGatherComparison(getTDNBQuote(), getTDCurrentCarrierInformationVersion1(), getTDCurrentCarrierInformationVersion2(), VersionsComparisonConstants.CURRENT_CARRIER_INFORMATION, "GeneralTab", "CurrentCarrierInformation");
	}

	//test for Endorsement comparison not needed, section can't be changed during Endorsement

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12885"})
	public void pas12885_renewalComparisonCurrentCarrierInformation(@Optional("AZ") String state) {
		renewalComparison(getTDNBPolicy(), getTDCurrentCarrierInformationVersion1(), getTDCurrentCarrierInformationVersion2(), VersionsComparisonConstants.CURRENT_CARRIER_INFORMATION, "GeneralTab", "CurrentCarrierInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12886"})
	public void pas12886_dataGatherComparisonPolicyInformation(@Optional("AZ") String state) {
		dataGatherComparison(getTDNBQuote(), getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsComparisonConstants.POLICY_INFORMATION, "GeneralTab", "PolicyInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12886"})
	public void pas12886_endorsementsComparisonPolicyInformation(@Optional("AZ") String state) {
		endorsementComparison(getTDNBPolicy(), getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsComparisonConstants.POLICY_INFORMATION, "GeneralTab", "PolicyInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12886"})
	public void pas12886_renewalComparisonPolicyInformation(@Optional("AZ") String state) {
		renewalComparison(getTDNBPolicy(), getTDPolicyInformationVersion1(), getTDPolicyInformationVersion2(), VersionsComparisonConstants.POLICY_INFORMATION, "GeneralTab", "PolicyInformation");
	}

	//Driver Information section

	private TestData getTDDriverInformationVersion1() {
		return getTestSpecificTD("TestData_DriverInformation_Version1");
	}

	private TestData getTDDriverInformationVersion2() {
		return getTestSpecificTD("TestData_DriverInformation_Version2");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12923", "PAS-12924"})
	public void pas12923_dataGatherComparisonDriverInformation(@Optional("AZ") String state) {
		dataGatherComparison(getTDNBQuote(), getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsComparisonConstants.DATA_GATHER_DRIVER_INFORMATION, "DriverTab", "DriverInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12923", "PAS-12924"})
	public void pas12923_endorsementsComparisonDriverInformation(@Optional("AZ") String state) {
		endorsementComparison(getTDNBPolicy(), getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_DRIVER_INFORMATION, "DriverTab", "DriverInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12923", "PAS-12924"})
	public void pas12923_renewalComparisonDriverInformation(@Optional("AZ") String state) {
		renewalComparison(getTDNBPolicy(), getTDDriverInformationVersion1(), getTDDriverInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_DRIVER_INFORMATION, "DriverTab", "DriverInformation");
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12469", "PAS-14821", "PAS-12929", "PAS-12930", "PAS-12933", "PAS-12932"})
	public void pas12469_dataGatherComparisonVehicleInformation(@Optional("AZ") String state) {
		dataGatherComparison(getTDNBQuote(), getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsComparisonConstants.DATA_GATHER_VEHICLE_INFORMATION, "VehicleTab", "VehicleInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12469", "PAS-14821", "PAS-12929", "PAS-12930", "PAS-12933", "PAS-12932"})
	public void pas12469_endorsementsComparisonVehicleInformation(@Optional("AZ") String state) {
		endorsementComparison(getTDNBPolicy(), getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_VEHICLE_INFORMATION, "VehicleTab", "VehicleInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12469", "PAS-14821", "PAS-12929", "PAS-12930", "PAS-12933", "PAS-12932"})
	public void pas12469_renewalComparisonVehicleInformation(@Optional("AZ") String state) {
		renewalComparison(getTDNBPolicy(), getTDVehicleInformationVersion1(), getTDVehicleInformationVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_VEHICLE_INFORMATION, "VehicleTab", "VehicleInformation");
	}

	private TestData getTDPremiumCoveragesVersion2() {
		return getTestSpecificTD("TestData_PremiumCoverages_Version2");
	}

	private TestData getTDPremiumCoveragesQuoteVersion2() {
		return getTestSpecificTD("TestData_PremiumCoverages_QuoteVersion2");
	}

	private TestData getTDPremiumCoveragesVersion1() {
		return getTestSpecificTD("TestData_PremiumCoverages_Version1");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12469", "PAS-14821", "PAS-12929", "PAS-12930", "PAS-12933", "PAS-12932"})
	public void pas12469_dataGatherComparisonPolicyOptionsInformation(@Optional("AZ") String state) {
		dataGatherComparison(getTDNBQuote(),getTDPremiumCoveragesVersion1(), getTDPremiumCoveragesQuoteVersion2(), VersionsComparisonConstants.DATA_GATHER_PREMIUM_AND_COVERAGES, "PremiumAndCoveragesTab", "PremiumCoveragesInformation");
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-12469", "PAS-14821", "PAS-12929", "PAS-12930", "PAS-12933", "PAS-12932"})
	public void pas12469_endorsementComparisonPolicyOptionsInformation(@Optional("AZ") String state) {
		endorsementComparison(getTDNBPolicy(),getTDPremiumCoveragesVersion1(), getTDPremiumCoveragesVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_PREMIUM_AND_COVERAGES, "PremiumAndCoveragesTab", "PremiumCoveragesInformation");
	}


	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-25887"})
	public void pas25887_renewalComparisonPolicyOptionsInformation(@Optional("AZ") String state) {
		renewalComparison(getTDNBPolicy(), getTDPremiumCoveragesVersion1(), getTDPremiumCoveragesVersion2(), VersionsComparisonConstants.ENDORSEMENT_RENEWAL_PREMIUM_AND_COVERAGES, "PremiumAndCoveragesTab", "PremiumCoveragesInformation");
	}

   //Assignment Tab section

	private TestData getTDNewBusiness() {
		return getTestSpecificTD("TestData_NB_Policy_NY"); }

	private TestData getTDAssignmentVersion1() {
		return getTestSpecificTD("TestData_Assignment_Version1");
	}

	private TestData getTDAssignmentVersion2() {
		return getTestSpecificTD("TestData_Assignment_Version2");
	}

	private TestData getTDAssignmentNoChangeVersion() {
		return getTestSpecificTD("TestData_Assignment_EndorsementRenewal_NY");
	}

	@Parameters({STATE_PARAM})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-25887"})
	public void pas25887_dataGatherComparisonAssignment(@Optional("NY") String state) {
		dataGatherComparison(getTDNewBusiness(), getTDAssignmentVersion1(), getTDAssignmentVersion2(), VersionsComparisonConstants.ASSIGNMENT, "AssignmentTab", "DriverVehicleRelationshipTable");
	}

	@Parameters({STATE_PARAM})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-25887"})
	public void pas25887_endorsementsComparisonAssignment(@Optional("NY") String state) {
		endorsementComparison(getTDNewBusiness(), getTDAssignmentNoChangeVersion(), getTDAssignmentVersion2(), VersionsComparisonConstants.ASSIGNMENT, "AssignmentTab", "DriverVehicleRelationshipTable");
	}

	@Parameters({STATE_PARAM})
	@StateList(states = {Constants.States.NY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-25887"})
	public void pas25887_renewalComparisonAssignment(@Optional("NY") String state) {
		renewalComparison(getTDNewBusiness(), getTDAssignmentNoChangeVersion(), getTDAssignmentVersion2(), VersionsComparisonConstants.ASSIGNMENT, "AssignmentTab", "DriverVehicleRelationshipTable");
	}

}