package aaa.modules.conversion.manual;

import com.google.common.collect.ImmutableMap;
import aaa.main.modules.policy.PolicyType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return formatter.format(date);
    }

}
