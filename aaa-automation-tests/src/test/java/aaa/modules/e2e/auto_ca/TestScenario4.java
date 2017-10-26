package aaa.modules.e2e.auto_ca;

import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario4;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario4 extends Scenario4 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(),
				AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());

		cashOverpaymentLow = new Dollar(499.99);
		cashOverpaymentHigh = new Dollar(500);
		
		super.createTestPolicy(policyCreationTD);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Overpayment(@Optional("CA") String state) {
		super.overpayment();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Automatic_refund(@Optional("CA") String state) {
		super.automaticRefund();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Overpayment_High(@Optional("CA") String state) {
		super.overpaymentHigh();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Automatic_refund_High(@Optional("CA") String state) {
		super.automaticRefundHigh();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Endorse_Policy(@Optional("CA") String state) {
		super.endorsePolicy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Off_Cycle_Bill(@Optional("CA") String state) {
		super.generateOffCycleBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_CancellNotice(@Optional("CA") String state) {
		super.generateCancelNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Payment_In_Full_Cancell_Notice_Amount(@Optional("CA") String state) {
		super.paymentInFullCancellNoticeAmount();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_Image_Generation(@Optional("CA") String state) {
		super.renewalImageGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Preview_Generation(@Optional("CA") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Offer_Generation(@Optional("CA") String state) {
		super.renewalOfferGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Expire_Policy(@Optional("CA") String state) {
		super.expirePolicy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Customer_Decline_Renewal(@Optional("CA") String state) {
		super.customerDeclineRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Pay_Renew_Offer(@Optional("CA") String state) {
		super.payRenewOffer();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Bind_Renew(@Optional("CA") String state) {
		super.bindRenew();
	}
}
