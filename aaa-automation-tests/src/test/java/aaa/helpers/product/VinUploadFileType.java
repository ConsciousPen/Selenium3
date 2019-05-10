package aaa.helpers.product;

public enum VinUploadFileType {
	REFRESHABLE_VIN("Refreshable"),
	REFRESHABLE_VIN_RESET_ORIGINAL("RefreshableResetOriginal"),
	NEW_VIN("NewVIN"),
	NEW_VIN2("New2VIN"),
	NEW_VIN3("New3VIN"),
	NEW_VIN4("New4VIN"),
	NEW_VIN5("New5VIN"),
	NEW_VIN6("New6VIN"),
	NEW_VIN7("New7VIN"),
	NEW_VIN_ADDED("NewVinAdded"),
	VIN_DOESNT_MATCH_AFTER_PRODUCT_CHANGE("VinDoesntMatchAfterProductChange"),
	MATCH_ON_NEW_BUSINESS_NO_MATCH_ON_RENEWAL("MatchOnNewBusinessNoMatchOnRenewal"),
	SUBSEQUENT_RENEWAL_35("SubSequentRenewal35"),
	SUBSEQUENT_RENEWAL_45("SubSequentRenewal45"),
	SUBSEQUENT_RENEWAL_46("SubSequentRenewal46"),
	NEW_VIN9("New9VIN"),
	R45("R45VIN"),
	NO_MATCH_NEW_QUOTE("NoMatchNewQuote"),
	NO_MATCH_ON_RENEWAL("NoMatchORenewal"),
	NO_MATCH_ON_NEW_BUSINESS_FULL_MATCH_ON_RENEWAL("NoMatchOnNewBusinessFullMatchOnRenewal"),
	PARTIAL_MATCH_NEW_QUOTE("PartialMatchNewQuote"),
	STATCODE_VIN_REFERSH_RENEWAL("StatcodeVINrefreshOnRenewal"),
	//PAS-23996- Added for Capping Refresh checks
	CAPPING_R30("CappingRefreshR30"),
	CAPPING_R35("CappingRefreshR35");

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
