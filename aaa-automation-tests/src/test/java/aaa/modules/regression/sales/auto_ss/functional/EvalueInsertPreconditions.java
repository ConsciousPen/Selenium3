package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Test;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

import java.util.Arrays;
import java.util.List;

public class EvalueInsertPreconditions {

	private static final String DOC_GEN_WEB_CLIENT = "update propertyconfigurerentity\n" +
			"set value = 'http://soaqa3.tent.trt.csaa.pri/3.1/StandardDocumentService'\n" +
			"where propertyname = 'docGenwebClient.endpointUri'";

	private static final String AAA_RETRIEVE_AGREEMENT_WEB_CLIENT = "update propertyconfigurerentity\n" +
			"set value = 'http://soaqa3.tent.trt.csaa.pri/1.1/RetrieveAgreementRelatedDocuments'\n" +
			"where propertyname = 'aaaRetrieveAgreementWebClient.endpointUri'";

	private static final String AAA_RETRIEVE_DOCUMENT_WEB_CLIENT = "update propertyconfigurerentity\n" +
			"set value = 'http://soaqa3.tent.trt.csaa.pri/1.1/RetrieveDocument'\n" +
			"where propertyname = 'aaaRetrieveDocumentWebClient.endpointUri'";

	private static final String EVALUE_PRIOR_BI_CONFIG_UPDATE = "update LOOKUPVALUE\n" +
			"set EFFECTIVE = (select SYSDATE-5 from dual)\n" +
			"WHERE LOOKUPLIST_ID IN \n" +
			"    (SELECT ID \n" +
			"    FROM LOOKUPLIST \n" +
			"    WHERE LOOKUPNAME='AAAeMemberQualifications')\n" +
			"and riskstatecd = 'VA'\n" +
			"and productCD = 'AAA_SS'\n" +
			"and code = 'priorBILimits'\n" +
			"and displayvalue = '25000/50000'";


	private static final String EVALUE_PRIOR_BI_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'priorBILimits', '50000/100000', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n";

	private static final String EVALUE_CURRENT_BI_CONFIG_UPDATE = "update LOOKUPVALUE\n" +
			"set EFFECTIVE = (select SYSDATE-5 from dual)\n" +
			"WHERE LOOKUPLIST_ID IN \n" +
			"    (SELECT ID \n" +
			"    FROM LOOKUPLIST \n" +
			"    WHERE LOOKUPNAME='AAAeMemberQualifications')\n" +
			"and riskstatecd = 'VA'\n" +
			"and productCD = 'AAA_SS'\n" +
			"and code = 'currentBILimits'\n" +
			"and displayvalue = '50000/100000'";


	private static final String EVALUE_CURRENT_BI_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'currentBILimits', '100000/300000', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n";

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

	private static final String EVALUE_MEMBERSHIP_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n";

	private static final String REFUND_DOCUMENT_GENERATION_CONFIGURATION_INSERT_SQL = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, lookuplist_id)\n" +
			"values\n" +
			"('AAARolloutEligibilityLookupValue', 'pcDisbursementEngine', 'TRUE', null, 'VA', \n" +
			"(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";


	private static final String PAPERLESSPREFRANCE_API_SERVICE = "update propertyconfigurerentity\n" +
			"set value = 'http://%s:9098/aaa-external-stub-services-app/ws/policy/preferences'\n" +
			"where propertyname = 'policyPreferenceApiService.policyPreferenceApiUri'";



	@Test()
	@TestInfo(isAuxiliary = true)
	public static void paperlessPreferencesConfigUpdate() {
		String app_host = PropertyProvider.getProperty("app.host");
		DBService.get().executeUpdate(String.format(PAPERLESSPREFRANCE_API_SERVICE, app_host));
	}

	@Test()
	@TestInfo(isAuxiliary = true)
	public static void eValueDocGenConfigInsert() {

		DBService.get().executeUpdate(DOC_GEN_WEB_CLIENT);
		DBService.get().executeUpdate(AAA_RETRIEVE_AGREEMENT_WEB_CLIENT);
		DBService.get().executeUpdate(AAA_RETRIEVE_DOCUMENT_WEB_CLIENT);
	}

	@Test()
	@TestInfo(isAuxiliary = true)
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

	@Test()
	@TestInfo(isAuxiliary = true)
	public static void eValuePriorBiCurrentBiConfigUpdateInsert() {

		DBService.get().executeUpdate(EVALUE_PRIOR_BI_CONFIG_UPDATE);
		DBService.get().executeUpdate(EVALUE_PRIOR_BI_CONFIG_INSERT);
		DBService.get().executeUpdate(EVALUE_CURRENT_BI_CONFIG_UPDATE);
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

	@Test ()
	@TestInfo(isAuxiliary = true)
	public static void eValueTerritoryChannelForVAConfigUpdate() {
		DBService.get().executeUpdate(EVALUE_TERRITORY_CHANNEL_FOR_VA_CONFIG_UPDATE);
	}

	@Test()
	@TestInfo(isAuxiliary = true)
	public static void eValueMembershipConfigInsert() {
		DBService.get().executeUpdate(EVALUE_MEMBERSHIP_CONFIG_INSERT);
	}

	@Test()
	@TestInfo(isAuxiliary = true)
	public static void refundDocumentGenerationConfigInsert() {
		DBService.get().executeUpdate(String.format(REFUND_DOCUMENT_GENERATION_CONFIGURATION_INSERT_SQL));
	}
}
