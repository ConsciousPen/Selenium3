package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestSymbolsPresenceTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestSymbolsPresenceApplicationFormForPremCalcQuote extends TestSymbolsPresenceTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Sarunas Jaraminas
	 * @name doc gen check for AA11CA
	 * @scenario
	 * 1. Calculate Premium CA policy
	 * 2. Get DeclarationPage from db
	 * 3. Check symbols presence CompDmgSymbl, CollDmgSymbl and VehSym
	 * 4. Check stat code
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-9064")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-9064")
	public void pas9064_MapCompAndCollSymbolsForDCS(@Optional("CA") String state) {
		verifySymbolsPresenceInDocsAfterPremCalc(); }
}