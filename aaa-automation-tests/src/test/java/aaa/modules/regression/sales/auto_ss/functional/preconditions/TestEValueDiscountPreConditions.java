package aaa.modules.regression.sales.auto_ss.functional.preconditions;

public interface TestEValueDiscountPreConditions {

    String EVALUE_CONFIGURATION_PER_STATE_CHECK =
            "select dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id from LOOKUPVALUE\n"
                    + " where lookuplist_id = \n"
                    + " (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n"
                    + " and CODE = 'eMember'\n"
                    + " and RISKSTATECD = '%s'";

    String PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_CHECK =
            "select dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id from LOOKUPVALUE\n"
                    + " where lookuplist_id = \n"
                    + " (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n"
                    + " and CODE = 'PaperlessPreferences'\n"
                    + " and RISKSTATECD = '%s'";

    String EVALUE_STATUS_CHECK = "select evaluestatus from(\n"
            + "select ps.id, em.EVALUESTATUS  from  policysummary ps\n"
            + "join AAAEMemberDetailsEntity em on em.id = ps.EMEMBERDETAIL_ID\n"
            + "where ps.policynumber = '%s'\n"
            + "order by ps.id desc)\n"
            + "where rownum=1";

    String EVALUE_TERRITORY_FOR_VA_CONFIG_CHECK = "select TerritoryCD from(\n"
            + "SELECT code, displayvalue, effective, productCd, riskstatecd, territoryCd, channelCd, underwritercd \n"
            + "FROM LOOKUPVALUE \n"
            + "WHERE LOOKUPLIST_ID IN (\n"
            + "    SELECT ID \n"
            + "    FROM PASADM.LOOKUPLIST \n"
            + "    WHERE LOOKUPNAME LIKE '%Rollout%') \n"
            + "    AND CODE='eMember' \n"
            + "    and RiskStateCd = 'VA')";

    String EVALUE_CHANNEL_FOR_VA_CONFIG_CHECK = "select ChannelCd from(\n"
            + "SELECT code, displayvalue, effective, productCd, riskstatecd, territoryCd, channelCd, underwritercd \n"
            + "FROM LOOKUPVALUE \n"
            + "WHERE LOOKUPLIST_ID IN (\n"
            + "    SELECT ID \n"
            + "    FROM PASADM.LOOKUPLIST \n"
            + "    WHERE LOOKUPNAME LIKE '%Rollout%') \n"
            + "    AND CODE='eMember' \n"
            + "    and RiskStateCd = 'VA')";

    String EVALUE_CURRENT_BI_CONFIG_CHECK = "select effective from (\n"
            + "SELECT code, displayValue, productCd, riskStateCd, effective, expiration \n"
            + "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
            + "    (SELECT ID \n"
            + "    FROM LOOKUPLIST \n"
            + "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
            + "and riskstatecd = 'VA'\n"
            + "and productCD = 'AAA_SS'\n"
            + "and code = 'currentBILimits'\n"
            + "and displayvalue = '50000/100000')";

    String EVALUE_PRIOR_BI_CONFIG_CHECK = "select Effective from (\n"
            + "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
            + "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
            + "    (SELECT ID \n"
            + "    FROM LOOKUPLIST \n"
            + "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
            + "and riskstatecd = 'OR'\n"
            + "and productCD = 'AAA_SS'\n"
            + "and code = 'priorBILimits'\n"
            + "and displayvalue = '25000/50000')";

    String EVALUE_MEMBERSHIP_CONFIG_CHECK = "select Effective from (\n"
            + "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
            + "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
            + "    (SELECT ID \n"
            + "    FROM LOOKUPLIST \n"
            + "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
            + "and riskstatecd = 'OR'\n"
            + "and productCD = 'AAA_SS'\n"
            + "and code = 'membershipEligibility'\n"
            + "and displayvalue = 'FALSE')";

    String PAPERLESS_PRFERENCE_STUB_POINT = "select VALUE from "
            + "PROPERTYCONFIGURERENTITY"
            + " WHERE propertyname='policyPreferenceApiService.policyPreferenceApiUri' "
            + " and  VALUE= 'http://%s:9098/aaa-external-stub-services-app/ws/policy/preferences'";

    String EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK = "select Effective from (\n"
            + "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
            + "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
            + "    (SELECT ID \n"
            + "    FROM LOOKUPLIST \n"
            + "    WHERE LOOKUPNAME=''''{0}'''')\n"
            + "and riskstatecd = ''''OR''''\n"
            + "and productCD = ''''AAA_SS''''\n"
            + "and code = ''''{1}''''\n"
            + "and displayvalue = ''''{2}'''' \n"
            + "and (SYSDATE-'{'0'}' <= effective and effective < SYSDATE-'{'1'}') \n"
            + "and (SYSDATE-'{'2'}' <= expiration and expiration < SYSDATE-'{'3'}'))";
}
