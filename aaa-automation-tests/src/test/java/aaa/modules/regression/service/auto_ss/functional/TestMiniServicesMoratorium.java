package aaa.modules.regression.service.auto_ss.functional;

import aaa.admin.metadata.product.MoratoriumMetaData;
import aaa.admin.modules.product.IProduct;
import aaa.admin.modules.product.ProductType;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.sales.template.functional.PolicyMoratorium;
import aaa.utils.StateList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestMiniServicesMoratorium extends PolicyMoratorium {

    private final IProduct moratorium = ProductType.MORATORIUM.get();

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PST-352: Soft Stop Moratorium set on New Business Premium Calculation and Hard Stop moratorium on New Business Bind")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PST-352")
    @StateList(states = Constants.States.AZ)
    public void test(@Optional("AZ") String state) {
        TestData td = getTestSpecificTD("TestData_Moratorium_DXP_Config_1");
        MoratoriumRule moratoriumRule = getMoratoriumRule(td);
        mockMoratoriumRuleAndRunTest(td, moratoriumRule, () -> {
            // write your test here
        });
    }

    private void mockMoratoriumRuleAndRunTest(TestData td, MoratoriumRule moratoriumRule, Runnable testBody) {
        try {
            //Step 1 -- Zip code entry needs to be added to the AAAMoratoriumGeographyLocationInfo lookup in order to be able to select it when creating moratorium in Step 2.
            log.info("Step 1: Add Zip Code entry in lookupvalue table if not exists.");
            DBService.get().executeUpdate(insertLookupEntry(moratoriumRule.getZipCode(), moratoriumRule.getCity(), parseState(moratoriumRule.getState())));
            //Step 2
            log.info("Step 2: Set Soft Stop moratorium on Premium Calculation and Hard Stop moratorium on Bind.");
            adminApp().open();
            moratorium.create(td.adjust(TestData.makeKeyPath("AddMoratoriumTab", MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel()), moratoriumRule.getName())
                    .adjust(TestData.makeKeyPath("AddMoratoriumTab", "AddRuleSection", MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel()), moratoriumRule.getDisplayMessage()));
            testBody.run();
        } finally {
            expireMoratorium(moratoriumRule.getName());
        }
    }

    private String parseState(String state) {
        return StringUtils.split(state, "-")[1].trim();
    }

    private MoratoriumRule getMoratoriumRule(TestData td) {
        String zipCode = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.ZIP_CODES.getLabel());
        String city = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.CITIES.getLabel());
        String state = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.STATES.getLabel());
        String name = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel());
        String displayMessage = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getTestData(MoratoriumMetaData.AddMoratoriumTab.ADD_RULE_SECTION.getLabel())
                .getValue(MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel());
        return new MoratoriumRule(zipCode, city, state, name, displayMessage);
    }

    private class MoratoriumRule {

        private final String zipCode;
        private final String city;
        private final String state;
        private final String name;
        private final String displayMessage;

        private MoratoriumRule(String zipCode, String city, String state, String name, String displayMessage) {
            this.zipCode = zipCode;
            this.city = city;
            this.state = state;
            this.name = name;
            this.displayMessage = displayMessage;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getName() {
            return name;
        }

        public String getDisplayMessage() {
            return displayMessage;
        }
    }

    @FunctionalInterface
    public interface MoratoriumTestBody {
        void accept(TestData td, MoratoriumRule moratoriumRule, String state);
    }
}
