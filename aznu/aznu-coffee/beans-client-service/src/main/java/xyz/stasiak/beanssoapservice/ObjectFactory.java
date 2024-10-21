
package xyz.stasiak.beanssoapservice;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the xyz.stasiak.beanssoapservice package. 
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

    private final static QName _CancelBeans_QNAME = new QName("http://beanssoapservice.stasiak.xyz/", "cancelBeans");
    private final static QName _CancelBeansResponse_QNAME = new QName("http://beanssoapservice.stasiak.xyz/", "cancelBeansResponse");
    private final static QName _GrindBeans_QNAME = new QName("http://beanssoapservice.stasiak.xyz/", "grindBeans");
    private final static QName _GrindBeansResponse_QNAME = new QName("http://beanssoapservice.stasiak.xyz/", "grindBeansResponse");
    private final static QName _BeansSoapException_QNAME = new QName("http://beanssoapservice.stasiak.xyz/", "BeansSoapException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: xyz.stasiak.beanssoapservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CancelBeans }
     * 
     */
    public CancelBeans createCancelBeans() {
        return new CancelBeans();
    }

    /**
     * Create an instance of {@link CancelBeansResponse }
     * 
     */
    public CancelBeansResponse createCancelBeansResponse() {
        return new CancelBeansResponse();
    }

    /**
     * Create an instance of {@link GrindBeans }
     * 
     */
    public GrindBeans createGrindBeans() {
        return new GrindBeans();
    }

    /**
     * Create an instance of {@link GrindBeansResponse }
     * 
     */
    public GrindBeansResponse createGrindBeansResponse() {
        return new GrindBeansResponse();
    }

    /**
     * Create an instance of {@link BeansSoapException }
     * 
     */
    public BeansSoapException createBeansSoapException() {
        return new BeansSoapException();
    }

    /**
     * Create an instance of {@link BeansSoapGrindRequest }
     * 
     */
    public BeansSoapGrindRequest createBeansSoapGrindRequest() {
        return new BeansSoapGrindRequest();
    }

    /**
     * Create an instance of {@link BeansSoapGrindResponse }
     * 
     */
    public BeansSoapGrindResponse createBeansSoapGrindResponse() {
        return new BeansSoapGrindResponse();
    }

    /**
     * Create an instance of {@link BeansSoapCancelRequest }
     * 
     */
    public BeansSoapCancelRequest createBeansSoapCancelRequest() {
        return new BeansSoapCancelRequest();
    }

    /**
     * Create an instance of {@link BeansSoapCancelResponse }
     * 
     */
    public BeansSoapCancelResponse createBeansSoapCancelResponse() {
        return new BeansSoapCancelResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelBeans }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CancelBeans }{@code >}
     */
    @XmlElementDecl(namespace = "http://beanssoapservice.stasiak.xyz/", name = "cancelBeans")
    public JAXBElement<CancelBeans> createCancelBeans(CancelBeans value) {
        return new JAXBElement<CancelBeans>(_CancelBeans_QNAME, CancelBeans.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelBeansResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CancelBeansResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://beanssoapservice.stasiak.xyz/", name = "cancelBeansResponse")
    public JAXBElement<CancelBeansResponse> createCancelBeansResponse(CancelBeansResponse value) {
        return new JAXBElement<CancelBeansResponse>(_CancelBeansResponse_QNAME, CancelBeansResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GrindBeans }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GrindBeans }{@code >}
     */
    @XmlElementDecl(namespace = "http://beanssoapservice.stasiak.xyz/", name = "grindBeans")
    public JAXBElement<GrindBeans> createGrindBeans(GrindBeans value) {
        return new JAXBElement<GrindBeans>(_GrindBeans_QNAME, GrindBeans.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GrindBeansResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GrindBeansResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://beanssoapservice.stasiak.xyz/", name = "grindBeansResponse")
    public JAXBElement<GrindBeansResponse> createGrindBeansResponse(GrindBeansResponse value) {
        return new JAXBElement<GrindBeansResponse>(_GrindBeansResponse_QNAME, GrindBeansResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BeansSoapException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BeansSoapException }{@code >}
     */
    @XmlElementDecl(namespace = "http://beanssoapservice.stasiak.xyz/", name = "BeansSoapException")
    public JAXBElement<BeansSoapException> createBeansSoapException(BeansSoapException value) {
        return new JAXBElement<BeansSoapException>(_BeansSoapException_QNAME, BeansSoapException.class, null, value);
    }

}
