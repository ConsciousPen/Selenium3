package aaa.helpers.openl.model.home_ca;

import static aaa.helpers.openl.model.OpenLFile.ADDRESS_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.ADDRESS_SHEET_NAME;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableColumnElement;
import aaa.utils.excel.bind.ExcelTableElement;

public class HomeCaOpenLDwelling {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	@ExcelTableElement(sheetName = ADDRESS_SHEET_NAME, headerRowNumber = ADDRESS_HEADER_ROW_NUMBER)
	protected List<HomeCaOpenLAddress> address;

	protected Integer ppcValue;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public List<HomeCaOpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<HomeCaOpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	public Integer getPpcValue() {
		return ppcValue;
	}

	public void setPpcValue(Integer ppcValue) {
		this.ppcValue = ppcValue;
	}

	@Override
	public String toString() {
		return "HomeCaOpenLDwelling{" +
				"number=" + number +
				", address=" + address +
				", ppcValue=" + ppcValue +
				'}';
	}
}
