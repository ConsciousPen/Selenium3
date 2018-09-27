package aaa.helpers.openl.model.auto_ss;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.mock.model.address.AddressReferenceMock;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;
import aaa.helpers.openl.mock_generator.MockGenerator;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_generator.AutoSSTestDataGenerator;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import toolkit.datax.TestData;

@ExcelTableElement(containsSheetName = OpenLFile.POLICY_SHEET_NAME, headerRowIndex = OpenLFile.POLICY_HEADER_ROW_NUMBER)
public class AutoSSOpenLPolicy extends OpenLPolicy {

	private AutoSSOpenLCappingDetails cappingDetails;
	private List<AutoSSOpenLVehicle> vehicles;
	private List<AutoSSOpenLDriver> drivers;

	private LocalDate effectiveDate;
	private Integer term;
	private Boolean isHomeOwner;
	private Integer creditScore;
	private Boolean isAAAMember;
	private String aaaHomePolicy;
	private String aaaRentersPolicy;
	private String aaaCondoPolicy;
	private Boolean aaaLifePolicy;
	private Boolean aaaMotorcyclePolicy;
	private Boolean isEMember;
	private Integer memberPersistency;
	private Integer autoInsurancePersistency;
	private Integer aaaInsurancePersistency;
	private Integer aaaAsdInsurancePersistency;
	private Boolean isAARP; // NV specific
	private Boolean isEmployee;
	private Boolean isAdvanceShopping;
	private String paymentPlanType;
	private String distributionChannel;
	private Boolean unacceptableRisk;
	private String priorBILimit;
	private Integer reinstatements;
	private Integer yearsAtFaultAccidentFree;
	private Integer yearsIncidentFree;
	private Integer aggregateCompClaims;
	private Integer nafAccidents;
	private Double avgAnnualERSperMember;
	private Integer insuredAge;
	private Integer noOfVehiclesExcludingTrailer;
	private Boolean multiCar;
	private Boolean supplementalSpousalLiability; // NY specific (but field was also found in OR file with FALSE value)
	private Boolean umbiConvCode; // CT specific
	private Integer aaaAPIPIncomeContBenLimit; // NJ specific
	private String aaaAPIPLengthIncomeCont; // NJ specific
	private Integer aaaPIPExtMedPayLimit; // NJ specific
	private Integer aaaPIPMedExpDeductible; // NJ specific
	private Integer aaaPIPMedExpLimit; // NJ specific
	private String aaaPIPNonMedExp; // NJ specific
	private String aaaPIPPrimaryInsurer; // NJ specific
	private Integer noOfAPIPAddlNamedRel; // NJ specific
	private Integer previousAaaInsurancePersistency; // PA specific
	private String rbTier; // NY specific
	private Integer yafAfterInception; // NY specific
	private Integer ycfAfterInception; // NY specific
	private String tort; // PA specific

	@Override
	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	@Override
	public Double getPreviousPolicyPremium() {
		return getCappingDetails().getPreviousPolicyPremium();
	}

	@Override
	public String getUnderwriterCode() {
		return getCappingDetails().getUnderwriterCode();
	}

	@Override
	public MocksCollection getRequiredMocks() {
		MocksCollection requiredMocks = new MocksCollection();
		MockGenerator mockGenerator = new MockGenerator();
		if (!mockGenerator.isMembershipSummaryMockPresent(getEffectiveDate(), getMemberPersistency(), getAvgAnnualERSperMember())) {
			RetrieveMembershipSummaryMock membershipMock = mockGenerator.getRetrieveMembershipSummaryMock(getEffectiveDate(), getMemberPersistency(), getAvgAnnualERSperMember());
			requiredMocks.add(membershipMock);
		}

		HashSet<String> postalCodes = getVehicles().stream().map(v -> v.getAddress().getZip()).collect(Collectors.toCollection(HashSet::new));
		for (String postalCode : postalCodes) {
			if (!mockGenerator.isAddressReferenceMockPresent(postalCode, getState())) {
				AddressReferenceMock addressReferenceMock = mockGenerator.getAddressReferenceMock(postalCode, getState());
				requiredMocks.add(addressReferenceMock);
			}
		}

		return requiredMocks;
	}

	public Integer getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(Integer creditScore) {
		this.creditScore = creditScore;
	}

	public String getAaaHomePolicy() {
		return aaaHomePolicy;
	}

	public void setAaaHomePolicy(String aaaHomePolicy) {
		this.aaaHomePolicy = aaaHomePolicy;
	}

	public String getAaaRentersPolicy() {
		return aaaRentersPolicy;
	}

	public void setAaaRentersPolicy(String aaaRentersPolicy) {
		this.aaaRentersPolicy = aaaRentersPolicy;
	}

	public String getAaaCondoPolicy() {
		return aaaCondoPolicy;
	}

	public void setAaaCondoPolicy(String aaaCondoPolicy) {
		this.aaaCondoPolicy = aaaCondoPolicy;
	}

	public Integer getMemberPersistency() {
		return memberPersistency;
	}

