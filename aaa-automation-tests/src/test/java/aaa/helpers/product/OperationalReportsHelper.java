package aaa.helpers.product;

import aaa.main.enums.OperationalReportsConstants;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationalReportsHelper {

    public static final String EXCEL_FILE_EXTENSION = ".xlsx";
    private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + PropertyProvider.getProperty("test.downloadfiles.location");

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

    public static List<String> getOpReportTableHeaders(String originalFileName) {
        return getEuwOpReportTable(originalFileName).getHeader().getColumnsNames();
    }

    public static Map<String, Double> getOpReportsTotals(String originalFileName) {
        List<TableRow> rows = getEuwOpReportTable(originalFileName).getRows();

        double writtenPremium = 0;
        double unearnedPremium = 0;
        double earnedPremium = 0;
        double changeInUnearnedMTD = 0;
        double changeInUnearnedYTD = 0;

        HashMap<String, Double> totalsFromOpReport = new HashMap<>();

        for (TableRow row: rows.subList(0, rows.size() - 1)) {
            writtenPremium += row.getDoubleValue(OperationalReportsConstants.EuwDetailOpReportTableHeaders.WRITTEN_PREMIUM);
            unearnedPremium += row.getDoubleValue(OperationalReportsConstants.EuwDetailOpReportTableHeaders.UNEARNED_PREMIUM);
            earnedPremium += row.getDoubleValue(OperationalReportsConstants.EuwDetailOpReportTableHeaders.EARNED_PREMIUM_CHANGE_IN_UNEARNED);
            changeInUnearnedMTD += row.getDoubleValue(OperationalReportsConstants.EuwDetailOpReportTableHeaders.CHANGE_IN_UNEARNED_MTD);
            changeInUnearnedYTD += row.getDoubleValue(OperationalReportsConstants.EuwDetailOpReportTableHeaders.CHANGE_IN_UNEARNED_YTD);
        }

        totalsFromOpReport.put(OperationalReportsConstants.EuwDetailOpReportTableHeaders.WRITTEN_PREMIUM, writtenPremium);
        totalsFromOpReport.put(OperationalReportsConstants.EuwDetailOpReportTableHeaders.UNEARNED_PREMIUM, unearnedPremium);
        totalsFromOpReport.put(OperationalReportsConstants.EuwDetailOpReportTableHeaders.EARNED_PREMIUM_CHANGE_IN_UNEARNED, earnedPremium);
        totalsFromOpReport.put(OperationalReportsConstants.EuwDetailOpReportTableHeaders.CHANGE_IN_UNEARNED_MTD, changeInUnearnedMTD);
        totalsFromOpReport.put(OperationalReportsConstants.EuwDetailOpReportTableHeaders.CHANGE_IN_UNEARNED_YTD, changeInUnearnedYTD);

        return totalsFromOpReport;
    }

    public static ExcelSheet readOpReport(String originalFileName) {
        File readFile = new File(DOWNLOAD_DIR + originalFileName + EXCEL_FILE_EXTENSION);
        return new ExcelManager(readFile).getFirstSheet();
    }

    public static ExcelTable getEuwOpReportTable(String originalFileName) {
        ExcelSheet report = readOpReport(originalFileName);
        return report.getTable(report.getRow(OperationalReportsConstants.EuwDetailOpReportTableHeaders.BUSINESS_UNIT).getIndex());
    }
}