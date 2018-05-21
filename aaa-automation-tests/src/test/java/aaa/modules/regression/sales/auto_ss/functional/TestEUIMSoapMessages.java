package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.soap.SoapClient;
import aaa.helpers.xml.XmlParser;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.utils.TestInfo;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.CheckBox;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author - Tyrone Jemison
 * @scenario - 1A(+Test on v1.5), 1B(-Test on v1.5), 2A(+Test on v1.6), 2B(-Test on v1.6)
 * @description - This test creates quotes in PAS with EUIM coverage, via third-party web-service call.
 */

public class TestEUIMSoapMessages extends AutoSSBaseTest {
    //Global-Test Level Parameters
    final static String HTTP = "http://";
    final static String FILEPATH = "src\\test\\resources\\uploadingfiles\\xmlParser\\";
    final static String RESPONSEOUTPUTLOCATION = "src\\test\\resources\\uploadingfiles\\xmlParser\\output\\";
    String hostName = PropertyProvider.getProperty(TestProperties.APP_HOST);
    SoapClient SOAPCLIENTOBJ = new SoapClient();
    String SOAPACTION = "http://exigenservices.com/ipb/policy/integration/RateQuote";
    String TAGNAMEFORPOLICYNUMBER = "Product:policyNumber";

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-11655 18.5-MD Enhanced UIMBI/PD Coverage: Comprater/QuickQuote")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12845")
    /** @scenario -
     *  1. Mock Request will flag UI in PAS.
     *  2. Convert XML into web-service message
     *  3. Send web-service request message to endpoint URL and receive response message
     *  4. Validate response contains calculated premium and quote #.
     *  5. Validate quote created in PAS w/ "Enhanced UIMBI/PD" coverage = true in P&C tab.
     */
    public void AC1A_Comprater_v1dot5_Flag(@Optional("") String state) {
        // [CONFIG] Scenario Variables

        String xmlInFileName = "CompraterRequest_YesFlag.xml";
        String xmlOutFileName = "CompraterResponse_YesFlag.xml";
        String soapEndPointUrl = HTTP + hostName + ":9095/aaa-admin/services/1.5/aaaSSPolicyRate";

        inputSoapRequestOutputResponse(SOAPCLIENTOBJ, soapEndPointUrl, SOAPACTION, FILEPATH,  xmlInFileName, RESPONSEOUTPUTLOCATION, xmlOutFileName);

        String policyNumber = getPolicyNumberFromResponse(RESPONSEOUTPUTLOCATION, xmlOutFileName, TAGNAMEFORPOLICYNUMBER);

        testUIForEUIM(policyNumber, true, true);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-11655 18.5-MD Enhanced UIMBI/PD Coverage: Comprater/QuickQuote")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12845")
    /** @scenario -
     *  1. Mock Request will NOT flag UI in PAS.
     *  2. Convert XML into web-service message
     *  3. Send web-service request message to endpoint URL and receive response message
     *  4. Validate response contains calculated premium and quote #.
     *  5. Validate quote created in PAS w/ "Enhanced UIMBI/PD" coverage = false in P&C tab.
     */
    public void AC1B_Comprater_v1dot5_NoFlag(@Optional("") String state) {

        // [CONFIG] Scenario Variables
        String xmlInFileName = "CompraterRequest_NoFlag.xml";
        String xmlOutFileName = "CompraterResponse_NoFlag.xml";
        String soapEndPointUrl = HTTP + hostName + ":9095/aaa-admin/services/1.5/aaaSSPolicyRate";

        inputSoapRequestOutputResponse(SOAPCLIENTOBJ, soapEndPointUrl, SOAPACTION, FILEPATH,  xmlInFileName, RESPONSEOUTPUTLOCATION, xmlOutFileName);

        String policyNumber = getPolicyNumberFromResponse(RESPONSEOUTPUTLOCATION, xmlOutFileName, TAGNAMEFORPOLICYNUMBER);

        testUIForEUIM(policyNumber, true, false);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-11655 18.5-MD Enhanced UIMBI/PD Coverage: Comprater/QuickQuote")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12845")
    /** @scenario -
     *  1. Mock Request will flag UI in PAS.
     *  2. Convert XML into web-service message
     *  3. Send web-service request message to endpoint URL and receive response message
     *  4. Validate response contains calculated premium and quote #.
     *  5. Validate quote created in PAS w/ "Enhanced UIMBI/PD" coverage = true in P&C tab.
     */
    public void AC2A_Comprater_v1dot6_Flag(@Optional("") String state) {
        // [CONFIG] Scenario Variables
        String xmlInFileName = "CompraterRequest_YesFlag.xml";
        String xmlOutFileName = "CompraterResponse_YesFlag.xml";
        String soapEndPointUrl = HTTP + hostName + ":9095/aaa-admin/services/1.6/aaaSSPolicyRate";

        inputSoapRequestOutputResponse(SOAPCLIENTOBJ, soapEndPointUrl, SOAPACTION, FILEPATH,  xmlInFileName, RESPONSEOUTPUTLOCATION, xmlOutFileName);

        String policyNumber = getPolicyNumberFromResponse(RESPONSEOUTPUTLOCATION, xmlOutFileName, TAGNAMEFORPOLICYNUMBER);

        testUIForEUIM(policyNumber, true, true);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-11655 18.5-MD Enhanced UIMBI/PD Coverage: Comprater/QuickQuote")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12845")
    /** @scenario -
     *  1. Mock Request will NOT flag UI in PAS.
     *  2. Convert XML into web-service message
     *  3. Send web-service request message to endpoint URL and receive response message
     *  4. Validate response contains calculated premium and quote #.
     *  5. Validate quote created in PAS w/ "Enhanced UIMBI/PD" coverage = false in P&C tab.
     */
    public void AC2B_Comprater_v1dot6_NoFlag(@Optional("") String state) {
        // [CONFIG] Scenario Variables
        String xmlInFileName = "CompraterRequest_NoFlag.xml";
        String xmlOutFileName = "CompraterResponse_NoFlag.xml";
        String soapEndPointUrl = HTTP + hostName + ":9095/aaa-admin/services/1.6/aaaSSPolicyRate";

        inputSoapRequestOutputResponse(SOAPCLIENTOBJ, soapEndPointUrl, SOAPACTION, FILEPATH,  xmlInFileName, RESPONSEOUTPUTLOCATION, xmlOutFileName);

        String policyNumber = getPolicyNumberFromResponse(RESPONSEOUTPUTLOCATION, xmlOutFileName, TAGNAMEFORPOLICYNUMBER);

        testUIForEUIM(policyNumber, true, false);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-11655 18.5-MD Enhanced UIMBI/PD Coverage: Comprater/QuickQuote")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12845")
    /** @scenario -
     *  1. Mock Request will flag UI in PAS.
     *  2. Convert XML into web-service message
     *  3. Send web-service request message to endpoint URL and receive response message
     *  4. Validate response contains calculated premium and quote #.
     *  5. Validate quote created in PAS w/ "Enhanced UIMBI/PD" coverage = true in P&C tab.
     */
    public void AC3A_QuickQuote_v1dot5_Flag(@Optional("") String state) {

        // [CONFIG] Scenario Variables
        String xmlInFileName = "QuickQuoteRequest_YesFlag.xml";
        String xmlOutFileName = "QuickQuoteResponse_YesFlag.xml";
        String soapEndPointUrl = HTTP + hostName + ":9095/aaa-admin/services/1.5/aaaSSPolicyRate";

        inputSoapRequestOutputResponse(SOAPCLIENTOBJ, soapEndPointUrl, SOAPACTION, FILEPATH,  xmlInFileName, RESPONSEOUTPUTLOCATION, xmlOutFileName);

        String policyNumber = getPolicyNumberFromResponse(RESPONSEOUTPUTLOCATION, xmlOutFileName, TAGNAMEFORPOLICYNUMBER);

        testUIForEUIM(policyNumber, false, true);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-11655 18.5-MD Enhanced UIMBI/PD Coverage: Comprater/QuickQuote")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12845")
    /** @scenario -
     *  1. Mock Request will NOT flag UI in PAS.
     *  2. Convert XML into web-service message
     *  3. Send web-service request message to endpoint URL and receive response message
     *  4. Validate response contains calculated premium and quote #.
     *  5. Validate quote created in PAS w/ "Enhanced UIMBI/PD" coverage = false in P&C tab.
     */
    public void AC3B_QuickQuote_v1dot5_NoFlag(@Optional("") String state) {

        // [CONFIG] Scenario Variables
        String xmlInFileName = "QuickQuoteRequest_NoFlag.xml";
        String xmlOutFileName = "QuickQuoteResponse_NoFlag.xml";
        String soapEndPointUrl = HTTP + hostName + ":9095/aaa-admin/services/1.5/aaaSSPolicyRate";

        inputSoapRequestOutputResponse(SOAPCLIENTOBJ, soapEndPointUrl, SOAPACTION, FILEPATH,  xmlInFileName, RESPONSEOUTPUTLOCATION, xmlOutFileName);

        String policyNumber = getPolicyNumberFromResponse(RESPONSEOUTPUTLOCATION, xmlOutFileName, TAGNAMEFORPOLICYNUMBER);

        testUIForEUIM(policyNumber, false, false);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-11655 18.5-MD Enhanced UIMBI/PD Coverage: Comprater/QuickQuote")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12845")
    /** @scenario -
     *  1. Mock Request will flag UI in PAS.
     *  2. Convert XML into web-service message
     *  3. Send web-service request message to endpoint URL and receive response message
     *  4. Validate response contains calculated premium and quote #.
     *  5. Validate quote created in PAS w/ "Enhanced UIMBI/PD" coverage = true in P&C tab.
     */
    public void AC4A_QuickQuote_v1dot6_Flag(@Optional("") String state) {

        // [CONFIG] Scenario Variables
        String xmlInFileName = "QuickQuoteRequest_YesFlag.xml";
        String xmlOutFileName = "QuickQuoteResponse_YesFlag.xml";
        String soapEndPointUrl = HTTP + hostName + ":9095/aaa-admin/services/1.6/aaaSSPolicyRate";

        inputSoapRequestOutputResponse(SOAPCLIENTOBJ, soapEndPointUrl, SOAPACTION, FILEPATH,  xmlInFileName, RESPONSEOUTPUTLOCATION, xmlOutFileName);

        String policyNumber = getPolicyNumberFromResponse(RESPONSEOUTPUTLOCATION, xmlOutFileName, TAGNAMEFORPOLICYNUMBER);

        testUIForEUIM(policyNumber, false, true);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "PAS-11655 18.5-MD Enhanced UIMBI/PD Coverage: Comprater/QuickQuote")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12845")
    /** @scenario -
     *  1. Mock Request will NOT flag UI in PAS.
     *  2. Convert XML into web-service message
     *  3. Send web-service request message to endpoint URL and receive response message
     *  4. Validate response contains calculated premium and quote #.
     *  5. Validate quote created in PAS w/ "Enhanced UIMBI/PD" coverage = false in P&C tab.
     */
    public void AC4B_QuickQuote_v1dot6_NoFlag(@Optional("") String state) {

        // [CONFIG] Scenario Variables
        String xmlInFileName = "QuickQuoteRequest_NoFlag.xml";
        String xmlOutFileName = "QuickQuoteResponse_NoFlag.xml";
        String soapEndPointUrl = HTTP + hostName + ":9095/aaa-admin/services/1.6/aaaSSPolicyRate";

        inputSoapRequestOutputResponse(SOAPCLIENTOBJ, soapEndPointUrl, SOAPACTION, FILEPATH,  xmlInFileName, RESPONSEOUTPUTLOCATION, xmlOutFileName);

        String policyNumber = getPolicyNumberFromResponse(RESPONSEOUTPUTLOCATION, xmlOutFileName, TAGNAMEFORPOLICYNUMBER);

        testUIForEUIM(policyNumber, false, false);
    }

    public ArrayList<String> trimBadValuesFromList(ArrayList<String> in_foundValuesFromTag) {
        // Remove extra value from list.
        for(String stringValue : in_foundValuesFromTag) {
            printToDebugLog("Found [" + stringValue + "] ----- ");
            if (stringValue.length()<=1) {
                printToDebugLog("Trimming " +stringValue + " from the list.");
                in_foundValuesFromTag.remove(stringValue);
            }
            if (!stringValue.startsWith("Q")) {
                printToDebugLog("Removing " +stringValue + " as policy for not starting with 'Q'.");
                in_foundValuesFromTag.remove(stringValue);
            }
        }
        return in_foundValuesFromTag;
    }

    public void makeEditsToCompraterQuote() {
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        GeneralTab generalTab = new GeneralTab();
        LocalDateTime systemDate = LocalDateTime.now();
        WebElement element = generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.PRIOR_MOVE_IN_DATE).getWebElement();
        element.clear();
        element.sendKeys(systemDate.getMonthValue() + "/" + systemDate.getDayOfMonth() + "/" + systemDate.getYear());
        generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_BTN).click();
        generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_BTN).click();
        BrowserController.get().driver().findElement(DialogsMetaData.AddressValidationMetaData.STREET_NUMBER.getLocator()).sendKeys("22205");
        BrowserController.get().driver().findElement(DialogsMetaData.AddressValidationMetaData.STREET_NAME.getLocator()).sendKeys("Greenspring Ave");
        BrowserController.get().driver().findElement(DialogsMetaData.AddressValidationMetaData.STREET_NUMBER.getLocator()).sendKeys();
        generalTab.getValidateAddressDialogAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.AddressValidation.BTN_OK).click();
    }

    public void inputSoapRequestOutputResponse(SoapClient soapClientObject, String _soapEndPointUrl, String _soapAction, String _filePath, String _xmlInFileName, String _responseOutputLocation, String _xmlOutFileName) {

        try {
            // First, get XML File and convert into XML String
            String xmlAsRawString = soapClientObject.convertXMLFileToString(_filePath + _xmlInFileName);

            // Convert XML String into SOAP Request
            SOAPMessage requestFromRawXML = soapClientObject.getSOAPMessageFromRawString(xmlAsRawString);

            // Send Request. Get Response.
            SOAPMessage soapResponse = soapClientObject.callSoapWebService(requestFromRawXML, _soapEndPointUrl, _soapAction);

            // Convert XML String into XML File
            soapClientObject.convertSoapMessageToXMLFile(soapResponse, _responseOutputLocation + _xmlOutFileName);

        }catch (ParserConfigurationException ex) {
            printToDebugLog("ParserConfigurationException EXCEPTION OCCURED!");
            printToDebugLog(ex.getStackTrace().toString());
        }catch (IOException ex) {
            printToDebugLog("IOException EXCEPTION OCCURED!");
            printToDebugLog(ex.getStackTrace().toString());
        }catch (SAXException ex) {
            printToDebugLog("SAXException EXCEPTION OCCURED!");
            printToDebugLog(ex.getStackTrace().toString());
        }catch (TransformerException ex) {
            printToDebugLog("TransformerException EXCEPTION OCCURED!");
            printToDebugLog(ex.getStackTrace().toString());
        }catch (SOAPException ex) {
            printToDebugLog("SOAPException EXCEPTION OCCURED!");
            printToDebugLog(ex.getStackTrace().toString());
        }
    }

    public String getPolicyNumberFromResponse(String _responseOutputLocation, String _xmlOutFileName, String _tagNameForPolicyNum) {

        ArrayList<String> _foundValuesFromTag = new ArrayList<String>();
        try {
            // Parse Output XML for values contained within tag, get policy number.
            _foundValuesFromTag = XmlParser.returnValueFromXMLNode(_responseOutputLocation, _xmlOutFileName, _tagNameForPolicyNum);

            _foundValuesFromTag = trimBadValuesFromList(_foundValuesFromTag);

        }catch (IOException ex) {
            printToDebugLog("IOException EXCEPTION OCCURED!");
            printToDebugLog(ex.getStackTrace().toString());
        }catch (SAXException ex) {
            printToDebugLog("SAXException EXCEPTION OCCURED!");
            printToDebugLog(ex.getStackTrace().toString());
        }

        // Validate values pulled from XML as string list.
        return _foundValuesFromTag.get(0);
    }

    public void testUIForEUIM(String policyNumber, Boolean bIsCompratorQuote, Boolean bShouldFlagBePresent) {

        // Launch Application to Check UI
        mainApp().open();
        SearchPage.openQuote(policyNumber);
        policy.dataGather().start();

        if (bIsCompratorQuote) {
            // Viewing Quote Now. Edit according to Comprator policy needs.
            makeEditsToCompraterQuote();
        }

        // Go To PNC Tab
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        CheckBox enhancedUIMCheckBox = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ENHANCED_UIM);

        if (bShouldFlagBePresent) {
            // Verify UI Checkbox is marked True.
            assertThat(enhancedUIMCheckBox.getWebElement().isSelected()).isTrue();
        }
        else{
            // Verify UI Checkbox is marked False.
            assertThat(enhancedUIMCheckBox.getWebElement().isSelected()).isFalse();
        }

    }
}