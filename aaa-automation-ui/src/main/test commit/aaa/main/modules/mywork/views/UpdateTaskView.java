package aaa.main.modules.mywork.views;

import aaa.common.Workspace;
import aaa.main.modules.mywork.actiontabs.UpdateTaskActionTab;

public class UpdateTaskView extends Workspace {
    public UpdateTaskView() {
        registerTab(UpdateTaskActionTab.class);
    }

}
