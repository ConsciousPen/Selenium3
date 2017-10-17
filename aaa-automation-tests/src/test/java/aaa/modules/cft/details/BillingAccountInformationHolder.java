package aaa.modules.cft.details;

import toolkit.exceptions.IstfException;

import java.util.ArrayList;
import java.util.List;

public class BillingAccountInformationHolder {

	private static ThreadLocal<List<BillingAccountDetails>> baDetails = ThreadLocal.withInitial(ArrayList::new);

	public static List<BillingAccountDetails> getBillingAccountsDetailsList() {
		return baDetails.get();
	}

	public static BillingAccountDetails getCurrentBillingAccountDetails() {
		if (getBillingAccountsDetailsList().isEmpty()) {
			throw new IstfException("There are no registered BA for current thread/test");
		}
		return getBillingAccountsDetailsList().get(getBillingAccountsDetailsList().size() - 1);
	}

	public static void addBillingAccountDetails(BillingAccountDetails billingAccountDetails) {
		baDetails.get().add(billingAccountDetails);
	}
}
