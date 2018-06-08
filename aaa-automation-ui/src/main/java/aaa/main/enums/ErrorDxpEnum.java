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
		RENEWAL_DOES_NOT_EXIST("ERROR_SERVICE_OBJECT_NOT_FOUND", "Renewal quote version or issued pending renewal not found for policy number "),
		POLICY_NOT_RATED("ERROR_SERVICE_OBJECT_NOT_FOUND", "Policy Not Rated"),
		POLICY_NOT_RATED_DXP("DXP-OP-ERR", "Cannot issue policy which was not rated!"),
		DUPLICATE_VIN("DXP-OP-ERR", "Each vehicle must have a unique VIN - 200031"),
		TOO_EXPENSIVE_VEHICLE("DXP-OP-ERR", "Vehicle value exceeds acceptable coverage limit - 200022"),
		MAX_NUMBER_OF_VEHICLES("PFO016", "Cannot add instance for 'Vehicle' because max instance count is reached or component is not applicable"),
		USAGE_IS_BUSINESS("AAA_SS1007147", "Usage is Business"),
		REGISTERED_OWNERS("AAA_SS1007148", "Registered Owners"),

		ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS("OP-01", "Error occurred while executing operations"),
		ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS_BRACKETS("PFO017", "Error occurred during operation execution: {1}"),
		VALIDATION_ERROR_HAPPENED_DURING_BIND("ERROR_SERVICE_VALIDATION", "Validation error happened during bind of the policy"),
		OPERATION_NOT_APPLICABLE_FOR_THE_STATE("ERROR_SERVICE_VALIDATION", "Operation not applicable for the state.");


		private String code;
		private String message;

		Errors() {
			setCode(this.name());
			setMessage(""); // to prevent NPE on getErrorMessage() call for rules with not defined error messages
		}

		Errors(String code) {
			setCode(code);
			setMessage("");
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
