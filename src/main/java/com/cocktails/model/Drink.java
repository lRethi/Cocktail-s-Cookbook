
package com.cocktails.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drinks")
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 60)
    private String category;

    @Column(length = 2000)
    private String instructions;

    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DrinkIngredient> ingredients = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "drink_tags",
        joinColumns = @JoinColumn(name = "drink_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"drink_id", "tag_id"})
    )
    private List<Tag> tags = new ArrayList<>();

    public Drink() {
    }

    public Drink(String name, String description, String category, String instructions) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.instructions = instructions;
    }

    public void addIngredient(DrinkIngredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setDrink(this);
    }

    public void removeIngredient(DrinkIngredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setDrink(null);
    }

    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<DrinkIngredient> getIngredients() {
        return ingredients;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
