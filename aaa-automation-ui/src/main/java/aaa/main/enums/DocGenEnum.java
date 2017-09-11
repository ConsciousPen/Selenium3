package aaa.main.enums;

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
		AHRCTXX("Insured Receipt For Funds Received By Agent"),
		PSIQXX("Personal Umbrella Liability Insurance Quote Page"),
		PS11("Application for Personal Umbrella Liability Insurance"),
		AHCDCT("Credit Disclosure Notice And Summary of Consumer Protections"),
		AHAPXX("AutoPay Authorization Form"),
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
		HS0988("HS 09 88","Additional Insured - Special Event"),
		_60_5000("60 5000", "Fee + Restriction Form"),
		_60_5001("60 5001", "Fee + No Restriction Form"),
		_60_5002("60 5002", "No Fee + No Restriction Form"),
		_60_5003("60 5003", "Payment Restriction Form"),
		AH61XX(""),
		HS61PA("NOTICE OF CANCELLATION OR REFUSAL TO RENEW"),
		_55_3500("55 3500", "Refund Check"),
		;

		private String id;
		private String idInXml;
		private String name;
		private String state;

		Documents() {
			setId(this.name());
			setIdInXml(this.name());
			setName(""); // to prevent NPE on getName() call for documents with not defined names
			setState(""); // to prevent NPE on getState() call for documents with not defined names
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
			return state;
		}

		public Documents setState(String state) {
			this.state = state;
			return this;
		}

		public String getId() {
			return id + getState();
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getIdInXml() {
			return idInXml;
		}

		public void setIdInXml(String idInXml) {
			this.idInXml = idInXml;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			String documentInfo = "Documents{id='%1$s'%2$s%3$s%4$s}'";
			return String.format(documentInfo, getId(),
					getIdInXml().equals(getId()) ? "" : ", idInXml='" + getIdInXml() + "'",
					getName().isEmpty() ? "" : ", name='" + getName() + "'",
					getState().isEmpty() ? "" : ", state='" + getState() + "'");
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
}
