package aaa.modules.regression.sales.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.QuoteManualTask;
import toolkit.utils.TestInfo;

public class TestQuoteManualTask extends QuoteManualTask {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3) 
	@Override
	public void testQuoteManualTask() {
		super.testQuoteManualTask();
	}
	
}
