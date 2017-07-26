package aaa.toolkit.webdriver.customcontrols;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import aaa.main.metadata.CustomerMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;

public class AgencyAssignmentMultiAssetList extends MultiAssetList {
	private Button buttonAddAssignment = new Button(By.id("crmForm:addAssignmentBtn"));
	private Button buttonSearch = new Button(By.id("brokerSearchFromcrmCustomerBrokerCd:searchBtn"));
	private Table tableSearchResults = new Table(By.id("brokerSearchFromcrmCustomerBrokerCd:body_brokerSearchResultscrmCustomerBrokerCd"));
	private ByT sectionTemplate = ByT.xpath("//span[@id='crmForm:currentCustomerAgencyCd_%1$s']");
	private ByT buttonSelectAgencyTemplate = ByT.xpath("//a[@id='crmForm:changeCustomerProducerCdBtn_%1$s']");
	private ByT buttonRemoveAssignmentTemplate = ByT.xpath("//a[@id='crmForm:removeAssignmentBtn_%1$s']");

	public AgencyAssignmentMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public AgencyAssignmentMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected List<TestData> getRawValue() {
		List<TestData> values = new ArrayList<>();
		int index = 0;
		while (sectionExists(index)) {
			values.add(getSectionValue(index));
			index++;
		}
		return values;
	}

	@Override
	protected boolean sectionExists(int index) {
		return new Button(buttonSelectAgencyTemplate.format(index)).isPresent();
	}

	@Override
	protected void addSection(int index, int size) {
		buttonAddAssignment.click();
	}

	@Override
	protected void selectSection(int index) {
		if (sectionExists(index)) {
			new Button(buttonSelectAgencyTemplate.format(index)).click();
		}
	}

	@Override
	protected void setSectionValue(int index, TestData value) {
		if (!buttonSearch.isPresent()) {
			selectSection(index);
		}
		super.setSectionValue(index, value);
		buttonSearch.click();
		tableSearchResults.getColumn(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_NAME.getLabel()).getCell(1).controls.links.getFirst().click();
	}

	@Override
	protected TestData getSectionValue(int index) {
		TestData td = DataProviderFactory.emptyData();
		String[] agencyCodeAndName = new StaticElement(sectionTemplate.format(index)).getValue().split("\\s-\\s");
		td.adjust(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_CODE.getLabel(), agencyCodeAndName[0]);
		td.adjust(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_NAME.getLabel(), agencyCodeAndName[1]);
		return td;
	}

	public void removeAssignment(int index) {
		new Button(buttonRemoveAssignmentTemplate.format(index)).click();
	}

	public void removeAllAssignments() {
		while (sectionExists(0)) {
			removeAssignment(0);
		}
	}
}
