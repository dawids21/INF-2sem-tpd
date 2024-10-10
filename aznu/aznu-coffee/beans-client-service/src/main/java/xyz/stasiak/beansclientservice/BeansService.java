package xyz.stasiak.beansclientservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.stasiak.beanssoapservice.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeansService {

    private final BeansSoapService beansSoapService;

    public BeansPrepareResponse prepareBeans(BeansPrepareRequest request) {
        BeansSoapGrindRequest beansSoapGrindRequest = new BeansSoapGrindRequest();
        beansSoapGrindRequest.setBrewId(request.brewId().toString());
        beansSoapGrindRequest.setName(request.name());
        beansSoapGrindRequest.setWeight(request.weight());
        BeansSoapGrindResponse beansSoapGrindResponse = beansSoapService.grindBeans(beansSoapGrindRequest);
        return new BeansPrepareResponse(UUID.fromString(beansSoapGrindResponse.getBrewId()), beansSoapGrindResponse.getName(), beansSoapGrindResponse.getWeight());
    }

    public BeansCancelResponse cancelBeans(BeansCancelRequest request) {
        BeansSoapCancelRequest beansSoapCancelRequest = new BeansSoapCancelRequest();
        beansSoapCancelRequest.setBrewId(request.brewId().toString());
        BeansSoapCancelResponse beansSoapCancelResponse = beansSoapService.cancelBeans(beansSoapCancelRequest);
        return new BeansCancelResponse(UUID.fromString(beansSoapCancelResponse.getBrewId()));
    }
}
