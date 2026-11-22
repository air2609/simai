package com.simai.service;

import com.simai.common.exception.BusinessException;
import com.simai.common.exception.ResourceNotFoundException;
import com.simai.domain.InventoryBalance;
import com.simai.domain.Item;
import com.simai.domain.Location;
import com.simai.domain.StockMovement;
import com.simai.domain.enums.StockMovementType;
import com.simai.repository.InventoryBalanceRepository;
import com.simai.repository.ItemRepository;
import com.simai.repository.LocationRepository;
import com.simai.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryBalanceRepository balanceRepository;
    private final StockMovementRepository movementRepository;
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;

    /**
     * Pola inti: update inventory_balances dan catat stock_movements
     * dalam SATU transaksi database agar saldo & riwayat selalu sinkron.
     */
    @Transactional
    public StockMovement recordMovement(Long itemId,
                                        Long locationId,
                                        StockMovementType type,
                                        BigDecimal quantityDelta,
                                        String referenceNo,
                                        String notes) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", locationId));

        InventoryBalance balance = balanceRepository.findByItemIdAndLocationId(itemId, locationId)
                .orElseGet(() -> {
                    InventoryBalance b = new InventoryBalance();
                    b.setItem(item);
                    b.setLocation(location);
                    b.setQuantity(BigDecimal.ZERO);
                    return b;
                });

        BigDecimal newQuantity = balance.getQuantity().add(quantityDelta);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Stok tidak mencukupi untuk item " + item.getSku());
        }

        balance.setQuantity(newQuantity);
        balanceRepository.save(balance);

        StockMovement movement = new StockMovement();
        movement.setItem(item);
        movement.setLocation(location);
        movement.setMovementType(type);
        movement.setQuantityDelta(quantityDelta);
        movement.setReferenceNo(referenceNo);
        movement.setNotes(notes);
        return movementRepository.save(movement);
    }
}
