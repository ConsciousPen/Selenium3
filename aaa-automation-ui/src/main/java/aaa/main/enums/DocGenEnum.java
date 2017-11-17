package aaa.main.enums;

import org.apache.commons.lang.StringUtils;

public final class DocGenEnum {
	private DocGenEnum() {
	}

	public enum Documents {
		AA11CO("Colorado Auto Insurance Application"),
		AA16CO("MEDICAL PAYMENTS REJECTION OF COVERAGE"),
		AA43CO("Named Driver Exclusion Endorsement"),
		AADNCO("Colorado Private Passenger Automobile Insurance Summary Disclosure Form"),
		AAIQCO("Auto Insurance Quote"),
		AHAUXX("Consumer Information Notice"),
		AHFMXX("Fax Memorandum"),
		AU03("Notice of Declination"),
		HSU01XX("HSU01XX", "HSU01", "Advisory Letter"),
		HSU02XX("HSU02XX", "HSU02", "Cancellation Letter"),
		HSU03XX("HSU03XX", "HSU03","Customer Decline at POS"),
		HSU04XX("HSU04XX", "HSU04", "Free Form to Insured"),
		HSU05XX("HSU05XX", "HSU05", "Free Form to Other"),
		HSU06XX("HSU06XX", "HSU06", "Free Form to Producer"),
		HSU07XX("HSU07XX", "HSU07", "Non-Renewal Letter"),
		HSU08XX("HSU08XX", "HSU08", "Request for Additional Information"),
		HSU09XX("HSU09XX", "HSU09", "Uprate"),
		F605005("60 5005", "Returning Payment"),
		PS0922("PS 09 22", "Named Driver Exclusion"),
		HSRFIXX("Request for Information"),
		HSRFIXXPUP("HSRFIXX","Request For Information"),
		AHRCTXX("Insured Receipt For Funds Received By Agent"),
		AHRCTXXAUTO("AHRCTXX", "Insured Receipt for Funds"),
		AHRCTXXPUP("AHRCTXX","Insured Receipt for Funds Received by Agent"),
		PSIQXX("Personal Umbrella Liability Insurance Quote Page"),
		PS11("Application for Personal Umbrella Liability Insurance"),
		AHCDCT("Credit Disclosure Notice And Summary of Consumer Protections"),
		AHAPXX("AutoPay Authorization Form"),
		AHAPXX_CA("AHAPXX", "Automatic Payment Authorization"),
		HSAUDVA("Virginia Adverse Action Underwriting Decision Notice"),
		HS11("Application for Homeowners Insurance"),
		HS11_4("HS11","HS11_4","Application for Renters Insurance"),
		HS11_6("HS11","HS11_6","Application for Condominium Owners Insurance"),
		HSIQXX("Homeowners Insurance Quote Page"),
		HSIQXX4("Renters Insurance Quote Page"),
		HSIQXX6("Condominium Owners Insurance Quote Page"),
		_438BFUNS("438BFUNS", "Lender’s Loss Payable Endorsement"),
		AHPNXX("Privacy Information Notice"),
		HS02("Owners Policy Declaration"),
		HS02_4("HS02","HS02_4","Renters Policy Declarations"),
		HS02_6("HS02","HS02_6","Condominium Owners Policy Declarations"),
		AHNBXX("New Business Welcome Letter"),
		HSEIXX("Evidence of Property Insurance"),
		HSES("HSES", "HSESXX", "Property Insurance Invoice"),
		HSILXX("Property Inventory List"),
		PS02("Personal Umbrella Policy Declarations"),
		HS0420("HS 04 20", "HS 04 20 Endorsement"),
		HS0435("HS 04 35", "Loss Assessment Coverage Endorsement"),
		HS0455("HS 04 55", "Identity Fraud Expense endorsement"),
		HS0465("HS 04 65", "Coverage C Increased Special Limits of Liability endorsement"),
		HS0490("HS 04 90", "Personal Property Replacement Cost Loss Settlement endorsement"),
		HS0495("HS 04 95", "Water Back Up and Sump Discharge or Overflow endorsement"),
		HS0614("HS 06 14", "Personal Property Located In A Self-Storage Facility"),
		HS0906("HS 09 06", "Mortgage Payment Protection endorsement"),
		HS0926("HS 09 26", "Fungi, Wet or Dry Rot or Bacteria"),
		HS0929("HS 09 29", "Fungi, Wet or Dry Rot, or Bacteria"),
		HS0930("HS 09 30", "Fungi, Wet or Dry Rot, or Bacteria"),
		HS0932("HS 09 32", "Fungi, Wet or Dry Rot, or Bacteria"),
		DS0929("DS 09 29", "Fungi, Wet or Dry Rot, or Bacteria"),
		HS0931("HS 09 31", "Coverage C Special Coverage endorsement"),
		HS0934("HS 09 34", "Rebuild to Green endorsement"),
		HS0965("HS 09 65", "Jewelry, watches, furs, precious and semiprecious stones Theft Limit endorsement"),
		HS0477("HS 04 77", "Building Code Upgrade endorsement"),
		AH60XXA("Rescission Notice"),
		AA41XX("Non-Owners Endorsement"),
		AA10OK(""),
		HS0988("HS 09 88","Additional Insured - Special Event"),
		_60_5000("60 5000", "Fee + Restriction Form"),
		_60_5001("60 5001", "Fee + No Restriction Form"),
		_60_5002("60 5002", "No Fee + No Restriction Form"),
		_60_5003("60 5003", "Payment Restriction Form"),
		AH61XX(""),
		HS61PA("NOTICE OF CANCELLATION OR REFUSAL TO RENEW"),
		_55_3500("55 3500", "Refund Check"),
		AH35XX("Autopay Schedule Compilation"),
		AHRBXX("AHRBXX", "Insurance Renewal Bill"),
		AA43AZ("Named Driver Exclusion Election"),
		AA43IN("Named Driver Exclusion Election"),
		AA43OH("Named Driver Exclusion Election"),
		AA43PA("Named Driver Exclusion Election"),
		AA43OK("Named Driver Exclusion Election"),
		AA43WV("Named Driver Exclusion Endorsement"),
		AASR22("Financial Responsibility"),
		AASR22OH("AASR22", "SR22 Financial Responsibility Form"),
		AA59XX("EXISTING DAMAGE ENDORSEMENT"),
		DS02("Rental Property Policy Declaration"),
		DS11("Application for Rental Property Insurance"),
		DSIQXX("Rental Property Insurance Quote page"),
		DS0420("DS 04 20", ""),
		DS0463("DS 04 63", ""),
		DS0468("DS 04 68", ""),
		DS0469("DS 04 69", "Earthquake"),
		DS0471("DS 04 71", ""),
		DS0473("DS 04 73", ""),
		DS0495("DS 04 95", ""),
		DS0926("DS 09 26", ""),
		DS0934("DS 09 34", ""),
		DS2482("DS 24 82", ""),
		AAGCAZ("Golf Cart Coverage Endorsement"),
		AA52AZ("Uninsured and Underinsured Motorist Coverage selection form"),
		AA52IN("Uninsured/Underinsured Motorist Coverage - Rejection or Election of Lower Limits"),
		AA53IN("Rejection of Uninsured Motorist Property Damage Coverage"),
		AARFIXX("Request for Information"),
		AA10XX("Insurance Identification Card"),
		AA10PA("Insurance Identification Card"),
		AA02AZ("Declaration page"),
		AA02IN("Declaration page"),
		AA02PA("Declaration page"),
		AA02OK("Declaration page"),
		AA02WV("Declaration page"),
		AHCWXX("AHCWXX", "Cancellation Notice Withdrawn"),
		AH34XX("AH34XX","AH34XX","Cancellation Notice Document (NonPayment)"),
		_55_6101("55 6101", "Earned Premium Bill 1"),
		_55_6102("55 6102", "Earned Premium Bill 2"),
		_55_6103("55 6103", "Earned Premium Bill 3"),
		AHIBXX("AHIBXX", "Premium Due Notice"),
		HSRNXX("HSRNXX", "Renewal Offer: Important Renewal Information"),
		AH67XX("Lapse Notice - Non-pay"),
		AH62XX("Reinstatement Notice"),
		AASR26("Cancellation of Financial Responsibility"),
		AAPDXX("Courtesy Coverage Letter"),
		_61_6528_HO3("61 6528", "Homeowners Insurance Quote Page"),
		_61_6528_HO4("61 6528", "Renters Insurance Quote Page"),
		_61_6528_HO6("61 6528", "Condominium Owners Insurance Quote Page"),
		_61_6528_DP3("61 6528", "Rental Property Insurance Quote Page"),
		F1122("Property Inventory List"),
		_347A0086("347A0086", "Offer of Earthquake Coverage"),
		_61_6530("61 6530", "California Residential Property Insurance Disclosure"),
		_61_3000("61 3000", "California Residential Property Insurance Bill of Rights"),
		_61_3026("61 3026", "Property Bill Plan Explanation"),
		_61_5121("61 5121", "Renewal Thank You Letter"),
		F1076B("California Application For Homeowners Insurance"),
		_62_6500("62 6500", "CA Evidence of Property Insurance"),
		WURFICA("Request for Information"),
		WURFICA_PUP("WURFICA", "Request For Information"),
		HSU01CA("HSU01CA", "HSU01", "Advisory Letter"),
		HSU06CA("HSU06CA", "HSU06", "Free Form to Producer"),
		HSU07CA("HSU07CA", "HSU07", "Non-Renewal Letter"),
		AHPNCA("Privacy Information Notice"),
		WUAUCA("Consumer Information Notice"),
		_61_6513("61 6513", "Property Insurance Invoice"),
		_61_2006("61 2006", "Offer of Earthquake Coverage Homeowners/Dwelling Fire Basic Earthquake Policy"),
		_61_5120("61 5120", "New Business Welcome Letter"),
		_1075("1075", "Homeowners Policy Declarations"),
		_60_5019("60 5019", "Subscriber Agreement"),
		_1075_HO4("1075", "1075A", "Homeowners Policy Declarations"),
		_61_4002("61 4002", "California Application For Condominium Owners Insurance"),
		_61_4003("61 4003", "California Application for Renters Insurance"),
		_60_5005("60 5005", "Returning Payment"),
		_61_1500("61 1500", ""),
		WU11DCA("California Application for Rental Property Insurance"),
		DF02CA("DF 02 CA", "Rental Property policy Declarations"),
		_58_4000("58 4000", "Personal Umbrella Policy Application"),
		_61_6528("61 6528", "PUP Insurance Quote Page"),
		_58_1027("58 1027", "Designated Recreational Motor Vehicle Exclusion Endorsement"),
		_58_1500("58 1500", ""),
		HSFLDNJ("Important Flood Information for New Jersey Policyholders"),
		HSCSND("Rental Property Insurance"),
		HSHU2NJ("New Jersey Policyholder Hurricane Percentage Deductible Consumer Guide"),
		HSELNJ("Important Notice Regarding Extraordinary Life Circumstance"),
		AHTPC("Third Party Designee Cover Page"),
		HSEQNJ("New Jersey Earthquake Insurance Availability Notice"),
		HSCSNA("Homeowners Insurance"),
		AHELCXXA("ELC UW Letter for Approval"),
		AHELCXXD("ELC UW Letter for Denial"),
		AHELCXXL("ELC UW Letter for Lack of Supporting Documentation"),
		AHELCXXP("ELC UW Letter for No Premium Benefit Denial"),
		HSHUVA("Virginia Adverse Action Underwriting Decision Notice"),
		HS_03_30("HS 03 30", "Special Hurricane Percentage Deductible"),
		HSVAAD("Policy holder Advisory Notice"),
		HSINVAP("Important Information Regarding Your Insurance"),
		HSINVA("Important Notice Regarding Flood and earthquake Exclusion"),
        AH64XX("Expiration Notice"),
        AAIQAZ("Auto Insurance Quote"),
        AATSXX("Critical Information For Teenage Drivers And Their Parents"),
        AA41PA(""),
        AA52UPAB(""),
        AA52IPAB(""),
        AA52UPAC(""),
        AA52IPAC(""),
        AASDPA(""),
        AADNPAC(""),
        AADNPAD(""),
        AADNPAE(""),
        AALTPA(""),
        AAFPPA(""),
        F122G(""),
        AAAEOK(""),
        AA52OK(""),
        
