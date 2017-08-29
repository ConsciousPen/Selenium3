
package aaa.soap.getAutoPolicyDetails.aaancnu_common_version2;

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
     * Create an instance of {@link RuleResults }
     * 
     */
    public RuleResults createRuleResults() {
        return new RuleResults();
    }

    /**
     * Create an instance of {@link ApplicationContext }
     * 
     */
    public ApplicationContext createApplicationContext() {
        return new ApplicationContext();
    }

    /**
     * Create an instance of {@link RuleResponse }
     * 
     */
    public RuleResponse createRuleResponse() {
        return new RuleResponse();
    }

    /**
     * Create an instance of {@link RuleAttribute }
     * 
     */
    public RuleAttribute createRuleAttribute() {
        return new RuleAttribute();
    }

    /**
     * Create an instance of {@link RuleResult }
     * 
     */
    public RuleResult createRuleResult() {
        return new RuleResult();
    }

    /**
     * Create an instance of {@link RuleAttributes }
     * 
     */
    public RuleAttributes createRuleAttributes() {
        return new RuleAttributes();
    }

    /**
     * Create an instance of {@link RuleResponses }
     * 
     */
    public RuleResponses createRuleResponses() {
        return new RuleResponses();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.aaancnuit.com.AAANCNU_Common_version2", name = "ErrorInfo")
    public JAXBElement<ErrorInfo> createErrorInfo(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_ErrorInfo_QNAME, ErrorInfo.class, null, value);
    }

}
