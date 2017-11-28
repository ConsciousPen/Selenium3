package aaa.modules.regression.sales.auto_ss.functional;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import aaa.helpers.config.CustomTestProperties;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;

public class EvalueInsertPreconditions {

	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);

	private static final String DOC_GEN_WEB_CLIENT = "update propertyconfigurerentity\n" +
			"set value = 'http://soaqa3.tent.trt.csaa.pri/3.1/StandardDocumentService'\n" +
			"where propertyname = 'docGenwebClient.endpointUri'";

	private static final String AAA_RETRIEVE_AGREEMENT_WEB_CLIENT = "update propertyconfigurerentity\n" +
			"set value = 'http://soaqa3.tent.trt.csaa.pri/1.1/RetrieveAgreementRelatedDocuments'\n" +
			"where propertyname = 'aaaRetrieveAgreementWebClient.endpointUri'";

	private static final String AAA_RETRIEVE_DOCUMENT_WEB_CLIENT = "update propertyconfigurerentity\n" +
			"set value = 'http://soaqa3.tent.trt.csaa.pri/1.1/RetrieveDocument'\n" +
			"where propertyname = 'aaaRetrieveDocumentWebClient.endpointUri'";

	private static final String EVALUE_PRIOR_BI_CONFIG_INSERT = "INSERT ALL\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values('BaseProductLookupValue', 'priorBILimits', '25000/50000', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values('BaseProductLookupValue', 'priorBILimits', '50000/100000', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	private static final String EVALUE_CURRENT_BI_CONFIG_INSERT = "INSERT ALL\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id) \n"
			+ "        values ('BaseProductLookupValue', 'currentBILimits', '50000/100000', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values ('BaseProductLookupValue', 'currentBILimits', '100000/300000', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), null ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	private static final String EVALUE_TERRITORY_CHANNEL_FOR_VA_CONFIG_UPDATE = "update lookupvalue\n" +
			"set territorycd = '212'\n" +//mid-Atlantic
			", channelCd = 'AZ Club Agent'\n" + //AAA Agent
			"WHERE LOOKUPLIST_ID IN (\n" +
			"    SELECT ID \n" +
			"    FROM PASADM.LOOKUPLIST \n" +
			"    WHERE LOOKUPNAME LIKE '%Rollout%') \n" +
			"AND CODE='eMember' \n" +
			"and RiskStateCd = 'VA'";

	private static final String EVALUE_CONFIGURATION_PER_STATE_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			" (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id)\n" +
			" values\n" +
			" ('AAARolloutEligibilityLookupValue', 'eMember', 'TRUE', 'AAA_SS', '%s', null, null, null,\n" +
			" (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	private static final String PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			" (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id)\n" +
			" values\n" +
			" ('AAARolloutEligibilityLookupValue', 'PaperlessPreferences', 'TRUE', 'AAA_SS', '%s', null, null, null,\n" +
			" (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	private static final String EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(DTYPE, CODE, DISPLAYVALUE, PRODUCTCD, RISKSTATECD, EFFECTIVE, EXPIRATION, LOOKUPLIST_ID)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'currentBILimits', '50000/100000', 'AAA_SS', '%s', TO_DATE('1-MAY-2017'), TO_DATE('1-MAY-2018'),\n" +
			"(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))";

	private static final String EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(DTYPE, CODE, DISPLAYVALUE, PRODUCTCD, RISKSTATECD, EFFECTIVE, EXPIRATION, LOOKUPLIST_ID)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'priorBILimits', '25000/50000', 'AAA_SS', '%s', TO_DATE('1-MAY-2017'), TO_DATE('1-MAY-2018'),\n" +
			"(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))";

	private static final String EVALUE_MEMBERSHIP_ELIGIBILITY_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n";

	private static final String EVALUE_MEMBERSHIP_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	private static final String EVALUE_CURRENT_BI_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values ('BaseProductLookupValue', 'currentBIRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-13 from dual), (select SYSDATE-11 from dual) ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values ('BaseProductLookupValue', 'currentBIRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual) ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	private static final String EVALUE_MYPOLICY_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'myPolicyRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'myPolicyRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	private static final String EVALUE_PAYPLAN_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paymentPlanRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-20 from dual), (select SYSDATE-17 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paymentPlanRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	private static final String EVALUE_CREDITCARD_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'pciCreditCard', 'TRUE', 'AAA_SS', 'VA',(select SYSDATE-13 from dual), (select SYSDATE-11 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentMethods'))\n"
			+ "Select * from dual";

	private static final String EVALUE_PAPERLESS_PREFERENCES_CONFIG_BLUE_BOX_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paperlessPreferencesRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paperlessPreferencesRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	private static final String EVALUE_PRIOR_INSURANCE_CONFIG_BLUE_BOX_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'priorInsurance', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'priorInsurance', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	private static final String REFUND_DOCUMENT_GENERATION_CONFIGURATION_INSERT_SQL = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, lookuplist_id)\n" +
			"values\n" +
			"('AAARolloutEligibilityLookupValue', 'pcDisbursementEngine', 'TRUE', null, 'VA', (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	private static final String PAYMENT_CENTRAL_STUB_ENDPOINT_UPDATE = "update PROPERTYCONFIGURERENTITY\n" +
			"set value ='http://%s:9098/aaa-external-stub-services-app/recordFinancialAccount.do'\n" +
			"where propertyname in('aaaBillingAccountUpdateActionBean.ccStorateEndpointURL','aaaPurchaseScreenActionBean.ccStorateEndpointURL','aaaBillingActionBean.ccStorateEndpointURL')\n";

	private static final String PAPERLESS_PREFERENCE_API_SERVICE_UPDATE = "update propertyconfigurerentity\n" +
			"set value = 'http://%s:9098/aaa-external-stub-services-app/ws/policy/preferences'\n" +
			"where propertyname = 'policyPreferenceApiService.policyPreferenceApiUri'";

	private static final String AHDRXX_CONFIG_CHECK = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'AHDRXX'";

	private static final String AHDEXX_CONFIG_CHECK = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'AHDEXX'";

	private static final String AHDRXX_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"(AAARolloutEligibilityLookupValue', 'AHDRXX', 'TRUE', 'AAA_SS', 'VA',null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	private static final String AHDEXX_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"(AAARolloutEligibilityLookupValue', 'AHDEXX', 'TRUE', 'AAA_SS', 'VA',null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	private static final String RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_UPDATE = "update propertyconfigurerentity\n" +
			"set value = 'http://%s:9098/aaa-external-stub-services-app/ws/membershipsummary'\n" +
			"where propertyname = 'retrieveMembershipSummaryServiceImpl.endpointRetrieveMembershipSummaryUri'";

	@Test(description = "Precondition updating Payperless Preferences Endpoint to a Stub")
	public static void paperlessPreferencesStubEndpointUpdate() {
		DBService.get().executeUpdate(String.format(PAPERLESS_PREFERENCE_API_SERVICE_UPDATE, APP_HOST));
	}

	@Test(description = "Precondition updating Membership Summary Endpoint to Stub")
	public static void retrieveMembershipSummaryStubEndpointUpdate() {
		DBService.get().executeUpdate(String.format(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_UPDATE, APP_HOST));
	}

	@Test(description = "Precondition for AHDRXX form generation")
	public static void ahdrxxConfigCheckUpdate() {
		if (!DBService.get().getValue(AHDRXX_CONFIG_CHECK).isPresent()) {
			DBService.get().executeUpdate(AHDRXX_CONFIG_INSERT);
		}
	}

	@Test(description = "Precondition for AHDEXX form generation")
	public static void ahdexxConfigCheckUpdate() {
		if (!DBService.get().getValue(AHDEXX_CONFIG_CHECK).isPresent()) {
			DBService.get().executeUpdate(AHDEXX_CONFIG_INSERT);
		}
	}

	@Test(description = "Precondition for eValue related Document Generation, different endpoint than Master or PAS13")
	public static void eValueDocGenStubEndpointInsert() {
		DBService.get().executeUpdate(DOC_GEN_WEB_CLIENT);
		DBService.get().executeUpdate(AAA_RETRIEVE_AGREEMENT_WEB_CLIENT);
		DBService.get().executeUpdate(AAA_RETRIEVE_DOCUMENT_WEB_CLIENT);
	}

	@Test(description = "Precondition for enabling eValue Configuration for States with Paperless Preferences stubbed")
	public static void eValueConfigInsert() {
		List<String> configForStates = Arrays.asList("VA"  //for Paperless Preferences = Yes
				, "MD"  //for Paperless Preferences = Pending
				, "DC"); //for Paperless Preferences = No
		//PA should not have eValue or Paperless Preferences Configuration
		for (String configForState : configForStates) {
			insertConfigForRegularStates(configForState);
		}

		List<String> configForStatesLimits = Arrays.asList(
				"MD"
				, "DC");
		for (String configForStatesLimit : configForStatesLimits) {
			insertConfigForLimitsRegularStates(configForStatesLimit);
		}
	}

	@Test(description = "Precondition for Current and Prior BI Limits configurations")
	public static void eValuePriorBiCurrentBiConfigUpdateInsert() {
		DBService.get().executeUpdate(EVALUE_PRIOR_BI_CONFIG_INSERT);
		DBService.get().executeUpdate(EVALUE_CURRENT_BI_CONFIG_INSERT);
	}

	private static void insertConfigForRegularStates(String state) {
		DBService.get().executeUpdate(String.format(EVALUE_CONFIGURATION_PER_STATE_INSERT, state));
		DBService.get().executeUpdate(String.format(PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_INSERT, state));
	}

	private static void insertConfigForLimitsRegularStates(String state) {
		DBService.get().executeUpdate(String.format(EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT, state));
		DBService.get().executeUpdate(String.format(EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT, state));
	}

	@Test(description = "Precondition for eValue Channel and Territory configurations")
	public static void eValueTerritoryChannelForVAConfigUpdate() {
		DBService.get().executeUpdate(EVALUE_TERRITORY_CHANNEL_FOR_VA_CONFIG_UPDATE);
	}

	@Test(description = "Precondition for eValue Membership Eligibility configurations")
	public static void eValueMembershipEligibilityConfigInsert() {
		DBService.get().executeUpdate(EVALUE_MEMBERSHIP_ELIGIBILITY_CONFIG_INSERT);
	}

	@Test(description = "Precondition Refund/Payment handling, turning on pcDisbursementEngine related functionality")
	public static void refundDocumentGenerationConfigInsert() {
		DBService.get().executeUpdate(REFUND_DOCUMENT_GENERATION_CONFIGURATION_INSERT_SQL);
	}

	@Test(description = "Precondition for to be able to Add Payment methods, Payment Central is stubbed")
	public static void paymentCentralStubEndPointUpdate() {
		DBService.get().executeUpdate(String.format(PAYMENT_CENTRAL_STUB_ENDPOINT_UPDATE, APP_HOST));
	}

	@Test(description = "Precondition")
	public static void eValueMembershipAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_MEMBERSHIP_CONFIG_ACKNOWLEDGEMENT_INSERT); }

	@Test(description = "Precondition")
	public static void eValueCurrentBIAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_CURRENT_BI_CONFIG_ACKNOWLEDGEMENT_INSERT); }

	@Test(description = "Precondition")
	public static void eValuePayPlanAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_PAYPLAN_CONFIG_ACKNOWLEDGEMENT_INSERT); }

	@Test(description = "Precondition")
	public static void eValueMyPolicyAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_MYPOLICY_CONFIG_ACKNOWLEDGEMENT_INSERT); }

	@Test(description = "Precondition")
	public static void eValueCreditCardAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_CREDITCARD_CONFIG_ACKNOWLEDGEMENT_INSERT); }

	@Test(description = "Precondition")
	public static void eValuePaperlessPreferencesBlueBoxConfigInsert() { DBService.get().executeUpdate(EVALUE_PAPERLESS_PREFERENCES_CONFIG_BLUE_BOX_INSERT); }

	@Test(description = "Precondition")
	public static void eValuePriorInsuranceBlueBoxConfigInsert() { DBService.get().executeUpdate(EVALUE_PRIOR_INSURANCE_CONFIG_BLUE_BOX_INSERT); }
}
