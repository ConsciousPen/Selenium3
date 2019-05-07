package aaa.helpers.openl.model.home_ss;

import aaa.common.enums.Constants;
import aaa.helpers.openl.model.OpenLAddress;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import toolkit.db.DBService;

@ExcelTableElement(sheetName = OpenLFile.ADDRESS_SHEET_NAME, headerRowIndex = OpenLFile.ADDRESS_HEADER_ROW_NUMBER)
public class HomeSSOpenLAddress extends OpenLAddress {
	private Boolean isRetCommunityPresent;

	public Boolean getRetCommunityPresent() {
		return isRetCommunityPresent;
	}

	public void setRetCommunityPresent(Boolean retCommunityPresent) {
		isRetCommunityPresent = retCommunityPresent;
	}

	public Boolean isOhioMineSubsidenceCounty() {
		Boolean isOhioMineSubsidenceCounty = false;
		if (getState().equals(Constants.States.OH)) {
			String getCountyQuery = String.format("select c.code from lookupvalue c join lookuplist p on c.lookuplist_id = p.id \n"
					+ "where lookupname = 'AAAHOMineSubsidenceCounties' and type = 'O' and riskstatecd = 'OH' and c.code in \n"
					+ "(select c.county from lookupvalue c join lookuplist p on c.lookuplist_id = p.id  where p.lookupname = 'AAACountyTownship' and c.postalcode = '%s')", getZip());
			isOhioMineSubsidenceCounty = !DBService.get().getValue(getCountyQuery).orElse("").isEmpty();
		}
		return isOhioMineSubsidenceCounty;
	}
}
