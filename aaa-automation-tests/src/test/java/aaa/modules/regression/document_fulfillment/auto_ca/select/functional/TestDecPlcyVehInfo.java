package aaa.modules.regression.document_fulfillment.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.regression.document_fulfillment.template.functional.TestDecPlcyVehInfoHelper;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestDecPlcyVehInfo extends AutoCaSelectBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name DecPage PlcyVehInfo content check
	 * @scenario 1. Create customer
	 * 2. Create a policy with  vehicle with valid VIN and with OTHER vehicle
	 * 3. Check 55 1500 contains PlcyVehInfo
	 * 4. Endorse, issue
	 * 5. Check 55 1500 contains PlcyVehInfo
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = "PAS-14156")
	public void pas14156_DecPagePlcyVehInfo(@Optional("CA") String state) {
		TestData policyTdAdjusted = getPolicyTD()
				.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData").getTestDataList("VehicleTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData").getTestData("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData").getTestData("PremiumAndCoveragesTab"))
				.adjust(new DocumentsAndBindTab().getMetaKey(), getTestSpecificTD("TestData").getTestData("DocumentsAndBindTab"));
		TestData endorsementTd = getPolicyTD("Endorsement", "TestData");

		TestDecPlcyVehInfoHelper testDecPlcyVehInfoHelper = new TestDecPlcyVehInfoHelper();
		testDecPlcyVehInfoHelper.pas14156_DecPagePlcyVehInfoBody(getPolicyType(), policyTdAdjusted, endorsementTd, "55 1500", DocGenEnum.Documents._55_1500);
	}
}
