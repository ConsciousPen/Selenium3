package aaa.main.enums;

public class ErrorEnum {

	public enum Errors {
		ERROR_200103("200103", "Driver with 3 or more Minor or Speeding violations are unacceptable");
		//TODO: add other rules there...

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
			return "Error{" +
					"Code='" + code + '\'' +
					", Message='" + message + '\'' +
					'}';
		}
	}

	public enum Duration {
		TERM("Term"),
		LIFE("Life");

		private String id;

		Duration(String id) {
			set(id);
		}

		public String get() {
			return id;
		}

		public void set(String id) {
			this.id = id;
		}
	}

	public enum ReasonForOverride {
		JUSTIFIED_THROUGH_SUPPORTING_DOCUMENTATION("Justified through supporting documentation"),
		AAA_ERROR("AAA Error"),
		SYSTEM_ISSUE("System Issue"),
		TEMPORARY_ISSUE("Temporary Issue"),
		OTHER("Other"),
		CONVERSION("Conversion"),
		CONVERSION_HIGH_PREMIUM_VARIANCE("Conversion - High Premium Variance"),
		EMPTY("");

		private String id;

		ReasonForOverride(String id) {
			set(id);
		}

		public String get() {
			return id;
		}

		public void set(String id) {
			this.id = id;
		}
	}
}
