package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(containsSheetName = OpenLFile.TESTS_SHEET_NAME, headerRowIndex = OpenLFile.TESTS_HEADER_ROW_NUMBER)
public class OpenLTest {

	@ExcelColumnElement(containsName = "p", isPrimaryKey = true)
	protected Integer policy;

	@ExcelColumnElement(containsName = "usState")
	protected String state;

	@ExcelColumnElement(containsName = "_res_")
	protected List<Dollar> premiums;

	@ExcelColumnElement(name = "Total Premium")
	protected Dollar totalPremium;

	public Integer getPolicy() {
		return this.policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<Dollar> getPremiums() {
		return this.premiums != null ? new ArrayList<>(this.premiums) : new ArrayList<>();
	}

	public void setPremiums(List<Dollar> premiums) {
		this.premiums = new ArrayList<>(premiums);
	}

	public Dollar getTotalPremium() {
		return this.totalPremium != null ? this.totalPremium : getPremiums().stream().filter(Objects::nonNull).reduce(Dollar::add).orElse(null);
	}

	public void setTotalPremium(Dollar totalPremium) {
		this.totalPremium = totalPremium;
	}

	@Override
	public String toString() {
		return "OpenLTest{" +
				"policy=" + this.policy +
				", premiums=" + this.premiums +
				", totalPremium=" + this.totalPremium +
				'}';
	}
}
