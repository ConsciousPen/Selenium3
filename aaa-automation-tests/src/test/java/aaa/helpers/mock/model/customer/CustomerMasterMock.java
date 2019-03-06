package aaa.helpers.mock.model.customer;

import aaa.helpers.mock.model.AbstractMock;
import aaa.utils.excel.bind.annotation.ExcelTransient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerMasterMock extends AbstractMock {
	@ExcelTransient
	public static final String FILE_NAME = "CustomerMasterMockData.xls";

	private List<CustomerMaster> customerMaster;

	public List<CustomerMaster> getCustomers() {
		return Collections.unmodifiableList(customerMaster);
	}

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	@Override
	public String toString() {
		return "CustomerMasterMock{" +
				"customerMaster=" + customerMaster +
				'}';
	}

	public List<String> getPolicies(String insurerName) {
		List<String> policies = new ArrayList<>();
		for (CustomerMaster cm : getCustomers()) {
			if (insurerName.equals(cm.getInsurerName())) {
				policies.add(cm.getPolicyNumber());
			}
		}
		return policies;
	}
}
