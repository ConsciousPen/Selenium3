package aaa.modules.regression.document_fulfillment.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.modules.regression.document_fulfillment.template.functional.TestDecPlcyVehInfoHelper;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestDecPlcyVehInfo extends AutoCaChoiceBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name DecPage PlcyVehInfo content check
	 * @scenario 1. Create customer
	 * 2. Create a policy with  vehicle with valid VIN and with OTHER vehicle
	 * 3. Check AA02CA contains PlcyVehInfo
	 * 4. Endorse, issue
	 * 5. Check AA02CA contains PlcyVehInfo
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = "PAS-14156")
	public void pas14156_DecPlcyVehInfo(@Optional("CA") String state) {
		TestData policyTdAdjusted = getPolicyTD()
				.adjust("VehicleTab", getTestSpecificTD("TestData").getTestDataList("VehicleTab"))
				.adjust("AssignmentTab", getTestSpecificTD("TestData").getTestData("AssignmentTab"))
				.adjust("PremiumAndCoveragesTab", getTestSpecificTD("TestData").getTestData("PremiumAndCoveragesTab"))
				.adjust("DocumentsAndBindTab", getTestSpecificTD("TestData").getTestData("DocumentsAndBindTab"));
		TestData endorsementTd = getPolicyTD("Endorsement", "TestData");

		TestDecPlcyVehInfoHelper testDecPlcyVehInfoHelper = new TestDecPlcyVehInfoHelper();
		testDecPlcyVehInfoHelper.pas14156_DecPagePlcyVehInfoBody(getPolicyType(), policyTdAdjusted, endorsementTd, "AA02CA", DocGenEnum.Documents.AA02CA);
	}
}
