package aaa.main.enums;

public final class ErrorDxpEnum {
	private ErrorDxpEnum() {
	}

	public enum Errors {

		//Start Endorsement Errors
		POLICY_TERM_DOES_NOT_EXIST("Cannot endorse policy - policy term does not exist for endorsement date", "Cannot endorse policy - policy term does not exist for endorsement date"),
		ACTION_IS_NOT_AVAILABLE("PFW093", "Action is not available"),
		OOSE_OR_FUTURE_DATED_ENDORSEMENT("OOSE or Future Dated Endorsement Exists", "OOSE or Future Dated Endorsement Exists"),
		DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_MT("200010_MT", "Driver under the age of 21 years with a DUI is unacceptable"),
		DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_NO_VA("200010", "Driver under the age of 21 years with a DUI is unacceptable"),
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
		REGISTERED_OWNERS_CA("AAA_CSA1007148", "Registered Owners"),
		USAGE_IS_BUSINESS_CA("AAA_CSA1007147", "Usage is Business"),
		MORATORIUM_EXIST("AAA_SS181109", "Moratorium Exists"),
		INSURER_NAME_POLICY_GROUP_CERTIFICATE_BLANK_NJ("AAA_SS4030000", "\"Insurer name\",\"Policy/Group #/Certificate #\" mandatory fields value cannot be left blank and  \r\n"
				+ "Requires at least 3 characters."),
		INSURER_NAME_POLICY_GROUP_CERTIFICATE_BLANK_NY("AAA_SS4030001", "\"Insurer name\",\"Policy/Group #/Certificate #\" mandatory fields value cannot be left blank and requires at least 3 characters."),

		//Mvr and Clue Errors
		ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS("OP-01", "Error occurred while executing operations"),
		ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS_BRACKETS("PFO017", "Error occurred during operation execution: {1}"),
		ORDER_REPORT_FOR_EXISTING_DRIVER_ERROR("ERROR_SERVICE_VALIDATION","Cannot order reports for existing drivers."),
		MVR_ERROR_C("200119_C", "MVR Error"),
		DRIVER_WITH_MORE_THAN_TWENTY_POINTS("200004", "Driver with more than 20 points is unacceptable"),
		DRIVER_WITH_MORE_THAN_TWENTY_POINTS_C("200004_C", "Driver with more than 20 points is unacceptable"),
		DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21("200010_VA", "Driver under the age of 21 years with a DUI is unacceptable"),
		DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_C("200010_C", "Driver under the age of 21 years with a DUI is unacceptable"),
		DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS("200005", "Driver with a narcotics, drug or felony conviction involving a motor vehicle is unacceptable"),
		DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS_C("200005_C", "Driver with a narcotics, drug or felony conviction involving a motor vehicle is unacceptable"),
		DRIVER_WITH_MAJOR_VIOLATION_DUI("200009","Driver with a Major violation, including a DUI is unacceptable"),
		DRIVER_WITH_MAJOR_VIOLATION_DUI_C("200009_C","Driver with a Major violation, including a DUI is unacceptable"),
		MORE_THAN_TWO_MINOR_VIOLATIONS("200095", "Driver with more than (2) Minor violations are unacceptable."),
		MORE_THAN_TWO_MINOR_VIOLATIONS_MD("200116", "No more than one (2) minor violations in the past 33 months."),
		MORE_THAN_TWO_MINOR_VIOLATIONS_C("200095_C", "Driver with more than (2) Minor violations are unacceptable."),

		MORE_THAN_TWO_MINOR_VIOLATIONS_C_MD("200116_C", "No more than one (2) minor violations in the past 33 months."),

		DRIVER_WITH_MORE_THAN_THREE_INCIDENTS("200096", "Drivers with more than (3) Incidents are unacceptable."),
		DRIVER_WITH_MORE_THAN_THREE_INCIDENTS_C("200096_C", "Drivers with more than (3) Incidents are unacceptable."),
		DRIVER_WITH_ONE_OR_MORE_FAULT_ACCIDENTS("200104", "Driver with 2 or more At-fault accidents are unacceptable"),
		DRIVER_WITH_ONE_OR_MORE_FAULT_ACCIDENTS_C("200104_C", "Driver with 2 or more At-fault accidents are unacceptable"),

