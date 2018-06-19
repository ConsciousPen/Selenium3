package aaa.helpers.product;

public enum VinUploadFileType {
	REFRESHABLE_VIN("Refreshable"),
	NEW_VIN("New"),
	NEW_VIN2("New2"),
	NEW_VIN3("New3"),
	NEW_VIN4("New4"),
	NEW_VIN5("New5"),
	NEW_VIN6("New6"),
	NEW_VIN7("New7"),
	NEW_VIN8("New8"),
	NEW_VIN9("New9"),
	NEW_VIN10("New10"),
	NEW_VIN11("New11"),
	REFRESHABLE_VIN2("Refreshable2"),
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
