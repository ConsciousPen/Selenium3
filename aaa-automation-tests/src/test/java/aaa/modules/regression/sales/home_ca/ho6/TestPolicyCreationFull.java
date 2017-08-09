package aaa.modules.regression.sales.home_ca.ho6;

import aaa.main.modules.policy.home_ca.defaulttabs.*;
import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO6BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kazarnovskiy Lev
 * @name TestPolicyCreation CaHO6 Full
 * @scenario:
 * 1. Create new or open existent Customer;
 * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO6;
 * 3. Fill all mandatory fields;
 * 4. Add Endorsement form
 * 5. Calculate premium;
 * 6. Issue policy;
 * 7. Check Policy status is Active.
 *
 * @details
 */
public class TestPolicyCreationFull extends HomeCaHO6BaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyCreation() throws InterruptedException {

		TestData td =getPolicyTD("DataGather", "TestDataFull");

		mainApp().open();

		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, BindTab.class, true);
		new BindTab().btnPurchase.click();
		new ErrorTab().fillTab(td).submitTab();
		new PurchaseTab().fillTab(td).submitTab();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: CaHO6 Full Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}

}

