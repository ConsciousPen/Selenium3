package aaa.main.modules.customer.views;

import aaa.common.Workspace;
import aaa.main.modules.customer.actiontabs.SearchMergeCustomerActionTab;

public class MergeView extends Workspace {
    public MergeView() {
        registerTab(SearchMergeCustomerActionTab.class);
    }
}