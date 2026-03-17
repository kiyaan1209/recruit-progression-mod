package com.recruitprogression.classselect;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;

import java.util.Random;

/**
 * Defines the weapon classes a recruit can self-select when spawned.
 * When a recruit is summoned it calls ClassSelector.pickRandomClass()
 * and permanently keeps that class (stored in its capability NBT).
 */
public enum RecruitClass {

    SWORDSMAN("Swordsman", "§b",
            Items.IRON_SWORD,
            "Fast and aggressive melee fighter."),

    AXEMAN("Axeman", "§4",
            Items.IRON_AXE,
            "Heavy hitter who can break shields."),

    ARCHER("Archer", "§a",
            Items.BOW,
            "Ranged attacker, keeps distance from threats."),

    CROSSBOWMAN("Crossbowman", "§6",
            Items.CROSSBOW,
            "Slower but hits harder at range."),

    SHIELD_BEARER("Shield Bearer", "§9",
            Items.SHIELD,
            "Defensive frontline — protects nearby allies."),

    SPEARMAN("Spearman", "§e",
            Items.IRON_SWORD,   // uses enchanted sword as stand-in; future: custom spear item
            "Reach fighter effective against mounted or large enemies.");

    // ─────────────────────────────────────────────────────────────────────────

    public final String displayName;
    public final String colorCode;
    public final Item primaryWeapon;
    public final String description;

    RecruitClass(String displayName, String colorCode, Item primaryWeapon, String description) {
        this.displayName = displayName;
        this.colorCode   = colorCode;
        this.primaryWeapon = primaryWeapon;
        this.description = description;
    }

    /** Formatted label, e.g. "§b[Swordsman]§r" */
    public String getFormattedName() {
        return colorCode + "[" + displayName + "]§r";
    }

    /**
     * Picks a random class for a newly-summoned recruit.
     * Weights can be tuned here; currently equal distribution.
     */
    public static RecruitClass pickRandom(Random rng) {
        RecruitClass[] values = values();
        return values[rng.nextInt(values.length)];
    }

    /** Look up a class by its display name (case-insensitive). */
    public static RecruitClass fromName(String name) {
        for (RecruitClass rc : values()) {
            if (rc.displayName.equalsIgnoreCase(name)) return rc;
        }
        return SWORDSMAN; // fallback
    }
}
