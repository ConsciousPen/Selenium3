package aaa.main.enums;

import org.apache.commons.lang.StringUtils;

public final class DocGenEnum {
	private DocGenEnum() {
	}

	public enum Documents {
		AA02AZ("Declaration page"),
		AA02CA("Declaration page"),
		AA02CO(""),
		AA02CT("Declaration page"),
		AA02DC(""),
		AA02IN("Declaration page"),
		AA02KS("Declaration page"),
		AA02NY("Personal Auto Policy Declarations"),
		AA02OH("Declaration page"),
		AA02OK("Declaration page"),
		AA02OR("Declaration page"),
		AA02SD("Declaration page"),
		AA02PA("Declaration page"),
		AA02VA("Declaration page"),
		AA02WV("Declaration page"),
		AA06XX("Agent Advice Memo"),
		AA06XX_AUTOSS("AA06XX", "Agent Advice Memo"),
		AA09CA(""),
		AA10OK(""),
		AA10PA("Insurance Identification Card"),
		AA10XX("Insurance Identification Card"),
		AA11AZ("Auto Insurance Application"),
		AA11CA("California Choice Auto Insurance Application"),
		AA11CO("Colorado Auto Insurance Application"),
		AA11CT("Auto Insurance Application"),
		AA11DC("Auto Insurance Application"),
		AA11DE("Delaware Auto Insurance Application"),
		AA11ID("Auto Insurance Application"),
		AA11IN("Indiana Auto Insurance Application"),
		AA11KS("Auto Insurance Application"),
		AA11KY("Auto Insurance Application"),
		AA11MD("Auto Insurance Application"),
		AA11MT("Auto Insurance Application"),
		AA11NJ("Auto Insurance Application"),
		AA11NV("Auto Insurance Application"),
		AA11NY("New York Auto Insurance Application"),
		AA11OH("Auto Insurance Application"),
		AA11OK("Auto Insurance Application"),
		AA11OR("Auto Insurance Application"),
		AA11PA("Auto Insurance Application"),
		AA11SD("Auto Insurance Application"),
		AA11UT("Auto Insurance Application"),
		AA11VA("Virginia Auto Insurance Application"),
		AA11WV("Auto Insurance Application"),
		AA11WY("Wyoming Auto Insurance Application"),
		AA16CO("MEDICAL PAYMENTS REJECTION OF COVERAGE"),
		AA16NV("Rejection of Medical Payments Coverage"),
		AA34CTA("Cancellation Warning Notice"),
		AA41CA("Non-Owner Automobile Endorsement"),
		AA41PA(""),
		AA41XX("Non-Owners Endorsement"),
		AA43AZ("Named Driver Exclusion Election"),
		AA43CA("Named Driver(s) Exclusion"),
		AA43CAB(""),
		AA43CO("Named Driver Exclusion Endorsement"),
		AA43CT("Named Driver Exclusion Endorsement"),
		AA43IN("Named Driver Exclusion Election"),
		AA43OH("Named Driver Exclusion Election"),
		AA43OK("Named Driver Exclusion Election"),
		AA43OR("Named Driver Exclusion Election"),
		AA43PA("Named Driver Exclusion Election"),
		AA43SD("Named Driver Exclusion Election"),
		AA43WV("Named Driver Exclusion Endorsement"),
		AA47CA(""),
		AA49CA(""),
		AA52AZ("Uninsured and Underinsured Motorist Coverage selection form"),
		AA52AZ_UPPERCASE("AA52AZ", "UNINSURED AND UNDERINSURED MOTORIST COVERAGE SELECTION FORM"),
		AA52CA("Agreement Deleting Uninsured/ Underinsured Motorist Bodily Injury Coverage"),
		AA52COA(""),
		AA52COB(""),
		AA52CT("Agreement Deleting Uninsured/ Underinsured Motorist Bodily Injury Coverage"),
		AA52IN("Uninsured/Underinsured Motorist Coverage - Rejection or Election of Lower Limits"),
		AA52IPAB(""),
		AA52IPAC(""),
		AA52MT("UM UIM Reject/Elect Lower Limits"),
		AA52NY("UM UIM Reject Elect Lower Limits"),
		AA52OH("Rejection of Uninsured/Underinsured Motorist Bodily Injury Coverage"),
		AA52OK(""),
		AA52KS(""),
		AA52OR("Rejection of Uninsured/Underinsured Motorist Bodily Injury Coverage"),
		AA52UPAB(""),
		AA52UPAC(""),
		AA52UPAA("RUUELLUU", "AA52UPAA", "Uninsured Motorists Coverage Selection/Rejection"),
		AA52VA("IMPORTANT NOTICE - UNINSURED MOTORIST COVERAGE"),
		AA52WV("Uninsured/Underinsured Motorists Coverage Offer"),
		AA52IPAA("UIMCRELL", "AA52IPAA","Underinsured Motorists Coverage Selection/Rejection"),
		AA53CA("Agreement Deleting Uninsured Motorist Property Damage Coverage"),
		AA53IN("Rejection of Uninsured Motorist Property Damage Coverage"),
		AA57CA(""),
		AA59CA(""),
		AA59XX("EXISTING DAMAGE ENDORSEMENT"),
		AA71COA(""),
		AA74CAA(""),
		AAACNY("Accident Prevention Course List - Notice To Policyholders"),
		AAAEOK(""),
		AAAUVA(""),
		AACDNYR("Credit Disclosure Notice"),
		AADDCA(""),
		AADNCO("Colorado Private Passenger Automobile Insurance Summary Disclosure Form"),
		AADNNY1("Identification Card Notice"),
		AADNNY2("DWI Notice"),
		AADNPAC(""),
		AADNPAD(""),
		AADNPAE(""),
		AADNUBI(""),
		AAEUIMMD("AAEUIMMD 07 18", "AAEUIMMD", "Maryland Enhanced Uninsured Motorists Coverage"),
		AAFPPA("AAFPPA","First Party Benefits Coverage and Limits Selection Form"),
		AAGCAZ("Golf Cart Coverage Endorsement"),
		AAIFNY2(""),
		AAIFNYC("Confirmation of Suspension of Physical Damage Coverage"),
		AAIFNYF(""),
		AAINXX1("Renewal Cover Letter"),
		AAPRN1(""),
		AAIQ("Auto Insurance Quote"),
		AAIQAZ("Auto Insurance Quote"),
		AAIQCA("Auto Insurance Quote"),
		AAIQCO("Auto Insurance Quote"),
		AALTPA(""),
		AAMTNY("Multi-tier Disclosure Notice"),
		AAOANY("Optional Basic Economic Loss (OBEL) Availability Notice"),
		AAPDXX("Courtesy Coverage Letter"),
		//AAPDXX("Courtesy Coverage - Permit Driver"),
		AAPNUBI(""),
		AAPNXX(""),
		AAPRN1OR("Pre-Renewal Notice Form"),
		AAPRN1MT("Pre-Renewal Notice Form"),
		AARFIXX("Request for Information"),
		AARIVA(""),
		AARNXX(""),
		AARRNY("Rental Car Reimbursement Notice"),
		AARTNY("Required Information Notice"),
		AASANY("SUM Availability Notice"),
		AASDPA(""),
		AASLNY("Supplelmental Spousal Liability Coverage"),
		AASR22("Financial Responsibility"),
		AASR22OH("AASR22", "SR22 Financial Responsibility Form"),
		AASR26("Cancellation of Financial Responsibility"),
		AATSXX("Critical Information For Teenage Drivers And Their Parents"),
		AAVICA("Community Service Survey"),
		AAUBI("AAA Usage Based Insurance Program Terms and Conditions"),
		AAUBI1("AAA with SMARTtrek Acknowledgement of T&Cs and Privacy Policies"),
		ACPUBI("ACP SMARTtrek Subscription Terms and Conditions"),
		ACPPNUBI(""),
		AH34XX("AH34XX", "AH34XX", "Cancellation Notice Document (NonPayment)"),
		AH35XX("Autopay Schedule Compilation"),
		AH60XXA("Rescission Notice"),
		AH61XX(""),
		AH61XXA(""),
		AH62XX("Reinstatement Notice"),
		AH63XX(""),
		AH64XX("Expiration Notice"),
		AH65XX(""),
		AH67XX("Lapse Notice - Non-pay"),
		AHAPXX("AutoPay Authorization Form"),
		AHAPXX_CA("AHAPXX", "Automatic Payment Authorization"),
		AHAUXX("Consumer Information Notice"),
		AHCAAG("Coverage Acceptance Statement"),
		AHCDCT("Credit Disclosure Notice And Summary of Consumer Protections"),
		AHCWXX("AHCWXX", "Cancellation Notice Withdrawn"),
		AHDEXX("DISCOUNT REMOVAL AT RENEWAL"),
		AHDRXX("DISCOUNT REMOVAL AT ENDORSEMENT"),
		AHELCXXA("ELC UW Letter for Approval"),
		AHELCXXD("ELC UW Letter for Denial"),
		AHELCXXL("ELC UW Letter for Lack of Supporting Documentation"),
		AHELCXXP("ELC UW Letter for No Premium Benefit Denial"),
		AHEVAXX("eValue Discount Acknowledgement"),
		AHFMXX("Fax Memorandum"),
		AHIBXX("AHIBXX", "Premium Due Notice"),
		AHNBXX("New Business Welcome Letter"),
		AHPNCA("Privacy Information Notice"),
		AHPNCAA(""),
		AHPNXX("Privacy Information Notice"),
		AHRBXX("AHRBXX", "Insurance Renewal Bill"),
		AHRCTXX("Insured Receipt For Funds Received By Agent"),
		AHRCTXXAUTO("AHRCTXX", "Insured Receipt for Funds"),
		AHRCTXXPUP("AHRCTXX", "Insured Receipt for Funds Received by Agent"),
		AHTPC("Third Party Designee Cover Page"),
		AHTPCCA(""),
		AHMVCNV("Membership Validation Letter"),
		AHMVXX2("Membership Validation Letter"),
		AU02("Notice of Cancellation"),
		AU03("Notice of Declination"),
		AU04("Free Form to Insured"),
		AU05("Premium Misquote Information"),
		AU06("Free Form to Producer"),
		AU07("Notice of Non-Renewal"),
		AU08("Request for Additional Information"),
		AU09("Uprate Notice"),
		AU10("Potential Uprate"),
		CAU01("Notice of Cancellation"),
		CAU02("Notice of Cancellation with Exclusion"),
		CAU04("Request for Information"),
		CAU07("Notice of Non-Renewal with Exclusion"),
		CAU08("Notice of Non-Renewal"),
		CAU09("Uprate Notice"),
		DF02CA("DF 02 CA", "Rental Property policy Declarations"),
		DS02("Rental Property Policy Declaration"),
		DS0420("DS 04 20", ""),
		DS0463("DS 04 63", ""),
		DS0468("DS 04 68", ""),
		DS0469("DS 04 69", "Earthquake"),
		DS0471("DS 04 71", ""),
		DS0473("DS 04 73", ""),
		DS0495("DS 04 95", ""),
		DS0926("DS 09 26", ""),
		DS0929("DS 09 29", "Fungi, Wet or Dry Rot, or Bacteria"),
		DS0934("DS 09 34", "Nonrenewal Notice"),
		DS65PA("DS65PA", ""),
		DSACCCMD("DSACCCMD", "DSACCCMD"),
		DS11("Application for Rental Property Insurance"),
		DS2482("DS 24 82", ""),
		DSIQXX("Rental Property Insurance Quote page"),
		DSIIDNV("Important Information Regarding Your Policy"),
		F1076B("California Application For Homeowners Insurance"),
		F1122("Property Inventory List"),
		F122G(""),
		F1455(""),
		F605005("60 5005", "Returning Payment"),
		FPCECA("FPCECA", "FPCECA", "FAIR Plan Companion Endorsement - California"),
		FPCECADP("FPCECADP", "FPCECADP", "FAIR Plan Companion Endorsement - California"),
		FS20("FS-20", "Insurance Identification Card"),
		HS_03_30("HS 03 30", "Special Hurricane Percentage Deductible"),
		HS_04_59("HS 04 59", ""),
		HS02("Owners Policy Declaration"),
		HS02_4("HS02", "HS02_4", "Renters Policy Declarations"),
		HS02_6("HS02", "HS02_6", "Condominium Owners Policy Declarations"),
		HS0312("HS 03 12", "Windstorm or Hail Deductible"),
		HS0420("HS 04 20", "HS 04 20 Endorsement"),
		HS0435("HS 04 35", "Loss Assessment Coverage Endorsement"),
		HS0436("HS 04 36", "Loss Assessment Coverage for Earthquake"),
		HS0454("HS 04 54", "Earthquake"),
		HS0455("HS 04 55", "Identity Fraud Expense endorsement"),
		HS0465("HS 04 65", "Coverage C Increased Special Limits of Liability endorsement"),
		HS0477("HS 04 77", "Building Code Upgrade endorsement"),
		HS0490("HS 04 90", "Personal Property Replacement Cost Loss Settlement endorsement"),
		HS0495("HS 04 95", "Water Back Up and Sump Discharge or Overflow endorsement"),
		HS0614("HS 06 14", "Personal Property Located In A Self-Storage Facility"),
		HS0906("HS 09 06", "Mortgage Payment Protection endorsement"),
		HS0926("HS 09 26", "Fungi, Wet or Dry Rot or Bacteria"),
		HS0929("HS 09 29", "Fungi, Wet or Dry Rot, or Bacteria"),
		HS0930("HS 09 30", "Fungi, Wet or Dry Rot, or Bacteria"),
		HS0931("HS 09 31", "Coverage C Special Coverage endorsement"),
		HS0932("HS 09 32", "Fungi, Wet or Dry Rot, or Bacteria"),
		HS0934("HS 09 34", "Rebuild to Green endorsement"),
		HS0965("HS 09 65", "Jewelry, watches, furs, precious and semiprecious stones Theft Limit endorsement"),
		HS0988("HS 09 88", "Additional Insured - Special Event"),
		HS2473("HS 24 73", "Farmers Personal Liability"),
		HS11("Application for Homeowners Insurance"),
		HS11_4("HS11", "HS11_4", "Application for Renters Insurance"),
		HS11_6("HS11", "HS11_6", "Application for Condominium Owners Insurance"),
		HS61PA("NOTICE OF CANCELLATION OR REFUSAL TO RENEW"),
		HS65PA("NOTICE OF CANCELLATION OR REFUSAL TO RENEW"),
		HS65MD("Nonrenewal Notice"),
		HSAUDVA("Virginia Adverse Action Underwriting Decision Notice"),
		HSCSNA("Homeowners Insurance"),
		HSCSND("Rental Property Insurance"),
		HSEIXX("Evidence of Property Insurance"),
		HSELNJ("Important Notice Regarding Extraordinary Life Circumstance"),
		HSEQNJ("New Jersey Earthquake Insurance Availability Notice"),
		HSES("HSES", "HSESXX", "Property Insurance Invoice"),
		HSFLDNJ("Important Flood Information for New Jersey Policyholders"),
		HSFLD("Important Flood Information for MD Policyholders"),
		HSHU2NJ("New Jersey Policyholder Hurricane Percentage Deductible Consumer Guide"),
		HSHUVA("Virginia Adverse Action Underwriting Decision Notice"),
		HSIIHNV("Important Information Regarding Your Policy"),
		HSILXX("Property Inventory List"),
		HSINVA("Important Notice Regarding Flood and earthquake Exclusion"),
		HSINVAP("Important Information Regarding Your Insurance"),
		HSIQXX("Homeowners Insurance Quote Page"),
		HSIQXX4("Renters Insurance Quote Page"),
		HSIQXX6("Condominium Owners Insurance Quote Page"),
		HSPISKY("Policy Information Sheet (KY)"),
		HSPIMDA("HSPIMDA", "Notice of Increased/Decreased Recalculated Premium"), 
		HSPRNMXX("Pre-Renewal letter (Mortgagee)"),
		HSPRNXX("HSPRNXX","Pre-Renewal letter"),
		HSRNMXX("Renewal Cover Letter (Mortgagee)"),
		HSRNPUPXX("Renewal Cover Letter PUP"),
		HSRNHODPXX("Renewal Cover Letter"),
		HSRRXX("Mortgagee Bill First Renewal Reminder"),
		HSRR2XX("Mortgagee Bill Final Expiration Notice"),
		HSRNHBXX("HSRNHBXX","Renewal Reminder (Home Banking)"),
		HSRNHBPUP("HSRNHBPUP","Renewal Reminder (Home Banking PUP)"),
		HSRFIXX("Request for Information"),
		HSRFIXXPUP("HSRFIXX", "Request For Information"),
		HSRMXX(""),
		HSRNXX("HSRNXX", "Renewal Offer: Important Renewal Information"),
		HSRNKY("HSRNKY", "Important Information Regarding Your Upcoming Renewal"),
		HSSNRKYXX("HSSNRKYXX", "Conversion Specific Special Non-renewal letter"),
		HSTP("Third Party Designation by Senior Citizen"),
		HSU01CA("HSU01CA", "HSU01", "Advisory Letter"),
		HSU01XX("HSU01XX", "HSU01", "Advisory Letter"),
		HSU02XX("HSU02XX", "HSU02", "Cancellation Letter"),
		HSU03XX("HSU03XX", "HSU03", "Customer Decline at POS"),
		HSU04XX("HSU04XX", "HSU04", "Free Form to Insured"),
		HSU05XX("HSU05XX", "HSU05", "Free Form to Other"),
		HSU06CA("HSU06CA", "HSU06", "Free Form to Producer"),
		HSU06XX("HSU06XX", "HSU06", "Free Form to Producer"),
		HSU07CA("HSU07CA", "HSU07", "Non-Renewal Letter"),
		HSU07XX("HSU07XX", "HSU07", "Non-Renewal Letter"),
		HSU08XX("HSU08XX", "HSU08", "Request for Additional Information"),
		HSU09XX("HSU09XX", "HSU09", "Uprate"),
		HSVAAD("Policy holder Advisory Notice"),
		HSMPDCNVXX("Multi-policy Discount Letter"),
		HSAOCMDA("Maryland Statement of Additional Optional Coverages (Homeowner's)"),
		HSSNMDA("Annual Summary of Maryland Homeowner’s Coverages and Exclusions including Availability of Windstorm Mitigation Discount"),
		HSCRRMD("Important Notice Regarding Cancellation or Refusal to Renew"),
		HSAOCMDB("Maryland Statement of Additional Optional Coverages (Renter's)"),
		HSAOCMDC("Maryland Statement of Additional Optional Coverages (Condominium Owner's)"),
		HSSNMDC("Annual Summary of Maryland Condominium Owner’s Coverages and Exclusions including Availability of Windstorm Mitigation Discount"),
		HSSNMDB("Annual Summary of Maryland Renter’s Coverages and Exclusions including Availability of Windstorm Mitigation Discount"),
		IL_09_10("IL 09 10","IL 09 10","Pennsylvania Notice"),
		HSSCCOA("HSSCCOA", "Summary of Coverage"),
		HSSCCOB("HSSCCOB", "Summary of Coverage"),
		HSSCCOC("HSSCCOC", "Summary of Coverage"),
		HSSCCOD("HSSCCOD", "Summary of Coverage"),
		PS02("Personal Umbrella Policy Declarations"),
		PS0922("PS 09 22", "Named Driver Exclusion"),
		PS11("Application for Personal Umbrella Liability Insurance"),
		PSIQXX("Personal Umbrella Liability Insurance Quote Page"),
		SR22SR1P("California Insurance Proof Certificate"),
		WU11DCA("California Application for Rental Property Insurance"),
		WUAECA(""),
		WUAUCA("Consumer Information Notice"),
		WURFICA("Request for Information"),
		WURFICA_PUP("WURFICA", "Request For Information"),
		_1075("1075", "Homeowners Policy Declarations"),
		_1075_HO4("1075", "1075A", "Homeowners Policy Declarations"),
		_347A0086("347A0086", "Offer of Earthquake Coverage"),
		_438BFUNS("438BFUNS", "Lender’s Loss Payable Endorsement"),
		_55_0001("55 0001", ""),
		_55_0002("55 0002", ""),
		_55_0019("55 0019", ""),
		_55_0038("55 0038", ""),
		_55_1000("55 1000", ""),
		_55_1001("55 1001", ""),
		_55_1004("55 1004", ""),
		_55_1005("55 1005", ""),
		_55_1006("55 1006", ""),
		_55_1007("55 1007", ""),
		_55_1500("55 1500", ""),
		_55_3333("55 3333", ""),
		_55_3500("55 3500", "Refund Check"),
		_55_5086("55 5086", ""),
		_55_5003("55 5003", "Automatic Reinstatement Letter"),
		_55_5080("55 5080", "Manual Reinstatement letter"),
		_55_5100("55 5100", ""),
		_55_6101("55 6101", "Earned Premium Bill 1"),
		_55_6102("55 6102", "Earned Premium Bill 2"),
		_55_6103("55 6103", "Earned Premium Bill 3"),
		_55_6109("55 6109", ""),
		_550002("550002", "Lien-Holder's Statement of Policy Coverage"),
		_550007("550007", "Uninsured Motorist Coverage Deletion or Selection of Limits Agreement"),
		_550009("550009", "Request for Additional Information"),
		_550010("550010", "Community Service Survey"),
		_550011("550011", "Camper Physical Damage Coverage Waiver"),
		_550014("550014", "CA Insurance Proof Certificate SR-22"),
		_550016("550016", "Temporary Evidence Of Liability Insurance"),
		_550018("550018", "Confirmation Of Liability Coverage"),
		_550019("550019", "Lessor Certificate of Coverage"),
		_550023("550023", "Automobile Policy Cancellation Request"),
		_550025("550025", "Adverse Underwriting Decision Notice"),
		_550026("550026", "Declaration Under Penalty of Perjury"),
		_550029("55 0029", ""),
		_550035("550035", "Auto Quote"),
		_550039("550039", "Evidence Of Liability Insurance"),
		_551003("551003", "Operator Exclusion Endorsement and Uninsured Motorist Coverage Deletion Endorsement"),
		_553333("553333", "Auto Billing Plan Explanation"),
		// CA Auto Insurance Application
		// Needed for Queries, in database there is a space
		_55_4000("55 4000", "California Car Policy Application"),
		// Needed for UI, to generate on demand document for example
		_554000("554000", "California Car Policy Application"),
		_58_1027("58 1027", "Designated Recreational Motor Vehicle Exclusion Endorsement"),
		_58_1500("58 1500", ""),
		_58_4000("58 4000", "Personal Umbrella Policy Application"),
		_60_5000("60 5000", "Fee + Restriction Form"),
		_60_5001("60 5001", "Fee + No Restriction Form"),
		_60_5002("60 5002", "No Fee + No Restriction Form"),
		_60_5003("60 5003", "Payment Restriction Form"),
		_60_5004("60 5004", ""),
		_60_5005("60 5005", "Returning Payment"),
		_60_5006("60 5006", ""),
		_60_5019("60 5019", "Subscriber Agreement"),
		_605004("605004", "Remove Recurring Payments"),
		_605005("605005", "Returning Enclosed Check"),
		_605005_SELECT("605005", "Returning Payment"),
		_605019("605019", "Subscriber Agreement"),
		_61_1500("61 1500", ""),
		_61_2006("61 2006", "Offer of Earthquake Coverage Homeowners/Dwelling Fire Basic Earthquake Policy"),
		_61_3000("61 3000", "California Residential Property Insurance Bill of Rights"),
		_61_3026("61 3026", "Property Bill Plan Explanation"),
		_61_4002("61 4002", "California Application For Condominium Owners Insurance"),
		_61_4003("61 4003", "California Application for Renters Insurance"),
		_61_5120("61 5120", "New Business Welcome Letter"),
		_61_5121("61 5121", "Renewal Thank You Letter"),
		_61_6513("61 6513", "Property Insurance Invoice"),
		_61_6528("61 6528", "PUP Insurance Quote Page"),
		_61_6528_DP3("61 6528", "Rental Property Insurance Quote Page"),
		_61_6528_HO3("61 6528", "Homeowners Insurance Quote Page"),
		_61_6528_HO4("61 6528", "Renters Insurance Quote Page"),
		_61_6528_HO6("61 6528", "Condominium Owners Insurance Quote Page"),
		_61_6530("61 6530", "California Residential Property Insurance Disclosure"),
		_62_6500("62 6500", "CA Evidence of Property Insurance"),
		AACSDC("District of Columbia Coverage Selection/Rejection Form"),
		AADNDE1("AADNDE1", "Delaware Motorists Protection Act");



