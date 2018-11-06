package aaa.modules.regression.sales.auto_ss.functional.preconditions;

import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

public interface EvalueInsertSetupPreConditions {

	String APP_HOST = PropertyProvider.getProperty(CsaaTestProperties.APP_HOST);
	String APP_STUB_URL = PropertyProvider.getProperty("app.stub.urltemplate");

	String DELETE_OLD_TASKS1 = "delete from ACT_RU_identitylink";
	String DELETE_OLD_TASKS2 = "delete from ACT_RU_TASK";

	/*eValue related endpoints
		String DOC_GEN_WEB_CLIENT = "update propertyconfigurerentity\n"
				+ "set value = 'http://soaqa3.tent.trt.csaa.pri/3.1/StandardDocumentService'\n"
				+ "where propertyname = 'docGenwebClient.endpointUri'";

		String AAA_RETRIEVE_AGREEMENT_WEB_CLIENT = "update propertyconfigurerentity\n"
				+ "set value = 'http://soaqa3.tent.trt.csaa.pri/1.1/RetrieveAgreementRelatedDocuments'\n"
				+ "where propertyname = 'aaaRetrieveAgreementWebClient.endpointUri'";

		String AAA_RETRIEVE_DOCUMENT_WEB_CLIENT = "update propertyconfigurerentity\n"
				+ "set value = 'http://soaqa3.tent.trt.csaa.pri/1.1/RetrieveDocument'\n"
				+ "where propertyname = 'aaaRetrieveDocumentWebClient.endpointUri'";
	*/
	/*PAS18.5 related endpoints*/
	String DOC_GEN_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://soaqa1.tent.trt.csaa.pri:80/3.1/StandardDocumentService'\n"
			+ "where propertyname = 'docGenwebClient.endpointUri'";

	String AAA_RETRIEVE_AGREEMENT_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://soaqa1.tent.trt.csaa.pri:80/1.1/RetrieveAgreementRelatedDocuments'\n"
			+ "where propertyname = 'aaaRetrieveAgreementWebClient.endpointUri'";

	String AAA_RETRIEVE_DOCUMENT_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://soaqa1.tent.trt.csaa.pri:80/1.1/RetrieveDocument'\n"
			+ "where propertyname = 'aaaRetrieveDocumentWebClient.endpointUri'";

	/*PAS18.2-18.4-Master related endpoints*/
	/*String DOC_GEN_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://sit-soaservices.tent.trt.csaa.pri:42000/3.1/StandardDocumentService'\n"
			+ "where propertyname = 'docGenwebClient.endpointUri'";

	String AAA_RETRIEVE_AGREEMENT_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://sit-soaservices.tent.trt.csaa.pri:42000/1.1/RetrieveAgreementRelatedDocuments'\n"
			+ "where propertyname = 'aaaRetrieveAgreementWebClient.endpointUri'";

	String AAA_RETRIEVE_DOCUMENT_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://sit-soaservices.tent.trt.csaa.pri:42000/1.1/RetrieveDocument'\n"
			+ "where propertyname = 'aaaRetrieveDocumentWebClient.endpointUri'";*/

