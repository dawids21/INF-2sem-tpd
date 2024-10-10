package xyz.stasiak.beansclientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import xyz.stasiak.beanssoapservice.BeansSoapService;
import xyz.stasiak.beanssoapservice.BeansSoapServiceService;

import javax.xml.namespace.QName;
import java.net.URL;

@SpringBootApplication
public class BeansClientServiceApplication {
    private static final QName SERVICE_NAME = new QName("http://beanssoapservice.stasiak.xyz/", "BeansSoapServiceService");

    public static void main(String[] args) {
        SpringApplication.run(BeansClientServiceApplication.class, args);
    }

    @Bean
    public BeansSoapService beansClientService() {
        URL wsdlURL = BeansSoapServiceService.WSDL_LOCATION;
        BeansSoapServiceService ss = new BeansSoapServiceService(wsdlURL, SERVICE_NAME);
        return ss.getBeansSoapServicePort();
    }
}
