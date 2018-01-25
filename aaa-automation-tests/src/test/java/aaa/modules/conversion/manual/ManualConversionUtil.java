package aaa.modules.conversion.manual;

import aaa.main.modules.policy.PolicyType;
import com.google.common.collect.ImmutableMap;

public class ManualConversionUtil {
    private static final ImmutableMap<String, String> productTypeMap = ImmutableMap.of(
            "HomeSS", "HO3",
            "HomeSS_HO4", "HO4",
            "HomeSS_HO6", "HO6",
            "HomeSS_DP3", "DP3"
    );

    public static String getShortPolicyType(PolicyType policyType) {
        return productTypeMap.get(policyType.getShortName());
    }

}
