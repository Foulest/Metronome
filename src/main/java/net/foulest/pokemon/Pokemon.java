package net.foulest.pokemon;

import net.foulest.Main;
import net.foulest.pokemon.data.Ability;
import net.foulest.pokemon.data.HeldItem;
import net.foulest.pokemon.data.Nature;
import net.foulest.pokemon.data.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pokemon {

    // The Pokemon's name.
    public String name;

    // The Pokemon's ability & possible abilities.
    public Ability ability = Ability.NONE;
    public List<Ability> possibleAbilities;

    // The Pokemon's level. This will be dynamically set later.
    public int level = 100;

    // The Pokemon's type. A Pokemon can have up to two types.
    public List<Type> type;

    // The Pokemon's base stat total. This is used when calculating level.
    public int baseStatTotal;

    // If a Pokemon is shiny or not. This will be randomly set later.
    public boolean shiny = false;

    // The Pokemon's Tera Type. This will be randomly set later.
    public Type teraType = Type.NORMAL;

    // The Pokemon's EVs. This will be randomly set later.
    public int evHP = 0;
    public int evAtk = 0;
    public int evDef = 0;
    public int evSpAtk = 0;
    public int evSpDef = 0;
    public int evSpeed = 0;

    // The Pokemon's Nature. This will be randomly set later.
    public Nature nature;

    // The Pokemon's Held Item. This will be randomly set later.
    public HeldItem heldItem;

    // The master list of all Pokemon.
    public static List<Pokemon> allPokemon = new ArrayList<>();

    /**
     * Constructor for Pokemon.
     */
    public Pokemon(String name, List<Ability> possibleAbilities, List<Type> type, int baseStatTotal) {
        this.name = name;
        this.possibleAbilities = possibleAbilities;
        this.type = type;
        this.baseStatTotal = baseStatTotal;

        ability = generateAbility();
        level = Main.balanceLevels ? generateLevel() : 100;
        shiny = generateShiny();
        teraType = generateTeraType();
        nature = generateNature();
        heldItem = generateHeldItem();

        generateAndSetEVs();
    }

    /**
     * This generates a fair level to balance weaker & stronger Pokemon.
     * <p>
     * This is based on the Pokemon's base stat total.
     * A base stat total of 400 will generate a level of 75.
     * A higher BST will lower the level, and vice versa.
     */
    public int generateLevel() {
        // Calculate the difference from the reference in terms of levels.
        int levelChange = (baseStatTotal - 400) / 10;

        // Adjust the reference level by the calculated change.
        int calculatedLevel = 75 - levelChange;

        // Ensure the level remains within the 1-100 range.
        return Math.max(1, Math.min(calculatedLevel, 100));
    }

    /**
     * This randomly generates an ability from a Pokemon's possible abilities.
     * <p>
     * Doesn't generate certain banned abilities if Metronome Mode is enabled.
     */
    public Ability generateAbility() {
        List<Ability> bannedAbilities = Arrays.asList(Ability.WATER_ABSORB, Ability.DRY_SKIN, Ability.GRASSY_SURGE,
                Ability.POISON_HEAL, Ability.VOLT_ABSORB, Ability.EARTH_EATER, Ability.RAIN_DISH, Ability.ICE_BODY,
                Ability.IRON_BARBS, Ability.ROUGH_SKIN, Ability.CURSED_BODY, Ability.COMMANDER, Ability.FUR_COAT,
                Ability.GORILLA_TACTICS, Ability.HUGE_POWER, Ability.MOODY, Ability.NEUTRALIZING_GAS,
                Ability.PARENTAL_BOND, Ability.PERISH_BODY, Ability.PRESSURE, Ability.PURE_POWER, Ability.SAND_STREAM,
                Ability.STAMINA, Ability.WONDER_GUARD, Ability.HARVEST, Ability.CHEEK_POUCH, Ability.SNOW_WARNING);

        if (Main.metronomeMode && possibleAbilities.size() == 1 && bannedAbilities.contains(possibleAbilities.get(0))) {
            // If the only possible ability is in the bannedAbilities list, generate a completely random ability.
            Ability randomNewAbility;
            do {
                randomNewAbility = Ability.values()[(int) (Math.random() * Ability.values().length)];
            } while (bannedAbilities.contains(randomNewAbility));
            return randomNewAbility;

        } else {
            Ability randomAbility;
            do {
                randomAbility = possibleAbilities.get((int) (Math.random() * possibleAbilities.size()));
            } while (Main.metronomeMode && bannedAbilities.contains(randomAbility));
            return randomAbility;
        }
    }

    /**
     * This randomly generates the shiny status of a Pokemon at 10% odds.
     */
    public boolean generateShiny() {
        return Math.random() <= 0.1;
    }

    /**
     * This randomly generates a Tera Type for a Pokemon.
     * <p>
     * Doesn't generate Steel types if Metronome Mode is enabled.
     */
    public Type generateTeraType() {
        Type randomType;
        do {
            randomType = Type.values()[(int) (Math.random() * Type.values().length)];
        } while (Main.metronomeMode && randomType == Type.STEEL);
        return randomType;
    }

    /**
     * This randomly generates a Nature for a Pokemon.
     */
    public Nature generateNature() {
        return Nature.values()[(int) (Math.random() * Nature.values().length)];
    }

    /**
     * This randomly generates a Held Item for a Pokemon.
     * <p>
     * Doesn't generate certain banned held items if Metronome Mode is enabled.
     */
    public HeldItem generateHeldItem() {
        List<HeldItem> bannedItems = List.of(HeldItem.HEAVY_DUTY_BOOTS);

        HeldItem randomItem;
        do {
            randomItem = HeldItem.values()[(int) (Math.random() * HeldItem.values().length)];
        } while (Main.metronomeMode && bannedItems.contains(randomItem));

        return randomItem;
    }

    /**
     * This generates a Pokemon's EVs.
     * <p>
     * An EV can't be greater than 252, and all EVs combined can't be greater than 510.
     */
    public void generateAndSetEVs() {
        int evTotal = 0;
        int maxEvForStat = 252;
        int maxEvTotal = 510;

        if (Main.randomEVs) {
            while (evTotal < maxEvTotal) {
                int ev = (int) (Math.random() * (maxEvForStat + 1));

                // Adjust the EV if it would exceed the max EV total of 510
                if (evTotal + ev > maxEvTotal) {
                    ev = maxEvTotal - evTotal;
                }

                int randomStatChoice = (int) (Math.random() * 6);

                switch (randomStatChoice) {
                    case 0 -> {
                        if (evHP + ev > maxEvForStat) {
                            ev = maxEvForStat - evHP;
                        }
                        evHP += ev;
                    }
                    case 1 -> {
                        if (evAtk + ev > maxEvForStat) {
                            ev = maxEvForStat - evAtk;
                        }
                        evAtk += ev;
                    }
                    case 2 -> {
                        if (evDef + ev > maxEvForStat) {
                            ev = maxEvForStat - evDef;
                        }
                        evDef += ev;
                    }
                    case 3 -> {
                        if (evSpAtk + ev > maxEvForStat) {
                            ev = maxEvForStat - evSpAtk;
                        }
                        evSpAtk += ev;
                    }
                    case 4 -> {
                        if (evSpDef + ev > maxEvForStat) {
                            ev = maxEvForStat - evSpDef;
                        }
                        evSpDef += ev;
                    }
                    case 5 -> {
                        if (evSpeed + ev > maxEvForStat) {
                            ev = maxEvForStat - evSpeed;
                        }
                        evSpeed += ev;
                    }
                }

                evTotal += ev;
            }
        } else {
            evHP = 252;
            evAtk = 252;
            evDef = 252;
            evSpAtk = 252;
            evSpDef = 252;
            evSpeed = 252;
        }
    }
}
