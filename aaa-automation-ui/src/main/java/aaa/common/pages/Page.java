/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.components.Dialog;
import aaa.common.components.DialogSearch;

public abstract class Page {

	public static final String DEFAULT_ASSETLIST_CONTAINER = "//div[@id='contentWrapper']";
	public static final String OPERATIONAL_REPORTS_ASSETLIST_CONTAINER = "//table[@id='contents']";
	public static final String DEFAULT_DIALOG_LOCATOR = "//div[(contains(@id, '_container') and (descendant::div[contains(@id, '_header')])"
			+ " or (contains(@id, 'PopupId') or (@role='dialog')) and (contains(@style, 'visibility: visible')))"
			+ " and not(@aria-hidden='true') and not(ancestor::div[contains(@style,'hidden')]) and not(ancestor::div[contains(@style,'display: none')])]";
	public static Dialog dialogConfirmation = new Dialog(DEFAULT_DIALOG_LOCATOR);
	public static DialogSearch dialogSearch = new DialogSearch(DEFAULT_DIALOG_LOCATOR);
	protected static Logger log = LoggerFactory.getLogger(Page.class);

}
