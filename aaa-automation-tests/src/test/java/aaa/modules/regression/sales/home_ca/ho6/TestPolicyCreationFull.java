package aaa.modules.regression.sales.home_ca.ho6;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO6BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kazarnovskiy Lev
 * <b> TestPolicyCreation CaHO6 Full </b>
 * <p> Steps::
 * <p> 1. Create new or open existent Customer;
 * <p> 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO6;
 * <p> 3. Fill all mandatory fields;
 * <p> 4. Add Endorsement form
 * <p> 5. Calculate premium;
 * <p> 6. Issue policy;
 * <p> 7. Check Policy status is Active.
 *
 *
 */
public class TestPolicyCreationFull extends HomeCaHO6BaseTest {

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6)
	public void testPolicyCreationFull(@Optional("CA") String state) {

		TestData td =getTestSpecificTD("TestDataFull");

		mainApp().open();

		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, BindTab.class, true);
		new BindTab().btnPurchase.click();
		new ErrorTab().fillTab(td).submitTab();
		new PurchaseTab().fillTab(td).submitTab();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: CaHO6 Full Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}

}

