package aaa.admin.modules.reports.dashboard;


public enum DashboardType {

    DASHBOARD("Dashboard", new Dashboard());

    private String dashboardType;
    private IDashboard dashboard;

    DashboardType(String dashboardType, IDashboard dashboard) {
        this.dashboardType = dashboardType;
        this.dashboard = dashboard;
    }

    public IDashboard get() {
        return dashboard;
    }

    public String getName() {
        return dashboardType;
    }

    public String getKey() {
        return dashboard.getClass().getSimpleName();
    }

}
