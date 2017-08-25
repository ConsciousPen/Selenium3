package aaa.main.enums;

public enum OnDemandDocumentEnum {
	AA11CO("Colorado Auto Insurance Application"),
	AA16CO("MEDICAL PAYMENTS REJECTION OF COVERAGE"),
	AA43CO("Named Driver Exclusion Endorsement"),
	AADNCO("Colorado Private Passenger Automobile Insurance Summary Disclosure Form"),
	AAIQCO("Auto Insurance Quote"),
	AHAUXX("Consumer Information Notice"),
	AHFMXX("Fax Memorandum"),
	AU03("Notice of Declination");
	//TODO-dchubkov: add remaining documents

	private String id;
	private String idInXml;
	private String name;

	OnDemandDocumentEnum() {
		setId(this.name());
		setIdInXml(this.name());
		setName(""); // to prevent NPE on getName() call for documents with not defined names
	}

	OnDemandDocumentEnum(String docName) {
		setId(this.name());
		setIdInXml(this.name());
		setName(docName);
	}

	OnDemandDocumentEnum(String docId, String docName) {
		setId(docId);
		setIdInXml(docId);
		setName(docName);
	}

	OnDemandDocumentEnum(String docId, String docIdInXml, String docName) {
		setId(docId);
		setIdInXml(docIdInXml);
		setName(docName);
	}

	public String getId() {
		return id;
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
		return "OnDemandDocumentEnum{" +
				"id='" + id + '\'' +
				", idInXml='" + idInXml + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
