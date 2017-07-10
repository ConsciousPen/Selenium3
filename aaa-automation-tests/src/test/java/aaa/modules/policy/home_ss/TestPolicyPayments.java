package aaa.modules.policy.home_ss;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPolicyPayments extends HomeSSBaseTest {
	
	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyPayments() {
		mainApp().open();
		
		createCustomerIndividual();
		
		TestData tdPolicy = testDataManager.policy.get(getPolicyType());
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(getStateTestData(tdPolicy, "DataGather", "TestData"), BindTab.class, true);
		new BindTab().submitTab();
		
		policy.purchase(tdPolicy.getTestData(this.getClass().getSimpleName(), "TestData"));
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);		
		
		BillingSummaryPage.open();
		
		Dollar depositAmount = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getColumn(11).getCell(1).getValue()).subtract(new Dollar(100));
		
		checkPaymentIsDisplaying(depositAmount.toString());
		checkPaymentIsDisplaying("50.00");
		checkPaymentIsDisplaying("40.00");
		checkPaymentIsDisplaying("10.00");
		
	
	}
	
	private void checkPaymentIsDisplaying(String amount){
	    	
		Map<String, String> query = new HashMap<>();
		query.put("Type", "Payment");
		query.put("Subtype/Reason", "Deposit Payment");
		query.put("Amount", "($" + amount + ")");
				
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query).verify.present();
	}
}