        AA11CA("California Choice Auto Insurance Application"),
        AA41CA("Non-Owner Automobile Endorsement"),
        AA43CA("Named Driver(s) Exclusion"),
        AA52CA("Agreement Deleting Uninsured/ Underinsured Motorist Bodily Injury Coverage"),
        AA53CA("Agreement Deleting Uninsured Motorist Property Damage Coverage"),
        CAU01("Notice of Cancellation"),
        CAU02("Notice of Cancellation with Exclusion"),
        CAU04("Request for Information"),
        CAU07("Notice of Non-Renewal with Exclusion"),
        CAU08("Notice of Non-Renewal"),
        CAU09("Uprate Notice"),
        AAIQCA("Auto Insurance Quote"),
        AA09CA(""),
        AA47CA(""),
        AA49CA(""),
        AA59CA(""),
        AADDCA(""),
        AA74CAA(""),
        AA43CAB(""),
        WUAECA(""),
        AA02CA("Declaration page"),
        AAVICA("Community Service Survey"),
        SR22SR1P("California Insurance Proof Certificate"),
        _605005("605005", "Returning Enclosed Check"),
        _605005_SELECT("605005", "Returning Payment"),
        _605004("605004", "Remove Recurring Payments"),
        AA06XX("Agent Advice Memo"),
        AA06XX_AUTOSS("AA06XX", "Agent Advise Memo"),
        
