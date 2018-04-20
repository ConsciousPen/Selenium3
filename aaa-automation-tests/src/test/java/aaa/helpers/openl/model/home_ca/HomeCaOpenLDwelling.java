package aaa.helpers.openl.model.home_ca;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DWELLING_SHEET_NAME, headerRowIndex = OpenLFile.DWELLING_HEADER_ROW_NUMBER)
public class HomeCaOpenLDwelling {
	protected List<HomeCaOpenLAddress> address;

	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

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
