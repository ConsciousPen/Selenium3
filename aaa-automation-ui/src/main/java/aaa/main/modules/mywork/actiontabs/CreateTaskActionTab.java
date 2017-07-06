package aaa.main.modules.mywork.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.modules.mywork.MyWorkType;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;

public class CreateTaskActionTab extends ActionTab {

	 public static Button buttonCreate = new Button(By.id("taskCreateCommandForm:startTask_footer"));
	    public static Button buttonCancel = new Button(By.id("taskCreateCommandForm:cancel_footer"));
	    public static Button buttonCreateTask = new Button(By.xpath("//a[contains(@id,'createTask') and text()='Create Task']"));
	    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));
	    public static Button buttonAssignTask = new Button(By.id("taskCreateForm:assignTask"));


	    @Override
	    public Tab fillTab(TestData td) {
	        super.fillTab(td);
	        if (td.containsKey(getMetaKey()) && !td.getTestData(getMetaKey()).getKeys().isEmpty()) {
	            if (td.getTestData(getMetaKey()).containsKey(AssignTaskToActionTab.class.getSimpleName())
	                    && !td.getTestData(getMetaKey(), AssignTaskToActionTab.class.getSimpleName()).getKeys().isEmpty()) {
	                buttonAssignTask.click();
	                MyWorkType.MY_WORK.get().assignTaskTo().getView().getTab(AssignTaskToActionTab.class).fillTab(td.getTestData(getMetaKey()));
	                AssignTaskToActionTab.buttonAssign.click();
	            }
	        }
	        return this;
	    }

	    public CreateTaskActionTab() {
	        super(MyWorkMetaData.CreateTaskActionTab.class);
	    }
}
