package aaa.admin.modules.reports.operationalreports;

public enum OperationalReportType {

    OPERATIONAL_REPORT("Operational Report", new OperationalReport());

    private String operationalReportType;
    private IOperationalReport operationalReport;

    OperationalReportType(String operationalReportType, IOperationalReport operationalReport) {
        this.operationalReportType = operationalReportType;
        this.operationalReport = operationalReport;
    }

    public IOperationalReport get() {
        return operationalReport;
    }

    public String getName() {
        return operationalReportType;
    }

    public String getKey() {
        return operationalReport.getClass().getSimpleName();
    }

}
