package aaa.modules.financials;

import java.util.ArrayList;
import java.util.List;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.PolicyBaseTest;

public class FinancialsBaseTest extends PolicyBaseTest {

	
	protected List<PolicyType> getPolicyTypes() {
		List<PolicyType> types = new ArrayList<>();
		types.add(PolicyType.AUTO_CA_CHOICE); types.add(PolicyType.AUTO_CA_SELECT); types.add(PolicyType.AUTO_SS); types.add(PolicyType.PUP);
		types.add(PolicyType.HOME_SS_HO3); types.add(PolicyType.HOME_SS_HO4); types.add(PolicyType.HOME_SS_HO6); types.add(PolicyType.HOME_SS_DP3);
		types.add(PolicyType.HOME_CA_HO3); types.add(PolicyType.HOME_CA_HO4); types.add(PolicyType.HOME_CA_HO6); types.add(PolicyType.HOME_CA_DP3);
		return types;
	}

	protected List<PolicyType> getSSPolicyTypes() {
		List<PolicyType> types = new ArrayList<>();
		types.add(PolicyType.AUTO_SS);
		types.add(PolicyType.HOME_SS_HO3); types.add(PolicyType.HOME_SS_HO4); types.add(PolicyType.HOME_SS_HO6); types.add(PolicyType.HOME_SS_DP3);
		return types;
	}

	protected List<PolicyType> getCAPolicyTypes() {
		List<PolicyType> types = new ArrayList<>();
		types.add(PolicyType.AUTO_CA_CHOICE); types.add(PolicyType.AUTO_CA_SELECT);
		types.add(PolicyType.HOME_CA_HO3); types.add(PolicyType.HOME_CA_HO4); types.add(PolicyType.HOME_CA_HO6); types.add(PolicyType.HOME_CA_DP3);
		return types;
	}

}
