package aaa.utils.openl.parser;

import java.util.Set;

public class AutoSSOpenLFields extends OpenLFields {

	public Set<String> getDriverFields() {
		return getNames(Driver.values());
	}

	@Override
	public Set<String> getPolicyFields() {
		Set<String> policyFiels = super.getPolicyFields();
		policyFiels.addAll(getNames(Policy.values()));
		return policyFiels;
	}

	public enum Policy implements OpenLField {
		/*PK("_PK_"),
		POLICY_NUMBER("policyNumber"),
		EFFECTIVE_DATE("effectiveDate"),*/
		TERM("term"),
		IS_HOME_OWNER("isHomeOwner"),
		CREDIT_SCORE("creditScore"),
		IS_AAA_MEMBER("isAAAMember"),
		AAA_HOME_POLICY("aaaHomePolicy"),
		AAA_RENTERS_POLICY("aaaRentersPolicy"),
		AAA_CONDO_POLICY("aaaCondoPolicy"),
		AAA_LIFE_POLICY("aaaLifePolicy"),
		AAA_MOTORCYCLE_POLICY("aaaMotorcyclePolicy"),
		IS_E_MEMBER("isEMember"),
		MEMBER_PERSISTENCY("memberPersistency"),
		AUTO_INSURANCE_PERSISTENCY("autoInsurancePersistency"),
		AAA_INSURANCE_PERSISTENCY("aaaInsurancePersistency"),
		AAA_ASD_INSURANCE_PERSISTENCY("aaaAsdInsurancePersistency"),
		IS_AARP("isAARP"),
		IS_EMPLOYEE("isEmployee"),
		IS_ADVANCE_SHOPPING("isAdvanceShopping"),
		PAYMENT_PLAN_TYPE("paymentPlanType"),
		DISTRIBUTION_CHANNEL("distributionChannel"),
		DRIVERS("drivers"),
		UNACCEPTABLE_RISK("unacceptableRisk"),
		PRIOR_BI_LIMIT("priorBILimit"),
		REINSTATEMENTS("reinstatements"),
		YEARS_AT_FAULT_ACCIDENTF_REE("yearsAtFaultAccidentFree"),
		YEARS_INCIDENTF_REE("yearsIncidentFree"),
		AGGREGATE_COMP_CLAIMS("aggregateCompClaims"),
		NAF_ACCIDENTS("nafAccidents"),
		AVG_ANNUAL_ERSPER_MEMBER("avgAnnualERSperMember"),
		INSURED_AGE("insuredAge"),
		NO_OF_VEHICLES_EXCLUDING_TRAILER("noOfVehiclesExcludingTrailer"),
		SUPPLEMENTAL_SPOUSALLIABILITY("supplementalSpousalLiability"),
		MULTI_CAR("multiCar");

		private final String name;

		Policy(String name) {
			this.name = name;
		}

		@Override
		public String get() {
			return name;
		}
	}

	public enum Driver implements OpenLField {
		PK("_PK_"),
		ID("id"),
		NAME("name"),
		GENDER("gender"),
		MARITAL_STATUS("maritalStatus"),
		TYDE("tyde"),
		DSR("dsr"),
		GOOD_STUDENT("goodStudent"),
		HAS_SR22("hasSR22"),
		DRIVER_AGE("driverAge"),
		AGE_BEFORE_ENDORSEMENT("ageBeforeEndorsement"),
		SMART_DRIVER("smartDriver"),
		DISTANT_STUDENT("distantStudent"),
		DEFENSIVE_DRIVING_COURSE("defensiveDrivingCourse"),
		FOREIGN_LICENSE("foreignLicense"),
		UNVERIFIABLE_DRIVING_RECORD("unverifiableDrivingRecord"),
		OUT_OF_STATE_LICENSE_SURCHARGE("outOfStateLicenseSurcharge"),
		EXPOSURE("exposure"),
		CLEAN_DRIVER("cleanDriver"),;

		private final String name;

		Driver(String name) {
			this.name = name;
		}

		@Override
		public String get() {
			return name;
		}
	}
}
