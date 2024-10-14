package xyz.stasiak.brewservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrewService {

    private final BrewOrderRepository brewOrderRepository;

    @Transactional
    public BrewStartResponse startBrew(BrewStartRequest request) throws InterruptedException {
        BrewOrder brewOrder = brewOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new BrewOrder(request.brewId(), request.waterVolume(), request.beansWeight()));
        int brewTime = brewOrder.startBrew();
        brewOrderRepository.save(brewOrder);
        return new BrewStartResponse(brewOrder.getBrewId(), brewTime);
    }

    @Transactional
    public BrewFinishResponse finishBrew(BrewFinishRequest request) {
        BrewOrder brewOrder = brewOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElseThrow(() -> new BrewException(request.brewId(), "Brew not found"));
        brewOrder.finish();
        brewOrderRepository.save(brewOrder);
        return new BrewFinishResponse(brewOrder.getBrewId());
    }

    @Transactional
    public BrewCancelResponse cancelBrew(BrewCancelRequest request) {
        BrewOrder brewOrder = brewOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new BrewOrder(request.brewId(), 0, 0));
        brewOrder.cancel();
        brewOrderRepository.save(brewOrder);
        return new BrewCancelResponse(brewOrder.getBrewId());
    }
}
