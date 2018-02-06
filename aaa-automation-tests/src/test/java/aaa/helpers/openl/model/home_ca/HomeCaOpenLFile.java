package aaa.helpers.openl.model.home_ca;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public abstract class HomeCaOpenLFile<P extends HomeCaOpenLPolicy> extends OpenLFile<P> {
	@ExcelTransient
	protected static final int SCHEDULED_PROPERTY_ITEM_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	protected static final String SCHEDULED_PROPERTY_ITEM_SHEET_NAME = "Batch- ScheduledPropertyItem";

	@ExcelTransient
	@ExcelTableElement(sheetName = ADDRESS_SHEET_NAME, headerRowNumber = ADDRESS_HEADER_ROW_NUMBER)
	protected List<HomeCaOpenLAddress> address;

	@ExcelTransient
	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME, headerRowNumber = COVERAGE_HEADER_ROW_NUMBER)
	protected List<HomeCaOpenLCoverage> coverages;

	public List<HomeCaOpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<HomeCaOpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	@Override
	public String toString() {
		return "HomeCaOpenLFile{" +
				"address=" + address +
				", tests=" + tests +
				'}';
	}
}
