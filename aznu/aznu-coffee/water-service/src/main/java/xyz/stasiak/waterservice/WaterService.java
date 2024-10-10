package xyz.stasiak.waterservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaterService {

    private final WaterOrderRepository waterOrderRepository;

    @Transactional
    public WaterPrepareResponse prepareWater(WaterPrepareRequest request) throws InterruptedException {
        WaterOrder waterOrder = waterOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new WaterOrder(request.brewId(), request.volume(), request.temperature()));
        waterOrder.prepare();
        waterOrderRepository.save(waterOrder);
        return new WaterPrepareResponse(waterOrder.getBrewId(), waterOrder.getVolume(), waterOrder.getTemperature());
    }

    @Transactional
    public WaterCancelResponse cancelWater(WaterCancelRequest request) {
        WaterOrder waterOrder = waterOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new WaterOrder(request.brewId(), 0, 0));
        waterOrder.cancel();
        waterOrderRepository.save(waterOrder);
        return new WaterCancelResponse(waterOrder.getBrewId());
    }
}
