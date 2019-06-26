package aaa.main.enums;

public final class ErrorEnum {
	private ErrorEnum() {
	}

	public enum Errors {

		ERROR_POLICY_NOT_RATED("Unprepared data", "Cannot issue policy which was not rated!"),
		MPD_COMPANION_VALIDATION("MPD_COMPANION_VALIDATION", "Companion policy used for Multi-Policy Discount is not system validated."),
		ERROR_200127("200127", "The selected pay plan is not allowed when Membership is \"\"No\"\". Please choose a plan with a minimum down payment of 50% or greater. Do not select 'refer for approval.' Any exceptions to this rule must be discussed directly with Service. (200127) [for ExistingPolicies.existingMembershipCd]"),
		ERROR_200103("200103", "Driver with 3 or more Minor or Speeding violations are unacceptable"),
		ERROR_200104("200104", "Driver with 2 or more At-fault accidents are unacceptable (200104) [for Drive"),
		ERROR_200123("200123", "Delaware Motorists Protection Act form must be received prior to issuing this transaction"),
		ERROR_200037_VA("200037_VA", "A signed IMPORTANT NOTICE - UNINSURED MOTORIST COVERAGE form must be received prior to issuing this transaction"),
		ERROR_200200_NJ("200200_NJ", "A signed \"Acknowledgement of Carco Inspection Requirements\" must be received prior to completing this transaction. (200200)"),
		ERROR_200204_NJ("200204_NJ", "Inspection Waiver - Sales Agreement Required must be received prior to issuing this transaction. (200204)"),
		ERROR_200304("200304", "If Limited Tort Liability coverage is selected, a signed form must be receive"),
		ERROR_200305("200305", "First Party Benefits signed form must be received (200305)"),
		ERROR_200306("200306", "A signed Uninsured motorist coverage selection form must be received prior to issuing this transaction (200306)"),
		ERROR_200401("200401", "Credit Adverse Impact requires Underwriting Review and approval"),
		ERROR_200900("200900", "A signed Coverage Selection/Rejection Form must be received prior to issuing this transaction (200900)"),
		ERROR_200060_CO("200060_CO", "If Medical Payments coverage is rejected, a signed form must be received"),
		ERROR_AAA_SS9140068("AAA_SS9140068", "It is too late in the term to change to the selected bill plan."),
		ERROR_AAA_CLUE_order_validation_SS("AAA_CLUE_order_validation_SS", "Current CLUE must be ordered"),
		ERROR_AAA_SS10240324("AAA_SS10240324", "At least one phone number must be provided."),
		ERROR_AAA_SS190125("AAA_SS190125", "Pennsylvania Notice to Named Insureds Regarding Tort Options must be received prior to issuing this transaction."),
		ERROR_200115_NY("200115_NY", "Supplementary Uninsured/Underinsured Coverage Waiver must be signed."),


