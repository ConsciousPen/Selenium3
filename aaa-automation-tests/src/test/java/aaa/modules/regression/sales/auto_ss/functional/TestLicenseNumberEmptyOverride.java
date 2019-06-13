package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.UpdateRulesOverrideActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestLicenseNumberEmptyOverride extends AutoSSBaseTest {

	private PurchaseTab purchaseTab = new PurchaseTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private ErrorTab errorTab = new ErrorTab();

	/**
	*@author Dominykas Razgunas
	*@name Override license number empty on renewal
	*@scenario
	 * 1. Initiate quote creation
	 * 2. Remove License number
	 * 3. Bind Quote
	 * 4. Check the error
	 * 5. Override the error
	 * 6. Bind the quote
	 * 7. Check the overridden error
	*@details
	*/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-29206")
	public void pas29206_OverrideLicenseNumberEmptyAtRenewal(@Optional("AZ") String state) {

		String name = "FirstName";
		String lName = "LastName";

		TestData tdPolicy = getPolicyTD().mask(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()));
		TestData tdCustomer = getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), name)
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), lName);

		mainApp().open();
		createCustomerIndividual(tdCustomer);
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdPolicy, DocumentsAndBindTab.class, true);

		ErrorEnum.Errors.ERROR_AAA_SS190605.setMessage("Driver " + name + " " + lName + " has a blank license number (AAA_SS190605) [for Driver.attributeForRules]");

		DocumentsAndBindTab.btnPurchase.click();
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS190605);
		errorTab.overrideAllErrors(ErrorEnum.Duration.TERM, ErrorEnum.ReasonForOverride.OTHER);
		errorTab.override();
		documentsAndBindTab.submitTab();

		purchaseTab.fillTab(getPolicyTD());
		purchaseTab.submitTab();

		policy.updateRulesOverride().start();

		Map<String, String> query = new HashMap<>();
		query.put(AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.STATUS.getLabel(), "overridden");
		query.put(AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.RULE_NAME.getLabel(), "AAA_SS190605");

		assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRow(query)).isPresent();
		assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRow(query).getCell(AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.DURATION.getLabel()).controls.radioGroups.getFirst()).hasValue(ErrorEnum.Duration.TERM.get());
		UpdateRulesOverrideActionTab.btnCancel.click();
	}
}