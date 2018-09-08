package aaa.helpers.db.queries;

import aaa.main.modules.policy.PolicyType;
import toolkit.db.DBService;

import java.util.Optional;


public class TimePointQueries {

    /**
     * Gets the appropriate renewal point from the DB for Renewal Timepoint 1 / STG3
     * @param policyType provides the appropriate productcd for the query.
     * @param state provides the appropriate riskstatecd for the query.
     * @return An integer that should be subtracted from renewal date.
     */
    public static int getRenewalTimePoint1_STG3(PolicyType policyType, String state){

        return getRenewalTimePoint("MEMCHECKPOINT1", policyType, state);

    }

    /**
     * Gets the appropriate renewal point from the DB for Renewal Timepoint 2 / STG4
     * @param policyType provides the appropriate productcd for the query.
     * @param state provides the appropriate riskstatecd for the query.
     * @return An integer that should be subtracted from renewal date.
     */
    public static int getRenewalTimePoint2_STG4(PolicyType policyType, String state){
        return getRenewalTimePoint("MEMCHECKPOINT2", policyType, state);
    }

    /**
     * Gets the Renewal TimePoint to be subtracted from the renewal date.
     * @param columnName is to locate the appropriate Timepoint.
     * @param policyType determines what productcd to query against.
     * @param state determines which US state to check timepoints on.
     * @return Query that will get the renewal timepoint.
     */
    private static int getRenewalTimePoint(String columnName, PolicyType policyType, String state){

        String productCd = getProductCdFromPolicy(policyType);
        String trimmedState = state.trim();

        String query =
                String.format(
                        "select %1s" +
                                " from lookupvalue " +
                                "where LOOKUPLIST_ID in (Select ID from PASADM.LOOKUPLIST " +
                                "where Lookupname='RenewalMembershipCheckPoint') and productcd in ('%2s') and RISKSTATECD='%s'",
                        columnName,    //%1s
                        productCd,     //%2s
                        trimmedState); //%3s Had to remove the 3 from the final %s because for some reason was inserting a space?

        Optional<String> response = DBService.get().getValue(query);

        if (!response.isPresent()){
            throw new NullPointerException(String.format(
                    "Could not find a Renewal Timepoint match in DB for productCd [%1s] and state [%2s]",
                    productCd,      // %1s
                    trimmedState)); // %2s
        }

        return Integer.parseInt(response.get());
    }


    /**
     * Narrows down the productcd from the policy type.
     * @param policyType to map against.
     * @return productcd for the final SQL query.
     * @throws IllegalArgumentException if you somehow pass in a policy that is not mappable to a productcd.
     */
    private static String getProductCdFromPolicy(PolicyType policyType)throws IllegalArgumentException {

        // Cut down possibilities to only the specific needed part of label.
        String policyShortName = policyType.getShortName().substring(0, 6);

        switch(policyShortName) {

            case "AutoSS":
                return "AAA_SS";

            case "AutoCA":
                return "AAA_CSA";

            case "HomeSS":
                return "AAA_HO_SS";

            case "HomeCA":
                return "AAA_HO_CA";
        }

        throw new IllegalArgumentException("Could not determine productcd from policyType: " + policyType.getShortName());
    }
}
