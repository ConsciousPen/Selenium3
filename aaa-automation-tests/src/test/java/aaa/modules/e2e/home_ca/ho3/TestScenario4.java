package aaa.modules.e2e.home_ca.ho3;

import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario4;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario4 extends Scenario4 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
	
	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(),
				HomeCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());
		
		super.createTestPolicy(policyCreationTD);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Overpayment() {
		super.overpayment();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Automatic_refund() {
		super.automaticRefund();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Overpayment_High() {
		super.overpaymentHigh();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Automatic_refund_High() {
		super.automaticRefundHigh();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Endorse_Policy() {
		super.endorsePolicy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Off_Cycle_Bill() {
		super.generateOffCycleBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_CancellNotice() {
		super.generateCancellNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Payment_In_Full_Cancell_Notice_Amount() {
		super.paymentInFullCancellNoticeAmount();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Verify_Form_AHCWXX() {
		super.verifyFormAHCWXX();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Image_Generation() {
		super.renewalImageGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Preview_Generation() {
		super.renewalPreviewGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Offer_Generation() {
		super.renewalOfferGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Expire_Policy() {
		super.expirePolicy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Customer_Decline_Renewal() {
		super.customerDeclineRenewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Pay_Renew_Offer() {
		super.payRenewOffer();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Bind_Renew() {
		super.bindRenew();
	}
}
