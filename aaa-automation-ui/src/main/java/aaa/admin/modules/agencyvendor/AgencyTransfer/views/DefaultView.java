package aaa.admin.modules.agencyvendor.AgencyTransfer.views;

import aaa.admin.modules.agencyvendor.AgencyTransfer.defaulttabs.AgencyTransferTab;
import aaa.common.Workspace;

public class DefaultView extends Workspace {

    public DefaultView() {
        registerTab(AgencyTransferTab.class);
    }
}
