package aaa.main.enums;

public class ErrorEnum {

	public enum Errors {
		ERROR_200103("200103", "Driver with 3 or more Minor or Speeding violations are unacceptable"),
		//Property errors
		ERROR_AAA_HO_SS10060735("AAA_HO_SS10060735", "Underwriter approval is required when Adversely Impacted is selected."), 
		ERROR_AAA_HO_SS624530_CO("AAA_HO_SS624530_CO", "Dwellings that have not had the roof replaced within the past 25 years if com..."), 
		ERROR_AAA_HO_SS10030560("AAA_HO_SS10030560", "Dwellings with a wood shake/shingle roof are unacceptable."),
		ERROR_AAA_HO_SS10030001("AAA_HO_SS10030001", "Dwellings with a T-Lock shingle roof are unacceptable."),
		ERROR_AAA_HO_SS7230342("AAA_HO_SS7230342", "Underwriting approval is required for the option you have selected."), 
		ERROR_AAA_HO_SS4260842("AAA_HO_SS4260842", "Wind/hail endorsement is required when roof type is wood shingle/wood shake.");
				
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
