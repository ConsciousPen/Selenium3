package aaa.modules.regression.sales.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.soap.aaaCSPolicyRate.CSPolicyRateWSClient;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_db.AntiTheftCode;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup.*;
import aaa.soap.aaaCSPolicyRate.com.exigeninsurance.eis.schema.*;
import aaa.soap.aaaCSPolicyRate.com.exigenservices.MessageStatusEnum;
import aaa.soap.aaaCSPolicyRate.com.exigenservices.Policy;
import aaa.soap.aaaCSPolicyRate.com.exigenservices.RatePolicyRequest;
import toolkit.utils.TestInfo;

public class TestAAACSPolicyRate extends AutoCaChoiceBaseTest {
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "")
	public void pas12465_TestAAACSPolicyRate(@Optional("") String state) throws DatatypeConfigurationException {
		CSPolicyRateWSClient csPolicyRateWSClient = new CSPolicyRateWSClient();
		RatePolicyRequest actualResponse = csPolicyRateWSClient.getCSPolicyRateServiceResponse(pas12465_CSRatePolicyRequest());
		assertThat(actualResponse.getPolicy().getMessageStatus().getMsgStatus()).isEqualTo(MessageStatusEnum.SUCCESS);

	}

	private String addressLine1 = "37352 Spruce Ter";
	private String city = "Fremont";
	private String postalCode = "94536-3778";
	private String driverOID = "N61";
	private String vehicleOID = "N100";
	private String Oid = "N15";
	private String insuredOid = "N15";
	private String firstName = "Alexander";
	private String lastName = "Petrovich";
	private String middleName = "Middle Name";

	private RatePolicyRequest pas12465_CSRatePolicyRequest() throws DatatypeConfigurationException {
		// Create request body
		AAACSAAutoPolicy aaaCSAAutoPolicy = prepareAAACsaAutoPolicy();

		PolicyComponent policyComponent = new PolicyComponent();
		policyComponent.setAAACSAAutoPolicy(aaaCSAAutoPolicy);

		Policy policy = new Policy();
		policy.setPolicy(policyComponent);

		RatePolicyRequest request = new RatePolicyRequest();
		request.setPolicy(policy);
		return request;
	}

	private AAACSAAutoPolicy prepareAAACsaAutoPolicy() throws DatatypeConfigurationException {
		XMLGregorianCalendar insuredBirthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().minusYears(26).format(dateTimeFormatter));

		AAACSAAutoPolicy aaaCSAAutoPolicy = new AAACSAAutoPolicy();
		// Create and fill aaa driver body
		aaaCSAAutoPolicy.setAAADriver(Arrays.asList(prepareDriver(firstName, lastName, middleName,insuredBirthDate)));
		// Create and fill aaa Insured body
		aaaCSAAutoPolicy.setAAAInsured(Arrays.asList(prepareAAAInsured(firstName, lastName, middleName, insuredBirthDate)));
		// Create and fill aaa coverages
		aaaCSAAutoPolicy.setAAAMPCoverage(prepareAAAMPCoverage());
		aaaCSAAutoPolicy.setAAAPDCoverage(prepareAAAPDCoverage());
		aaaCSAAutoPolicy.setAAAUIMBICoverage(prepareAAAUIMBICoverage());
		aaaCSAAutoPolicy.setAAAUMBICoverage(prepareAAAUMBICoverage());
		// Create and fill aaa vehicle
		aaaCSAAutoPolicy.setAAAVehicle(prepareAAAVehicles());
		aaaCSAAutoPolicy.setBICoverage(prepareBICoverage());

		aaaCSAAutoPolicy.setEffective(DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().format(dateTimeFormatter)));
		aaaCSAAutoPolicy.setImportedFrom(LegacySystem.MPOQ);
		aaaCSAAutoPolicy.setOfficeType(AAAOfficeType.AZ_CLUB_AGENT);

		aaaCSAAutoPolicy.setOtherOrPriorPolicy(prepareOtherOrPriorPolicy());

		aaaCSAAutoPolicy.setProducerCd("500001005");
		aaaCSAAutoPolicy.setProductCd("AAA_CSA");
		aaaCSAAutoPolicy.setRiskStateCd(StateProvCd.CA);
		aaaCSAAutoPolicy.setSubProducerCd("500005456");
		aaaCSAAutoPolicy.setAgencyCd("500001005");
		aaaCSAAutoPolicy.setAgentCd("500005456");
		aaaCSAAutoPolicy.setAgentGeneralNumber("003");
		aaaCSAAutoPolicy.setAgencyLocation("500001005");
		aaaCSAAutoPolicy.setUnderwriterCd("CSAAIB");
		return aaaCSAAutoPolicy;
	}

	private List<OtherOrPriorPolicy> prepareOtherOrPriorPolicy() {
		OtherOrPriorPolicy otherOrPriorPolicy = new OtherOrPriorPolicy();
		otherOrPriorPolicy.setInsurerCd("Yes");
		otherOrPriorPolicy.setInsurerName(firstName);
		otherOrPriorPolicy.setPolicyNumber("4290041592172402");
		otherOrPriorPolicy.setProductCd(OtherProductCode.MEMBERSHIP);
		return Arrays.asList(otherOrPriorPolicy);
	}

	private BICoverage prepareBICoverage() {
		BICoverage biCoverage = new BICoverage();
		biCoverage.setCombinedLimitAmount("100000/300000");
		biCoverage.setCoverageCd(CoverageCode.BI);
		return biCoverage;
	}

	private List<AAAVehicle> prepareAAAVehicles(){
		AAAVehicle aaaVehicle = new AAAVehicle();

		aaaVehicle.setOid(vehicleOID);
		aaaVehicle.setAAAAllRiskCoverage(prepareAAAAllRiskCoverage());
		aaaVehicle.setAAACOLLCoverage(prepareAAACOLLCoverage());
		aaaVehicle.setAAACOMPCoverage(prepareAAACOMPCoverage());
		aaaVehicle.setAAAETECoverage(prepareAAAETECoverage());
		aaaVehicle.setAAAGaragingAddress(prepareAAAGaragingAddress());
		/* Vehicle Coverages */
		aaaVehicle.setAAAVehicleCoverageBI(prepareAAAVehicleCoverageBI());
		aaaVehicle.setAAAVehicleCoverageMP(prepareAAAVehicleCoverageMP());
		aaaVehicle.setAAAVehicleCoveragePD(prepareAAAVehicleCoveragePD());
		aaaVehicle.setAAAVehicleCoverageUIMBI(prepareAAAVehicleCoverageUIMBI());
		aaaVehicle.setAAAVehicleCoverageUMBI(prepareAAAVehicleCoverageUMBI());
		aaaVehicle.setAAAGlassCoverage(prepareAAAGlassCoverage());
		aaaVehicle.setAAALoanLeaseCoverage(prepareAAALoanLeaseCoverage());
		aaaVehicle.setAAANewCarCoverage(prepareAAANewCarCoverage());
		aaaVehicle.setAAAOEMCoverage(prepareAAAOEMCoverage());
		aaaVehicle.setAAARideShareCoverage(prepareAAARideShareCoverage());

		aaaVehicle.setAAASpecialEquipmentCoverage(prepareAAASpecialEquipmentCoverage());
		aaaVehicle.setAAAVehicleRatingInfo(prepareAAAVehicleRatingInfo());

		aaaVehicle.setAirBagStatusCd("0007");
		aaaVehicle.setAntitheft(AntiTheftCode.STD);
		aaaVehicle.setHybrid(false);
		aaaVehicle.setKitCarInd(false);
		aaaVehicle.setManufacturer("TOYOTA");
		aaaVehicle.setModel("COROLLA");
		aaaVehicle.setModelYear("2013");
		aaaVehicle.setOwnershipTypeCd(AAAOwnershipType.OWN);
		//aaaVehicle.setPrincipalDriverOID(principalDriverOID);
		aaaVehicle.setSeries("COROLLA/S/LE");
		aaaVehicle.setVehBodyTypeCd("SEDAN 4 DOOR");
		// Not needed in this case
		//aaaVehicle.setVehIdentificationNo("");
		aaaVehicle.setVehTypeCd(AAAVehicleType.REGULAR);
		aaaVehicle.setVinMatchedInd(false);

		List<AAAVehicle> listOfVehicles = new ArrayList<>(Arrays.asList(aaaVehicle));

		return listOfVehicles;
	}

	private AAAVehicleRatingInfo prepareAAAVehicleRatingInfo() {
		AAAVehicleRatingInfo aaaVehicleRatingInfo = new AAAVehicleRatingInfo();
		aaaVehicleRatingInfo.setAdditionalEquipmentInd(false);
		aaaVehicleRatingInfo.setCompCollSymbol("22");
		aaaVehicleRatingInfo.setCollSymbol("15");
		aaaVehicleRatingInfo.setCompSymbol("10");
		aaaVehicleRatingInfo.setCostPurchase(BigDecimal.valueOf(220000));
		aaaVehicleRatingInfo.setDeclaredAnnualmiles(BigDecimal.valueOf(13000));
		aaaVehicleRatingInfo.setDistanceOneWayToWork(BigDecimal.valueOf(60));
		aaaVehicleRatingInfo.setNumDaysDrivenPerWeek(BigDecimal.valueOf(5));
		aaaVehicleRatingInfo.setStat("I");
		aaaVehicleRatingInfo.setVehicleUsageCd(AAAVehicleUseCd.WC);
		aaaVehicleRatingInfo.setVehSymbolCd("22");
		aaaVehicleRatingInfo.setWaivedLiabilityInd(false);

		return aaaVehicleRatingInfo;
	}

	private AAASpecialEquipmentCoverage prepareAAASpecialEquipmentCoverage() {
		AAASpecialEquipmentCoverage aaaSpecialEquipmentCoverage = new AAASpecialEquipmentCoverage();
		aaaSpecialEquipmentCoverage.setCoverageCd(CoverageCode.SPECEQUIP);
		return aaaSpecialEquipmentCoverage;

	}
	private AAARideShareCoverage prepareAAARideShareCoverage() {
		AAARideShareCoverage aaaRideShareCoverage = new AAARideShareCoverage();
		aaaRideShareCoverage.setLimitAmount("0");
		aaaRideShareCoverage.setCoverageCd(CoverageCode.RIDESHARE);
		return aaaRideShareCoverage;

	}

	private AAAOEMCoverage prepareAAAOEMCoverage() {
		AAAOEMCoverage aaaOEMCoverage = new AAAOEMCoverage();
		aaaOEMCoverage.setLimitAmount("0");
		aaaOEMCoverage.setCoverageCd(CoverageCode.OEM);
		return aaaOEMCoverage;

	}

	private AAANewCarCoverage prepareAAANewCarCoverage() {
		AAANewCarCoverage aaaNewCarCoverage = new AAANewCarCoverage();
		aaaNewCarCoverage.setLimitAmount("0");
		aaaNewCarCoverage.setCoverageCd(CoverageCode.NEWCAR);
		return aaaNewCarCoverage;

	}

	private AAALoanLeaseCoverage prepareAAALoanLeaseCoverage() {
		AAALoanLeaseCoverage aaaLoanLeaseCoverage = new AAALoanLeaseCoverage();
		aaaLoanLeaseCoverage.setLimitAmount("0");
		aaaLoanLeaseCoverage.setCoverageCd(CoverageCode.LOAN);
		return aaaLoanLeaseCoverage;

	}

	private AAAGlassCoverage prepareAAAGlassCoverage() {
		AAAGlassCoverage aaaGlassCoverage = new AAAGlassCoverage();
		aaaGlassCoverage.setLimitAmount("0");
		aaaGlassCoverage.setCoverageCd(CoverageCode.GLASS);
		return aaaGlassCoverage;

	}

	private AAAVehicleCoverageUMBI prepareAAAVehicleCoverageUMBI() {
		AAAVehicleCoverageUMBI aaaVehicleCoverageUMBI = new AAAVehicleCoverageUMBI();
		aaaVehicleCoverageUMBI.setCombinedLimitAmount("100000/300000");
		aaaVehicleCoverageUMBI.setCoverageCd(CoverageCode.UMBI);

		return aaaVehicleCoverageUMBI;
	}

	private AAAVehicleCoverageUIMBI prepareAAAVehicleCoverageUIMBI() {
		AAAVehicleCoverageUIMBI aaaVehicleCoverageUIMBI = new AAAVehicleCoverageUIMBI();
		aaaVehicleCoverageUIMBI.setCombinedLimitAmount("100000/300000");
		aaaVehicleCoverageUIMBI.setCoverageCd(CoverageCode.UIMBI);

		return aaaVehicleCoverageUIMBI;
	}

	private AAAVehicleCoveragePD prepareAAAVehicleCoveragePD() {
		AAAVehicleCoveragePD aaaVehicleCoveragePD = new AAAVehicleCoveragePD();
		aaaVehicleCoveragePD.setCoverageCd(CoverageCode.PD);
		aaaVehicleCoveragePD.setLimitAmount("100000");
		return aaaVehicleCoveragePD;
	}

	/* Start Vehicle */
	private AAAVehicleCoverageMP prepareAAAVehicleCoverageMP(){
		AAAVehicleCoverageMP aaaVehicleCoverageMP = new AAAVehicleCoverageMP();
		aaaVehicleCoverageMP.setCoverageCd(CoverageCode.MEDPM);
		aaaVehicleCoverageMP.setLimitAmount("5000");
		return aaaVehicleCoverageMP;
	}

	private AAAVehicleCoverageBI prepareAAAVehicleCoverageBI(){
		AAAVehicleCoverageBI aaaVehicleCoverageBI = new AAAVehicleCoverageBI();
		aaaVehicleCoverageBI.setCombinedLimitAmount("100000/300000");
		aaaVehicleCoverageBI.setCoverageCd(CoverageCode.BI);
		return aaaVehicleCoverageBI;
	}

	private AAAGaragingAddress prepareAAAGaragingAddress(){
		AAAGaragingAddress aaaGaragingAddress = new AAAGaragingAddress();
		aaaGaragingAddress.setAddressLine1(addressLine1);
		aaaGaragingAddress.setCity(city);
		aaaGaragingAddress.setCountryCd(Country.US.toString());
		aaaGaragingAddress.setPostalCode(postalCode);
		aaaGaragingAddress.setResidentialAddressInd(false);
		aaaGaragingAddress.setStandartized(true);
		aaaGaragingAddress.setStateProvCd(StateProvCd.CA);

		return aaaGaragingAddress;
	}

	private AAAETECoverage prepareAAAETECoverage(){
		AAAETECoverage aaaETECoverage = new AAAETECoverage();
		aaaETECoverage.setCombinedLimitAmount("25/750");
		aaaETECoverage.setCoverageCd(CoverageCode.ETEC);
		return aaaETECoverage;
	}

	private AAACOMPCoverage prepareAAACOMPCoverage(){
		AAACOMPCoverage aaaCOMPCoverage = new AAACOMPCoverage();
		aaaCOMPCoverage.setCoverageCd(CoverageCode.COMPDED);
		aaaCOMPCoverage.setDeductibleAmount("500");
		return aaaCOMPCoverage;
	}

	private AAACOLLCoverage prepareAAACOLLCoverage(){
		AAACOLLCoverage aaaCOLLCoverage = new AAACOLLCoverage();
		aaaCOLLCoverage.setCoverageCd(CoverageCode.COLLDED);
		aaaCOLLCoverage.setDeductibleAmount("500");
		return aaaCOLLCoverage;
	}

	private AAAAllRiskCoverage prepareAAAAllRiskCoverage(){
		AAAAllRiskCoverage aaaAllRiskCoverage = new AAAAllRiskCoverage();
		aaaAllRiskCoverage.setCoverageCd(CoverageCode.ALLRISK);
		aaaAllRiskCoverage.setDeductibleAmount("0");
		return aaaAllRiskCoverage;
	}
	/* End Vehicle */

	private AAAUMBICoverage prepareAAAUMBICoverage(){
		AAAUMBICoverage aaaUMBICoverage = new AAAUMBICoverage();
		aaaUMBICoverage.setCombinedLimitAmount("100000/300000");
		aaaUMBICoverage.setCoverageCd(CoverageCode.UMBI);
		return aaaUMBICoverage;
	}

	private AAAUIMBICoverage prepareAAAUIMBICoverage(){
		AAAUIMBICoverage aaaUIMBICoverage = new AAAUIMBICoverage();
		aaaUIMBICoverage.setCombinedLimitAmount("100000/300000");
		aaaUIMBICoverage.setCoverageCd(CoverageCode.UIMBI);
		return aaaUIMBICoverage;
	}

	private AAAPDCoverage prepareAAAPDCoverage(){
		AAAPDCoverage aaaPDCoverage = new AAAPDCoverage();
		aaaPDCoverage.setCoverageCd(CoverageCode.PD);
		aaaPDCoverage.setLimitAmount("100000");
		return aaaPDCoverage;
	}

	private AAAMPCoverage prepareAAAMPCoverage(){
		AAAMPCoverage aaampCoverage = new AAAMPCoverage();
		aaampCoverage.setCoverageCd(CoverageCode.MEDPM);
		aaampCoverage.setLimitAmount("5000");
		return aaampCoverage;
	}

	private AAAInsured prepareAAAInsured(String firstName,String lastName,String middleName,XMLGregorianCalendar insuredBirthDate) throws DatatypeConfigurationException {

		AAAInsuredMailingAddress aaaInsuredMailingAddress = new AAAInsuredMailingAddress();
		aaaInsuredMailingAddress.setAddressLine1(addressLine1);
		aaaInsuredMailingAddress.setCity(city);
		aaaInsuredMailingAddress.setCountryCd(Country.US);
		aaaInsuredMailingAddress.setIsAddressValidated(true);
		aaaInsuredMailingAddress.setPostalCode(postalCode);
		aaaInsuredMailingAddress.setStateProvCd(StateProvCd.CA);
		aaaInsuredMailingAddress.setUsageType(AddressType.MAILING);

		AAAInsuredPrimaryAddress aaaInsuredPrimaryAddress = new AAAInsuredPrimaryAddress();
		aaaInsuredPrimaryAddress.setAddressLine1(addressLine1);
		aaaInsuredPrimaryAddress.setCity(city);
		aaaInsuredPrimaryAddress.setCountryCd(Country.US);
		aaaInsuredPrimaryAddress.setIsAddressValidated(true);
		aaaInsuredPrimaryAddress.setPostalCode(postalCode);
		aaaInsuredPrimaryAddress.setStateProvCd(StateProvCd.CA);
		aaaInsuredPrimaryAddress.setUsageType(AddressType.LIVING);

		AAAInsured aaaInsured = new AAAInsured();
		aaaInsured.setCommunicationInfoEmail("aedutra1337@gmail.com");
		aaaInsured.setCommunicationInfoPhoneNumber1("(760) 525-7914");
		aaaInsured.setDateOfBirth(insuredBirthDate);
		aaaInsured.setFirstName(firstName);
		aaaInsured.setInsuredBaseDt(DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().minusYears(2).format(dateTimeFormatter)));
		aaaInsured.setLastName(lastName);
		aaaInsured.setMiddleName(middleName);
		aaaInsured.setPrincipalRoleCd("policyHolder");
		aaaInsured.setOid0(insuredOid);

		aaaInsured.setAAAInsuredMailingAddress(aaaInsuredMailingAddress);
		aaaInsured.setAAAInsuredPrimaryAddress(aaaInsuredPrimaryAddress);
		return aaaInsured;
	}

	private AAADriver prepareDriver(String firstName, String lastName, String middleName, XMLGregorianCalendar date) throws DatatypeConfigurationException {
		AAADriver aaaDriver = new AAADriver();
		aaaDriver.setOid(Oid);

		aaaDriver.setAAADriverRatingInfo(prepareAAADriverRatingInfo());
		aaaDriver.setAAADrivingLicense(prepareAAADrivingLicense());
		aaaDriver.setAAADrivingRecord(prepareAAADrivingRecords());

		aaaDriver.setAdbCoverageInd(false);
		aaaDriver.setAge(BigDecimal.valueOf(26));
		aaaDriver.setBirthDate(date);
		aaaDriver.setDriverRelToApplicantCd(AAARelationshipFNInsured.IN);
		aaaDriver.setDriverTypeCd(AAADriverType.AFR);
		aaaDriver.setEmployeeBenefitType(AAAEmployeeBenefitCd.NONE);
		aaaDriver.setFinancialResponsibilityInd(false);
		aaaDriver.setFirstLicenseAge(BigDecimal.valueOf(16));

		aaaDriver.setFirstName(firstName);
		aaaDriver.setGenderCd(GenderCd.MALE);
		aaaDriver.setInsuredOid(insuredOid);
		aaaDriver.setLastName(lastName);
		aaaDriver.setMaritalStatusCd(AAAMaritalStatusCd.S);
		aaaDriver.setMiddleName(middleName);
		aaaDriver.setPermitInd(true);
		aaaDriver.setRidesharingCovDriver(false);
		aaaDriver.setRidesharingIncome(false);
		return aaaDriver;
	}

	private List<AAADrivingRecord> prepareAAADrivingRecords() throws DatatypeConfigurationException {

		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().minusYears(2).format(dateTimeFormatter));

		AAADrivingRecord aaaDrivingRecord = new AAADrivingRecord();
		aaaDrivingRecord.setAccidentViolationDt(xmlCal);
		aaaDrivingRecord.setActivitySource(AAAActivitySource.CUST);
		aaaDrivingRecord.setActivityType("minv");
		aaaDrivingRecord.setConvictionDt(xmlCal);
		aaaDrivingRecord.setIncidentDescription("Disregard traffic device or sign");
		aaaDrivingRecord.setIncludeToRatingInd(true);

		List<AAADrivingRecord> listOfRecords = new ArrayList<>(Arrays.asList(aaaDrivingRecord, aaaDrivingRecord, aaaDrivingRecord));

		return listOfRecords;
	}

	// Prepare prepareAAADriverRatingInfo
	private AAADriverRatingInfo prepareAAADriverRatingInfo() {
		AAADriverRatingInfo aaaDriverRatingInfo = new AAADriverRatingInfo();
		aaaDriverRatingInfo.setMatureDriverInd(false);
		aaaDriverRatingInfo.setNewDriverInd(false);
		aaaDriverRatingInfo.setRecentBPA(MostRecentGPA.NONE);
		return aaaDriverRatingInfo;
	}

	// Prepare prepareAAADrivingLicense
	private AAADrivingLicense prepareAAADrivingLicense() throws DatatypeConfigurationException {
		AAADrivingLicense aaaDrivingLicense = new AAADrivingLicense();
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().minusYears(1).format(dateTimeFormatter));

		aaaDrivingLicense.setFirstLicenseDt(xmlCal);
		aaaDrivingLicense.setStateProvCd(StateProvCd.CA);
		aaaDrivingLicense.setUsLicenseYrsInd(true);

		return aaaDrivingLicense;
	}
}
