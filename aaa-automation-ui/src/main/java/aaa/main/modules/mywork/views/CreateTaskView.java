package aaa.main.modules.mywork.views;

import aaa.common.Workspace;
import aaa.main.modules.mywork.actiontabs.CreateTaskActionTab;

public class CreateTaskView extends Workspace {
    public CreateTaskView() {
        registerTab(CreateTaskActionTab.class);
    }

}
