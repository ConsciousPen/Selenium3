package aaa.modules.regression.sales.auto_ss.functional.PreConditions;

public interface TestAutoPolicyLockPreConditions {
    String INSERT_QUERY = "INSERT INTO lookupValue "
            + "(lookUpList_id,dType,code,displayValue,effective,expiration,productCD,riskStateCD) "
            + "VALUES (%s,'AAAFactorsLockLookupValue','%s','TRUE',%s,null,'AAA_SS','%s')";

    String DELETE_QUERY = "DELETE FROM lookupValue lv "
            + "WHERE lv.lookupList_id IN %s AND CODE = '%s' AND DISPLAYVALUE='TRUE' AND EFFECTIVE=%s AND RISKSTATECD='%s'";
}
