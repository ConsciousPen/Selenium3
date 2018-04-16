package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.soap.aaaCSPolicyRate.CSPolicyRateWSClient;
import aaa.soap.aaaCSPolicyRate.RatePolicyRequest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import javax.xml.datatype.DatatypeConfigurationException;

public class TestAAACSPolicyRate extends AutoCaChoiceBaseTest {

	private CSPolicyRateWSClient csPolicyRateWSClient = new CSPolicyRateWSClient();

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "")
	public void test(@Optional("") String state) throws DatatypeConfigurationException {

		RatePolicyRequest actualResponse = csPolicyRateWSClient.getcsPolicyRateServiceResponse();

	}
}