		VALIDATION_ERROR_HAPPENED_DURING_BIND("ERROR_SERVICE_VALIDATION", "Validation error happened during bind of the policy"),
		OPERATION_NOT_APPLICABLE_FOR_THE_STATE("ERROR_SERVICE_VALIDATION", "Operation not applicable for the state."),

		INCOMPLETE_OR_UNACCEPTABLE_SELECTION("AAA_SS072412", "Incomplete or unacceptable selection(s) made on the Assignment page"),
		INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA("AAA_SS10230241_VA", "Incomplete or unacceptable selection(s) made on the Assignment page"),
		INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2("AAA_SS10230238_VA", "Incomplete or unacceptable selection(s) made on the Assignment page"),

		ZIP_CODE_IS_NOT_APPLICABLE("AAA_SS1273145", "Zip code is not applicable; please check again. If this is incorrect, please contact Agency Support."),
		GARAGED_OUT_OF_STATE("200019", "Unacceptable due to one or more vehicles garaged out of the policy state"),
		GARAGED_IN_MICHIGAN("200020", "Unacceptable due to one or more vehicles garaged in Michigan"),
		GARAGED_OUT_OF_STATE_ONLY_VEHICLE("200018", "Unacceptable due to all vehicles garaged out of the policy state"),
		UNIQUE_VIN("200031", "Each vehicle must have a unique Vehicle Identification Number"),
		MUST_HAVE_PPA("200016", "Policy must cover at least one Private Passenger Automobile"),
		EXPENSIVE_VEHICLE("200022", "Vehicle value exceeds acceptable coverage limit"),
		TOO_OLD_DRIVER_ERROR("AAA_SS7120048", "The date of birth provided for the Driver Available for Rating should be between 01/01/1900 and today's date"),
		TOO_OLD_DRIVER_ERROR_CA("AAA_CSA2230649", "Birth date should be between 01/01/1900 and today's date (AAA_CSA2230649) [for DriverView.Driver.birthDate]"),
		AGE_FIRST_LICENSED_GREATER_THAN_DOB("AAA_CSA10260784", "Age First Licensed is greater than Current Age (AAA_CSA10260784) [for DriverView.Driver.firstLicenseAge]"),

