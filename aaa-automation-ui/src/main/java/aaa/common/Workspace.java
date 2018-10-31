/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

/**
 * Abstract workspace class.
 * @category Static
 */
public abstract class Workspace {
	protected final Map<Class<? extends Tab>, Tab> tabs;

	protected Workspace() {
		tabs = new LinkedHashMap<>();
	}

	/**
	 * Register tab associated with this workspace
	 * Tabs are stored in a linked hash map so the order of calls determines the order of tabs.
	 * @param tabClass tab class
	 */
	protected void registerTab(Class<? extends Tab> tabClass) {
		try {
			tabs.put(tabClass, tabClass.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IstfException("Tab class " + tabClass.getSimpleName() + " should have public default constructor", e);
		}
	}

	/**
	 * Fill registered tabs of this workspace.
	 * If TestData by tab's metakey is present in td and is not empty, the tab is filled.
	 * If it is empty, the tab is submitted without filling
	 * If the metakey is not present at all, the tab is assumed to be absent on com.exigen.ipb.etcsa.
	 * Override to customize tab filling procedure
	 * @param td TestData object which contains data for registered tabs. Also see {@link products.Tab.fillTab()}
	 */
	public Workspace fill(TestData td) {
		for (Tab t : tabs.values()) {
			if (td.containsKey(t.getMetaKey())) {
				t.fillTab(td);
				t.submitTab();
			}
		}
		return this;
	}

	/**
	 * Fill registered tabs from the first one up to the specified one.
	 * @param td TestData object
	 * @param tabClass class of the last tab to be filled
	 */
	public Workspace fillUpTo(TestData td, Class<? extends Tab> tabClass) {
		if (tabs.containsKey(tabClass)) {
			for (Tab t : tabs.values()) {
				if (td.containsKey(t.getMetaKey())) {
					if (tabClass.isInstance(t)) {
						break;
					}
					t.fillTab(td);
					t.submitTab();
				}
			}
		} else {
			throw new IstfException("Tab " + tabClass.getSimpleName() + " is not registered in " + this.getClass().getSimpleName());
		}
		return this;
	}

	/**
	 * Fill registered all tab until specified one.
	 * @param td TestData object
	 * @param tabClass class of the last tab to be filled
	 * @param isFillTab true if you want to fill tabClass or false if tabClass shouldn't be filled
	 * @return
	 */
	public Workspace fillUpTo(TestData td, Class<? extends Tab> tabClass, boolean isFillTab) {
		fillUpTo(td, tabClass);
		if (isFillTab) {
			getTab(tabClass).fillTab(td);
		}
		return this;
	}

	/**
	 * Fill registered tabs from the actual specified one up to the specified one.
	 * @param td TestData object
	 * @param tabClassFrom class of the actual tab to be filled
	 * @param tabClassTo class of the last tab to be filled
	 */
	public Workspace fillFromTo(TestData td, Class<? extends Tab> tabClassFrom, Class<? extends Tab> tabClassTo) {
		List<Tab> listTabs = new ArrayList<>(tabs.values());
		if (tabs.containsKey(tabClassFrom) && tabs.containsKey(tabClassTo)) {
			search:
			for (int i = 0; i <= listTabs.size(); i++) {
				if (listTabs.get(i).getMetaKey().equals(tabClassFrom.getSimpleName())) {
					for (int j = i; j <= listTabs.size(); j++) {
						if (td.containsKey(listTabs.get(j).getMetaKey())) {
							if (tabClassTo.isInstance(listTabs.get(j))) {
								break search;
							}
							listTabs.get(j).fillTab(td);
							listTabs.get(j).submitTab();
						}
					}
				}
			}
		} else {
			throw new IstfException(
					"Tab " + tabClassFrom.getSimpleName() + "/" + tabClassTo.getSimpleName() + " is not registered in " + this.getClass().getSimpleName());
		}
		return this;
	}

	public Workspace fillFromTo(TestData td, Class<? extends Tab> tabClassFrom, Class<? extends Tab> tabClassTo, boolean isFillTab) {
		fillFromTo(td, tabClassFrom, tabClassTo);
		if (isFillTab) {
			getTab(tabClassTo).fillTab(td);
		}
		return this;
	}

	/**
	 * Get registered tab by its class
	 * @param tabClass desired tab class
	 * @return tab instance
	 */
	@SuppressWarnings("unchecked")
	public <T extends Tab> T getTab(Class<T> tabClass) {
		T tab = (T) tabs.get(tabClass);
		if (tab == null) {
			throw new IstfException("No registered tab with class " + tabClass.getName());
		}
		return tab;
	}
}
