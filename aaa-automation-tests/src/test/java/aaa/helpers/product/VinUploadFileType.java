package aaa.helpers.product;

public enum VinUploadFileType {
	REFRESHABLE_VIN("Refreshable"),
	NEW_VIN("New"),
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