		DRIVER_UNDER_AGE_COMMON("AAA_CSA6220000", "Drivers under age 16 must be excluded or not available for rating"), //the same as in PAS
		DRIVER_UNDER_AGE_VA("AAA_CSA6220000_VA", "Drivers under age 16 must be not available for rating"), //the same as in PAS
		DRIVER_UNDER_AGE_NV("AAA_CSA6220000_NV", "Drivers under age 16 must be set to not available for rating"), //the same as in PAS
		DRIVER_UNDER_AGE_KS("AAA_CSA6220000_KS", "Drivers under age 15 must be not available for rating"), //the same as in PAS
		DRIVER_UNDER_AGE_MT("AAA_CSA6220000_MT", "Drivers under age 15 must be not available for rating"), //the same as in PAS
		DRIVER_UNDER_AGE_SD("AAA_CSA6220000_SD", "Drivers under age 14 must be excluded or not available for rating"), //the same as in PAS
		DRIVER_UNDER_AGE_CA("AAA_CSA6220000_CA", "Driver must be at least 16 years old (AAA_CSA6220000) [for DriverView.Driver.age]"),
		AGE_FIRST_LICENSED_ERROR("Age First Licensed must be 14 or greater (BAU00209) [for DriverView.Driver.firstLicenseAge]"),
		DUPLICATE_DRIVER_LICENSE_ERROR("Duplicate Driver License (AAASS200008) [for DriverView.DrivingLicense.licensePermitNumber]"),
		VALIDATE_DRIVER_LICENSE_BY_STATE("License number is inconsistent with state format (AAA_CSA3040364) [for DriverView.DrivingLicense.licensePermitNumber]"),
		INSURANCE_SCORE_ORDER_MESSAGE("Need Insurance Score Order (AAA_SS9192341) [for DriverView.Driver.insuredOid]"),
		RELATIONSHIP_TO_FNI_ERROR("AAA_SS180807-NTzjT","Relationship to FNI needs review"),
		RELATIONSHIP_TO_FNI_ERROR_CA("AAA_SS180807-CAzjT","Relationship to FNI needs review (AAA_SS180807-CAzjT) [for DriverView.Driver.driverRelToApplicantCd]"),
		DRIVER_NAME_MISMATCH("AAA_SS180806-Bx9ip", "Driver name returned from DMV does not match Driver name entered for the Name Mismatch. Please verify that Driver name provided on the application is correct"),
		DRIVER_GENDER_MISMATCH("AAA_SS180806-F4Bjy", "The gender returned from DMV does not match the gender entered for Name Mismatch. Please verify that Driver gender provided on the application is correct"),
		DRIVER_DOB_MISMATCH("AAA_SS180806-Qpc85", "The date of birth returned from DMV does not match the DOB entered for Other Mismatches. Please verify that Driver date of birth provided on the application is correct"),
		DRIVER_GENDER_MISMATCHES("AAA_SS180806-F4Bjy","The gender returned from DMV does not match the gender entered for Other Mismatches. Please verify that Driver gender provided on the application is correct"),
		DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE("AAA_SS10230239", "Drivers must be assigned a unique vehicle"),
		DRIVER_GENDER_MISMATCHS("The gender returned from DMV does not match the gender entered for Other Mismatches. Please verify that Driver gender provided on the application is correct"),
		VERIFY_PUP_POLICY("AAA_SS180824-g8oKe", "Verify PUP Policy"),
		DRIVER_WITH_THREE_OR_MORE_SPEEDING_VIOLATION("200103", "Driver with 3 or more Minor or Speeding violations are unacceptable"),
		DRIVER_WITH_THREE_OR_MORE_SPEEDING_VIOLATION_C("200103_C", "Driver with 3 or more Minor or Speeding violations are unacceptable"),
		REVERT_DELETE_DRIVER_ERROR("ERROR_SERVICE_VALIDATION", "Removal can not be cancelled. Revert option is not available on driver."),
		REVERT_DELETE_VEHICLE_ERROR("ERROR_SERVICE_VALIDATION", "Removal can not be cancelled. Revert option is not available on vehicle."),
		DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_NY("200010_NY", "Driver under the age of 21 years with a DUI is unacceptable"),
		VEHICLE_CANNOT_BE_REMOVED_ERROR("ERROR_SERVICE_VALIDATION", "Vehicle cannot be removed. Remove action is not available on vehicle."),
		DRIVER_WITH_MORE_THAN_TWO_AT_FAULT_VIOLATION("200105", "Driver with 3 or more Non-fault accidents are unacceptable"),
		VEHICLE_CANNOT_BE_ADDED_ERROR("AAA_SS181009-lwY5B", "Vehicles cannot be added with a purchase date within 30 days from the transaction effective date."),
		DRIVER_WITH_MORE_THAN_TWO_AT_FAULT_VIOLATION_C("200105_C", "Driver with 3 or more Non-fault accidents are unacceptable."),
		MAJOR_CONVICTION("10015002_C", "A driver must not have a major conviction within the last thirty-six (36) months"),
		DRUG_FELONY("AAA_CAC7133540_C", "Driver with a narcotics, drug or felony conviction involving a motor vehicle is unacceptable."),
		DRIVER_TEN_YEAR_MAJOR_CONVICTION("AAA_CSA3292736_C", "Driver with 10 year Major conviction within the last 10  years is unacceptable"),
		MAJOR_VIOLATION("10015004_C", "Driver with more than 2 Minor violations are unacceptable."),
		INJYRY_ACCIDENT("10015021_C", "Driver with more than 1 At-fault injury accident is unacceptable"),
		TWO_AT_FAULT("10015023_C", "Driver must not have more than 2 At-fault accidents in the past 3 years"),
		COMBINATION_FAULT_ACCIDENT("10015015_C", "Driver with a combination of at least one Minor violation AND at least one At-fault accident within last 36 months is unacceptable."),
		DRIVER_WITH_SUSPENDED("10001001_C", "Driver with a suspended or revoked drivers license in the last 36 months is unacceptable");

		private final String code;
		private final String message;
		private final String field;

		Errors(String message) {
			this.code = null;
			this.message = message; // if we have message only
			this.field = null;
		}

		Errors(String code, String message) {
			this.code = code;
			this.message = message;
			this.field = null;
		}

		Errors(String code, String message, String field) {
			this.code = code;
			this.message = message;
			this.field = field;
		}

		public String getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

		public String getField() {
			return field;
		}

		@Override
		public String toString() {
			return "Error{" + "Code='" + code + '\'' + ", Message='" + message + '\'' + '}';
		}
	}
}
