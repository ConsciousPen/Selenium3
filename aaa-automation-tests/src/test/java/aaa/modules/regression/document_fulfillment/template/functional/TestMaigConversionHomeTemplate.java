package aaa.modules.regression.document_fulfillment.template.functional;
import toolkit.verification.CustomAssert;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static aaa.main.enums.DocGenEnum.Documents.HSRNMXX;

public class TestMaigConversionHomeTemplate extends MaigConversionHomeCommonMethods {


    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that HSPRNXX document is getting generated
     * @details
     */
    public void pas2305_preRenewalLetterHSPRNXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGeneration(getConversionPolicyDefaultTD(), HSPRNXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Initiate PUP conversion policy
     * 5. Check that HSPRNXX document is getting generated
     * @details
     */
    public void pas2305_preRenewalLetterPupConvHSPRNXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGenerationPup(getConversionPolicyDefaultTD(), HSPRNXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSPRNXX document is getting generated with PUP section
     * @details
     */
    public void pas9170_preRenewalLetterPupHSPRNXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGeneration(adjustWithPupData(getConversionPolicyDefaultTD()), HSPRNXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that HSPRNMXX document is getting generated
     * @details
     */
    public void pas7342_preRenewalLetterHSPRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSPRNMXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan
     * 4. Initiate PUP conversion policy
     * 5. Check that HSPRNMXX document is getting generated
     * @details
     */
    public void pas7342_preRenewalLetterPupConvHSPRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSPRNMXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan and PUP added to OtherActiveAAAPolicies  for Home
     * 4. Check that HSPRNMXX document is getting generated
     * @details
     */
    public void pas9170_preRenewalLetterPupHSPRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyDefaultTD())), HSPRNMXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that HSRNHODPXX document is getting generated
     * @details
     */
    public void pas2309_renewalCoverLetterHSRNHODPXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGeneration(getConversionPolicyDefaultTD(), HSRNHODPXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for home
     * 4. Initiate PUP conversion policy
     * 5. Check that HSRNHODPXX document is getting generated
     * @details
     */
    public void pas2309_renewalCoverLetterPupConvHSRNHODPXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGenerationPup(getConversionPolicyDefaultTD(), HSRNHODPXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSRNHODPXX document is getting generated
     * @details
     */
    public void pas2309_renewalCoverLetterPupHSRNHODPXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGeneration(adjustWithPupData(getConversionPolicyDefaultTD()), HSRNHODPXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that HSRNMXX document is getting generated
     * @details
     */
    public void pas2570_renewalCoverLetterHSRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRNMXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Initiate PUP conversion policy.
     * 5. Check that HSRNMXX document is getting generated
     * @details
     */
    public void pas2570_renewalCoverLetterPupConvHSRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRNMXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan and PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSRNMXX document is getting generated
     * @details
     */
    public void pas2570_renewalCoverLetterPupHSRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyDefaultTD())), HSRNMXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }


    public void pas2674_test(String state){
        test(getConversionPolicyDefaultTD(), HSPRNXX, false);
    }
}
