/* Copyright ?? 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.general;

import org.openqa.selenium.By;
import aaa.admin.pages.AdminPage;
import aaa.common.pages.Page;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

public class GeneralSchedulerPage extends AdminPage {

	public static Table tableScheduledJobs = new Table(By.xpath("//div[@id='jobs:jobsTable']//table"));
	private static final int MAX_JOB_RUN_RETRIES = 500;
	private static final int MAX_JOB_RUN_TIMEOUT = 1200000;
	private static final int JOB_RUN_RETRIES_SLEEP = 5;
	private static final String JOB_RESULT_XPATH_TEMPLATE = "//table[@id='jobs:jobsTable']/tbody/tr[td[position()=1 and normalize-space(.)='%s']]/td[2]/table";

	public static Button buttonSaveJob = new Button(By.id("jobsForm:saveButton_footer"));
	public static Button buttonAddNewGroup = new Button(By.id("jobs:new"));
	public static Button buttonAddJobButton = new Button(By.id("jobsForm:addJobButton"));
	public static TextBox textBoxGroupName = new TextBox(By.id("jobsForm:groupName"));
	public static ComboBox comboBoxJobBatch = new ComboBox(By.id("jobsForm:job_0_class"));

	public static String getJobStatus(String jobName) {
		return new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName))).getRow(1).getCell(2).getValue().split(":")[1];
	}

	public static int getJobCounter(String jobName) {
		return Integer.parseInt(new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName))).getRow(1).getCell(1).getValue().split(":")[1]);
	}

	public static String getJobResult(String jobName) {
		return new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName))).getRow(2).getCell(2).getValue().split(":")[1];
	}

	public static void eliminateJob(String jobName) {
		new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName))).getRow(3).getCell(4).controls.links.getFirst().click();
		Page.dialogConfirmation.confirm();
	}

	private static void executeJob(String jobName) {
		new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName))).getRow(1).getCell(4).controls.links.getFirst().click();
	}

}
