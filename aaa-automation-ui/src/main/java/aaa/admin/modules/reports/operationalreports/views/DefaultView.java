package aaa.admin.modules.reports.operationalreports.views;

import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.common.Workspace;

public class DefaultView extends Workspace {

    public DefaultView() {
        super();
        registerTab(OperationalReportsTab.class);
    }

}
