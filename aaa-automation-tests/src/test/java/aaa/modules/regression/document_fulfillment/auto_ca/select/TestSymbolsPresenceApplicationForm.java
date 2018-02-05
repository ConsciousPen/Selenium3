package aaa.modules.regression.document_fulfillment.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.modules.regression.sales.template.functional.TestSymbolsPresenceTemplate;
import toolkit.utils.TestInfo;

public class TestSymbolsPresenceApplicationForm extends TestSymbolsPresenceTemplate {
	private GenerateOnDemandDocumentActionTab generateOnDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Viktor Petrenko
	 * @name doc gen check for AA11CA
	 * @scenario
	 * 1. Issue CA policy
	 * 2. Get DeclarationPage from db
	 * 3. Check symbols presence CompDmgSymbl and CollDmgSymbl
	 * 4. Check stat code
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-2713")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2713")
	// All states except CA / NY document is generated with "N/A" in the current STAT field
	public void pas2713_ApplicationFormStatCodeNotNA(@Optional("CA") String state) {
		verifySymbolsPresenceInDocs();
	}
}