		private String id;
		private String idInXml;
		private String name;
		private ThreadLocal<String> state = ThreadLocal.withInitial(() -> "");

		Documents() {
			setId(this.name());
			setIdInXml(this.name());
			setName("");
			setState("");
		}

		Documents(String docName) {
			setId(this.name());
			setIdInXml(this.name());
			setName(docName);
			setState("");
		}

		Documents(String docId, String docName) {
			setId(docId);
			setIdInXml(docId);
			setName(docName);
			setState("");
		}

		Documents(String docId, String docIdInXml, String docName) {
			setId(docId);
			setIdInXml(docIdInXml);
			setName(docName);
			setState("");
		}

		Documents(String docId, String docIdInXml, String docName, String state) {
			setId(docId);
			setIdInXml(docIdInXml);
			setName(docName);
			setState(state);
		}

		public String getState() {
			return this.state.get();
		}

		public Documents setState(String state) {
			this.state.set(state);
			return this;
		}

		public String getId() {
			return id + getState();
		}

		private void setId(String id) {
			this.id = id;
		}

		public String getIdInXml() {
			return idInXml;
		}

		private void setIdInXml(String idInXml) {
			this.idInXml = idInXml;
		}

		public String getName() {
			return name;
		}

		private void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			String documentInfo = "Documents{id='%1$s'%2$s%3$s%4$s}'";
			return String.format(documentInfo, getId(),
					getIdInXml().equals(getId()) ? "" : ", idInXml='" + getIdInXml() + "'",
					StringUtils.isEmpty(getName()) ? "" : ", name='" + getName() + "'",
					StringUtils.isEmpty(getState()) ? "" : ", state='" + getState() + "'");
		}
	}

	public enum DeliveryMethod {
		EMAIL("Email"),
		FAX("Fax"),
		CENTRAL_PRINT("Central Print"),
		E_SIGNATURE("eSignature"),
		LOCAL_PRINT("Local Print");

		private String id;

		DeliveryMethod(String id) {
			set(id);
		}

		public String get() {
			return id;
		}

		public void set(String id) {
			this.id = id;
		}
	}

	public static class DataElementChoiceTag {
		public static final String TEXTFIELD = "TextField";
		public static final String DATETIMEFIELD = "DateTimeField";
	}

	public static class XmlnsNamespaces {
		public static final String DOC_PREFIX = "doc";
		public static final String DOC_URI = "http://www.aaancnuie.com/DCS/2012/01/DocumentCreation";
		public static final String DOC_URI2 = "http://www.aaancnuie.com/DCS/2012/01/DocumentDistribution";

		public static final String AAAN_PREFIX = "aaan";
		public static final String AAAN_URI = "http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1";

		public static final String XSI_PREFIX = "xsi";
		public static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
	}

	public enum XmlnsDbFormat {
		DOC("xmlns:doc=\"http://www.aaancnuie.com/DCS/2012/01/DocumentCreation\""),
		AAAN("xmlns:aaan=\"http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1\"");

		private String xmlns;

		XmlnsDbFormat(String xmlns) {
			this.xmlns = xmlns;
		}

		public String getXmlns() {
			return xmlns;
		}
	}
}
