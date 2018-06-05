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