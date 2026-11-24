
package com.cocktails;

import com.cocktails.dao.*;
import com.cocktails.model.*;
import com.cocktails.util.HibernateUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static Long findOrCreateIngredient(
        IngredientDAO dao,
        String name,
        String type
    ) {
        if (!dao.existsByName(name)) {
            return dao.create(new Ingredient(name, type));
        }

        return dao.findAll()
            .stream()
            .filter(i -> i.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow()
            .getId();
    }

    private static Long findOrCreateTag(
        TagDAO dao,
        String name,
        String type
    ) {
        if (!dao.existsByName(name)) {
            return dao.create(new Tag(name, type));
        }

        return dao.findAll()
            .stream()
            .filter(t -> t.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow()
            .getId();
    }

    private static Long findOrCreateDrink(
        DrinkDAO dao,
        Drink drink
    ) {
        return dao.findByName(drink.getName())
            .stream()
            .filter(d -> d.getName().equalsIgnoreCase(drink.getName()))
            .findFirst()
            .map(Drink::getId)
            .orElseGet(() -> dao.create(drink));
    }

    private static boolean hasIngredient(
        List<DrinkIngredient> associations,
        Long ingredientId
    ) {
        return associations.stream()
            .anyMatch(item ->
                item.getIngredient().getId().equals(ingredientId)
            );
    }

    public static void main(String[] args) {
        DrinkDAO drinkDAO = new DrinkDAO();
        IngredientDAO ingredientDAO = new IngredientDAO();
        DrinkIngredientDAO drinkIngredientDAO = new DrinkIngredientDAO();
        TagDAO tagDAO = new TagDAO();

        try {
            System.out.println("=== CADASTRO DE INGREDIENTES ===");

            Map<String, Long> ingredientIds = new HashMap<>();

            String[][] ingredients = {
                {"Limão", "Fruta"},
                {"Açúcar", "Adoçante"},
                {"Gelo", "Sólido"},
                {"Vodka", "Álcool"},
                {"Whiskey", "Álcool"}
            };

            for (String[] data : ingredients) {
                Long id = findOrCreateIngredient(
                    ingredientDAO,
                    data[0],
                    data[1]
                );

                ingredientIds.put(data[0], id);

                System.out.println(
                    data[0] + " cadastrado ou já existente. ID: " + id
                );
            }

            System.out.println("\n=== LISTAGEM DE INGREDIENTES ===");

            for (Ingredient ingredient : ingredientDAO.findAll()) {
                System.out.println(
                    ingredient.getId() + " - " +
                    ingredient.getName() + " (" +
                    ingredient.getType() + ")"
                );
            }

            System.out.println("\n=== CADASTRO DE TAGS ===");

            Long refreshingTagId = findOrCreateTag(
                tagDAO,
                "Refrescante",
                "Característica"
            );

            Long citrusTagId = findOrCreateTag(
                tagDAO,
                "Cítrica",
                "Sabor"
            );

            Long alcoholicTagId = findOrCreateTag(
                tagDAO,
                "Alcoólica",
                "Tipo"
            );

            System.out.println("Tags cadastradas ou já existentes.");

            System.out.println("\n=== CADASTRO DE BEBIDAS ===");

            Drink lemonade = new Drink(
                "Limonada",
                "Bebida preparada com limão, açúcar e gelo.",
                "Refrescante",
                "Misturar os ingredientes e servir."
            );

            Drink caipiroska = new Drink(
                "Caipiroska",
                "Bebida preparada com vodka, limão e gelo.",
                "Alcoólica",
                "Misturar os ingredientes e servir."
            );

            Drink caipirinha = new Drink(
                "Caipirinha",
                "Bebida preparada com whiskey, limão e gelo.",
                "Alcoólica",
                "Misturar os ingredientes e servir."
            );

            Long lemonadeId = findOrCreateDrink(drinkDAO, lemonade);
            Long caipiroskaId = findOrCreateDrink(drinkDAO, caipiroska);
            Long caipirinhaId = findOrCreateDrink(drinkDAO, caipirinha);

            System.out.println("Limonada. ID: " + lemonadeId);
            System.out.println("Caipiroska. ID: " + caipiroskaId);
            System.out.println("caipirinha. ID: " + caipirinhaId);

            System.out.println("\n=== ASSOCIAÇÃO DE INGREDIENTES ===");

            Long lemonId = ingredientIds.get("Limão");
            Long sugarId = ingredientIds.get("Açúcar");
            Long iceId = ingredientIds.get("Gelo");
            Long vodkaId = ingredientIds.get("Vodka");
            Long whiskeyId = ingredientIds.get("Whiskey");

            List<DrinkIngredient> lemonadeIngredients =
                drinkIngredientDAO.findByDrink(lemonadeId);

            if (!hasIngredient(lemonadeIngredients, lemonId)) {
                drinkIngredientDAO.add(
                    lemonadeId,
                    lemonId,
                    new BigDecimal("40"),
                    "ml",
                    "Suco de limão"
                );
            }

            if (!hasIngredient(lemonadeIngredients, sugarId)) {
                drinkIngredientDAO.add(
                    lemonadeId,
                    sugarId,
                    new BigDecimal("15"),
                    "g",
                    "Açúcar"
                );
            }

            if (!hasIngredient(lemonadeIngredients, iceId)) {
                drinkIngredientDAO.add(
                    lemonadeId,
                    iceId,
                    new BigDecimal("4"),
                    "unidades",
                    "Cubos de gelo"
                );
            }

            List<DrinkIngredient> caipiroskaIngredients =
                drinkIngredientDAO.findByDrink(caipiroskaId);

            if (!hasIngredient(caipiroskaIngredients, vodkaId)) {
                drinkIngredientDAO.add(
                    caipiroskaId,
                    vodkaId,
                    new BigDecimal("50"),
                    "ml",
                    "Vodka"
                );
            }

            if (!hasIngredient(caipiroskaIngredients, lemonId)) {
                drinkIngredientDAO.add(
                    caipiroskaId,
                    lemonId,
                    new BigDecimal("30"),
                    "ml",
                    "Suco de limão"
                );
            }

            if (!hasIngredient(caipiroskaIngredients, iceId)) {
                drinkIngredientDAO.add(
                    caipiroskaId,
                    iceId,
                    new BigDecimal("4"),
                    "unidades",
                    "Cubos de gelo"
                );
            }

            List<DrinkIngredient> caipirinhaIngredients =
                drinkIngredientDAO.findByDrink(caipirinhaId);

            if (!hasIngredient(caipirinhaIngredients, vodkaId)) {
                drinkIngredientDAO.add(
                    caipirinhaId,
                    whiskeyId,
                    new BigDecimal("50"),
                    "ml",
                    "Whiskey"
                );
            }

            if (!hasIngredient(caipirinhaIngredients, lemonId)) {
                drinkIngredientDAO.add(
                    caipirinhaId,
                    lemonId,
                    new BigDecimal("25"),
                    "ml",
                    "Suco de limão"
                );
            }

            if (!hasIngredient(caipirinhaIngredients, iceId)) {
                drinkIngredientDAO.add(
                    caipirinhaId,
                    iceId,
                    new BigDecimal("4"),
                    "unidades",
                    "Cubos de gelo"
                );
            }

            System.out.println("Ingredientes associados às bebidas.");

            System.out.println("\n=== ASSOCIAÇÃO DE TAGS ===");

            drinkDAO.addTag(lemonadeId, refreshingTagId);
            drinkDAO.addTag(lemonadeId, citrusTagId);

            drinkDAO.addTag(caipiroskaId, citrusTagId);
            drinkDAO.addTag(caipiroskaId, alcoholicTagId);

            drinkDAO.addTag(caipirinhaId, refreshingTagId);
            drinkDAO.addTag(caipirinhaId, alcoholicTagId);

            System.out.println("Tags associadas às bebidas.");

            System.out.println("\n=== BUSCA POR ID ===");

            Drink foundDrink = drinkDAO.findById(lemonadeId);

            if (foundDrink != null) {
                System.out.println(
                    foundDrink.getName() + " - " +
                    foundDrink.getDescription()
                );

                for (DrinkIngredient item : foundDrink.getIngredients()) {
                    System.out.println(
                        item.getIngredient().getName() + ": " +
                        item.getQuantity() + " " +
                        item.getUnit()
                    );
                }

                for (Tag tag : foundDrink.getTags()) {
                    System.out.println("Tag: " + tag.getName());
                }
            }

            System.out.println("\n=== BUSCA POR NOME ===");

            List<Drink> nameResults = drinkDAO.findByName("a");

            for (Drink drink : nameResults) {
                System.out.println(
                    drink.getId() + " - " + drink.getName()
                );
            }

            System.out.println("\n=== BUSCA POR INGREDIENTE ===");

            List<DrinkIngredient> lemonAssociations =
                drinkIngredientDAO.findDrinksByIngredient(lemonId);

            for (DrinkIngredient item : lemonAssociations) {
                System.out.println(
                    item.getDrink().getName() + " - " +
                    item.getQuantity() + " " + item.getUnit()
                );
            }

            System.out.println("\n=== BUSCA POR QUANTIDADE MÍNIMA ===");

            List<DrinkIngredient> minimumResults =
                drinkIngredientDAO.findByIngredientAndMinimumQuantity(
                    "Limão",
                    new BigDecimal("20"),
                    "ml"
                );

            for (DrinkIngredient item : minimumResults) {
                System.out.println(
                    item.getDrink().getName() + " possui " +
                    item.getQuantity() + " ml de limão."
                );
            }

            System.out.println("\n=== BUSCA POR INTERVALO DE QUANTIDADE ===");

            List<DrinkIngredient> rangeResults =
                drinkIngredientDAO.findByIngredientBetweenQuantities(
                    "Limão",
                    new BigDecimal("10"),
                    new BigDecimal("35"),
                    "ml"
                );

            for (DrinkIngredient item : rangeResults) {
                System.out.println(
                    item.getDrink().getName() + ": " +
                    item.getQuantity() + " ml de limão."
                );
            }

            System.out.println("\n=== BUSCA POR QUANTIDADE MÁXIMA ===");

            List<DrinkIngredient> maximumResults =
                drinkIngredientDAO.findByIngredientAndMaximumQuantity(
                    "Limão",
                    new BigDecimal("35"),
                    "ml"
                );

            for (DrinkIngredient item : maximumResults) {
                System.out.println(
                    item.getDrink().getName() + ": " +
                    item.getQuantity() + " ml de limão."
                );
            }

            System.out.println("\n=== BEBIDAS SEM AÇÚCAR ===");

            List<Drink> withoutSugar =
                drinkDAO.findWithoutIngredient("Açúcar");

            for (Drink drink : withoutSugar) {
                System.out.println(drink.getName());
            }

            System.out.println("\n=== PESQUISA COM DUAS CONDIÇÕES ===");

            List<Drink> combinedResults =
                drinkDAO.findByIngredientAndMinimumQuantityWithoutAnother(
                    "Limão",
                    new BigDecimal("20"),
                    "ml",
                    "Açúcar"
                );

            for (Drink drink : combinedResults) {
                System.out.println(
                    drink.getName() +
                    " possui mais de 20 ml de limão e não contém açúcar."
                );
            }

            System.out.println("\n=== ATUALIZAÇÃO DE BEBIDA ===");

            boolean drinkUpdated = drinkDAO.update(
                caipiroskaId,
                "Caipigin",
                "Bebida preparada com gin, limão e gelo.",
                "Alcoólica",
                "Misturar os ingredientes e servir."
            );

            System.out.println("Bebida atualizada: " + drinkUpdated);

            System.out.println("\n=== ATUALIZAÇÃO DE INGREDIENTE ===");

            boolean ingredientUpdated = ingredientDAO.update(
                vodkaId,
                "Gin",
                "Álcool"
            );

            System.out.println("Ingrediente atualizado: " + ingredientUpdated);

            System.out.println("\n=== ATUALIZAÇÃO DE ASSOCIAÇÃO ===");

            List<DrinkIngredient> caipiroskaAssociations =
                drinkIngredientDAO.findByDrink(caipiroskaId);

            if (!caipiroskaAssociations.isEmpty()) {
                DrinkIngredient association = caipiroskaAssociations.get(0);

                boolean associationUpdated = drinkIngredientDAO.update(
                    association.getId(),
                    association.getQuantity(),
                    association.getUnit(),
                    "Associação atualizada para teste."
                );

                System.out.println(
                    "Associação atualizada: " + associationUpdated
                );
            }

            System.out.println("\n=== DUPLICAÇÃO DE BEBIDA ===");

            Drink original = drinkDAO.findById(caipirinhaId);

            Long duplicatedId = original != null
                ? drinkDAO.duplicate(original)
                : null;

            System.out.println("Cópia criada. ID: " + duplicatedId);

            System.out.println("\n=== REMOÇÃO DE ASSOCIAÇÃO ===");

            List<DrinkIngredient> caipirinhaAssociations =
                drinkIngredientDAO.findByDrink(caipirinhaId);

            if (!caipirinhaAssociations.isEmpty()) {
                boolean associationRemoved =
                    drinkIngredientDAO.remove(
                        caipirinhaAssociations.get(0).getId()
                    );

                System.out.println(
                    "Associação removida: " + associationRemoved
                );
            }

            System.out.println("\n=== REMOÇÃO DE TAG ===");

            Long temporaryTagId = findOrCreateTag(
                tagDAO,
                "Tag temporária",
                "Teste"
            );

            drinkDAO.addTag(caipiroskaId, temporaryTagId);

            boolean tagRemoved = tagDAO.delete(temporaryTagId);

            System.out.println("Tag removida: " + tagRemoved);

            System.out.println("\n=== EXCLUSÃO DE BEBIDA ===");

            if (duplicatedId != null) {
                boolean drinkRemoved = drinkDAO.delete(duplicatedId);

                System.out.println("Bebida removida: " + drinkRemoved);
            }

            System.out.println("\n=== EXCLUSÃO DE INGREDIENTE ===");

            Long temporaryIngredientId = findOrCreateIngredient(
                ingredientDAO,
                "Ingrediente temporário",
                "Teste"
            );

            boolean ingredientRemoved =
                ingredientDAO.delete(temporaryIngredientId);

            System.out.println(
                "Ingrediente removido: " + ingredientRemoved
            );

            System.out.println("\n=== FIM DOS TESTES ===");

        } catch (Exception e) {
            System.err.println(
                "Erro durante a execução: " + e.getMessage()
            );

            e.printStackTrace();

        } finally {
            HibernateUtil.shutdown();
        }
    }
}
