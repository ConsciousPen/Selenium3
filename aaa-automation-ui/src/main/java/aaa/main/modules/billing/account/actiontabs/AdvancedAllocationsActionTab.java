package aaa.main.modules.billing.account.actiontabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;

public class AdvancedAllocationsActionTab extends ActionTab {
    public AdvancedAllocationsActionTab() {
        super(BillingAccountMetaData.AdvancedAllocationsActionTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
