package aaa.helpers.product;

public enum VinUploadFileType {
	REFRESHABLE_VIN("Refreshable"),
	NEW_VIN("NewVIN"),
	NEW_VIN2("New2VIN"),
	NEW_VIN3("New3VIN"),
	NEW_VIN4("New4VIN"),
	NEW_VIN5("New5VIN"),
	NEW_VIN6("New6VIN"),
	NEW_VIN7("New7VIN"),
	VinDoesntMatchAfterProductChange("VinDoesntMatchAfterProductChange"),
	MATCH_ON_NEW_BUSINESS_NO_MATCH_ON_RENEWAL("MatchOnNewBusinessNoMatchOnRenewal"),
	NEW_VIN9("New9VIN"),
	PARTIAL_MATCH("PartialMatch"),
	R45("R45");

	private String type;

	VinUploadFileType(String type) {
		set(type);
	}

	public void set(String type) {
		this.type = type;
	}

	public String get() {
		return type;
	}

}
