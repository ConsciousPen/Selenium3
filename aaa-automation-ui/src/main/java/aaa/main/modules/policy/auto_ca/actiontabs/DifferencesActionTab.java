/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.actiontabs;

import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyActions;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
//TODO Resolve any duplication of effort with renewal merge code

public class DifferencesActionTab extends ActionTab {
	public DifferencesActionTab() {
		super(AutoCaMetaData.DifferencesActionTab.class);
	}

	public static Table tableDifferences = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));

	/**
	 *
	 * @param selectCurrentChange true -> selects all current changes for differences roll on
	 */
	public void applyDifferences(boolean selectCurrentChange){
		expandsRows();
		selectCurrentRollOn(selectCurrentChange);
		submit();
	}

	public void expandsRows() {
		for (int i = 0; i < tableDifferences.getRowsCount(); i++) {
			Link linkTriangle = new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i
					+ "']/td[1]/span[contains(@class, 'ui-treetable-toggler')]"));
			if (linkTriangle.isPresent() && linkTriangle.isVisible()) {
				linkTriangle.click();

				Link linkTriangleSubTree = new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i + "_0"
						+ "']/td[1]/span[contains(@class, 'ui-treetable-toggler')]"));
				linkTriangleSubTree.click();
			}
		}
	}

	public void selectCurrentRollOn(boolean setCurrentValues) {
		int rowsCount = tableDifferences.getRowsCount();
		int columnsCount = tableDifferences.getColumnsCount();

		if (tableDifferences.isPresent()) {
			Link linkSetValue;
			for (int i = 1; i <= rowsCount; i++) {
				linkSetValue = tableDifferences.getRow(i).getCell(columnsCount).controls.links.get(
						setCurrentValues ? 1 : 2);

				if (linkSetValue.isPresent() && linkSetValue.isVisible()) {
					linkSetValue.click();
				}
			}
		}
	}

	public void submit() {
		PolicyActions.buttonRollOnChanges.click();
	}
}
