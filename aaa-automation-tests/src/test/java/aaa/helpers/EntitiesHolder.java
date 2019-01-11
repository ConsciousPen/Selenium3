package aaa.helpers;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;

public class EntitiesHolder {

	private EntitiesHolder() {
	}

	//private static Map<String, LinkedMap> entities = new HashMap<>();
	private static Map<String, String> singleEntities = new LinkedHashMap<>();
//	private static final int LIMIT_ENTITY = 10;

	public static Map<String, String> getEntities() {
		Map<String, String> returnValue = singleEntities;
		return returnValue;
	}

	public static String makeCustomerKey(CustomerType customerType, String state) {
		String pattern = "%s_%s";
		String customerTypeStr = customerType == null ? "" : customerType.getName();
		String stateStr = state == null ? "" : state;

		return String.format(pattern, customerTypeStr, stateStr).intern();
	}

	public static String makePolicyKey(PolicyType policyType, String state) {
		String pattern = "%s_%s";
		String policyTypeStr = policyType == null ? "" : policyType.getShortName();
		String stateStr = state == null ? "" : state;

		return String.format(pattern, policyTypeStr, stateStr).intern();
	}
	
	public static String makeDefaultPolicyKey(PolicyType policyType, String state) {
		String pattern = "%s_default_%s_%s";
		String policyTypeStr = policyType == null ? "" : policyType.getShortName();
		String stateStr = state == null ? "" : state;

		return String.format(pattern, policyTypeStr, stateStr, TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().toString()).intern();
	}
	
	public static void addNewEntity(String key, String value) {
		singleEntities.put(key, value);
	}

	public static boolean isEntityPresent(String key) {
		return singleEntities.containsKey(key) && StringUtils.isNotBlank(singleEntities.get(key));
	}
	
	public static String getEntity(String key) {
		return singleEntities.get(key);
	}

}
