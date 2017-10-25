package aaa.toolkit.webdriver.customcontrols.endorsements;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;

import toolkit.datax.TestData;
import toolkit.datax.TestData.Type;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Control for filling Forms for Policy, Vehicle or Driver on Forms tab for Auto products.</br>
 * List of test data can be send as input for filling multiple Forms Subjects (few drivers or few vehicles).</br>
 * Add field 'Forms Subject' to specify what entity will be worked with, currently opened entity will be used if this field is not added.</br>
 * Add field 'Action' to Forms's test data with value 'Add', 'Edit' or 'Remove' to specify action for form (Add action is used if value is not set).</br>
 * </br>
 * <b>Example of Test Data:</b></br>
 * <pre>
 * Driver Forms: [{
 *   Forms Subject: 'Michael Trevor',
 *   ADBE: {
 *     Action: Add
 *   },
 *   SR22: {
 *     Action: Edit,
 *     State: CO,
 *     Expiration Date: '/today'
 *   }
 * },{
 *   Forms Subject: 'John Smith',
 *   ADBE: {
 *     Action: Add
 *   }
 * }]
 *
 */
public abstract class AutoFormsController extends AbstractContainer<List<TestData>, List<TestData>>{
	
	public Table tableSwitcher;
	public Table tableSelectedForms;
	public Table tableAvailableForms;
	
	public AutoFormsController(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}
	
	@Override
	protected void setRawValue(List<TestData> td) {
		int size = td.size();
		for (int i = 0; i < size; i++) {
			if (td.get(i).containsKey("Forms Subject")) {
				openFormsSubject(td.get(i).getValue("Forms Subject"));
			}
			//Fill each Form that has description in MetaData
			for (Map.Entry<String, BaseElement<?, ?>> entry : getAssetCollection().entrySet()) {
				String formName = entry.getValue().getName();
				//Check that we have test data for Form
				if (td.get(i).containsKey(formName)) {
					String action = "Add";
					if (td.get(i).getTestData(formName).containsKey("Action")) {
						action = td.get(i).getTestData(formName).getValue("Action");
					}
					
					if (action.equals("Add")) {
						addAndFillForm(formName, td.get(i).getTestData(formName));
					} else if (action.equals("Edit")) {
						openAndFillForm(formName, td.get(i).getTestData(formName));
					} else if (action.equals("Remove")) {
						getRemoveLink(formName).click();
					} else {
						throw new IstfException(String.format("Unknown Action '%s' for Form '%s'", action, formName));
					}
				}
			}
		}
	}
	
	@Override
	protected List<TestData> getRawValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Select Driver or Vehicle from list to work with forms for this entity
	 * @param subject - value that will be used to search entity (by contains)
	 */
	public void openFormsSubject(String subject) {
		if (tableSwitcher.getRowContains(1, subject).isPresent()) {
			tableSwitcher.getRowContains(1, subject).getCell(tableSwitcher.getColumnsCount()).controls.links.get("View/Edit").click();
		} else {
			throw new IstfException("Forms Subject " + subject + " not found for " + getName());
		}
	}
	
	public Link getAddLink(String formName) {
		return tableAvailableForms.getRow("Name", formName).getCell(tableAvailableForms.getColumnsCount()).controls.links.get("Add to Policy");
	}
	
	public Link getOpenLink(String formName) {
		return tableSelectedForms.getRow("Name", formName).getCell("Name").controls.links.get(1);
	}
	
	public Link getRemoveLink(String formName) {
		return tableSelectedForms.getRow("Name", formName).getCell(tableSelectedForms.getColumnsCount()).controls.links.get("Remove from Policy");
	}
	
	/**
	 * Click Add for specified Form, fill values and confirm
	 * @param formName
	 * @param td - test data with fields values (not TestData with link to form's test data)
	 */
	public void addAndFillForm(String formName, TestData td) {
		getAddLink(formName).click();
		((AssetList)getAssetCollection().get(formName)).setValue(td);
		new Button(getAssetCollection().get(formName), By.xpath(".//input[@value='OK']")).click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
	}
	
	/**
	 * Open specified Form, that is already added, fill values and confirm
	 * @param formName
	 * @param td - test data with fields values (not TestData with link to form's test data)
	 */
	public void openAndFillForm(String formName, TestData td) {
		getOpenLink(formName).click();
		((AssetList)getAssetCollection().get(formName)).setValue(td);
		new Button(getAssetCollection().get(formName), By.xpath(".//input[@value='OK']")).click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
	}

	@Override
	public void fill(TestData td) {
		if (td.containsKey(name)) {
			setValue(td.getTestDataList(name));
		}
	}
	
	@Override
	public Type testDataType() {
		return Type.LIST_TESTDATA;
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
}
