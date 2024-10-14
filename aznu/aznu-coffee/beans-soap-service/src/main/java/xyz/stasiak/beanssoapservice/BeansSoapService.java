package xyz.stasiak.beanssoapservice;

import jakarta.jws.WebService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import xyz.stasiak.beanssoapservice.types.BeansSoapCancelRequest;
import xyz.stasiak.beanssoapservice.types.BeansSoapCancelResponse;
import xyz.stasiak.beanssoapservice.types.BeansSoapGrindRequest;
import xyz.stasiak.beanssoapservice.types.BeansSoapGrindResponse;

@WebService
@Service
@RequiredArgsConstructor
@Slf4j
public class BeansSoapService {
    private final BeansGrindService beansGrindService;

    @SneakyThrows
    public BeansSoapGrindResponse grindBeans(BeansSoapGrindRequest beansSoapGrindRequest) {
        log.info("Received grind request: {}", beansSoapGrindRequest);
        BeansSoapGrindResponse beansSoapGrindResponse;
        try {
            beansSoapGrindResponse = beansGrindService.grindBeans(beansSoapGrindRequest);
        } catch (DataIntegrityViolationException e) {
            // retry in case of concurrent modification
            log.info("Retrying grind request");
            beansSoapGrindResponse = beansGrindService.grindBeans(beansSoapGrindRequest);
        }
        return beansSoapGrindResponse;
    }

    public BeansSoapCancelResponse cancelBeans(BeansSoapCancelRequest beansSoapCancelRequest) {
        log.info("Received cancel request: {}", beansSoapCancelRequest);
        BeansSoapCancelResponse beansSoapCancelResponse;
        try {
            beansSoapCancelResponse = beansGrindService.cancelBeans(beansSoapCancelRequest);
        } catch (DataIntegrityViolationException e) {
            // retry in case of concurrent modification
            log.info("Retrying cancel request");
            beansSoapCancelResponse = beansGrindService.cancelBeans(beansSoapCancelRequest);
        }
        return beansSoapCancelResponse;
    }
}
