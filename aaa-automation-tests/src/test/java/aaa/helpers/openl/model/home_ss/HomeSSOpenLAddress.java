package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLAddress;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.ADDRESS_SHEET_NAME, headerRowIndex = OpenLFile.ADDRESS_HEADER_ROW_NUMBER)
public class HomeSSOpenLAddress extends OpenLAddress {
	private Boolean isRetCommunityPresent;

	public Boolean getRetCommunityPresent() {
		return isRetCommunityPresent;
	}

	public void setRetCommunityPresent(Boolean retCommunityPresent) {
		isRetCommunityPresent = retCommunityPresent;
	}
}
