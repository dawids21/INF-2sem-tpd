package xyz.stasiak.beanssoapservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.stasiak.beanssoapservice.types.*;

@Service
@RequiredArgsConstructor
public class BeansGrindService {
    private final BeansOrderRepository beansOrderRepository;

    @Transactional
    public BeansSoapGrindResponse grindBeans(BeansSoapGrindRequest request) throws InterruptedException {
        BeansOrder beansOrder = beansOrderRepository.findByBrewIdForUpdate(request.getBrewId())
                .orElse(new BeansOrder(request.getBrewId(), request.getName(), request.getWeight()));
        beansOrder.grind();
        beansOrderRepository.save(beansOrder);
        BeansSoapGrindResponse response = new BeansSoapGrindResponse();
        response.setBrewId(beansOrder.getBrewId());
        response.setName(beansOrder.getName());
        response.setWeight(beansOrder.getWeight());
        return response;
    }

    @Transactional
    public BeansSoapCancelResponse cancelBeans(BeansSoapCancelRequest request) {
        BeansOrder beansOrder = beansOrderRepository.findByBrewIdForUpdate(request.getBrewId())
                .orElse(new BeansOrder(request.getBrewId(), "", 0));
        beansOrder.cancel();
        beansOrderRepository.save(beansOrder);
        BeansSoapCancelResponse response = new BeansSoapCancelResponse();
        response.setBrewId(beansOrder.getBrewId());
        return response;
    }
}
