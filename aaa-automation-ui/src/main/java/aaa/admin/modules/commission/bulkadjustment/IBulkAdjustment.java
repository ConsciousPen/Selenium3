package aaa.admin.modules.commission.bulkadjustment;

import aaa.admin.modules.IAdmin;
import aaa.admin.modules.commission.bulkadjustment.BulkAdjustmentActions.AddBulkAdjustmentRule;
import aaa.admin.modules.commission.bulkadjustment.BulkAdjustmentActions.DeleteBulkAdjustment;
import aaa.admin.modules.commission.bulkadjustment.BulkAdjustmentActions.EditBulkAdjustment;
import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface IBulkAdjustment extends IAdmin {

    Workspace getDefaultView();

    void create(TestData td);

    void initiate();

    EditBulkAdjustment edit();

    AddBulkAdjustmentRule addBulkAdjustmentRule();

    DeleteBulkAdjustment delete();
}
