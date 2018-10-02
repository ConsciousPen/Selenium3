package aaa.helpers.openl.model.home_ca;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public abstract class HomeCaOpenLFile<P extends HomeCaOpenLPolicy<?, ?>> extends OpenLFile<P> {
	@ExcelTransient
	public static final int SCHEDULED_PROPERTY_ITEM_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	public static final String SCHEDULED_PROPERTY_ITEM_SHEET_NAME = "Batch- ScheduledPropertyItem";

	@ExcelTransient
	protected List<HomeCaOpenLAddress> address;
	@ExcelTransient
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
