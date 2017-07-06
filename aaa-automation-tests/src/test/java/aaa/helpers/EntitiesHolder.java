package aaa.helpers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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

		return String.format(pattern, customerTypeStr, stateStr);
	}

	public static String makePolicyKey(PolicyType policyType, String state) {
		String pattern = "%s_%s";
		String policyTypeStr = policyType == null ? "" : policyType.getShortName();
		String stateStr = state == null ? "" : state;

		return String.format(pattern, policyTypeStr, stateStr);
	}
	
	public static String makeDefaultPolicyKey(PolicyType policyType, String state) {
		String pattern = "%s_default_%s";
		String policyTypeStr = policyType == null ? "" : policyType.getShortName();
		String stateStr = state == null ? "" : state;

		return String.format(pattern, policyTypeStr, stateStr);
	}

	public static void addNewEntiry(String key, String value) {
		singleEntities.put(key, value);
	}

	public static boolean isEntityPresent(String key) {
		if (singleEntities.containsKey(key) && StringUtils.isNotBlank(singleEntities.get(key)))
			return true;
		else
			return false;
	}
	
	public static String getEntity(String key) {
		return singleEntities.get(key);
	}

	/*
	 * public static void addNewCustomer(String key, String customerID) { if
	 * (entities.get(key) == null) { LinkedMap entityList = new LinkedMap();
	 * entities.put(key, entityList); } Entity entity = new Entity();
	 * entity.customerID = customerID; entities.get(key).put(customerID,
	 * entity); }
	 * 
	 * public static String getLastCustomerID(String key) { return ((String)
	 * entities.get(key).lastKey()); }
	 * 
	 * public static String getPolicyID(String key, String customerNumber) {
	 * return ((Entity)
	 * entities.get(key).get(customerNumber)).policyNumbers.get(0); }
	 * 
	 * public static boolean isCustomerNotPresent(String key) { if
	 * (entities.get(key) == null || entities.get(key).size() == 0) { return
	 * true; } if (((Entity)
	 * entities.get(key).get(getLastCustomerID(key))).policyNumbers.size() >=
	 * LIMIT_ENTITY) { return true; } return false; }
	 * 
	 * public static boolean isPolicyNotPresent(String key, String
	 * customerNumber) { if (entities.get(key) == null ||
	 * entities.get(key).size() == 0 || ((Entity)
	 * entities.get(key).get(customerNumber)).policyNumbers.size() == 0) {
	 * return true; } return false; }
	 * 
	 * public static void addPolicy(String key, String customerNumber, String
	 * policyNumber) { ((Entity)
	 * entities.get(key).get(customerNumber)).policyNumbers.add(policyNumber); }
	 * 
	 * public static String getKey(CustomerType customerType, PolicyType
	 * policyType, String state) { String pattern = "%s_%s_%s"; String
	 * customerTypeStr = customerType == null ? "" : customerType.getName();
	 * String policyTypeStr = policyType == null ? "" :
	 * policyType.getShortName(); String stateStr = state == null ? "" : state;
	 * 
	 * return String.format(pattern, customerTypeStr, policyTypeStr, stateStr);
	 * }
	 * 
	 * 
	 * 
	 * public static Map<String, LinkedMap> getEntities() { Map<String,
	 * LinkedMap> returnValue = entities; return returnValue; }
	 */

	/*
	 * public static class Entity { public String customerID; public
	 * List<String> policyNumbers;
	 * 
	 * private Entity() { policyNumbers = new ArrayList<>(); customerID = new
	 * String(); } }
	 */

}
