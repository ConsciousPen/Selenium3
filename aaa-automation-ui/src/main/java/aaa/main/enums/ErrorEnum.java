package aaa.main.enums;

public class ErrorEnum {

    public enum Errors {
        ERROR_200103("200103", "Driver with 3 or more Minor or Speeding violations are unacceptable"),
        ERROR_200401("200401", "Credit Adverse Impact requires Underwriting Review and approval"),
        ERROR_200060_CO("200060_CO", "If Medical Payments coverage is rejected, a signed form must be received"),
        //Property errors

        ERROR_AAA_HO_CA_15011_1("AAA_HO_CA_15011_1", "Dwellings built prior to 1940 must have all four major systems fully renovated."),
        ERROR_AAA_HO_CACovAReplacementCost("AAA_HO_CACovAReplacementCost", "Coverage A greater than 120% of replacement cost requires underwriting approval."),
        ERROR_AAA_HO_CA12190315("AAA_HO_CA12190315", "Applicants with any paid claims over $25,000 in the last 3 years are ineligible."),
        ERROR_AAA_HO_CA1219040y("AAA_HO_CA1219040y", "Applicants with any liability claims in the past 3 years are ineligible."),
        ERROR_AAA_HO_CA12240000("AAA_HO_CA12240000", "Policy is ineligible as combined total amount of all the detached structures ..."),
        ERROR_AAA_HO_CA12240080("AAA_HO_CA12240080", "A detached structure greater than 50% of Coverage A is ineligible."),
        ERROR_AAA_HO_CA12260015("AAA_HO_CA12260015", "Dwellings with more than 2 detached building structures rented to others on t..."),
        ERROR_AAA_HO_CA12261856("AAA_HO_CA12261856", "More than 2 additional Interests require Underwriting approval"),
        ERROR_AAA_HO_CA3230672("AAA_HO_CA3230672", "Policy effective date cannot be backdated more than three days from today's d..."),
        ERROR_AAA_HO_CA7220104("AAA_HO_CA7220104", "Dwellings built prior to 1900 are ineligible."),
        ERROR_AAA_HO_CA7220432("AAA_HO_CA7220432", "Dwellings with more than 2 roof layers are ineligible."),
        ERROR_AAA_HO_CA7220704("AAA_HO_CA7220704", "Maximum total number of livestock is 100."),
        ERROR_AAA_HO_CA7231530("AAA_HO_CA7231530", "More than 2 additional Insureds require Underwriting approval"),
        ERROR_AAA_HO_CA9230000("AAA_HO_CA9230000", "Dwellings with more than 3 detached building structures on the residence prem..."),

        ERROR_AAA_HO_SS10060735("AAA_HO_SS10060735", "Underwriter approval is required when Adversely Impacted is selected."),
        ERROR_AAA_HO_SS624530_CO("AAA_HO_SS624530_CO", "Dwellings that have not had the roof replaced within the past 25 years if com..."),
        ERROR_AAA_HO_SS10030560("AAA_HO_SS10030560", "Dwellings with a wood shake/shingle roof are unacceptable."),
        ERROR_AAA_HO_SS10030001("AAA_HO_SS10030001", "Dwellings with a T-Lock shingle roof are unacceptable."),
        ERROR_AAA_HO_SS7230342("AAA_HO_SS7230342", "Underwriting approval is required for the option you have selected."),
        ERROR_AAA_HO_SS4260842("AAA_HO_SS4260842", "Wind/hail endorsement is required when roof type is wood shingle/wood shake."),
        ERROR_AAA_HO_SS14061993("AAA_HO_SS14061993", "	Dwellings with a Zip Code Level Match returned for Fireline require further u..."),
        ERROR_AAA_HO_SS3150198("AAA_HO_SS3150198", "Risk must be endorsed with the appropriate business or farming endorsement wh..."),
        ERROR_AAA_HO_SS3151364("AAA_HO_SS3151364", "Business or farming activity is ineligible. Dwellings or applicants that perf..."),
        ERROR_AAA_HO_SS1020340_OR("AAA_HO_SS1020340_OR", "Applicants with more than 1 paid non-CAT claim and/or more than 1 paid CAT cl..."),
        ERROR_AAA_HO_SS1050670_OR("AAA_HO_SS1050670_OR", "Applicants with any paid non-CAT claim and/or more than 1 paid CAT claim in t..."),
        ERROR_AAA_HO_SS12023000("AAA_HO_SS12023000", "Applicants with any liability claims in the past 3 years are ineligible."),
        ERROR_AAA_HO_SS12200234("AAA_HO_SS12200234", "Applicants with any paid claims over $25,000 in the last 3 years are ineligible."), 
        ERROR_AAA_HO_SS7160042("AAA_HO_SS7160042", "Applicants who have been cancelled, refused insurance or non-renewed in the p..."),

        ERROR_AAA_PUP_SS3171100("AAA_PUP_SS3171100", "UW approval is required to bind the policy if any applicants or insureds are ..."),
        ERROR_AAA_PUP_SS5310180("AAA_PUP_SS5310180", "Applicants who own property, or reside for extended periods, outside of the U..."),
        ERROR_AAA_PUP_SS5310750("AAA_PUP_SS5310750", "Vehicles used for business, promotional or racing are ineligible."),
        ERROR_AAA_PUP_SS5311428("AAA_PUP_SS5311428", "Applicants who have been cancelled, refused insurance or non-renewed in the p..."),

        ERROR_AAA_CSA3080819("AAA_CSA3080819", "Home policy is indicated but home policy # doesn't exist"),
        ERROR_AAA_CSA3080903("AAA_CSA3080903", "Condo policy is indicated but condo policy # doesn't exist"),
        ERROR_AAA_CSA3082394("AAA_CSA3082394", "Life policy is indicated but life policy # doesn't exist"),
        ERROR_AAA_CSA3083444("AAA_CSA3083444", "Renters policy is indicated but renters policy # doesn't exist");

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
