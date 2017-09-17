package aaa.modules.e2e.pup;

import aaa.common.enums.Constants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario4;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario4 extends Scenario4 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(),
				PersonalUmbrellaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());
		
		super.createTestPolicy(policyCreationTD);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Overpayment(String state) {
		super.overpayment();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Automatic_refund(String state) {
		super.automaticRefund();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Overpayment_High(String state) {
		super.overpaymentHigh();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Automatic_refund_High(String state) {
		super.automaticRefundHigh();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Endorse_Policy(String state) {
		super.endorsePolicy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Off_Cycle_Bill(String state) {
		super.generateOffCycleBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_CancellNotice(String state) {
		super.generateCancellNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Payment_In_Full_Cancell_Notice_Amount(String state) {
		super.paymentInFullCancellNoticeAmount();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Verify_Form_AHCWXX(String state) {
		super.verifyFormAHCWXX();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Image_Generation(String state) {
		super.renewalImageGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Preview_Generation(String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Offer_Generation(String state) {
		super.renewalOfferGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Premium_Notice(String state) {
		if (!getState().equals(Constants.States.CA)) {
			super.renewalPremiumNotice();
		}
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Expire_Policy(String state) {
		super.expirePolicy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Customer_Decline_Renewal(String state) {
		super.customerDeclineRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Pay_Renew_Offer(String state) {
		super.payRenewOffer();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Bind_Renew(String state) {
		super.bindRenew();
	}
}
