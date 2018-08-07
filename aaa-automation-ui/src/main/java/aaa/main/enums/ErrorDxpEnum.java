package aaa.main.enums;

public final class ErrorDxpEnum {
	private ErrorDxpEnum() {
	}

	public enum Errors {

		//Start Endorsement Errors
		POLICY_TERM_DOES_NOT_EXIST("Cannot endorse policy - policy term does not exist for endorsement date", "Cannot endorse policy - policy term does not exist for endorsement date"),
		ACTION_IS_NOT_AVAILABLE("PFW093", "Action is not available"),
		OOSE_OR_FUTURE_DATED_ENDORSEMENT("OOSE or Future Dated Endorsement Exists", "OOSE or Future Dated Endorsement Exists"),
		POLICY_IS_LOCKED("Policy is locked", "Policy is locked"),
		ENTITY_IS_LOCKED_BY_OTHER_USER("ERROR_SERVICE_INTERNAL_ERROR", "The requested entity is currently locked by other user"),
		COULD_NOT_ACQUIRE_LOCK("Could not acquire a new lock: the requested entity is currently locked", "Could not acquire a new lock: the requested entity is currently locked"),
		STATE_DOES_NOT_ALLOW_ENDORSEMENTS("State does not allow endorsements", "State does not allow endorsements"),
		NANO_POLICY("NANO Policy", "NANO Policy"),

		SYSTEM_CREATED_PENDED_ENDORSEMENT("System Created Pended Endorsement", "System Created Pended Endorsement"),
		CUSTOMER_CREATED_ENDORSEMENT("Customer Created Endorsement", "Customer Created Endorsement"),
		RENEWAL_DOES_NOT_EXIST("POLICY_NOT_FOUND", "Renewal quote version or issued pending renewal not found for policy number "),
		POLICY_NOT_RATED("ERROR_SERVICE_OBJECT_NOT_FOUND", "Policy Not Rated"),
		POLICY_NOT_RATED_DXP("DXP-OP-ERR", "Cannot issue policy which was not rated!"),
		DUPLICATE_VIN("DXP-OP-ERR", "Each vehicle must have a unique VIN - 200031"),
		TOO_EXPENSIVE_VEHICLE("DXP-OP-ERR", "Vehicle value exceeds acceptable coverage limit - 200022"),
		MAX_NUMBER_OF_VEHICLES("PFO016", "Cannot add instance for 'Vehicle' because max instance count is reached or component is not applicable"),
		MAX_NUMBER_OF_DRIVERS("AAA_SS4100066", "This policy already has 7 drivers that are not excluded.  Please contact underwriting to create a continuation policy."),
		USAGE_IS_BUSINESS("AAA_SS1007147", "Usage is Business"),
		REGISTERED_OWNERS("AAA_SS1007148", "Registered Owners"),
		MORE_THAN_TWO_MINOR_VIOLATIONS_VA("200095", "Driver with more than (2) Minor violations are unacceptable. (200095)"),
		DRIVER_WITH_MORE_THAN_THREE_INCIDENTS("200096", "Drivers with more than (3) Incidents are unacceptable. (200096)"),
		DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS("200005", "Driver with a narcotics, drug or felony conviction involving a motor vehicle is unacceptable (200005)"),
		DRIVER_WITH_MORE_THAN_TWENTY_POINTS_VA("200004_VA", "Driver with more than 20 points is unacceptable (200004)"),
		DRIVER_WITH_MAJOR_VIOLATION_VA("200009_VA", "Driver with a Major violation, including a DUI is unacceptable (200009)"),

		ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS("OP-01", "Error occurred while executing operations"),
		ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS_BRACKETS("PFO017", "Error occurred during operation execution: {1}"),
		VALIDATION_ERROR_HAPPENED_DURING_BIND("ERROR_SERVICE_VALIDATION", "Validation error happened during bind of the policy"),
		OPERATION_NOT_APPLICABLE_FOR_THE_STATE("ERROR_SERVICE_VALIDATION", "Operation not applicable for the state."),

		INCOMPLETE_OR_UNACCEPTABLE_SELECTION("AAA_SS072412", "Incomplete or unacceptable selection(s) made on the Assignment page (AAA_SS072412)"),
		INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA("AAA_SS10230241_VA", "Incomplete or unacceptable selection(s) made on the Assignment page (AAA_SS10230241)"),
		INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2("AAA_SS10230238_VA", "Incomplete or unacceptable selection(s) made on the Assignment page (AAA_SS10230238)"),

		DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE("AAA_SS10230239", "Drivers must be assigned a unique vehicle (AAA_SS10230239)"),

		ZIP_CODE_IS_NOT_APPLICABLE("AAA_SS1273145", "Zip code is not applicable; please check again. If this is incorrect, please contact Agency Support. (AAA_SS1273145)"),
		GARAGED_OUT_OF_STATE("200019", "Unacceptable due to one or more vehicles garaged out of the policy state (200019)"),
		GARAGED_OUT_OF_STATE_RATE("200019_C", "Unacceptable due to one or more vehicles garaged out of the policy state (200019_C)"),
		GARAGED_IN_MICHIGAN("200020", "Unacceptable due to one or more vehicles garaged in Michigan (200020)"),
		GARAGED_IN_MICHIGAN_RATE("200020_C", "Unacceptable due to one or more vehicles garaged in Michigan (200020_C)"),
		GARAGED_OUT_OF_STATE_ONLY_VEHICLE("200018", "Unacceptable due to all vehicles garaged out of the policy state (200018)"),
		GARAGED_OUT_OF_STATE_ONLY_VEHICLE_RATE("200018_C", "Unacceptable due to all vehicles garaged out of the policy state (200018_C)"),
		UNIQUE_VIN("200031", "Each vehicle must have a unique Vehicle Identification Number (200031)"),
		UNIQUE_VIN_RATE("200031_C", "Each vehicle must have a unique Vehicle Identification Number (200031_C)"),
		MUST_HAVE_PPA("200016", "Policy must cover at least one Private Passenger Automobile (200016)"),
		MUST_HAVE_PPA_RATE("200016_C", "Policy must cover at least one Private Passenger Automobile (200016_C)"),
		EXPENSIVE_VEHICLE("200022", "Vehicle value exceeds acceptable coverage limit (200022)"),
		EXPENSIVE_VEHICLE_RATE("200022_C", "Vehicle value exceeds acceptable coverage limit (200022_C)"),

		DRIVER_UNDER_AGE_COMMON("AAA_CSA6220000", "Drivers under age 16 must be excluded or not available for rating (AAA_CSA6220000)"), //the same as in PAS
		DRIVER_UNDER_AGE_VA("AAA_CSA6220000_VA", "Drivers under age 16 must be not available for rating (AAA_CSA6220000)"), //the same as in PAS
		DRIVER_UNDER_AGE_NV("AAA_CSA6220000_NV", "Drivers under age 16 must be set to not available for rating (AAA_CSA6220000)"), //the same as in PAS
		DRIVER_UNDER_AGE_KS("AAA_CSA6220000_KS", "Drivers under age 15 must be not available for rating (AAA_CSA6220000)"), //the same as in PAS
		DRIVER_UNDER_AGE_MT("AAA_CSA6220000_MT", "Drivers under age 15 must be not available for rating (AAA_CSA6220000)"), //the same as in PAS
		DRIVER_UNDER_AGE_SD("AAA_CSA6220000_SD", "Drivers under age 14 must be excluded or not available for rating (AAA_CSA6220000)"), //the same as in PAS
		AGE_FIRST_LICENSED_ERROR("Age First Licensed must be 14 or greater (BAU00209)"),
		DUPLICATE_DRIVER_LICENSE_ERROR("Duplicate Driver License (AAASS200008)"),
		VALIDATE_DRIVER_LICENSE_BY_STATE("License number is inconsistent with state format (AAA_CSA3040364)"),
		INSURANCE_SCORE_ORDER_MESSAGE("Need Insurance Score Order (AAA_SS9192341)"),
		;



		private String code;
		private String message;

		Errors(String message) {
			setMessage(message); // if we have message only
		}

		Errors() {
			setCode(this.name());
			setMessage(""); // to prevent NPE on getErrorMessage() call for rules with not defined error messages
		}

		Errors(String code, String message) {
			setCode(code);
			setMessage(message);
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return "Error{" + "Code='" + code + '\'' + ", Message='" + message + '\'' + '}';
		}
	}
}
