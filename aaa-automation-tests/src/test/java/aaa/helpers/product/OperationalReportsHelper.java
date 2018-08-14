package aaa.helpers.product;

import toolkit.db.DBService;

public class OperationalReportsHelper {

    private static final String DELETE_EUW_OP_REPORTS_PRIVILEGES = "DELETE S_ROLE_PRIVILEGES\n"
            + "WHERE PRIV_ID IN (SELECT ID FROM S_AUTHORITY WHERE DTYPE='PRIV' AND NAME IN\n"
            + "(SELECT CODE FROM LOOKUPVALUE WHERE CODE LIKE '%euwPremiumReport%' AND LOOKUPLIST_ID =\n"
            + "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='privilegeMappings')))\n"
            + "AND ROLE_ID = (SELECT ID FROM  S_AUTHORITY WHERE DTYPE='ROLE' AND NAME = 'all')";

    private static final String INSERT_EUW_OP_REPORTS_PRIVILEGES = "INSERT INTO S_ROLE_PRIVILEGES\n"
            + "SELECT ROLE.ID, PRIV.ID From (SELECT ID FROM  S_AUTHORITY WHERE DTYPE='ROLE' AND NAME = 'all') ROLE,\n"
            + "(SELECT ID FROM S_AUTHORITY WHERE DTYPE='PRIV' AND NAME IN\n"
            + "(SELECT CODE FROM LOOKUPVALUE WHERE CODE LIKE '%euwPremiumReport%' AND lookuplist_id =\n"
            + "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='privilegeMappings'))) PRIV";


    public static void prepareEuwOpReportsPrivileges() {
        DBService.get().executeUpdate(DELETE_EUW_OP_REPORTS_PRIVILEGES);
        DBService.get().executeUpdate(INSERT_EUW_OP_REPORTS_PRIVILEGES);
    }
}