		// Property errors
		ERROR_AAA_HO_Fireline("AAA_HO_Fireline","FireLine score is ineligible. (AAA_HO_Fireline) [for AAAFirelineDetailsMVO.fi" ),
		ERROR_AAA_HO_Fireline_CA02122017("Fireline_CA02122017","FireLine score is ineligible. (Fireline_CA02122017) [for AAAHOCADetermineElig"),
		ERROR_AAA_HO_CA10107077("AAA_HO_CA10107077", "Dwellings with a Zip Code Level Match returned for Fireline are ineligible"),
		ERROR_AAA_HO_SS3054048("AAA_HO_SS3054048", "Order CLUE report before binding. (AAA_HO_SS3054048) [for AAAHOOrderClueRepor"),
		ERROR_AAA_HO_CA20180518("AAA_HO_CA20180518", "Signed FAIR Plan Companion Endorsement - California is required. (AAA_HO_CA20"),
		ERROR_AAA_HO_CA20180517("AAA_HO_CA20180517", "Signed FAIR Plan Companion Endorsement - California is required. (AAA_HO_CA20"),
		ERROR_AAA_HO_CA02122017("AAA_HO_CA02122017", "Dwellings located in PPC 10 are ineligible."),
		ERROR_AAA_HO_CA10100616("AAA_HO_CA10100616", "Log homes must be in protection class 1-8 and assembled by a licensed buildin"),
		ERROR_AAA_HO_CSA25636985("AAA_CSA25636985", "Policy cannot be bound with Current AAA Member as Pending"),
		ERROR_AAA_HO_CA_15011_1("AAA_HO_CA_15011_1", "Dwellings built prior to 1940 must have all four major systems fully renovated."),
		ERROR_AAA_HO_CACovAReplacementCost("AAA_HO_CACovAReplacementCost", "Coverage A greater than 120% of replacement cost requires underwriting approval."),
		ERROR_AAA_HO_CA1210012("AAA_HO_CA1210012", "Underwriting approval is required for claim(s) that have been modified (AAA_H"),
		ERROR_AAA_HO_CA12190315("AAA_HO_CA12190315", "Applicants with any paid claims over $25,000 in the last 3 years are ineligible."),
		ERROR_AAA_HO_CA1219040y("AAA_HO_CA1219040y", "Applicants with any liability claims in the past 3 years are ineligible."),
		ERROR_AAA_HO_CA12230792("AAA_HO_CA12230792", "Risks with more than 2 resident employees are ineligible."),
		ERROR_AAA_HO_CA12240000("AAA_HO_CA12240000", "Policy is ineligible as combined total amount of all the detached structures "),
		ERROR_AAA_HO_CA12240080("AAA_HO_CA12240080", "A detached structure greater than 50% of Coverage A is ineligible."),
		ERROR_AAA_HO_CA12260015("AAA_HO_CA12260015", "Dwellings with more than 2 detached building structures rented to others on t"),
		ERROR_AAA_HO_CA12261856("AAA_HO_CA12261856", "More than 2 additional Interests require Underwriting approval"),
		ERROR_AAA_HO_CA1302295("AAA_HO_CA1302295", "Dwellings with FireLine score greater than 2 with wood shingle/wood shake roo"),
		ERROR_AAA_HO_CA3230672("AAA_HO_CA3230672", "Policy effective date cannot be backdated more than three days from today's d"),
		ERROR_AAA_HO_CA338657_3("AAA_HO_CA338657_3", "Dwellings or applicants that perform a home day care, including child day car"),
		ERROR_AAA_HO_CA338657_6("AAA_HO_CA338657_6", "Farming/Ranching on premises is unacceptable unless it is incidental and not "),
		ERROR_AAA_HO_CA338657_7("AAA_HO_CA338657_7", "Other business exposures on premises are unacceptable."),
		ERROR_AAA_HO_CA338657_17("AAA_HO_CA338657_17", "Policies must be endorsed with the HO 04 42 10 00 Permitted Incidental Occupa"),
		ERROR_AAA_HO_CA338657_18("AAA_HO_CA338657_18", "Applicants who have been cancelled, refused insurance or non-renewed in the past 3 years are unacceptable unless approved by underwriting."),
		ERROR_AAA_HO_CA338657_20("AAA_HO_CA338657_20", "Dwelling must not have been in foreclosure within the past 18 months unless approved by underwriting."),
		ERROR_AAA_HO_CA338657_23("AAA_HO_CA338657_23", "Applicants/insureds with any dogs or other animals, reptiles, or pets with an"),
		ERROR_AAA_HO_CA338657_28("AAA_HO_CA338657_28", "Water heaters (except electric heaters) must be strapped to the wall or if lo"),
		ERROR_AAA_HO_CA338657_36("AAA_HO_CA338657_36", "Risks located within 500 feet of bay or coastal water is ineligible."),
		ERROR_AAA_HO_CA7150360 ("AAA_HO_CA7150360", "Order PPC report before quote."),
		ERROR_AAA_HO_CA7220104("AAA_HO_CA7220104", "Dwellings built prior to 1900 are ineligible."),
		ERROR_AAA_HO_CA7220432("AAA_HO_CA7220432", "Dwellings with more than 2 roof layers are ineligible."),
		ERROR_AAA_HO_CA7220704("AAA_HO_CA7220704", "Maximum total number of livestock is 100."),
		ERROR_AAA_HO_CA7231530("AAA_HO_CA7231530", "More than 2 additional Insureds require Underwriting approval"),
		ERROR_AAA_HO_CA9230000("AAA_HO_CA9230000", "Dwellings with more than 3 detached building structures on the residence prem"),
		ERROR_AAA_HO_SS6260765("AAA_HO_SS6260765", "Cannot bind policy without underwriter approval. Risks without valid riskmete"),
		ERROR_AAA_HO_SS3230000("AAA_HO_SS3230000", "Policy effective date cannot be backdated more than three days from today's d"),
		ERROR_AAA_HO_SS10060735("AAA_HO_SS10060735", "Underwriter approval is required when Adversely Impacted is selected."),
		ERROR_AAA_HO_SS1210011("AAA_HO_SS1210011", "Underwriting approval is required for claim(s) that have been modified (AAA_H"),
		ERROR_AAA_HO_SS624530_CO("AAA_HO_SS624530_CO", "Dwellings that have not had the roof replaced within the past 25 years if com"),
		ERROR_AAA_HO_SS10030560("AAA_HO_SS10030560", "Dwellings with a wood shake/shingle roof are unacceptable."),
		ERROR_AAA_HO_SS10030001("AAA_HO_SS10030001", "Dwellings with a T-Lock shingle roof are unacceptable."),
		ERROR_AAA_HO_SS2240042("AAA_HO_SS2240042", "Dwellings with FireLine score greater than 2 with wood shingle/wood shake roo"),
		ERROR_AAA_HO_SS7230342("AAA_HO_SS7230342", "Underwriting approval is required for the option you have selected."),
		ERROR_AAA_HO_SS4260842("AAA_HO_SS4260842", "Wind/hail endorsement is required when roof type is wood shingle/wood shake."),
		ERROR_AAA_HO_SS14061993("AAA_HO_SS14061993", "	Dwellings with a Zip Code Level Match returned for Fireline require further u"),
		ERROR_AAA_HO_SS3150198("AAA_HO_SS3150198", "Risk must be endorsed with the appropriate business or farming endorsement wh"),
		ERROR_AAA_HO_SS3151364("AAA_HO_SS3151364", "Business or farming activity is ineligible. Dwellings or applicants that perf"),
		ERROR_AAA_HO_SS1020340_OR("AAA_HO_SS1020340_OR", "Applicants with more than 1 paid non-CAT claim and/or more than 1 paid CAT cl"),
		ERROR_AAA_HO_SS1050670_OR("AAA_HO_SS1050670_OR", "Applicants with any paid non-CAT claim and/or more than 1 paid CAT claim in t"),
		ERROR_AAA_HO_SS12023000("AAA_HO_SS12023000", "Applicants with any liability claims in the past 3 years are ineligible."),
		ERROR_WM_0523("WM-0523", "Applicants with 2 or more paid non-CAT claims OR 2 or more paid CAT claims in"),
		ERROR_AAA_HO_SS4040546("AAA_HO_SS4040546", "Applicants with 2 or more paid non-CAT claims OR 2 or more paid CAT claims in"),
		ERROR_WM_0523_SD("WM-0523_SD", "Applicants with more than one paid non-weather claim and/or more than two pai"),
		ERROR_AAA_HO_SS12200234("AAA_HO_SS12200234", "Applicants with any paid claims over $25,000 in the last 3 years are ineligible."),
		ERROR_AAA_HO_SS7160042("AAA_HO_SS7160042", "Applicants who have been cancelled, refused insurance or non-renewed in the p"),
		ERROR_AAA_HO_SS11120040("AAA_HO_SS11120040", "Dwellings built prior to 1900 are ineligible."),
		ERROR_AAA_HO_SS3282256("AAA_HO_SS3282256", "Dwellings built prior to 1940 must have all four major systems fully renovated."),
		ERROR_AAA_HO_SS2103528("AAA_HO_SS2103528", "Dwellings built prior to 1940 must have all four major systems fully renovated."),
		ERROR_AAA_HO_SS1100204("AAA_HO_SS1100204", "Proof of plumbing, electrical, heating/cooling system and roof renovations is"),
		ERROR_AAA_HO_SS3200008("AAA_HO_SS3200008", "Risks with more than 3 horses or 4 livestock are unacceptable."),
		ERROR_AAA_HO_SS3195184("AAA_HO_SS3195184", "Risks with more than 3 horses or 4 livestock are unacceptable."),
		ERROR_AAA_HO_SS12141800("AAA_HO_SS12141800", "Underwriting approval required. Primary home of the applicant is not insured "),
		ERROR_AAA_HO_SS1160000("AAA_HO_SS1160000", "Coverage A greater than 120% of replacement cost requires underwriting approval."),
		ERROR_AAA_HO_SS1162304("AAA_HO_SS1162304", "Coverage A greater than $1,000,000 requires underwriting approval."),
		ERROR_AAA_HO_SS1162304_MD("AAA_HO_SS1162304_MD", "Coverage A greater than $2,000,000 requires underwriting approval."),
		ERROR_AAA_HO_SS4250648("AAA_HO_SS4250648", "Coverage B must be less than 50% of Coverage A to bind."),
		ERROR_AAA_HO_SS3281224("AAA_HO_SS3281224", "Coverage B cannot exceed Coverage A."),
		ERROR_AAA_HO_SS3280000_1("AAA_HO_SS3280000_1", "Dwellings with more than 2 detached building structures rented to others on t"),
		ERROR_AAA_HO_SS3281092("AAA_HO_SS3281092", "Dwellings with more than 3 detached building structures on the residence prem"),
		ERROR_AAA_HO_SS3230162("AAA_HO_SS3230162", "More than 2 additional Insureds require Underwriting approval"),
		ERROR_AAA_HO_SS3230756("AAA_HO_SS3230756", "More than 2 additional Interests require Underwriting approval"),
		ERROR_AAA_HO_SS3282281("AAA_HO_SS3282281", "Limit of any one Scheduled Personal Property item cannot exceed the max limit for that class"),
		ERROR_AAA_HO_SS4230168("AAA_HO_SS4230168", "Limit of any one Scheduled Personal Property class cannot exceed 25% of Coverage C"),
		ERROR_AAA_HO_SS4230108("AAA_HO_SS4230108", "Total limit of Scheduled Personal Property cannot exceed 50% of Coverage C"),
		ERROR_AAA_HO_UWApp_HS0988_endorsement("AAA_HO_UWApp_HS0988_endorsement", "Underwriting approval is required to add Additional Insured - Special Event e"),
		ERROR_AAA_HO_SS67cbad46("AAA_HO_SS67cbad46", "All named insureds must have an insurance score ordered."),
		ERROR_AAA_HO_SS11200000("(AAA_HO_SS11200000", "'Mortgagee Clause' is required (AAA_HO_SS11200000) [for AAAHOMortgageeInfo.legalMortgageeName]"),
		ERROR_AAA_HO_SS12141500("AAA_HO_SS12141500", "'Loan number' is required (AAA_HO_SS12141500) [for AAAHOMortgageeInfo.account"),
		ERROR_AAA_HO_SS4030518("AAA_HO_SS4030518", "Legal mortgagee name is required for bind. (AAA_HO_SS4030518) [for AAAHOMortg"),
		ERROR_AAA_PUP_SS1263335("AAA_PUP_SS1263335", "Applicants with any liability claims in the past 3 years are ineligible."),
		ERROR_AAA_PUP_SS2260177("AAA_PUP_SS2260177", "Risk is ineligible because applicant does not have a valid license."),
		ERROR_AAA_PUP_SS2220189("AAA_PUP_SS2220189", "Risks with a property liability limit less than $300,000 are ineligible."),
		ERROR_AAA_PUP_SS2220190("AAA_PUP_SS2220190", "Risks with a property liability limit less than $500,000 are ineligible."),
		ERROR_AAA_PUP_SS3171100("AAA_PUP_SS3171100", "UW approval is required to bind the policy if any applicants or insureds are "),
		ERROR_AAA_PUP_SS3415671("AAA_PUP_SS3415671", "BI Limit should not be less than $500,000/500,000"),
		ERROR_AAA_PUP_SS3415672("AAA_PUP_SS3415672", "BI Limit should not be less than $500,000/500,000"),
		ERROR_AAA_PUP_SS4240323("AAA_PUP_SS4240323", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4240324("AAA_PUP_SS4240324", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4241938("AAA_PUP_SS4241938", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4241939("AAA_PUP_SS4241939", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4243800("AAA_PUP_SS4243800", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4243801("AAA_PUP_SS4243801", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4240323_CA("AAA_PUP_SS4240323_CA", "BI Limits should not be less than $500,000/500,000."),
		ERROR_AAA_PUP_SS4240324_CA("AAA_PUP_SS4240324_CA", "BI Limits should not be less than $500,000/500,000."),
		ERROR_AAA_PUP_SS4241938_CA("AAA_PUP_SS4241938_CA", "BI Limits should not be less than $500,000/500,000."),
		ERROR_AAA_PUP_SS4241939_CA("AAA_PUP_SS4241939_CA", "BI Limits should not be less than $500,000/500,000."),
		ERROR_AAA_PUP_SS4290091("AAA_PUP_SS4290091", "PD Limits should not be less than $100,000."),
		ERROR_AAA_PUP_SS4244182("AAA_PUP_SS4244182", "PD Limits should not be less than $100,000."),
		ERROR_AAA_PUP_SS4243200("AAA_PUP_SS4243200", "PD Limits should not be less than $100,000."),
		ERROR_AAA_PUP_SS4220760("AAA_PUP_SS4220760", "'Year' is required"),
		ERROR_AAA_PUP_SS4221558("AAA_PUP_SS4221558", "'Model' is required"),
		ERROR_AAA_PUP_SS4223895("AAA_PUP_SS4223895", "'Make' is required"),
		ERROR_AAA_PUP_SS4242760("AAA_PUP_SS4242760", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4242761("AAA_PUP_SS4242761", "BI Limits should not be less than $250,000/500,000."),
		ERROR_AAA_PUP_SS4242880("AAA_PUP_SS4242880", "PD Limits should not be less than $100,000."),
		ERROR_AAA_PUP_SS4245880("AAA_PUP_SS4245880", "Combined Single Limit should not be less than $500,000."),
		ERROR_AAA_PUP_SS4300780_AE("AAA_PUP_SS4300780_AE", "Drivers with more than 1 driving violation and/or at-fault accident in the pr"),
		ERROR_AAA_PUP_SS5071440("AAA_PUP_SS5071440", "Applicant with no underlying auto policy and endorsement PS 9811 is ineligible."),
		ERROR_AAA_PUP_SS5310180("AAA_PUP_SS5310180", "Applicants who own property, or reside for extended periods, outside of the U"),
		ERROR_AAA_PUP_SS5310750("AAA_PUP_SS5310750", "Vehicles used for business, promotional or racing are ineligible."),
		ERROR_AAA_PUP_SS5311428("AAA_PUP_SS5311428", "Applicants who have been cancelled, refused insurance or non-renewed in the p"),
		ERROR_AAA_PUP_SS7050000("AAA_PUP_SS7050000", "A high level premium variance has been detected between the legacy premium an"),
		ERROR_AAA_PUP_SS7150344("AAA_PUP_SS7150344", "Applicants who have been cancelled, refused insurance or non-renewed in the p"),
		ERROR_AAA_PUP_SS7160072("AAA_PUP_SS7160072", "Trustees/LLCs on underlying risks are ineligible."),
		ERROR_AAA_PUP_SS7160090("AAA_PUP_SS7160090", "Risks with a property liability limit less than $500,000 are ineligible."),
		ERROR_AAA_PUP_SS7121080("AAA_PUP_SS7121080", "Underwriting approval required for Liability Coverage limits greater than $1,"),
		ERROR_AAA_PUP_SS7121080_CA("AAA_PUP_SS7121080_CA", "Underwriting approval required for Liability Coverage limits greater than $2,"),
		ERROR_AAA_PUP_SSER10054("AAA_PUP_SSER10054", "Applicants/drivers with an alcohol-related or major violation in the last 5 years"),
		AAA_PUP_UWApp_AddRes_OtherThanPrimary("AAA_PUP_UWApp_AddRes_OtherThanPrimary", "Applicants with 6 or more additional residences are ineligible."),
		AAA_PUP_UWApp_AddRes_Rental("AAA_PUP_UWApp_AddRes_Rental", "Applicants with 7 or more rental residences are ineligible."),
		AAA_PUP_UWApp_Auto_Antique("AAA_PUP_UWApp_Auto_Antique", "Applicants with 5 or more antique vehicles are ineligible."),
		AAA_PUP_UWApp_Auto_PPA("AAA_PUP_UWApp_Auto_PPA", "Applicants with 9 or more automobiles are ineligible."),
		AAA_PUP_UWApp_Motorcycle("AAA_PUP_UWApp_Motorcycle", "Applicants with 5 or more motorcycles are ineligible."),
		AAA_PUP_UWApp_RecreationalVeh("AAA_PUP_UWApp_RecreationalVeh", "Applicants with 5 or more recreational vehicles are ineligible."),
		AAA_PUP_UWApp_RecreationalVeh_ATV("AAA_PUP_UWApp_RecreationalVeh_ATV", "Applicants with 5 or more ATVs are ineligible."),
		AAA_PUP_UWApp_RecreationalVeh_GC("AAA_PUP_UWApp_RecreationalVeh_GC", "Applicants with 3 or more golf carts are ineligible."),
		AAA_PUP_UWApp_RecreationalVeh_Snowmobile("AAA_PUP_UWApp_RecreationalVeh_Snowmobile", "Applicants with 5 or more snowmobiles are ineligible."),
		AAA_PUP_UWApp_Watercraft("AAA_PUP_UWApp_Watercraft", "Applicants with 6 or more watercraft are ineligible."),
		ERROR_AAA_CSA3080819("AAA_CSA3080819", "Home policy is indicated but home policy # doesn't exist"),
		ERROR_AAA_CSA3080903("AAA_CSA3080903", "Condo policy is indicated but condo policy # doesn't exist"),
		ERROR_AAA_CSA3082394("AAA_CSA3082394", "Life policy is indicated but life policy # doesn't exist"),
		ERROR_AAA_CSA3083444("AAA_CSA3083444", "Renters policy is indicated but renters policy # doesn't exist"),
		ERROR_AAA_CSA3081512("AAA_CSA3081512", "Motorcycle policy is indicated but motorcycle policy # doesn't exist"),
		ERROR_AAA_CSA9231984("AAA_CSA9231984", "At least one phone number must be provided (AAA_CSA9231984) [for AAADocumentRulesComponent.attributeForRules]"),
		ERROR_AAA_CAC7150833("AAA_CAC7150833_CA_CHOICE", "Driver with more than 2 Major violations in the last 3 years is unacceptable"),
		ERROR_AAA_CSACN0100("AAA_CSACN0100", "Current Term Policy was Reinstated Late (AAA_CSACN0100) [for Policy.effective]"),
		// Message needs to be adjusted for individual driver with first name and last name
		ERROR_AAA_CSA190521("AAA_CSA190521", "One or more drivers have an unacceptable MVR license status (AAA_CSA190521) [for AAADARTabBindRules.attributeForRules]"),
		ERROR_AAA_CA_HOCN0400("AAA_CA_HOCN0400", "A high level premium variance has been detected between the legacy renewal pr"),
		ERROR_AAA_HO_SS_MAIG_IS_REPORT("AAA_HO_SS_MAIG_IS_REPORT", "A valid insurance score report for at least one named insured or insurance sc"),
		ERROR_AAA_HO_CA7230928("AAA_HO_CA7230928", "'Loan number' is required (AAA_HO_CA7230928) [for AAAHOMortgageeInfo.accountN"),
		ERROR_AAA_HO_AAA_HO_CA723000018("AAA_HO_CA723000018", "Legal mortgagee name is required for bind. (AAA_HO_CA723000018) [for AAAHOMor"),

		// Auto Errors
		ERROR_AAA_200307("200307", "A signed Underinsured motorist coverage selection form must be received prior to issuing this transaction (200307)"),
		ERROR_AAA_10022001("10022001", "Base year on rewrite must be current year (10022001) [for Policy.policyNumber]"),
		ERROR_AAA_200005("200005", "Driver with a narcotics, drug or felony conviction involving a motor vehicle "),
		ERROR_AAA_200008("200008", "Each driver must have a unique Driver's License Number."),
		ERROR_AAA_200009("200009", "Driver with a Major violation, including a DUI is unacceptable (200009) [for "),
		ERROR_AAA__200009_PA("200009_PA", "Driver with a Major violation, including a DUI is unacceptable (200009) [for "),
		ERROR_AAA_200011("200011","Requested Effective Date not Available (200011) [for Policy.effective]"),
		ERROR_AAA_200034("200034_OR", "A signed Named Driver Exclusion Endorsement must be received prior to issuing"),
		ERROR_AAA_200037("200037", "A signed Uninsured And Underinsured Motorist Coverage Selection Form must be"),
		ERROR_AAA_200037_CO("200037_CO", "A signed Uninsured and Underinsured motorist coverage selection form must be received prior to issuing this transaction"),
		ERROR_AAA_200111("200111_OR", "Named Driver Exclusion requires Underwriting Approval"),
		ERROR_AAA_SS171018("AAA_SS171018", "Non-members are ineligible for coverage."),
		ERROR_AAA_SS171018_DE("AAA_SS171018_DE", "Policies with unsuccessful membership validation results require prior approval."),
		ERROR_AAA_SS171018_NJ("AAA_SS171018_NJ", "Policies with unsuccessful membership validation results require prior approval."),
		ERROR_AAA_SS171019("AAA_SS171019", "Policies being rated as having no prior insurance are ineligible for coverage"),
		ERROR_AAA_SS41800881_MD("AAA_SS41800881_MD", "EUMPD limits should match the PD limit"),
		ERROR_AAA_SS41800882_MD("AAA_SS41800882_MD", "EUMBI limits should match the BI limit."),
		ERROR_AAA_CSA1801266BZWW("AAA_CSA180126-6BzwW", "You must recalculate premium. Return to the Premium and Coverages tab to comp"),
		ERROR_AAA_SS1801266BZWW("AAA_SS180126-6BzwW", "You must recalculate premium. Return to the Premium and Coverages tab to comp"),
		ERROR_AAA_MES_PC_0017_CA_CHOICE("MES-PC-0017_CA_CHOICE", "Rating variables stat code or Value($) is required for rating (MES-PC-0017) ["),
		ERROR_AAA_MVR_order_validation_SS("AAA_MVR_order_validation_SS", "Current MVR for "),
		ERROR_AAA_200306("200306", "A signed Uninsured motorist coverage selection form must be received prior to"),
		ERROR_AAA_200205("200205", "Vehicles with Physical Damage Coverage requires a CARCO Inspection"),
		ERROR_AAA_200200_NY("200200_NY", "Vehicles with Physical Damage Coverage require a CARCO Inspection or Proof of New Vehicle (200200)"),
		ERROR_AAA_200203("200203", "If Uninsured/Underinsured Motorist (UM/UIM) Coverage is rejected. A signed Un"),
		ERROR_AAA_10006002_CA("10006002", "Do not bind the endorsement more than 30 days in advance. (10006002) [for Pol"),
		ERROR_AAA_validate_pu_clue_claim_2("AAA_validate_pu_clue_claim_2", "Confirm CLUE activity is a permissive use claim based on a CLUE report or oth"),
		ERROR_AAA_10015021_CA_SELECT("10015021", "Driver with more than 1 At-fault injury accident is unacceptable (10015021) [for Driver.attributeForRules]"),
		ERROR_AAA_10004001_CA("10004001", "Driver must possess a valid license status (10004001) [for Driver.attributeFo" ),
		ERROR_AAA_CAC7100525_CA_CHOICE("AAA_CAC7100525_CA_CHOICE","Any Driver on the policy with an out-of-state U.S., Canadian, or Foreign lice"),
		ERROR_AAA_200108_1_CA_SELECT("200108_1", "Driver with a Foreign or International License is unacceptable (200108_1) [fo"),
		AAA_SS02012019("AAA_SS02012019", "Policy cannot be bound with an unbound companion policy."),
        ERROR_AAA_10015015_CA_SELECT("10015015", "Driver with a combination of at least one Minor violation AND at least one At-fault accident within last 36 months is unacceptable. (10015015) [for Driver.attributeForRules]"),
        ERROR_AAA_10015023_CA_SELECT_CHOICE("10015023", "Driver must not have more than 2 At-fault accidents in the past 3 years (10015023) [for Driver.attributeForRules]"),
		ERROR_AAA_SS190605("AAA_SS190605", "Driver Name LName has a blank license number (AAA_SS190605) [for Driver.attributeForRules]"),
		ERROR_AAA_CAC7161836_CA_CHOICE("AAA_CAC7161836_CA_CHOICE", "A driver with more than 26 Violation points is unacceptable (AAA_CAC7161836) [for Driver.attributeForRules]"),

		//MEMBERSHIP Errors
		ERROR_AAA_AUTO_SS_MEM_LASTNAME("AAA_HO_SS_MEM_LASTNAME", "Membership Validation Failed. Please review the Membership Report and confirm Member details."),
		ERROR_AAA_HO_SS_MEM_LASTNAME("AAA_HO_SS_MEM_LASTNAME", "Membership Validation Failed. Please review the Membership Report and confirm Member details."),
		ERROR_AAA_HO_CA_MEM_LASTNAME("AAA_HO_SS_MEM_LASTNAME", "Membership Validation Failed. Please review the Membership Report and confirm Member details."),
		ERROR_AAA_AUTO_CA_MEM_LASTNAME("AAA_AUTO_CA_MEM_LASTNAME", "Membership Validation Failed. Please review the Membership Report and confirm Member details."),
		ERROR_AAA_MES_IRE_06("MES-IRE-06", "Too early to rate a policy in PAS."),
		ERROR_AAA_MES_IRE_07("MES-IRE-07", "Too late to rate a policy in PAS."),
		ERROR_AAA_MES_IRE_08("MES-IRE-08", "already created for previous policy.");
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
