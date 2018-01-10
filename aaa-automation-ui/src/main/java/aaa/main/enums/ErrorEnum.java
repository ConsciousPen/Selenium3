package aaa.main.enums;

public final class ErrorEnum {
	private ErrorEnum() {
	}

	public enum Errors {
		ERROR_200103("200103", "Driver with 3 or more Minor or Speeding violations are unacceptable"),
		ERROR_200401("200401", "Credit Adverse Impact requires Underwriting Review and approval"),
		ERROR_200060_CO("200060_CO", "If Medical Payments coverage is rejected, a signed form must be received"),
		ERROR_AAA_SS9140068("AAA_SS9140068", "It is too late in the term to change to the selected bill plan."),
		ERROR_AAA_CLUE_order_validation_SS("AAA_CLUE_order_validation_SS", "Current CLUE must be ordered"),
		ERROR_AAA_SS10240324("AAA_SS10240324", "At least one phone number must be provided. (AAA_SS10240324) [for AAADocumentRulesComponent.attributeForRules]"),

		// Property errors
		ERROR_AAA_HO_CA_15011_1("AAA_HO_CA_15011_1", "Dwellings built prior to 1940 must have all four major systems fully renovated."),
		ERROR_AAA_HO_CACovAReplacementCost("AAA_HO_CACovAReplacementCost", "Coverage A greater than 120% of replacement cost requires underwriting approval."),
		ERROR_AAA_HO_CA12190315("AAA_HO_CA12190315", "Applicants with any paid claims over $25,000 in the last 3 years are ineligible."),
		ERROR_AAA_HO_CA1219040y("AAA_HO_CA1219040y", "Applicants with any liability claims in the past 3 years are ineligible."),
		ERROR_AAA_HO_CA12230792("AAA_HO_CA12230792", "Risks with more than 2 resident employees are ineligible."),
		ERROR_AAA_HO_CA12240000("AAA_HO_CA12240000", "Policy is ineligible as combined total amount of all the detached structures ..."),
		ERROR_AAA_HO_CA12240080("AAA_HO_CA12240080", "A detached structure greater than 50% of Coverage A is ineligible."),
		ERROR_AAA_HO_CA12260015("AAA_HO_CA12260015", "Dwellings with more than 2 detached building structures rented to others on t..."),
		ERROR_AAA_HO_CA12261856("AAA_HO_CA12261856", "More than 2 additional Interests require Underwriting approval"),
		ERROR_AAA_HO_CA3230672("AAA_HO_CA3230672", "Policy effective date cannot be backdated more than three days from today's d..."),
		ERROR_AAA_HO_CA338657_3("AAA_HO_CA338657_3", "Dwellings or applicants that perform a home day care, including child day car..."),
		ERROR_AAA_HO_CA338657_6("AAA_HO_CA338657_6", "Farming/Ranching on premises is unacceptable unless it is incidental and not ..."),
		ERROR_AAA_HO_CA338657_7("AAA_HO_CA338657_7", "Other business exposures on premises are unacceptable."),
		ERROR_AAA_HO_CA338657_17("AAA_HO_CA338657_17", "Policies must be endorsed with the HO 04 42 10 00 Permitted Incidental Occupa..."),
		ERROR_AAA_HO_CA338657_18("AAA_HO_CA338657_18", "Applicants who have been cancelled, refused insurance or non-renewed in the past 3 years are unacceptable unless approved by underwriting."),
		ERROR_AAA_HO_CA338657_20("AAA_HO_CA338657_20", "Dwelling must not have been in foreclosure within the past 18 months unless approved by underwriting."),
		ERROR_AAA_HO_CA338657_23("AAA_HO_CA338657_23", "Applicants/insureds with any dogs or other animals, reptiles, or pets with an..."),
		ERROR_AAA_HO_CA338657_28("AAA_HO_CA338657_28", "Water heaters (except electric heaters) must be strapped to the wall or if lo..."),
		ERROR_AAA_HO_CA338657_36("AAA_HO_CA338657_36", "Risks located within 500 feet of bay or coastal water is ineligible."),
		ERROR_AAA_HO_CA7220104("AAA_HO_CA7220104", "Dwellings built prior to 1900 are ineligible."),
		ERROR_AAA_HO_CA7220432("AAA_HO_CA7220432", "Dwellings with more than 2 roof layers are ineligible."),
		ERROR_AAA_HO_CA7220704("AAA_HO_CA7220704", "Maximum total number of livestock is 100."),
		ERROR_AAA_HO_CA7231530("AAA_HO_CA7231530", "More than 2 additional Insureds require Underwriting approval"),
		ERROR_AAA_HO_CA9230000("AAA_HO_CA9230000", "Dwellings with more than 3 detached building structures on the residence prem..."),
		ERROR_AAA_HO_SS3230000("AAA_HO_SS3230000", "Policy effective date cannot be backdated more than three days from today's d..."),
		ERROR_AAA_HO_SS10060735("AAA_HO_SS10060735", "Underwriter approval is required when Adversely Impacted is selected."),
		ERROR_AAA_HO_SS624530_CO("AAA_HO_SS624530_CO", "Dwellings that have not had the roof replaced within the past 25 years if com..."),
		ERROR_AAA_HO_SS10030560("AAA_HO_SS10030560", "Dwellings with a wood shake/shingle roof are unacceptable."),
		ERROR_AAA_HO_SS10030001("AAA_HO_SS10030001", "Dwellings with a T-Lock shingle roof are unacceptable."),
		ERROR_AAA_HO_SS7230342("AAA_HO_SS7230342", "Underwriting approval is required for the option you have selected."),
		ERROR_AAA_HO_SS4260842("AAA_HO_SS4260842","Wind/hail endorsement is required when roof type is wood shingle/wood shake."),
		ERROR_AAA_HO_SS14061993("AAA_HO_SS14061993", "	Dwellings with a Zip Code Level Match returned for Fireline require further u..."),
		ERROR_AAA_HO_SS3150198("AAA_HO_SS3150198", "Risk must be endorsed with the appropriate business or farming endorsement wh..."),
		ERROR_AAA_HO_SS3151364("AAA_HO_SS3151364", "Business or farming activity is ineligible. Dwellings or applicants that perf..."),
		ERROR_AAA_HO_SS1020340_OR("AAA_HO_SS1020340_OR", "Applicants with more than 1 paid non-CAT claim and/or more than 1 paid CAT cl..."),
		ERROR_AAA_HO_SS1050670_OR("AAA_HO_SS1050670_OR","Applicants with any paid non-CAT claim and/or more than 1 paid CAT claim in t..."),
		ERROR_AAA_HO_SS12023000("AAA_HO_SS12023000", "Applicants with any liability claims in the past 3 years are ineligible."),
		ERROR_WM_0523("WM-0523", "Applicants with 2 or more paid non-CAT claims OR 2 or more paid CAT claims in..."),
		ERROR_WM_0523_SD("WM-0523_SD", "Applicants with more than one paid non-weather claim and/or more than two pai..."),
		ERROR_AAA_HO_SS12200234("AAA_HO_SS12200234", "Applicants with any paid claims over $25,000 in the last 3 years are ineligible."),
		ERROR_AAA_HO_SS7160042("AAA_HO_SS7160042", "Applicants who have been cancelled, refused insurance or non-renewed in the p..."),
		ERROR_AAA_HO_SS11120040("AAA_HO_SS11120040", "Dwellings built prior to 1900 are ineligible."),
		ERROR_AAA_HO_SS3282256("AAA_HO_SS3282256", "Dwellings built prior to 1940 must have all four major systems fully renovated."),
		ERROR_AAA_HO_SS1100204("AAA_HO_SS1100204", "Proof of plumbing, electrical, heating/cooling system and roof renovations is..."),
		ERROR_AAA_HO_SS3200008("AAA_HO_SS3200008", "Risks with more than 3 horses or 4 livestock are unacceptable."),
		ERROR_AAA_HO_SS3195184("AAA_HO_SS3195184", "Risks with more than 3 horses or 4 livestock are unacceptable."),
		ERROR_AAA_HO_SS12141800("AAA_HO_SS12141800", "Underwriting approval required. Primary home of the applicant is not insured ..."),
		ERROR_AAA_HO_SS1160000("AAA_HO_SS1160000", "Coverage A greater than 120% of replacement cost requires underwriting approval."),
		ERROR_AAA_HO_SS1162304("AAA_HO_SS1162304", "Coverage A greater than $1,000,000 requires underwriting approval."),
		ERROR_AAA_HO_SS4250648("AAA_HO_SS4250648", "Coverage B must be less than 50% of Coverage A to bind."),
		ERROR_AAA_HO_SS3281224("AAA_HO_SS3281224", "Coverage B cannot exceed Coverage A."),
		ERROR_AAA_HO_SS3280000_1("AAA_HO_SS3280000_1", "Dwellings with more than 2 detached building structures rented to others on t..."),
		ERROR_AAA_HO_SS3281092("AAA_HO_SS3281092", "Dwellings with more than 3 detached building structures on the residence prem..."),
		ERROR_AAA_HO_SS3230162("AAA_HO_SS3230162", "More than 2 additional Insureds require Underwriting approval"),
		ERROR_AAA_HO_SS3230756("AAA_HO_SS3230756", "More than 2 additional Interests require Underwriting approval"),
		ERROR_AAA_PUP_SS3171100("AAA_PUP_SS3171100", "UW approval is required to bind the policy if any applicants or insureds are ..."),
		ERROR_AAA_PUP_SS3415672("AAA_PUP_SS3415672", "BI Limit should not be less than $500,000/500,000"),
		ERROR_AAA_PUP_SS4240323("AAA_PUP_SS4240323", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4240324("AAA_PUP_SS4240324", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4241939("AAA_PUP_SS4241939", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4240324_CA("AAA_PUP_SS4240324_CA", "BI Limits should not be less than $500,000/500,000."),
		ERROR_AAA_PUP_SS4290091("AAA_PUP_SS4290091", "PD Limits should not be less than $100,000."),
		ERROR_AAA_PUP_SS4220760("AAA_PUP_SS4220760", "'Year' is required"),
		ERROR_AAA_PUP_SS4221558("AAA_PUP_SS4221558", "'Model' is required"),
		ERROR_AAA_PUP_SS4223895("AAA_PUP_SS4223895", "'Make' is required"),
		ERROR_AAA_PUP_SS5071440("AAA_PUP_SS5071440", "Applicant with no underlying auto policy and endorsement PS 9811 is ineligibl..."),
		ERROR_AAA_PUP_SS5310180("AAA_PUP_SS5310180", "Applicants who own property, or reside for extended periods, outside of the U..."),
		ERROR_AAA_PUP_SS5310750("AAA_PUP_SS5310750", "Vehicles used for business, promotional or racing are ineligible."),
		ERROR_AAA_PUP_SS5311428("AAA_PUP_SS5311428", "Applicants who have been cancelled, refused insurance or non-renewed in the p..."),
		ERROR_AAA_PUP_SS7150344("AAA_PUP_SS7150344", "Applicants who have been cancelled, refused insurance or non-renewed in the p..."),
		ERROR_AAA_CSA3080819("AAA_CSA3080819", "Home policy is indicated but home policy # doesn't exist"),
		ERROR_AAA_CSA3080903("AAA_CSA3080903", "Condo policy is indicated but condo policy # doesn't exist"),
		ERROR_AAA_CSA3082394("AAA_CSA3082394", "Life policy is indicated but life policy # doesn't exist"),
		ERROR_AAA_CSA3083444("AAA_CSA3083444", "Renters policy is indicated but renters policy # doesn't exist"),
		ERROR_AAA_CSA3081512("AAA_CSA3081512", "Motorcycle policy is indicated but motorcycle policy # doesn't exist"),
		ERROR_AAA_CSA9231984("AAA_CSA9231984", "At least one phone number must be provided (AAA_CSA9231984) [for AAADocumentRulesComponent.attributeForRules]"),
		ERROR_AAA_CAC7150833("AAA_CAC7150833_CA_CHOICE", "Driver with more than 2 Major violations in the last 3 years is unacceptable ..."),

		// Auto Errors
		ERROR_AAA_SS171018("AAA_SS171018","Non-members are ineligible for coverage."),
		ERROR_AAA_SS171018_DE("AAA_SS171018_DE","Policies with unsuccessful membership validation results require prior approval."),
		ERROR_AAA_SS171018_NJ("AAA_SS171018_NJ","Policies with unsuccessful membership validation results require prior approval."),
		ERROR_AAA_SS171019("AAA_SS171019", "Policies being rated as having no prior insurance are ineligible for coverage"),

		//MEMBERSHIP Errors
		ERROR_AAA_AUTO_SS_MEM_LASTNAME("AAA_HO_SS_MEM_LASTNAME", "Membership Validation Failed. Please review the Membership Report and confirm..."),
		ERROR_AAA_HO_SS_MEM_LASTNAME("AAA_HO_SS_MEM_LASTNAME", "Membership Validation Failed. Please review the Membership Report and confirm..."),
		ERROR_AAA_HO_CA_MEM_LASTNAME("AAA_HO_SS_MEM_LASTNAME", "Membership Validation Failed. Please review the Membership Report and confirm..."),
		ERROR_AAA_AUTO_CA_MEM_LASTNAME("AAA_HO_SS_MEM_LASTNAME", "Membership Validation Failed. Please review the Membership Report and confirm..."),;
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

	public enum ErrorsColumn {
		CODE("Code"),
		SEVERITY("Severity"),
		MESSAGE("Message");

		String id;

		ErrorsColumn(String id) {
			this.id = id;
		}

		public String get() {
			return id;
		}
	}
}
