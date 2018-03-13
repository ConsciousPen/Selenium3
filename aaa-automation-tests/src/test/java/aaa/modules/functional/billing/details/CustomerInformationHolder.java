package aaa.modules.functional.billing.details;

import aaa.modules.cft.details.BillingAccountDetails;
import toolkit.exceptions.IstfException;

import java.util.ArrayList;
import java.util.List;

public class CustomerInformationHolder {

	private static ThreadLocal<List<CustomerAccountDetails>> caDetails = ThreadLocal.withInitial(ArrayList::new);

	public static List<CustomerAccountDetails> getCustomerAccountsDetailsList() {
		return caDetails.get();
	}

	public static CustomerAccountDetails getCurrentCustomerAccountDetails() {
		if (getCustomerAccountsDetailsList().isEmpty()) {
			throw new IstfException("There are no registered customers for current thread/test");
		}
		return getCustomerAccountsDetailsList().get(getCustomerAccountsDetailsList().size() - 1);
	}

	public static void addCustomerAccountDetails(CustomerAccountDetails customerAccountDetails) {
		caDetails.get().add(customerAccountDetails);
	}
}
