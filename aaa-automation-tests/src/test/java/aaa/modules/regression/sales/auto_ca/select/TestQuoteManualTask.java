package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.QuoteManualTask;
import toolkit.utils.TestInfo;

public class TestQuoteManualTask extends QuoteManualTask {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	@Override
	public void testQuoteManualTask() {
		super.testQuoteManualTask();
	}
	
}
