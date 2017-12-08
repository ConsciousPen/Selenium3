package aaa.modules.regression.sales.auto_ss.functional.preconditions;

import aaa.helpers.config.CustomTestProperties;
import toolkit.config.PropertyProvider;

public interface EvalueInsertSetupPreConditions {

	String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);

	String DOC_GEN_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://soaqa3.tent.trt.csaa.pri/3.1/StandardDocumentService'\n"
			+ "where propertyname = 'docGenwebClient.endpointUri'";

	String AAA_RETRIEVE_AGREEMENT_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://soaqa3.tent.trt.csaa.pri/1.1/RetrieveAgreementRelatedDocuments'\n"
			+ "where propertyname = 'aaaRetrieveAgreementWebClient.endpointUri'";

	String AAA_RETRIEVE_DOCUMENT_WEB_CLIENT = "update propertyconfigurerentity\n"
			+ "set value = 'http://soaqa3.tent.trt.csaa.pri/1.1/RetrieveDocument'\n"
			+ "where propertyname = 'aaaRetrieveDocumentWebClient.endpointUri'";

	String EVALUE_PRIOR_BI_CONFIG_INSERT = "INSERT ALL\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values('BaseProductLookupValue', 'priorBILimits', '25000/50000', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values('BaseProductLookupValue', 'priorBILimits', '50000/100000', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_CURRENT_BI_CONFIG_INSERT = "INSERT ALL\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id) \n"
			+ "        values ('BaseProductLookupValue', 'currentBILimits', '100000/300000', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values ('BaseProductLookupValue', 'currentBILimits', '50000/100000', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), null ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_TERRITORY_CHANNEL_FOR_VA_CONFIG_UPDATE = "update lookupvalue\n"
			+ "set territorycd = '212'\n" //mid-Atlantic
			+ ", channelCd = 'AZ Club Agent'\n" //AAA Agent
			+ "WHERE LOOKUPLIST_ID IN (\n"
			+ "    SELECT ID \n"
			+ "    FROM PASADM.LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME LIKE '%Rollout%') \n"
			+ "AND CODE='eMember' \n"
			+ "and RiskStateCd = 'VA'";

	String EVALUE_CONFIGURATION_PER_STATE_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ " (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id)\n"
			+ " values\n"
			+ " ('AAARolloutEligibilityLookupValue', 'eMember', 'TRUE', 'AAA_SS', '%s', null, null, null,\n"
			+ " (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	String PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ " (dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id)\n"
			+ " values\n"
			+ " ('AAARolloutEligibilityLookupValue', 'PaperlessPreferences', 'TRUE', 'AAA_SS', '%s', null, null, null,\n"
			+ " (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	String EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(DTYPE, CODE, DISPLAYVALUE, PRODUCTCD, RISKSTATECD, EFFECTIVE, EXPIRATION, LOOKUPLIST_ID)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'currentBILimits', '50000/100000', 'AAA_SS', '%s', TO_DATE('1-MAY-2017'), TO_DATE('1-MAY-2018'),\n"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))";

	String EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(DTYPE, CODE, DISPLAYVALUE, PRODUCTCD, RISKSTATECD, EFFECTIVE, EXPIRATION, LOOKUPLIST_ID)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'priorBILimits', '25000/50000', 'AAA_SS', '%s', TO_DATE('1-MAY-2017'), TO_DATE('1-MAY-2018'),\n"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))";

	String EVALUE_MEMBERSHIP_ELIGIBILITY_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n";

	String REFUND_DOCUMENT_GENERATION_CONFIGURATION_INSERT_SQL = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd, lookuplist_id)\n"
			+ "values\n"
			+ "('AAARolloutEligibilityLookupValue', 'pcDisbursementEngine', 'TRUE', null, 'VA', (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	String PAYMENT_CENTRAL_STUB_ENDPOINT_UPDATE = "update PROPERTYCONFIGURERENTITY\n"
			+ "set value ='http://%s:9098/aaa-external-stub-services-app/recordFinancialAccount.do'\n"
			+ "where propertyname in('aaaBillingAccountUpdateActionBean.ccStorateEndpointURL','aaaPurchaseScreenActionBean.ccStorateEndpointURL','aaaBillingActionBean.ccStorateEndpointURL')\n";

	String PAPERLESS_PREFERENCE_API_SERVICE_UPDATE = "update propertyconfigurerentity\n"
			+ "set value = 'http://%s:9098/aaa-external-stub-services-app/ws/policy/preferences'\n"
			+ "where propertyname = 'policyPreferenceApiService.policyPreferenceApiUri'";

	String AHDRXX_CONFIG_CHECK = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'AHDRXX'";

	String AHDEXX_CONFIG_CHECK = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'AHDEXX'";

	String AHDRXX_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('AAARolloutEligibilityLookupValue', 'AHDRXX', 'TRUE', 'AAA_SS', 'VA',null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	String AHDEXX_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n"
			+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "values\n"
			+ "('AAARolloutEligibilityLookupValue', 'AHDEXX', 'TRUE', 'AAA_SS', 'VA',null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup'))";

	String RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_UPDATE = "update propertyconfigurerentity\n"
			+ "set value = 'http://%s:9098/aaa-external-stub-services-app/ws/membershipsummary'\n"
			+ "where propertyname = 'retrieveMembershipSummaryServiceImpl.endpointRetrieveMembershipSummaryUri'";

	String EVALUE_MEMBERSHIP_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-10 from dual), (select SYSDATE-6 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'membershipEligibility', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_CURRENT_BI_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values ('BaseProductLookupValue', 'currentBIRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-13 from dual), (select SYSDATE-11 from dual) ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "    INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "        values ('BaseProductLookupValue', 'currentBIRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual) ,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_MYPOLICY_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'myPolicyRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'myPolicyRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_PAYPLAN_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paymentPlanRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-20 from dual), (select SYSDATE-17 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paymentPlanRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_CREDITCARD_CONFIG_ACKNOWLEDGEMENT_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'pciCreditCard', 'TRUE', 'AAA_SS', 'VA',(select SYSDATE-13 from dual), (select SYSDATE-11 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentMethods'))\n"
			+ "Select * from dual";

	String EVALUE_PAPERLESS_PREFERENCES_CONFIG_BLUE_BOX_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paperlessPreferencesRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'paperlessPreferencesRequired', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	String EVALUE_PRIOR_INSURANCE_CONFIG_BLUE_BOX_INSERT = "INSERT ALL\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'priorInsurance', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-16 from dual), (select SYSDATE-14 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "	INTO LOOKUPVALUE (dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
			+ "		values ('BaseProductLookupValue', 'priorInsurance', 'FALSE', 'AAA_SS', 'VA',(select SYSDATE-5 from dual), (select SYSDATE-1 from dual),(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n"
			+ "Select * from dual";

	String REFUND_CONFIG_UPDATE = "update LOOKUPVALUE\n"
			+ "set  DISPLAYVALUE='TRUE' where CODE='eRefunds'";

	String LAST_PAYMENT_METHOD_STUB_POINT_UPDATE = "update propertyconfigurerentity\n"
			+ "set value = 'http://%s:9098/aaa-external-stub-services-app/ws/billing/lastPayment'\n"
			+ "where propertyname = 'lastPaymentService.lastPaymentServiceUrl'";

	String PENDING_REFUND_CONFIGURATION_UPDATE = "update BILLINGREFUNDPAYMENTMETHOD set DEFAULTREFUNDMETHOD = 'pendingRefund' where id = (select id from BILLINGREFUNDPAYMENTMETHOD)";
}