        _550007("550007", "Uninsured Motorist Coverage Deletion or Selection of Limits Agreement"),
        _550011("550011", "Camper Physical Damage Coverage Waiver"),
        _550026("550026", "Declaration Under Penalty of Perjury"),
        _550035("550035", "Auto Quote"),
        _551003("551003", "Operator Exclusion Endorsement and Uninsured Motorist Coverage Deletion Endorsement"),
        _554000("554000", "California Car Policy Application"),
        _605019("605019", "Subscriber Agreement"),
        _550002("550002", "Lien-Holder's Statement of Policy Coverage"),
        _550010("550010", "Community Service Survey"),
        _550014("550014", "CA Insurance Proof Certificate SR-22"),
        _550016("550016", "Temporary Evidence Of Liability Insurance"),
        _550018("550018", "Confirmation Of Liability Coverage"),
        _550019("550019", "Lessor Certificate of Coverage"),
        _550023("550023", "Automobile Policy Cancellation Request"),
        _550025("550025", "Adverse Underwriting Decision Notice"),
        _553333("553333", "Auto Billing Plan Explanation"),
        _550039("550039", "Evidence Of Liability Insurance"),
        _550009("550009", "Request for Additional Information"),
        _55_3333("55 3333", ""),
        _55_1500("55 1500", ""),
        _55_0038("55 0038", ""),
        _55_0002("55 0002", ""),
        _55_0019("55 0019", ""),
        _55_1006("55 1006", ""),
        _55_1000("55 1000", ""),
        _55_1001("55 1001", ""),
        _55_1004("55 1004", ""),
        _55_1005("55 1005", ""),
        _55_1007("55 1007", ""),
        _55_5086("55 5086", ""),
        _55_0001("55 0001", ""),
        _55_6109("55 6109", ""),
        
