package xyz.stasiak.brewservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrewService {

    private final BrewOrderRepository brewOrderRepository;

    @Transactional
    public Optional<BrewStartResponse> startBrew(BrewStartRequest request) throws InterruptedException {
        BrewOrder brewOrder = brewOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new BrewOrder(request.brewId(), request.waterVolume(), request.beansWeight()));
        int brewTime = brewOrder.startBrew();
        if (brewTime == -1) {
            return Optional.empty();
        }
        brewOrderRepository.save(brewOrder);
        return Optional.of(new BrewStartResponse(request.brewId(), brewTime));
    }

    @Transactional
    public Optional<BrewFinishResponse> finishBrew(BrewFinishRequest request) {
        BrewOrder brewOrder = brewOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElseThrow(() -> new BrewException(request.brewId(), "Brew not found"));
        boolean result = brewOrder.finish();
        if (!result) {
            return Optional.empty();
        }
        brewOrderRepository.save(brewOrder);
        return Optional.of(new BrewFinishResponse(brewOrder.getBrewId()));
    }

    @Transactional
    public Optional<BrewCancelResponse> cancelBrew(BrewCancelRequest request) {
        BrewOrder brewOrder = brewOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new BrewOrder(request.brewId(), 0, 0));
        boolean result = brewOrder.cancel();
        if (!result) {
            return Optional.empty();
        }
        brewOrderRepository.save(brewOrder);
        return Optional.of(new BrewCancelResponse(brewOrder.getBrewId()));
    }
}
