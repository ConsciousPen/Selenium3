package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestPolicyCreateHO3Full extends HomeCaHO3BaseTest {

	/**
	 * @author Jurij Kuznecov
	 * @name Test Create CAH Policy HO3-Full
	 * @scenario
	 * 1. Create new or open existent Customer;
	 * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO3;
	 * 3. Fill all mandatory fields;
	 * 4. Add Endorsement form
	 * 5. Calculate premium;
	 * 6. Issue policy;
	 * 7. Check Policy status is Active.
	 */

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyCreation() {

		CustomAssert.assertTrue("NOT COMPLETED TEST: add HO3_FULL test data from old project's file \"CA_HSS_Smoke.xls\"", false);
		
		TestData td = tdPolicy.getTestData("DataGather", "TestData").adjust("EndorsementAddForm", "TestData");

		mainApp().open();

		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, EndorsementTab.class, false);

		// add Endorsement form HO-61C
		new EndorsementTab().tblOptionalEndorsements.getRow(HomeCaMetaData.EndorsementTab.OptionalEndorsementsTblHeaders.FORM_ID.get(), "HO-61C")
				.getCell(HomeCaMetaData.EndorsementTab.OptionalEndorsementsTblHeaders.CONTROLS.get()).controls.links.get("Add").click();
		EndorsementTab.buttonSave.click();

		policy.getDefaultView().fillFromTo(td, EndorsementTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}
