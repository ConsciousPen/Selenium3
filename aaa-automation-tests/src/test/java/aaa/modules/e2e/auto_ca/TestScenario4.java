package aaa.modules.e2e.auto_ca;

import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario4;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario4 extends Scenario4 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(),
				AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());

		cashOverpaymentLow = new Dollar(499.99);
		cashOverpaymentHigh = new Dollar(500);
		
		super.createTestPolicy(policyCreationTD);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Overpayment() {
		super.TC02_Overpayment();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Automatic_refund() {
		super.TC03_Automatic_refund();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Overpayment_High() {
		super.TC04_Overpayment_High();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Automatic_refund_High() {
		super.TC05_Automatic_refund_High();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Endorse_Policy() {
		super.TC06_Endorse_Policy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Off_Cycle_Bill() {
		super.TC07_Generate_Off_Cycle_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_CancellNotice() {
		super.TC08_Generate_CancellNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Payment_In_Full_Cancell_Notice_Amount() {
		super.TC09_Payment_In_Full_Cancell_Notice_Amount();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Verify_Form_AHCWXX() {
		super.TC10_Verify_Form_AHCWXX();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Image_Generation() {
		super.TC11_Renewal_Image_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Preview_Generation() {
		super.TC12_Renewal_Preview_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Offer_Generation() {
		super.TC13_Renewal_Offer_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Premium_Notice() {
		super.TC14_Renewal_Premium_Notice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Expire_Policy() {
		super.TC15_Expire_Policy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Customer_Decline_Renewal() {
		super.TC16_Customer_Decline_Renewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Pay_Renew_Offer() {
		super.TC17_Pay_Renew_Offer();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Bind_Renew() {
		super.TC18_Bind_Renew();
	}
}
