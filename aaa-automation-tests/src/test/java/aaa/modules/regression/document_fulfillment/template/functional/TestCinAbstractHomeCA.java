package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class TestCinAbstractHomeCA extends TestCinAbstract{

    /******* This section should be refactored once generic solution for building paths is implemented ********/
    //Home CA specific paths
    public static final String PRODUCT_OWNED_PATH = TestData.makeKeyPath(
            HomeCaMetaData.ApplicantTab.class.getSimpleName(),
            HomeCaMetaData.ApplicantTab.AAAMembership.class.getSimpleName());

    public static final String MEMBERSHIP_REPORT_PATH = TestData.makeKeyPath(
            HomeCaMetaData.ReportsTab.class.getSimpleName(),
            HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());

    public static final String NAME_INSURED_FIRST_NAME = TestData.makeKeyPath(
            HomeCaMetaData.ApplicantTab.class.getSimpleName(),
            HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(),
            HomeCaMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel());

    public static final String NAME_INSURED_LAST_NAME = TestData.makeKeyPath(
            HomeCaMetaData.ApplicantTab.class.getSimpleName(),
            HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(),
            HomeCaMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel());

    public static final String PUBLIC_PROTECTION_CLASS_PATH = TestData.makeKeyPath(
            HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
            HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel());

    public static final String RENTAL_CLAIM_PATH = TestData.makeKeyPath(
            HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
            HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(),
            HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM.getLabel());
    /**********************************************************************************************************/

    @Override
    protected void performRenewal(TestData renewalTD) {
        new HomeCaPolicyActions.Renew().performAndFill(renewalTD);
    }
    
    @Override
    protected void performDoNotRenew(TestData doNotRenewTD) {
    	new HomeCaPolicyActions.DoNotRenew().perform(doNotRenewTD);
    }
    
    @Override
    protected void performRemoveDoNotRenew() {
    	new HomeCaPolicyActions.RemoveDoNotRenew().perform(new SimpleDataProvider());
    }
}
