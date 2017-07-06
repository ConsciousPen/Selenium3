package aaa.admin.modules.reports.operationalreports;

import aaa.admin.modules.IAdmin;
import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface IOperationalReport extends IAdmin {

    Workspace getDefaultView();

    void create(TestData td);

    void initiate(TestData td);

    void schedule(TestData td);

}
