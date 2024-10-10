package xyz.stasiak.waterservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaterService {

    private final WaterOrderRepository waterOrderRepository;

    @Transactional
    public void prepareWater(WaterPrepareRequest request) {
        try {
            WaterOrder waterOrder = waterOrderRepository.findByBrewIdForUpdate(request.brewId())
                    .orElse(new WaterOrder(request.brewId(), request.volume(), request.temperature()));
            waterOrder.prepare();
            waterOrderRepository.save(waterOrder);
        } catch (InterruptedException e) {
            throw new WaterException(request.brewId(), "InterruptedException", e);
        }
    }

    @Transactional
    public void cancelWater(WaterCancelRequest request) {
        WaterOrder waterOrder = waterOrderRepository.findByBrewIdForUpdate(request.brewId())
                .orElse(new WaterOrder(request.brewId(), 0, 0));
        waterOrder.cancel();
        waterOrderRepository.save(waterOrder);
    }
}
