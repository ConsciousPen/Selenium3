/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.AutoSSMetaData;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.*;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class PremiumAndCoveragesTab extends Tab {
    public static Table tableRatingDetailsQuoteInfo = new Table(By.id("ratingDetailsPopupForm:policy_summary"));
    public static Table tableRatingDetailsUnderwriting = new Table(By.id("ratingDetailsPopupForm:underwriting_summary"));
    public static Table tableRatingDetailsDrivers = new Table(By.id("ratingDetailsPopupForm:driver_summary"));
    public static Table tableRatingDetailsVehicles = new Table(By.id("ratingDetailsPopupForm:vehicle_summary"));
    public static Table tableRatingDetailsActivities = new Table(By.id("ratingDetailsPopupForm:incident_summary"));
    public static Table tableDiscounts = new Table(By.id("policyDataGatherForm:discountSurchargeSummaryTable"));
    public static Table tableFormsSummary = new Table(By.id("policyDataGatherForm:formSummaryTable"));
    public static Table tablefeesSummary = new Table(By.id("policyDataGatherForm:feesSummaryTable"));

    public static Button buttonCalculatePremium = new Button(By.id("policyDataGatherForm:premiumRecalc"));
    public static Button buttonViewRatingDetails = new Button(By.id("policyDataGatherForm:viewRatingDetails_Link_1"));
    public static Button buttonContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);
    public static Button buttonRatingDetailsOk = new Button(By.id("ratingDetailsPopupButton:ratingDetailsPopupCancel"));

    public static StaticElement totalTermPremium = new StaticElement(By.xpath("//span[@class='TOTAL_TERM_PREMIUM']"));
    public static StaticElement totalActualPremium  = new StaticElement(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAPremiumSummary_body']/table/tbody/tr/td[2]/span"));



    public PremiumAndCoveragesTab() {
        super(AutoSSMetaData.PremiumAndCoveragesTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        super.fillTab(td);
        buttonCalculatePremium.click();
        return this;
    }
    
    @Override
    public Tab submitTab() {
        if (buttonRatingDetailsOk.isPresent() && buttonRatingDetailsOk.isVisible()) {
            buttonRatingDetailsOk.click();
        }
        buttonContinue.click();
        return this;
    }

    public TestData getRatingDetailsQuoteInfoData() {
        if (!tableRatingDetailsQuoteInfo.isPresent()) {
            buttonViewRatingDetails.click();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        List<String> keys = tableRatingDetailsQuoteInfo.getColumn(1).getValue();
        List<String> values = tableRatingDetailsQuoteInfo.getColumn(2).getValue();
        CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());

        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }

        keys = tableRatingDetailsQuoteInfo.getColumn(3).getValue();
        values = tableRatingDetailsQuoteInfo.getColumn(4).getValue();
        CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }

        return new SimpleDataProvider(map);
    }

    public List<TestData> getRatingDetailsDriversData() {
        final ByT pagePattern = ByT.xpath("//div[@id='ratingDetailsPopupForm:driverPanel_body']//center//td[@class='pageText']//*[text()='%s']");
        return getTestDataFromTable(tableRatingDetailsDrivers, pagePattern);
    }

    public List<TestData> getRatingDetailsVehiclesData() {
        final ByT pagePattern = ByT.xpath("//div[@id='ratingDetailsPopupForm:vehiclePanel']//center//td[@class='pageText']//*[text()='%s']");
        return getTestDataFromTable(tableRatingDetailsVehicles, pagePattern);
    }
    

    private List<TestData> getTestDataFromTable(Table table, ByT pagePattern) {
        List<TestData> testDataList = new ArrayList<>();

        if (!table.isPresent()) {
            buttonViewRatingDetails.click();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        List<String> keys = table.getColumn(1).getValue();

        int pageNumber = 1;
        while (new Link(pagePattern.format(pageNumber)).isPresent()) {
            new Link(pagePattern.format(pageNumber)).click();

            for (int column = 2; column <= table.getColumnsCount(); column++) {
                List<String> values = table.getColumn(column).getValue();
                if (values.stream().allMatch(String::isEmpty)) {
                    continue; // empty column means absent vehicle
                }

                CustomAssert.assertEquals("Number of keys in table is not equal to number of values.", keys.size(), values.size());

                for (int i = 0; i < keys.size(); i++) {
                    map.put(keys.get(i), values.get(i));
                }

                testDataList.add(new SimpleDataProvider(map));
                map.replaceAll((k, v) -> null);
            }
            pageNumber++;
        }

        return testDataList;
    }
}
