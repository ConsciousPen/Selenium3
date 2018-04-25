package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.TESTS_SHEET_NAME, headerRowIndex = OpenLFile.TESTS_HEADER_ROW_NUMBER)
public class OpenLTest {

	@ExcelColumnElement(isPrimaryKey = true)
	private Integer policy;

	@ExcelColumnElement(containsName = "usState")
	private String state;

	@ExcelColumnElement(containsName = "_res_")
	private List<Integer> premiums;

	@ExcelColumnElement(name = "Total Premium")
	private Integer totalPremium;

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

	public List<Integer> getPremiums() {
		return new ArrayList<>(this.premiums);
	}

	public void setPremiums(List<Integer> premiums) {
		this.premiums = new ArrayList<>(premiums);
	}

	public Dollar getTotalPremium() {
		Integer totalPremium = this.totalPremium != null ? this.totalPremium : getPremiums().stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
		return new Dollar(totalPremium);
	}

	public void setTotalPremium(int totalPremium) {
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
