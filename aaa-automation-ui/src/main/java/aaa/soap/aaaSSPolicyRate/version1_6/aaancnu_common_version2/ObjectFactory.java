
package aaa.soap.aaaSSPolicyRate.version1_6.aaancnu_common_version2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the aaancnu_common_version2.com.aaancnuit package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ErrorInfo_QNAME = new QName("http://www.aaancnuit.com.AAANCNU_Common_version2", "ErrorInfo");
    private final static QName _ExtensionArea_QNAME = new QName("http://www.aaancnuit.com.AAANCNU_Common_version2", "ExtensionArea");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: aaancnu_common_version2.com.aaancnuit
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ErrorInfo }
     * 
     */
    public ErrorInfo createErrorInfo() {
        return new ErrorInfo();
    }

    /**
     * Create an instance of {@link ExtensionArea }
     * 
     */
    public ExtensionArea createExtensionArea() {
        return new ExtensionArea();
    }

    /**
     * Create an instance of {@link ApplicationContext }
     * 
     */
    public ApplicationContext createApplicationContext() {
        return new ApplicationContext();
    }

    /**
     * Create an instance of {@link Status }
     * 
     */
    public Status createStatus() {
        return new Status();
    }

    /**
     * Create an instance of {@link StatusWithCommonReason }
     * 
     */
    public StatusWithCommonReason createStatusWithCommonReason() {
        return new StatusWithCommonReason();
    }

    /**
     * Create an instance of {@link RuleResponse }
     * 
     */
    public RuleResponse createRuleResponse() {
        return new RuleResponse();
    }

    /**
     * Create an instance of {@link RuleResult }
     * 
     */
    public RuleResult createRuleResult() {
        return new RuleResult();
    }

    /**
     * Create an instance of {@link RuleAttribute }
     * 
     */
    public RuleAttribute createRuleAttribute() {
        return new RuleAttribute();
    }

    /**
     * Create an instance of {@link SearchCriteria }
     * 
     */
    public SearchCriteria createSearchCriteria() {
        return new SearchCriteria();
    }

    /**
     * Create an instance of {@link ServiceOptions }
     * 
     */
    public ServiceOptions createServiceOptions() {
        return new ServiceOptions();
    }

    /**
     * Create an instance of {@link ResponseSort }
     * 
     */
    public ResponseSort createResponseSort() {
        return new ResponseSort();
    }

    /**
     * Create an instance of {@link UserInfo }
     * 
     */
    public UserInfo createUserInfo() {
        return new UserInfo();
    }

    /**
     * Create an instance of {@link ObjectReference }
     * 
     */
    public ObjectReference createObjectReference() {
        return new ObjectReference();
    }

    /**
     * Create an instance of {@link CurrencyAmount }
     * 
     */
    public CurrencyAmount createCurrencyAmount() {
        return new CurrencyAmount();
    }

    /**
     * Create an instance of {@link Amount }
     * 
     */
    public Amount createAmount() {
        return new Amount();
    }

    /**
     * Create an instance of {@link ObjectProperty }
     * 
     */
    public ObjectProperty createObjectProperty() {
        return new ObjectProperty();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.aaancnuit.com.AAANCNU_Common_version2", name = "ErrorInfo")
    public JAXBElement<ErrorInfo> createErrorInfo(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_ErrorInfo_QNAME, ErrorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensionArea }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.aaancnuit.com.AAANCNU_Common_version2", name = "ExtensionArea")
    public JAXBElement<ExtensionArea> createExtensionArea(ExtensionArea value) {
        return new JAXBElement<ExtensionArea>(_ExtensionArea_QNAME, ExtensionArea.class, null, value);
    }

}
