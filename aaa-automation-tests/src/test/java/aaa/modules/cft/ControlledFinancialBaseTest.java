/* Copyright ?? 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.cft;

import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.modules.BaseTest;
import org.testng.annotations.BeforeSuite;

public class ControlledFinancialBaseTest extends BaseTest {


	@BeforeSuite(alwaysRun = true)
	public void runCFTJob() {
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
	}


	protected void renewAction(){
		switch ("state"){
			case "asd":
			{}
		default:{

		}
		}
	}

}
