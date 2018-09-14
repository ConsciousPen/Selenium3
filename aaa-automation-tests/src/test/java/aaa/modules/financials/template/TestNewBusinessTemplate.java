package aaa.modules.financials.template;

import aaa.modules.financials.FinancialsBaseTest;
import toolkit.utils.datetime.DateTimeUtils;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

	protected void testNBZ_01() {
		mainApp().open();
		createCustomerIndividual();
		createFinancialPolicy();
	}

	protected void testNBZ_03() {
		mainApp().open();
		createCustomerIndividual();
		createFinancialPolicy(adjustTdEffectiveDate(getPolicyTD(), DateTimeUtils.getCurrentDateTime().plusWeeks(1).format(DateTimeUtils.MM_DD_YYYY)));
	}

}
