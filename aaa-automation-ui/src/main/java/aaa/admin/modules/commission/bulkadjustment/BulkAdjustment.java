package aaa.admin.modules.commission.bulkadjustment;

import aaa.EntityLogger;
import aaa.admin.modules.commission.bulkadjustment.BulkAdjustmentActions.AddBulkAdjustmentRule;
import aaa.admin.modules.commission.bulkadjustment.BulkAdjustmentActions.DeleteBulkAdjustment;
import aaa.admin.modules.commission.bulkadjustment.BulkAdjustmentActions.EditBulkAdjustment;
import aaa.admin.modules.commission.bulkadjustment.views.DefaultView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class BulkAdjustment implements IBulkAdjustment {

    private Workspace defaultView = new DefaultView();

    @Override
    public void search(TestData td) {
        CommissionPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.COMMISSION_BULK_ADJUSTMENT.get());
    }

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.BULK_ADJUSTMENT);
        CommissionPage.buttonSaveBulkAdjustment.click();
        log.info("Created Bulk Adjustment " + entity);
    }

    @Override
    public void initiate() {
        navigate();
        CommissionPage.buttonAddBulkAdjustment.click();
    }

    @Override
    public EditBulkAdjustment edit() {
        return new EditBulkAdjustment();
    }

    @Override
    public AddBulkAdjustmentRule addBulkAdjustmentRule() {
        return new AddBulkAdjustmentRule();
    }

    @Override
    public DeleteBulkAdjustment delete() {
        return new DeleteBulkAdjustment();
    }
}