	String EVALUE_PRIOR_BI_CONFIG_INSERT = "INSERT ALL\n"
			+ " INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ " values('BaseProductLookupValue', 'priorBILimits', '25000/50000', 'AAA_SS', 'OR',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ " INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ " values('BaseProductLookupValue', 'priorBILimits', '50000/100000', 'AAA_SS', 'OR',(select SYSDATE-5 from dual), null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_CURRENT_BI_CONFIG_INSERT = "INSERT ALL\n"
			+ " INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id) \n"
			+ " values ('BaseProductLookupValue', 'currentBILimits', '100000/300000', 'AAA_SS', 'OR',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ " INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ " values ('BaseProductLookupValue', 'currentBILimits', '50000/100000', 'AAA_SS', 'OR',(select SYSDATE-5 from dual), null ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_CONFIGURATION_PER_STATE_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ " (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id)\n"
			+ " values\n"
			+ " ('AAARolloutEligibilityLookupValue', 'eValue', 'TRUE', 'AAA_SS', '%s', null, null, null,\n"
			+ " (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	String EVALUE_TERRITORY_CHANNEL_FOR_VA_CONFIG_UPDATE = "update lookupvalue\n"
			+ "set territorycd = '212'\n" //mid-Atlantic
			+ ", channelCd = 'AZ Club Agent'\n" //AAA Agent
			+ "WHERE LOOKUPLIST_ID IN (\n"
			+ " SELECT ID \n"
			+ " FROM LOOKUPLIST \n"
			+ " WHERE LOOKUPNAME LIKE '%Rollout%') \n"
			+ "AND CODE='eValue' \n"
			+ "and RiskStateCd = 'OR'";

	String EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(DTYPE, CODE, DISPLAYVALUE, PRODUCTCD, RISKSTATECD, EFFECTIVE, EXPIRATION, LOOKUPLIST_ID)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'currentBILimits', '50000/100000', 'AAA_SS', '%s', TO_DATE('1-MAY-2017'), TO_DATE('1-MAY-2018'),\n"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))";

	String EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(DTYPE, CODE, DISPLAYVALUE, PRODUCTCD, RISKSTATECD, EFFECTIVE, EXPIRATION, LOOKUPLIST_ID)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'priorBILimits', '25000/50000', 'AAA_SS', '%s', TO_DATE('1-MAY-2017'), TO_DATE('1-MAY-2018'),\n"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))";

	String PAYMENT_CENTRAL_STUB_ENDPOINT_UPDATE = "update PROPERTYCONFIGURERENTITY\n"
			+ "set value ='http://%s%srecordFinancialAccount.do'\n"
			+ "where propertyname in('aaaBillingAccountUpdateActionBean.ccStorateEndpointURL','aaaPurchaseScreenActionBean.ccStorateEndpointURL','aaaBillingActionBean.ccStorateEndpointURL')\n";

	String PAPERLESS_PREFERENCE_API_SERVICE_UPDATE = "update propertyconfigurerentity\n"
			+ "set value = '%s'\n"
			+ "where propertyname = 'policyPreferenceApiService.policyPreferenceApiUri'";

	String AHDRXX_CONFIG_CHECK = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ " (SELECT ID \n"
			+ " FROM LOOKUPLIST \n"
			+ " WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n"
			+ "and riskstatecd = '%s'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'AHDRXX'";

	String AHDEXX_CONFIG_CHECK = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ " (SELECT ID \n"
			+ " FROM LOOKUPLIST \n"
			+ " WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n"
			+ "and riskstatecd = '%s'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'AHDEXX'";

	String AHDRXX_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('AAARolloutEligibilityLookupValue', 'AHDRXX', 'TRUE', 'AAA_SS', '%s', null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	String AHDEXX_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('AAARolloutEligibilityLookupValue', 'AHDEXX', 'TRUE', 'AAA_SS', '%s', null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	String CHANNEL_ID_RESOLVER_STUB_POINT_UPDATE = "update propertyconfigurerentity\n"
			+ "set value = 'http://%s%sws/channelIdResolver'\n"
			+ "where propertyname = 'channelIdRetrievalServiceImpl.channelIdRetrievalUri'";

	String RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_UPDATE = "update propertyconfigurerentity\n"
			+ "set value = 'http://%s%sws/membershipsummary'\n"
			+ "where propertyname = 'retrieveMembershipSummaryServiceImpl.endpointRetrieveMembershipSummaryUri'";

	String EVALUE_MEMBERSHIP_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_CURRENT_BI_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ " INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ " values ('BaseProductLookupValue', 'currentBIRequired', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-13 from dual), (select SYSDATE-11 from dual) ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ " INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ " values ('BaseProductLookupValue', 'currentBIRequired', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual) ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_MYPOLICY_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'myPolicyRequired', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'myPolicyRequired', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_PAYPLAN_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paymentPlanRequired', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-20 from dual), (select SYSDATE-17 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paymentPlanRequired', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_CREDITCARD_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'pciCreditCard', 'TRUE', 'AAA_SS', 'OR',(select SYSDATE-13 from dual), (select SYSDATE-11 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentMethods'))\n"
			+ "Select * from dual";

	String EVALUE_PAPERLESS_PREFERENCES_CONFIG_BLUE_BOX_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paperlessPreferencesRequired', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paperlessPreferencesRequired', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_PRIOR_INSURANCE_CONFIG_BLUE_BOX_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'priorInsurance', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'priorInsurance', 'FALSE', 'AAA_SS', 'OR',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifications'))\n"
			+ "Select * from dual";

	String LAST_PAYMENT_METHOD_STUB_POINT_UPDATE_WIREMOCK = "update propertyconfigurerentity\n"
			+ "set value = '%s/%s/payments/lastTransactionInfo/retrieveByPolicyInfo'\n"
			+ "where propertyname = 'lastPaymentService.lastPaymentServiceUrl'";

	String PENDING_REFUND_CONFIGURATION_UPDATE = "update BILLINGREFUNDPAYMENTMETHOD set DEFAULTREFUNDMETHOD = 'pcDisbursementEngine' where id = (select id from BILLINGREFUNDPAYMENTMETHOD)";

	String AUTHENTICATION_STUB_POINT_UPDATE = "update propertyconfigurerentity\n"
			+ "set value = lower('http://%s%sws/local/authentication')\n"
			+ "where propertyname = 'oAuthClient.oAuthPingUri'";

	String DELETE_UNNECESSARY_PRIVILEGE_FROM_ALL_ROLES = "delete from s_role_privileges\n"
			+ "where priv_id in (select id from s_authority ar\n"
			+ "where name in ('Billing Refund Cash', 'Refund Actions', 'Policy Split'))";

	//original endpoint - https://preferenceUI-perf.tent.trt.csaa.pri/prefmgmt-portal/prefsetup, but none of envs are connected to it.
	String PAPERLESS_PREFERENCES_POPUP_STUB_POINT = "update propertyconfigurerentity\n"
			+ "set value = 'http://localhost:8090/prefmgmt-portal/prefsetup'\n"
			+ "where propertyname = 'aaaPreferenceUrlBuilder.prefSharedUrl'";

	String DXP_AUTHENTICATION_PARAMETERS_INSERT = "INSERT\n"
			+ " INTO PROPERTYCONFIGURERENTITY (author, description, PROPERTYNAME, VALUE)\n"
			+ " values('%s', '%s', '%s', '%s')\n";

	String PROPERTY_CONFIGURER_ENTITY_INSERT = "INSERT\n"
			+ " INTO PROPERTYCONFIGURERENTITY (author, description, PROPERTYNAME, VALUE)\n"
			+ " values('%s', '%s', '%s', '%s')\n";

	String PAPERLESS_PREFERENCES_ELIGIBILITY_CHECK_FOR_PRODUCT = "select dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id\n"
			+ "from LOOKUPVALUE\n"
			+ "where 1=1\n"
			+ "and code = 'PaperlessPreferences'\n"
			+ "and displayValue = 'TRUE'\n"
			+ "and productCd = '%s'\n"
			+ "and riskstatecd = '%s'";

	String PAPERLESS_PREFERENCES_ELIGIBILITY_INSERT_FOR_PRODUCT = "INSERT ALL \n"
			+ " INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id) \n"
			+ " values ('AAARolloutEligibilityLookupValue', 'PaperlessPreferences', 'TRUE', '%s', '%s',(select SYSDATE-27 from dual), (select SYSDATE-23 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))\n"
			+ " Select * from dual";

}
