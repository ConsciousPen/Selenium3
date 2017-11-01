
package aaa.soap.autopolicy.models.aaancnu_wsdl_getautopolicydetail_version2;;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the aaa.soap.autopolicy.models.aaancnu_wsdl_getautopolicydetail_version2; package.
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

    private final static QName _GetAutoPolicyDetailResponse_QNAME = new QName("http://www.aaancnuit.com.AAANCNU_WSDL_GetAutoPolicyDetail_version2", "getAutoPolicyDetailResponse");
    private final static QName _GetAutoPolicyDetailRequest_QNAME = new QName("http://www.aaancnuit.com.AAANCNU_WSDL_GetAutoPolicyDetail_version2", "getAutoPolicyDetailRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: aaa.soap.autopolicy.models.aaancnu_wsdl_getautopolicydetail_version2;
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAutoPolicyDetailRequest }
     * 
     */
    public GetAutoPolicyDetailRequest createGetAutoPolicyDetailRequest() {
        return new GetAutoPolicyDetailRequest();
    }

    /**
     * Create an instance of {@link GetAutoPolicyDetailResponse }
     * 
     */
    public GetAutoPolicyDetailResponse createGetAutoPolicyDetailResponse() {
        return new GetAutoPolicyDetailResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAutoPolicyDetailResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.aaancnuit.com.AAANCNU_WSDL_GetAutoPolicyDetail_version2", name = "getAutoPolicyDetailResponse")
    public JAXBElement<GetAutoPolicyDetailResponse> createGetAutoPolicyDetailResponse(GetAutoPolicyDetailResponse value) {
        return new JAXBElement<GetAutoPolicyDetailResponse>(_GetAutoPolicyDetailResponse_QNAME, GetAutoPolicyDetailResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAutoPolicyDetailRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.aaancnuit.com.AAANCNU_WSDL_GetAutoPolicyDetail_version2", name = "getAutoPolicyDetailRequest")
    public JAXBElement<GetAutoPolicyDetailRequest> createGetAutoPolicyDetailRequest(GetAutoPolicyDetailRequest value) {
        return new JAXBElement<GetAutoPolicyDetailRequest>(_GetAutoPolicyDetailRequest_QNAME, GetAutoPolicyDetailRequest.class, null, value);
    }

}
