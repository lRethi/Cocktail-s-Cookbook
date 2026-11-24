
package com.cocktails.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "drink_ingredients")
public class DrinkIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drink_id", nullable = false)
    private Drink drink;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false, length = 30)
    private String unit;

    @Column(length = 500)
    private String preparation;

    public DrinkIngredient() {
    }

    public DrinkIngredient(Ingredient ingredient, BigDecimal quantity, String unit, String preparation) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
        this.preparation = preparation;
    }

    public Long getId() {
        return id;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }
}
