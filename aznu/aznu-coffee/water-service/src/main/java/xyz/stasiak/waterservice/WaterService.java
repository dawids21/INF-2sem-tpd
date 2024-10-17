package xyz.stasiak.waterservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaterService {

    private final WaterOrderRepository waterOrderRepository;

    @Transactional
    public Optional<WaterPrepareResponse> prepareWater(WaterPrepareRequest request) throws InterruptedException {
        WaterOrder waterOrder = waterOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new WaterOrder(request.brewId(), request.temperature()));
        boolean result = waterOrder.prepare();
        if (!result) {
            return Optional.empty();
        }
        waterOrderRepository.save(waterOrder);
        return Optional.of(
                new WaterPrepareResponse(waterOrder.getBrewId(), waterOrder.getVolume())
        );
    }

    @Transactional
    public Optional<WaterCancelResponse> cancelWater(WaterCancelRequest request) {
        WaterOrder waterOrder = waterOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new WaterOrder(request.brewId(), 0));
        boolean result = waterOrder.cancel();
        if (!result) {
            return Optional.empty();
        }
        waterOrderRepository.save(waterOrder);
        return Optional.of(
                new WaterCancelResponse(waterOrder.getBrewId())
        );
    }
}
