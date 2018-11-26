package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.soap.aaaSSPolicyRate.SSPolicyRateWSClient;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.aaa_ss_lookup.*;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.AAAImportedFrom;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.CoverageCode;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.data.eis.OtherProductCode;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigeninsurance.schema.*;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices.MessageStatusEnum;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices.Policy;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices.RatePolicyRequest;
import toolkit.utils.TestInfo;

public class TestAAASSPolicyRate extends AutoCaChoiceBaseTest {
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "")
	public void pas12465_TestAAASSPolicyRate(@Optional("") String state) throws DatatypeConfigurationException {
		SSPolicyRateWSClient ssPolicyRateWSClient = new SSPolicyRateWSClient();
		RatePolicyRequest actualResponse = ssPolicyRateWSClient.getSSPolicyRateServiceResponse(pas12465_SSRatePolicyRequest());
		assertThat(actualResponse.getPolicy().getMessageStatus().getMsgStatus()).isEqualTo(MessageStatusEnum.SUCCESS);
	}

	private String addressLine1 = "123 St";
	private String city = "PHOENIX";
	private String postalCode = "85021";
	private String driverOidAttribute = "N36";
	private String vehicleOid = "N63";
	private String Oid = "N15";
	private String insuredOid = "N13";
	private String firstName = "Igor";
	private String lastName = "Akrapovic";
	private String middleName = "EvolutionMN";

	private RatePolicyRequest pas12465_SSRatePolicyRequest() throws DatatypeConfigurationException {
		// Create request body
		AAASSAutoPolicy aaaSSAutoPolicy = prepareSSAutoPolicy();

		PolicyComponent policyComponent = new PolicyComponent();
		policyComponent.setAAACSAAutoPolicy(aaaSSAutoPolicy);

		Policy policy = new Policy();
		policy.setPolicy(policyComponent);

		RatePolicyRequest request = new RatePolicyRequest();
		request.setPolicy(policy);
		return request;
	}

	private AAASSAutoPolicy prepareSSAutoPolicy() throws DatatypeConfigurationException {
		XMLGregorianCalendar insuredBirthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().minusYears(63).format(dateTimeFormatter));
		XMLGregorianCalendar expirationDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(5).format(dateTimeFormatter));
		XMLGregorianCalendar effectiveDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(5).minusYears(1).format(dateTimeFormatter));

		AAASSAutoPolicy aaaSSAutoPolicy = new AAASSAutoPolicy();
		// Create and fill aaa driver body
		aaaSSAutoPolicy.setAAADriver(Collections.singletonList(prepareDriver(insuredBirthDate)));
		// Create and fill aaa Insured body
		aaaSSAutoPolicy.setAAAInsured(Collections.singletonList(prepareAAAInsured(insuredBirthDate)));
		aaaSSAutoPolicy.setAAAPaymentOption(prepareAAAPaymentOption());
		// Coverages
		aaaSSAutoPolicy.setAAAMPCoverage(prepareAAAMPCoverage());
		aaaSSAutoPolicy.setAAAPDCoverage(prepareAAAPDCoverage());
		aaaSSAutoPolicy.setAAAUIMBICoverage(prepareAAAUIMBICoverage());
		aaaSSAutoPolicy.setAAAUMBICoverage(prepareAAAUMBICoverage());

		aaaSSAutoPolicy.setAAAVehicle(prepareAAAVehicles());

		aaaSSAutoPolicy.setClubCd("002");
		aaaSSAutoPolicy.setBICoverage(prepareBICoverage());

		aaaSSAutoPolicy.setEffective(effectiveDate);
		aaaSSAutoPolicy.setContractTermTypeCd("AN");
		aaaSSAutoPolicy.setExpiration(expirationDate);
		aaaSSAutoPolicy.setImportedFrom(AAAImportedFrom.WMSB_2_C);
		aaaSSAutoPolicy.setOfficeType(AAAOfficeType.AZ_CLUB_AGENT);
		aaaSSAutoPolicy.setOtherOrPriorPolicy(prepareOtherOrPriorPolicy());
		aaaSSAutoPolicy.setAAAOtherOrPriorPolicy(prepareAAAOtherOrPriorPolicy());

		aaaSSAutoPolicy.setContractDisplay("12");
		aaaSSAutoPolicy.setProductCd("AAA_SS");
		aaaSSAutoPolicy.setRiskStateCd(StateProvCd.AZ);
		aaaSSAutoPolicy.setSubProducerCd("Online");
		aaaSSAutoPolicy.setLeadSourceCd("19");
		aaaSSAutoPolicy.setOrderCreditScore(true);
		aaaSSAutoPolicy.setTollNumberCd("8669282602");

		aaaSSAutoPolicy.setAAAPrefill(prepareAAAPrefill(insuredBirthDate));

		return aaaSSAutoPolicy;
	}

	private AAAPrefill prepareAAAPrefill(XMLGregorianCalendar insuredBirthDate) {
		AAAPrefill aaaPrefill = new AAAPrefill();
		aaaPrefill.setFirstName(firstName);
		aaaPrefill.setLastName(lastName);
		aaaPrefill.setBirthDate(insuredBirthDate);
		aaaPrefill.setRiskStateCd(StateProvCd.AZ);

		aaaPrefill.setAAAPrefillAddress(prepareAAAPrefillAddress());
		return aaaPrefill;
	}

	private AAAPrefillAddress prepareAAAPrefillAddress() {
		AAAPrefillAddress aaaPrefillAddress = new AAAPrefillAddress();
		aaaPrefillAddress.setCity(city);
		aaaPrefillAddress.setPostalCode(postalCode);
		aaaPrefillAddress.setAddressLine(addressLine1);
		aaaPrefillAddress.setStateProvCd(StateProvCd.AZ);
		return aaaPrefillAddress;
	}

	private AAAOtherOrPriorPolicy prepareAAAOtherOrPriorPolicy() {
		AAAOtherOrPriorPolicy aaaOtherOrPriorPolicy = new AAAOtherOrPriorPolicy();
		aaaOtherOrPriorPolicy.setContinuousMonthsInsured(false);
		aaaOtherOrPriorPolicy.setInsurerCd("0");
		return aaaOtherOrPriorPolicy;
	}


	private List<OtherOrPriorPolicy> prepareOtherOrPriorPolicy() {
		OtherOrPriorPolicy otherOrPriorPolicy = new OtherOrPriorPolicy();
		otherOrPriorPolicy.setInsurerCd("Pending");
		otherOrPriorPolicy.setInsurerName(firstName);
		otherOrPriorPolicy.setProductCd(OtherProductCode.MEMBERSHIP);
		return Arrays.asList(otherOrPriorPolicy);
	}

	private BICoverage prepareBICoverage() {
		BICoverage biCoverage = new BICoverage();
		biCoverage.setCombinedLimitAmount("15000/30000");
		biCoverage.setCoverageCd(CoverageCode.BI);
		return biCoverage;
	}

	private List<AAAVehicle> prepareAAAVehicles() throws DatatypeConfigurationException {
		AAAVehicle aaaVehicle = new AAAVehicle();

		aaaVehicle.setOid(vehicleOid);
		aaaVehicle.setAAACOLLCoverage(prepareAAACOLLCoverage());
		aaaVehicle.setAAACOMPCoverage(prepareAAACOMPCoverage());
		aaaVehicle.setAAAGaragingAddress(prepareAAAGaragingAddress());
		aaaVehicle.setAAALoanLeaseCoverage(prepareAAALoanLeaseCoverage());
		aaaVehicle.setAAARentalReimbursementCoverage(prepareAAARentalReimbursementCoverage());
		aaaVehicle.setAAASpecialEquipmentCoverage(prepareAAASpecialEquipmentCoverage());
		aaaVehicle.setAAATowingAndLaborCoverage(prepareAAATowingAndLaborCoverage());

		aaaVehicle.setAAAVehicleCoverageBI(prepareAAAVehicleCoverageBI());
		aaaVehicle.setAAAVehicleCoverageMP(prepareAAAVehicleCoverageMP());
		aaaVehicle.setAAAVehicleCoveragePD(prepareAAAVehicleCoveragePD());
		aaaVehicle.setAAAVehicleCoverageUIMBI(prepareAAAVehicleCoverageUIMBI());
		aaaVehicle.setAAAVehicleCoverageUMBI(prepareAAAVehicleCoverageUMBI());

		aaaVehicle.setAAAVehicleRatingInfo(prepareAAAVehicleRatingInfo());
		aaaVehicle.setAirBagStatusCd("000R");
		aaaVehicle.setAntitheft("NONE");
		aaaVehicle.setHybrid(false);
		aaaVehicle.setManufacturer("ACURA");
		aaaVehicle.setModel("ILX");
		aaaVehicle.setModelYear(2016);
		aaaVehicle.setOwnershipTypeCd(AAAOwnershipType.OWN);
		aaaVehicle.setSeries("ILX PREMIUM STYLE");
		aaaVehicle.setVehBodyTypeCd("SEDAN");
		aaaVehicle.setVehTypeCd(AAAVehicleType.PPA);
		aaaVehicle.setVinMatchedInd(false);

		List<AAAVehicle> listOfVehicles = new ArrayList<>(Arrays.asList(aaaVehicle));

		return listOfVehicles;
	}

	private AAAVehicleRatingInfo prepareAAAVehicleRatingInfo() throws DatatypeConfigurationException {
		AAAVehicleRatingInfo aaaVehicleRatingInfo = new AAAVehicleRatingInfo();
		aaaVehicleRatingInfo.setCompCollSymbol(28);
		aaaVehicleRatingInfo.setDistanceOneWayToWork(BigDecimal.valueOf(10));
		aaaVehicleRatingInfo.setStat("AI");
		aaaVehicleRatingInfo.setVehicleUsageCd(AAAVehicleUseCd.WORK_COMMUTE);
		aaaVehicleRatingInfo.setVehSymbolCd("0");
		aaaVehicleRatingInfo.setFullGlassCoverageInd(true);
		aaaVehicleRatingInfo.setPurchaseDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().minusYears(2).format(dateTimeFormatter)));

		return aaaVehicleRatingInfo;
	}

	private AAAVehicleCoverageUMBI prepareAAAVehicleCoverageUMBI() {
		AAAVehicleCoverageUMBI aaaVehicleCoverageUMBI = new AAAVehicleCoverageUMBI();
		aaaVehicleCoverageUMBI.setCombinedLimitAmount("15000/30000");
		aaaVehicleCoverageUMBI.setCoverageCd(CoverageCode.UMBI);

		return aaaVehicleCoverageUMBI;
	}

	private AAAVehicleCoverageUIMBI prepareAAAVehicleCoverageUIMBI() {
		AAAVehicleCoverageUIMBI aaaVehicleCoverageUIMBI = new AAAVehicleCoverageUIMBI();
		aaaVehicleCoverageUIMBI.setCombinedLimitAmount("15000/30000");
		aaaVehicleCoverageUIMBI.setCoverageCd(CoverageCode.UIMBI);

		return aaaVehicleCoverageUIMBI;
	}

	private AAAVehicleCoveragePD prepareAAAVehicleCoveragePD() {
		AAAVehicleCoveragePD aaaVehicleCoveragePD = new AAAVehicleCoveragePD();
		aaaVehicleCoveragePD.setCoverageCd(CoverageCode.PD);
		aaaVehicleCoveragePD.setLimitAmount(BigDecimal.valueOf(15000));
		return aaaVehicleCoveragePD;
	}

	private AAAVehicleCoverageMP prepareAAAVehicleCoverageMP(){
		AAAVehicleCoverageMP aaaVehicleCoverageMP = new AAAVehicleCoverageMP();
		aaaVehicleCoverageMP.setCoverageCd(CoverageCode.MEDPM);
		aaaVehicleCoverageMP.setLimitAmount(BigDecimal.valueOf(0));
		return aaaVehicleCoverageMP;
	}

	private AAAVehicleCoverageBI prepareAAAVehicleCoverageBI(){
		AAAVehicleCoverageBI aaaVehicleCoverageBI = new AAAVehicleCoverageBI();
		aaaVehicleCoverageBI.setCombinedLimitAmount("15000/30000");
		aaaVehicleCoverageBI.setCoverageCd(CoverageCode.BI);
		return aaaVehicleCoverageBI;
	}

	private AAATowingAndLaborCoverage prepareAAATowingAndLaborCoverage() {
		AAATowingAndLaborCoverage aaaTowingAndLaborCoverage = new AAATowingAndLaborCoverage();
		aaaTowingAndLaborCoverage.setCoverageCd(CoverageCode.TOWINGLABOR);
		aaaTowingAndLaborCoverage.setCombinedLimitAmount("0/0");
		return aaaTowingAndLaborCoverage;
	}

	private AAASpecialEquipmentCoverage prepareAAASpecialEquipmentCoverage() {
		AAASpecialEquipmentCoverage aaaSpecialEquipmentCoverage = new AAASpecialEquipmentCoverage();
		aaaSpecialEquipmentCoverage.setLimitAmount(1000);
		aaaSpecialEquipmentCoverage.setCoverageCd(CoverageCode.SPECEQUIP);
		return aaaSpecialEquipmentCoverage;

	}

	private AAARentalReimbursementCoverage prepareAAARentalReimbursementCoverage() {
		AAARentalReimbursementCoverage aaaRentalReimbursementCoverage = new AAARentalReimbursementCoverage();
		aaaRentalReimbursementCoverage.setCombinedLimitAmount("30/900");
		aaaRentalReimbursementCoverage.setCoverageCd(CoverageCode.RREIM);
		return aaaRentalReimbursementCoverage;

	}

	private AAALoanLeaseCoverage prepareAAALoanLeaseCoverage() {
		AAALoanLeaseCoverage aaaLoanLeaseCoverage = new AAALoanLeaseCoverage();
		aaaLoanLeaseCoverage.setLimitAmount("0");
		aaaLoanLeaseCoverage.setCoverageCd(CoverageCode.LOAN);
		return aaaLoanLeaseCoverage;

	}

	private AAAGaragingAddress prepareAAAGaragingAddress() {
		AAAGaragingAddress aaaGaragingAddress = new AAAGaragingAddress();
		aaaGaragingAddress.setAddressLine1(addressLine1);
		aaaGaragingAddress.setCity(city);
		aaaGaragingAddress.setCountryCd(Country.US.toString());
		aaaGaragingAddress.setPostalCode(postalCode);
		aaaGaragingAddress.setResidentialAddressInd(false);
		aaaGaragingAddress.setStandartized(false);
		aaaGaragingAddress.setStateProvCd(StateProvCd.AZ);

		return aaaGaragingAddress;
	}

	private AAACOMPCoverage prepareAAACOMPCoverage() {
		AAACOMPCoverage aaaCOMPCoverage = new AAACOMPCoverage();
		aaaCOMPCoverage.setCoverageCd(CoverageCode.COMPDED);
		aaaCOMPCoverage.setDeductibleAmount("500");
		return aaaCOMPCoverage;
	}

	private AAACOLLCoverage prepareAAACOLLCoverage() {
		AAACOLLCoverage aaaCOLLCoverage = new AAACOLLCoverage();
		aaaCOLLCoverage.setCoverageCd(CoverageCode.COLLDED);
		aaaCOLLCoverage.setDeductibleAmount(BigDecimal.valueOf(500));
		return aaaCOLLCoverage;
	}

	private AAAUMBICoverage prepareAAAUMBICoverage() {
		AAAUMBICoverage aaaUMBICoverage = new AAAUMBICoverage();
		aaaUMBICoverage.setCombinedLimitAmount("15000/30000");
		aaaUMBICoverage.setCoverageCd(CoverageCode.UMBI);
		return aaaUMBICoverage;
	}

	private AAAUIMBICoverage prepareAAAUIMBICoverage() {
		AAAUIMBICoverage aaaUIMBICoverage = new AAAUIMBICoverage();
		aaaUIMBICoverage.setCombinedLimitAmount("15000/30000");
		aaaUIMBICoverage.setCoverageCd(CoverageCode.UIMBI);
		return aaaUIMBICoverage;
	}

	private AAAPDCoverage prepareAAAPDCoverage() {
		AAAPDCoverage aaaPDCoverage = new AAAPDCoverage();
		aaaPDCoverage.setCoverageCd(CoverageCode.PD);
		aaaPDCoverage.setLimitAmount(BigDecimal.valueOf(15000));
		return aaaPDCoverage;
	}

	private AAAMPCoverage prepareAAAMPCoverage() {
		AAAMPCoverage aaampCoverage = new AAAMPCoverage();
		aaampCoverage.setCoverageCd(CoverageCode.MEDPM);
		aaampCoverage.setLimitAmount(BigDecimal.valueOf(0));
		return aaampCoverage;
	}

	private AAAPaymentOption prepareAAAPaymentOption() {
		AAAPaymentOption aaaPaymentOption = new AAAPaymentOption();
		aaaPaymentOption.setPaymentPlanCd(BillingPaymentPlans.STANDART_SS);
		return aaaPaymentOption;
	}

	private AAAInsured prepareAAAInsured(XMLGregorianCalendar insuredBirthDate) throws DatatypeConfigurationException {
		AAAInsuredMailingAddress aaaInsuredMailingAddress = new AAAInsuredMailingAddress();
		aaaInsuredMailingAddress.setAddressLine1(addressLine1);
		aaaInsuredMailingAddress.setCity(city);
		aaaInsuredMailingAddress.setCountryCd(Country.US);
		aaaInsuredMailingAddress.setPostalCode(postalCode);
		aaaInsuredMailingAddress.setStateProvCd(StateProvCd.AZ);
		aaaInsuredMailingAddress.setUsageType(AddressType.MAILING);

		AAAInsuredPrimaryAddress aaaInsuredPrimaryAddress = new AAAInsuredPrimaryAddress();
		aaaInsuredPrimaryAddress.setAddressLine1(addressLine1);
		aaaInsuredPrimaryAddress.setCity(city);
		aaaInsuredPrimaryAddress.setCountryCd(Country.US);
		aaaInsuredPrimaryAddress.setPostalCode(postalCode);
		aaaInsuredPrimaryAddress.setStateProvCd(StateProvCd.AZ);
		aaaInsuredPrimaryAddress.setUsageType(AddressType.LIVING);

		AAAInsured aaaInsured = new AAAInsured();
		aaaInsured.setCommunicationInfoEmail("akrapovich@gmail.com");
		aaaInsured.setCommunicationInfoPhoneNumber1("(555) 555-5555");
		aaaInsured.setDateOfBirth(insuredBirthDate);
		aaaInsured.setFirstName(firstName);
		aaaInsured.setInsuredBaseDt(DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().minusYears(2).format(dateTimeFormatter)));
		aaaInsured.setLastName(lastName);
		aaaInsured.setMiddleName(middleName);
		aaaInsured.setPrincipalRoleCd(InsuredOrPrincipalRole.POLICY_HOLDER);
		aaaInsured.setResidentTypeCd(AAAResidentType.OWNHME);
		aaaInsured.setOid(insuredOid);

		aaaInsured.setAAAInsuredMailingAddress(aaaInsuredMailingAddress);
		aaaInsured.setAAAInsuredPrimaryAddress(aaaInsuredPrimaryAddress);
		return aaaInsured;
	}

	private AAADriver prepareDriver(XMLGregorianCalendar date) {
		AAADriver aaaDriver = new AAADriver();

		aaaDriver.setOid(driverOidAttribute);
		aaaDriver.setAAADriverRatingInfo(prepareAAADriverRatingInfo());
		aaaDriver.setAAADrivingLicense(prepareAAADrivingLicense());

		aaaDriver.setAdbCoverageInd(false);
		aaaDriver.setAge(BigDecimal.valueOf(62));

		aaaDriver.setBirthDate(date);

		aaaDriver.setDriverRelToApplicantCd(AAARelationshipFNInsured.IN);
		aaaDriver.setDriverTypeCd(AAADriverType.AFR);
		aaaDriver.setEmployeeBenefitType(AAAEmployeeBenefitCd.NONE);
		aaaDriver.setFinancialResponsibilityInd(false);
		aaaDriver.setFirstLicenseAge(BigDecimal.valueOf(21));
		aaaDriver.setFirstName(firstName);
		aaaDriver.setGenderCd(GenderCd.MALE);
		aaaDriver.setInsuredOid(insuredOid);
		aaaDriver.setLastName(lastName);
		aaaDriver.setMaritalStatusCd(AAAMaritalStatusCd.S);
		aaaDriver.setOccupationTypeCd(AAAOccupation.EMPLOYED);
		aaaDriver.setSpecificDisabilityInd(false);
		return aaaDriver;
	}

	// Prepare prepareAAADriverRatingInfo
	private AAADriverRatingInfo prepareAAADriverRatingInfo() {
		AAADriverRatingInfo aaaDriverRatingInfo = new AAADriverRatingInfo();
		aaaDriverRatingInfo.setNewDriverInd(false);
		aaaDriverRatingInfo.setSmartDrCrsInd(false);
		return aaaDriverRatingInfo;
	}

	// Prepare prepareAAADrivingLicense
	private AAADrivingLicense prepareAAADrivingLicense() {
		AAADrivingLicense aaaDrivingLicense = new AAADrivingLicense();

		aaaDrivingLicense.setStateProvCd(StateProvCd.AZ);
		aaaDrivingLicense.setLicenseStatusCd(LicenseClass.LICUS);

		return aaaDrivingLicense;
	}
}
