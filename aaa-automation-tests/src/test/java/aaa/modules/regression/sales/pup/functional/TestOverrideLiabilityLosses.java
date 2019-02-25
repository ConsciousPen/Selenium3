package aaa.modules.regression.sales.pup.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.MD, Constants.States.PA, Constants.States.DE, Constants.States.NJ, Constants.States.VA})
public class TestOverrideLiabilityLosses extends PersonalUmbrellaBaseTest {

	private ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);
	private PurchaseTab purchaseTab = policy.getDefaultView().getTab(PurchaseTab.class);
	private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);

	/**
	 * @author Dominykas Razgunas
	 * @name Liability losses bind rules can now be overridden
	 * @scenario.document_fulfillment.auto_ss.functional.TestCinNewBusinessAutoSS.testPriorBILimitLessThan500k
	 * 1. Create PUP Policy
	 * 2. Add Liability
	 * 3. Override Bind Error
	 * 4. Issue Policy
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6963")
	public void pas6963_OverrideLiabilityLosses(@Optional("NJ") String state) {

		mainApp().open();
		createCustomerIndividual();

		TestData testdata = getPolicyDefaultTD();

		testdata.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.ClaimsTab.class.getSimpleName()), getTestSpecificTD("TestData_ClaimsTab"))
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.class.getSimpleName()), getTestSpecificTD("TestData_UnderlyingAuto"));

		PolicyType.PUP.get().initiate();
		policy.getDefaultView().fillUpTo(testdata, BindTab.class);
		overrideAndBind();

	}

	//Override rule
private void overrideAndBind() {
	bindTab.submitTab();
	errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_PUP_SS1263335);
	errorTab.overrideAllErrors();
	errorTab.override();
	bindTab.submitTab();

	if(!PolicySummaryPage.labelPolicyNumber.isPresent()){
		purchaseTab.fillTab(getPolicyTD());
		purchaseTab.submitTab();
	}
	assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
}
}