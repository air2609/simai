package com.simai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_opname_items",
        uniqueConstraints = @UniqueConstraint(name = "uq_stock_opname_item",
                columnNames = {"stock_opname_id", "item_id"}))
@Getter
@Setter
@NoArgsConstructor
public class StockOpnameItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_opname_id")
    private StockOpname stockOpname;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "system_qty", nullable = false, precision = 19, scale = 4)
    private BigDecimal systemQty = BigDecimal.ZERO;

    @Column(name = "physical_qty", nullable = false, precision = 19, scale = 4)
    private BigDecimal physicalQty = BigDecimal.ZERO;

    @Column(name = "variance_qty", nullable = false, precision = 19, scale = 4)
    private BigDecimal varianceQty = BigDecimal.ZERO;

    @Column(columnDefinition = "text")
    private String notes;
}