	public void setMemberPersistency(Integer memberPersistency) {
		this.memberPersistency = memberPersistency;
	}

	public Integer getAutoInsurancePersistency() {
		return autoInsurancePersistency;
	}

	public void setAutoInsurancePersistency(Integer autoInsurancePersistency) {
		this.autoInsurancePersistency = autoInsurancePersistency;
	}

	public Integer getAaaInsurancePersistency() {
		return aaaInsurancePersistency;
	}

	public void setAaaInsurancePersistency(Integer aaaInsurancePersistency) {
		this.aaaInsurancePersistency = aaaInsurancePersistency;
	}

	public Integer getAaaAsdInsurancePersistency() {
		return aaaAsdInsurancePersistency;
	}

	public void setAaaAsdInsurancePersistency(Integer aaaAsdInsurancePersistency) {
		this.aaaAsdInsurancePersistency = aaaAsdInsurancePersistency;
	}

	public String getPaymentPlanType() {
		return paymentPlanType;
	}

	public void setPaymentPlanType(String paymentPlanType) {
		this.paymentPlanType = paymentPlanType;
	}

	public String getDistributionChannel() {
		return distributionChannel;
	}

	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}

	public String getPriorBILimit() {
		return priorBILimit;
	}

	public void setPriorBILimit(String priorBILimit) {
		this.priorBILimit = priorBILimit;
	}

	public Integer getReinstatements() {
		return reinstatements;
	}

	public void setReinstatements(Integer reinstatements) {
		this.reinstatements = reinstatements;
	}

	public Integer getYearsAtFaultAccidentFree() {
		return yearsAtFaultAccidentFree;
	}

	public void setYearsAtFaultAccidentFree(Integer yearsAtFaultAccidentFree) {
		this.yearsAtFaultAccidentFree = yearsAtFaultAccidentFree;
	}

	public Integer getYearsIncidentFree() {
		return yearsIncidentFree;
	}

	public void setYearsIncidentFree(Integer yearsIncidentFree) {
		this.yearsIncidentFree = yearsIncidentFree;
	}

	public Integer getAggregateCompClaims() {
		return aggregateCompClaims;
	}

	public void setAggregateCompClaims(Integer aggregateCompClaims) {
		this.aggregateCompClaims = aggregateCompClaims;
	}

	public Integer getNafAccidents() {
		return nafAccidents;
	}

	public void setNafAccidents(Integer nafAccidents) {
		this.nafAccidents = nafAccidents;
	}

	public Double getAvgAnnualERSperMember() {
		return avgAnnualERSperMember;
	}

	public void setAvgAnnualERSperMember(Double avgAnnualERSperMember) {
		this.avgAnnualERSperMember = avgAnnualERSperMember;
	}

	public Integer getInsuredAge() {
		return insuredAge;
	}

	public void setInsuredAge(Integer insuredAge) {
		this.insuredAge = insuredAge;
	}

	public AutoSSOpenLCappingDetails getCappingDetails() {
		return cappingDetails;
	}

	public void setCappingDetails(AutoSSOpenLCappingDetails cappingDetails) {
		this.cappingDetails = cappingDetails;
	}

	public List<AutoSSOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoSSOpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	public Integer getNoOfVehiclesExcludingTrailer() {
		return noOfVehiclesExcludingTrailer;
	}

	public void setNoOfVehiclesExcludingTrailer(int noOfVehiclesExcludingTrailer) {
		this.noOfVehiclesExcludingTrailer = noOfVehiclesExcludingTrailer;
	}

	public List<AutoSSOpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<AutoSSOpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	public Boolean getUmbiConvCode() {
		return umbiConvCode;
	}

	public void setUmbiConvCode(Boolean umbiConvCode) {
		this.umbiConvCode = umbiConvCode;
	}

	public Integer getAaaAPIPIncomeContBenLimit() {
		return aaaAPIPIncomeContBenLimit;
	}

	public void setAaaAPIPIncomeContBenLimit(Integer aaaAPIPIncomeContBenLimit) {
		this.aaaAPIPIncomeContBenLimit = aaaAPIPIncomeContBenLimit;
	}

	public String getAaaAPIPLengthIncomeCont() {
		return aaaAPIPLengthIncomeCont;
	}

	public void setAaaAPIPLengthIncomeCont(String aaaAPIPLengthIncomeCont) {
		this.aaaAPIPLengthIncomeCont = aaaAPIPLengthIncomeCont;
	}

	public Integer getAaaPIPExtMedPayLimit() {
		return aaaPIPExtMedPayLimit;
	}

	public void setAaaPIPExtMedPayLimit(Integer aaaPIPExtMedPayLimit) {
		this.aaaPIPExtMedPayLimit = aaaPIPExtMedPayLimit;
	}

	public Integer getAaaPIPMedExpDeductible() {
		return aaaPIPMedExpDeductible;
	}

	public void setAaaPIPMedExpDeductible(Integer aaaPIPMedExpDeductible) {
		this.aaaPIPMedExpDeductible = aaaPIPMedExpDeductible;
	}

	public Integer getAaaPIPMedExpLimit() {
		return aaaPIPMedExpLimit;
	}

	public void setAaaPIPMedExpLimit(Integer aaaPIPMedExpLimit) {
		this.aaaPIPMedExpLimit = aaaPIPMedExpLimit;
	}

	public String getAaaPIPNonMedExp() {
		return aaaPIPNonMedExp;
	}

	public void setAaaPIPNonMedExp(String aaaPIPNonMedExp) {
		this.aaaPIPNonMedExp = aaaPIPNonMedExp;
	}

	public String getAaaPIPPrimaryInsurer() {
		return aaaPIPPrimaryInsurer;
	}

	public void setAaaPIPPrimaryInsurer(String aaaPIPPrimaryInsurer) {
		this.aaaPIPPrimaryInsurer = aaaPIPPrimaryInsurer;
	}

	public Integer getNoOfAPIPAddlNamedRel() {
		return noOfAPIPAddlNamedRel;
	}

	public void setNoOfAPIPAddlNamedRel(Integer noOfAPIPAddlNamedRel) {
		this.noOfAPIPAddlNamedRel = noOfAPIPAddlNamedRel;
	}

	public Integer getPreviousAaaInsurancePersistency() {
		return previousAaaInsurancePersistency;
	}

	public void setPreviousAaaInsurancePersistency(Integer previousAaaInsurancePersistency) {
		this.previousAaaInsurancePersistency = previousAaaInsurancePersistency;
	}

	public String getRbTier() {
		return rbTier;
	}

	public void setRbTier(String rbTier) {
		this.rbTier = rbTier;
	}

	public Integer getYafAfterInception() {
		return yafAfterInception;
	}

	public void setYafAfterInception(Integer yafAfterInception) {
		this.yafAfterInception = yafAfterInception;
	}

	public Integer getYcfAfterInception() {
		return ycfAfterInception;
	}

	public void setYcfAfterInception(Integer ycfAfterInception) {
		this.ycfAfterInception = ycfAfterInception;
	}

	public String getTort() {
		return tort;
	}

	public void setTort(String tort) {
		this.tort = tort;
	}

	@Override
	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	@Override
	public AutoSSTestDataGenerator getTestDataGenerator(TestData baseTestData) {
		return new AutoSSTestDataGenerator(this.getState(), baseTestData);
	}

	@Override
	public Map<String, String> getFilteredOpenLFieldsMap() {
		return removeOpenLFields(super.getFilteredOpenLFieldsMap(),
				"^policy\\.drivers\\[\\d+\\]\\.id$",
				"^policy\\.vehicles\\[\\d+\\]\\.id$",
				"^policy\\.vehicles\\[\\d+\\].annualMileage$",
				"^policy\\.vehicles\\[\\d+\\]\\.ratedDriver\\.id$",
				"^policy\\.vehicles\\[\\d+\\]\\.coverages\\[\\d+\\]\\.additionalLimitAmount$"
		);
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public void setHomeOwner(Boolean homeOwner) {
		isHomeOwner = homeOwner;
	}

	public void setAAAMember(Boolean isAAAMember) {
		this.isAAAMember = isAAAMember;
	}

	public void setAaaLifePolicy(Boolean aaaLifePolicy) {
		this.aaaLifePolicy = aaaLifePolicy;
	}

	public void setAaaMotorcyclePolicy(Boolean aaaMotorcyclePolicy) {
		this.aaaMotorcyclePolicy = aaaMotorcyclePolicy;
	}

	public void setEMember(Boolean isEMember) {
		this.isEMember = isEMember;
	}

	public void setAARP(Boolean isAARP) {
		this.isAARP = isAARP;
	}

	public void setEmployee(Boolean employee) {
		isEmployee = employee;
	}

	public void setAdvanceShopping(Boolean advanceShopping) {
		isAdvanceShopping = advanceShopping;
	}

	public void setUnacceptableRisk(Boolean unacceptableRisk) {
		this.unacceptableRisk = unacceptableRisk;
	}

	public void setMultiCar(Boolean multiCar) {
		this.multiCar = multiCar;
	}

	public void setSupplementalSpousalLiability(Boolean supplementalSpousalLiability) {
		this.supplementalSpousalLiability = supplementalSpousalLiability;
	}

	public Boolean isHomeOwner() {
		return isHomeOwner;
	}

	public Boolean isAAAMember() {
		return isAAAMember;
	}

	public Boolean isAaaLifePolicy() {
		return aaaLifePolicy;
	}

	public Boolean isAaaMotorcyclePolicy() {
		return aaaMotorcyclePolicy;
	}

	public Boolean isEMember() {
		return isEMember;
	}

	public Boolean isAARP() {
		return isAARP;
	}

	public Boolean isEmployee() {
		return isEmployee;
	}

	public Boolean isAdvanceShopping() {
		return isAdvanceShopping;
	}

	public Boolean isUnacceptableRisk() {
		return unacceptableRisk;
	}

	public Boolean isMultiCar() {
		return multiCar;
	}

	public Boolean isSupplementalSpousalLiability() {
		return supplementalSpousalLiability;
	}
}
