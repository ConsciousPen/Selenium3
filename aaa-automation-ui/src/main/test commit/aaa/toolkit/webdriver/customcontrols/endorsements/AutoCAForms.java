package aaa.toolkit.webdriver.customcontrols.endorsements;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;

public class AutoCAForms {

	public static class AutoCAPolicyFormsController extends AutoFormsController {
		public AutoCAPolicyFormsController(By locator, Class<? extends MetaData> metaDataClass) {
			super(locator, metaDataClass);
			tableSelectedForms = new Table(By.id("policyDataGatherForm:body_selectedManageableComponentsTable_PolicyEndorsementFormsManager"));
			tableAvailableForms = new Table(By.id("policyDataGatherForm:body_availableManageableComponentsTable_PolicyEndorsementFormsManager"));
		}
	}

	public static class AutoCAVehicleFormsController extends AutoFormsController {
		public AutoCAVehicleFormsController(By locator, Class<? extends MetaData> metaDataClass) {
			super(locator, metaDataClass);
			tableSwitcher = new Table(By.id("policyDataGatherForm:formsTableVehicleFormsTable"));
			tableSelectedForms = new Table(By.id("policyDataGatherForm:body_selectedManageableComponentsTable_VehicleEndorsementFormsManager"));
			tableAvailableForms = new Table(By.id("policyDataGatherForm:body_availableManageableComponentsTable_VehicleEndorsementFormsManager"));
		}
	}

	public static class AutoCADriverFormsController extends AutoFormsController {
		public AutoCADriverFormsController(By locator, Class<? extends MetaData> metaDataClass) {
			super(locator, metaDataClass);
			tableSwitcher = new Table(By.id("policyDataGatherForm:formsTableDriverFormsTable"));
			tableSelectedForms = new Table(By.id("policyDataGatherForm:body_selectedManageableComponentsTable_DriverEndorsementFormsManager"));
			tableAvailableForms = new Table(By.id("policyDataGatherForm:body_availableManageableComponentsTable_DriverEndorsementFormsManager"));
		}
	}
}
