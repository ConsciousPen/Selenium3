package aaa.modules.financials.template;

import aaa.modules.financials.FinancialsBaseTest;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

	protected void testNewBusinessScenario_1() {
		mainApp().open();
		createCustomerIndividual();
		createFinancialPolicy();
	}

}
