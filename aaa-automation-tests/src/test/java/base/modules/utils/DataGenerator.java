/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;
import aaa.main.modules.customer.Customer;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class DataGenerator extends BaseTest {

	private static Map<String, List<String>> products = new TreeMap<>();

	static {
		products.put(PolicyType.AUTO_SS.getName(), new ArrayList<String>());
		products.put(PolicyType.HOME_SS.getName(), new ArrayList<String>());
	}

	@Override
	@AfterSuite
	public void afterSuite() {
		StringBuilder sb = new StringBuilder();

		sb.append("Environment: " + PropertyProvider.getProperty(TestProperties.APP_HOST) + PropertyProvider.getProperty(TestProperties.EU_URL_TEMPLATE));
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());

		for (Map.Entry<String, List<String>> entry : products.entrySet()) {
			if (entry.getValue().size() != 0) {
				for (String number : entry.getValue()) {
					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(number);
					sb.append(System.lineSeparator());
				}
			}
		}

		File file = new File("data-generator.txt");
		try (FileWriter fr = new FileWriter(file.getAbsolutePath())) {
			fr.write(sb.toString());
			log.info(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void createHomePreconfigured() {
		createPersonalLinesQuotes(PolicyType.HOME_SS, Integer.parseInt(PropertyProvider.getProperty("HOME_PRECONFIGURED", "0")));
	}

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void createAutoPreconfigured() {
		createPersonalLinesQuotes(PolicyType.AUTO_SS, Integer.parseInt(PropertyProvider.getProperty("AUTO_PRECONFIGURED", "0")));
	}

	private void createPersonalLinesQuotes(PolicyType type, int count) {
		TestData tdc = testDataManager.policy.get(type);

		if (count > 0) {
			mainApp().open();

			TestData tdCustomer = testDataManager.customer.get(CustomerType.INDIVIDUAL);

			new Customer().create(tdCustomer.getTestData("DataGather", "TestData"));
			type.get().createQuote(tdc.getTestData("DataGather", "TestData"));

			products.get(type.getName()).add(PolicySummaryPage.labelPolicyNumber.getValue());
		}
		if (count > 1) {
			for (int i = 1; i < count; i++) {
				try {
					type.get().copyQuote().perform(tdc.getTestData("CopyFromQuote", "TestData"));
					type.get().calculatePremium(tdc.getTestData("DataGather", "TestData"));

					products.get(type.getName()).add(PolicySummaryPage.labelPolicyNumber.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
