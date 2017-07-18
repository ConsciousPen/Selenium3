package aaa.modules.policy.cea;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.cea.defaulttabs.BindTab;
import aaa.main.modules.policy.cea.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.CaliforniaEarthquakeBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test CEA Policy Payments
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Create new CA HSS quote.
 * 3. Fill all tabs and navigate to Bind tab. 
 * 4. On Bind tab click Purchase button. 
 * 6. On Purchase screen add Credit Card and EFT payment methods. 
 * 7. Fill AutoPay section.
 * 8. Make 4 payments with all possible methods: 
 * 		Cash, Check, Credit Card, EFT to purchase the policy.
 * 9. On Policy Summary page verify that all deposit payments are displaying. 
 * @details
 */
public class TestPolicyPayments extends CaliforniaEarthquakeBaseTest {
	
	@Test
	@TestInfo(component = "Policy.PersonalLines.CEA")
	public void testPolicyPayments(){
		mainApp().open();
		
		createCustomerIndividual();
		
		TestData td = tdPolicy.getTestData("DataGather", "TestData");
		td = adjustHO3PrimaryPolicy(td, getPrimaryHO3Policy());
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, BindTab.class, true);
		new BindTab().submitTab();
		
		new PurchaseTab().fillTab(tdPolicy.getTestData(this.getClass().getSimpleName(), "TestData")).submitTab();
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.BOUND);	
		
		log.info("TEST: Payments for HSS Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
				
		Dollar depositAmount = new Dollar(PolicySummaryPage.tablePaymentSummary.getColumn("Term Premium").getCell(1).getValue()).subtract(new Dollar(100));

		checkPaymentIsDisplaying("Cash", "$10.00");
		checkPaymentIsDisplaying("Check", "$40.00");
		checkPaymentIsDisplaying("Visa ****1111 expiring 10/20", "$50.00");
		checkPaymentIsDisplaying("Checking/Savings (ACH) #,1234567890", depositAmount.toString());
		
	}
	
    private void checkPaymentIsDisplaying(String paymentMethod, String amount){
    	
		Map<String, String> query = new HashMap<>();
		query.put("Payment Method", paymentMethod);
		query.put("Amount Paid", amount);
			
		PolicySummaryPage.tableTransactionSummary.getRow(query).verify.present();
	
    }
}
