package aaa.helpers.policy.groupbenefits;

import java.util.Map.Entry;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.enums.PolicyConstants;
import toolkit.webdriver.controls.composite.table.Table;

public class PolicySummaryHelper extends TableVerifier {

    Table table;
    String tableName;

    public PolicySummaryHelper(Table table, String tableName) {

        this.table = table;
        this.tableName = tableName;
    }

    @Override
    protected Table getTable() {
        return this.table;
    }

    @Override
    protected String getTableName() {
        return this.tableName;
    }

    public PolicySummaryHelper setParticipants(String value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.PARTICIPANTS, value);
        return this;
    }

    public PolicySummaryHelper setVolume(String value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.VOLUME, value);
        return this;
    }

    public PolicySummaryHelper setVolume(Dollar value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.VOLUME, value.toString());
        return this;
    }

    public PolicySummaryHelper setAnnualPremium(String value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.ANNUAL_PREMIUM, value);
        return this;
    }

    public PolicySummaryHelper setAnnualPremium(Dollar value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.ANNUAL_PREMIUM, value.toString());
        return this;
    }

    public PolicySummaryHelper setModalPremium(String value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM, value);
        return this;
    }

    public PolicySummaryHelper setModalPremium(Dollar value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM, value.toString());
        return this;
    }

    public void verifyRow(int rowNumber) {
        for (Entry<String, String> entry : values.entrySet()) {
            getTable().getRow(rowNumber).getCell(entry.getKey()).verify
                    .value(String.format("Table '%s', Row '%s', Column '%s'", getTableName(), rowNumber, entry.getKey()), entry.getValue());
        }
    }
}