        AA11VA("Virginia Auto Insurance Application"),
        AA52VA("IMPORTANT NOTICE - UNINSURED MOTORIST COVERAGE"),
        AA52OH("Rejection of Uninsured/Underinsured Motorist Bodily Injury Coverage"),
        AA52WV("Uninsured/Underinsured Motorists Coverage Offer"),
        AA52AZ_UPPERCASE("AA52AZ", "UNINSURED AND UNDERINSURED MOTORIST COVERAGE SELECTION FORM"),
        AAIQ("Auto Insurance Quote"),
        AA11AZ("Auto Insurance Application"),
        AA11IN("Indiana Auto Insurance Application"),
        AA11OH("Auto Insurance Application"),
        AA11WV("Auto Insurance Application"),
        AAAUVA(""),
        AA02VA("Declaration page"),
        AA02OH("Declaration page"),
        
        AU02("Notice of Cancellation"),
        AU04("Free Form to Insured"),
        AU05("Premium Misquote Information"),
        AU06("Free Form to Producer"),
        AU07("Notice of Non-Renewal"),
        AU08("Request for Additional Information"),
        AU09("Uprate Notice"),
        AU10("Potential Uprate"),
        AAPNXX("")
		;

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
		public final static String TEXTFIELD = "TextField";
		public final static String DATETIMEFIELD = "DateTimeField";
	}

	public static class XmlnsNamespaces {
		public final static String DOC_PREFIX = "doc";
		public final static String DOC_URI = "http://www.aaancnuie.com/DCS/2012/01/DocumentCreation";
		public final static String DOC_URI2 = "http://www.aaancnuie.com/DCS/2012/01/DocumentDistribution";

		public final static String AAAN_PREFIX = "aaan";
		public final static String AAAN_URI = "http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1";

		public final static String XSI_PREFIX = "xsi";
		public final static String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
	}


	public enum XmlnsDbFormat{
		DOC("xmlns:doc=\"http://www.aaancnuie.com/DCS/2012/01/DocumentCreation\""),
		AAAN("xmlns:aaan=\"http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1\"");

		private String xmlns;
		XmlnsDbFormat(String xmlns){
			this.xmlns = xmlns;
		}

		public String getXmlns() {
			return xmlns;
		}
	}
}
