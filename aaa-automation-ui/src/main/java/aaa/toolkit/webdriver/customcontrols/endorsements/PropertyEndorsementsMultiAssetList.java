package aaa.toolkit.webdriver.customcontrols.endorsements;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import aaa.common.pages.Page;
import aaa.main.modules.policy.abstract_tabs.PropertyEndorsementsTab;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

/**
 * Asset list for filling Endorsements tab for property products.</br>
 * Add field 'Action' to test data with value 'Edit' or 'Remove' to edit or remove endorsement form or one of it's instances.</br>
 * Add field 'Instance Number' to test data with number of instance that should be affected by Edit or Remove action.</br>
 * Default value for instance number equals to dataset's index in list.</br>
 * Keep in mind that instance number should be actual on moment of action execution, 
 * if you want to remove instances 2,3, and 4 use Remove action on Instance Number 2 three times</br>
 * </br>
 * <b>Example of Test Data:</b></br>
 * <pre>
 * 'HS 24 64': [{
 Make or Model: 'Model1',
 Serial or Motor number: 'FR424'
 }, {
 Action: 'Edit',
 Instance Number: 1,
 Make or Model: 'Model2',
 Serial or Motor number: 'JG533'
 }, {
 Action: 'Remove',
 Instance Number: 1,
 }]
 * </pre>
 */
public abstract class PropertyEndorsementsMultiAssetList extends AbstractContainer<List<TestData>, List<TestData>> {

	protected PropertyEndorsementsTab endorsementsTab;

	public PropertyEndorsementsMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected List<TestData> getRawValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setRawValue(List<TestData> value) {
		int size = value.size();
		for (int i = 0; i < size; i++) {
			String action = "Add";
			if (value.get(i).containsKey("Action")) {
				action = value.get(i).getValue("Action");
			}
			int instanceNum = i + 1;
			if (value.get(i).containsKey("Instance Number")) {
				instanceNum = Integer.valueOf(value.get(i).getValue("Instance Number"));
			}

			if (action.equals("Add")) {
				addSection();
				setSectionValue(instanceNum, value.get(i));
			} else if (action.equals("Edit")) {
				editSection(instanceNum);
				setSectionValue(instanceNum, value.get(i));
			} else if (action.equals("Remove")) {
				removeSection(instanceNum);
			} else {
				throw new IstfException(String.format("Unknown Action '%s' for Endorsement '%s'", action, getName()));
			}
		}
	}

	@Override
	public void fill(TestData td) {
		if (td.containsKey(name)) {
			setValue(td.getTestDataList(name));
		}
	}

	@Override
	public TestData.Type testDataType() {
		return TestData.Type.LIST_TESTDATA;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<TestData> normalize(Object rawValue) {
		boolean isCorrect = true;
		if (rawValue instanceof List) {
			for (Object o : (List<?>) rawValue) {
				if (!(o instanceof TestData)) {
					isCorrect = false;
					break;
				}
			}
		} else {
			isCorrect = false;
		}

		if (isCorrect) {
			return (List<TestData>) rawValue;
		} else {
			throw new IllegalArgumentException("Value " + rawValue + " has incorrect type and/or generic type " + rawValue.getClass());
		}
	}

	protected void setSectionValue(int index, TestData value) {
		for (Map.Entry<String, BaseElement<?, ?>> entry : getAssetCollection().entrySet()) {
			entry.getValue().fill(value);
		}
		endorsementsTab.btnSaveForm.click();
	}

	protected void addSection() {
		endorsementsTab.getAddEndorsementLink(getName()).click();
	}

	protected void editSection(int index) {
		endorsementsTab.getEditEndorsementLink(getName(), index).click();
	}

	protected void removeSection(int index) {
		endorsementsTab.getRemoveEndorsementLink(getName(), index).click();
		Page.dialogConfirmation.confirm();
	}
}
