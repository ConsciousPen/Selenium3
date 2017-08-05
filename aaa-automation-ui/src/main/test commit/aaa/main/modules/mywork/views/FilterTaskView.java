package aaa.main.modules.mywork.views;

import aaa.common.Workspace;
import aaa.main.modules.mywork.actiontabs.FilterTaskActionTab;

public class FilterTaskView extends Workspace {
    public FilterTaskView() {
        registerTab(FilterTaskActionTab.class);
    }

}
