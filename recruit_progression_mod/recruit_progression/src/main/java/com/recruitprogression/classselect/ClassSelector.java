package com.recruitprogression.classselect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

/**
 * Applies loadout (equipment) to a recruit based on its chosen RecruitClass.
 * Called once when the recruit entity first spawns (EntityJoinLevelEvent).
 *
 * Equipment scales with rank — this method should be called again on rank-up
 * via ClassSelector.upgradeEquipment().
 */
public class ClassSelector {

    /**
     * Assign a random class to a brand-new recruit, equip it accordingly.
     *
     * @param recruit  the recruit entity (must be a LivingEntity)
     * @param rng      random source (use entity's level random for reproducibility)
     * @return         the chosen class
     */
    public static RecruitClass assignRandomClass(LivingEntity recruit, Random rng) {
        RecruitClass chosen = RecruitClass.pickRandom(rng);
        applyLoadout(recruit, chosen, 0); // rank 0 = Apprentice equipment
        return chosen;
    }

    /**
     * Re-equip the recruit with better gear after a rank-up.
     *
     * @param recruit       the recruit entity
     * @param recruitClass  the recruit's class (unchanged)
     * @param rankOrdinal   new rank ordinal (0–7)
     */
    public static void upgradeEquipment(LivingEntity recruit, RecruitClass recruitClass, int rankOrdinal) {
        applyLoadout(recruit, recruitClass, rankOrdinal);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private static void applyLoadout(LivingEntity entity, RecruitClass cls, int rankOrdinal) {
        // Clear slots first
        entity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        entity.setItemSlot(EquipmentSlot.OFFHAND,  ItemStack.EMPTY);
        entity.setItemSlot(EquipmentSlot.HEAD,      ItemStack.EMPTY);
        entity.setItemSlot(EquipmentSlot.CHEST,     ItemStack.EMPTY);
        entity.setItemSlot(EquipmentSlot.LEGS,      ItemStack.EMPTY);
        entity.setItemSlot(EquipmentSlot.FEET,      ItemStack.EMPTY);

        switch (cls) {
            case SWORDSMAN    -> equipSwordsman(entity, rankOrdinal);
            case AXEMAN       -> equipAxeman(entity, rankOrdinal);
            case ARCHER       -> equipArcher(entity, rankOrdinal);
            case CROSSBOWMAN  -> equipCrossbowman(entity, rankOrdinal);
            case SHIELD_BEARER-> equipShieldBearer(entity, rankOrdinal);
            case SPEARMAN     -> equipSpearman(entity, rankOrdinal);
        }
    }

    // ── Loadout helpers ───────────────────────────────────────────────────────

    private static void equipSwordsman(LivingEntity e, int rank) {
        e.setItemSlot(EquipmentSlot.MAINHAND, tierSword(rank));
        e.setItemSlot(EquipmentSlot.HEAD,     tierHelmet(rank));
        e.setItemSlot(EquipmentSlot.CHEST,    tierChestplate(rank));
        e.setItemSlot(EquipmentSlot.LEGS,     tierLeggings(rank));
        e.setItemSlot(EquipmentSlot.FEET,     tierBoots(rank));
    }

    private static void equipAxeman(LivingEntity e, int rank) {
        e.setItemSlot(EquipmentSlot.MAINHAND, tierAxe(rank));
        e.setItemSlot(EquipmentSlot.HEAD,     tierHelmet(rank));
        e.setItemSlot(EquipmentSlot.CHEST,    tierChestplate(rank));
        e.setItemSlot(EquipmentSlot.LEGS,     tierLeggings(rank));
        e.setItemSlot(EquipmentSlot.FEET,     tierBoots(rank));
    }

    private static void equipArcher(LivingEntity e, int rank) {
        ItemStack bow = new ItemStack(Items.BOW);
        if (rank >= 4) enchant(bow, Enchantments.POWER_ARROWS, Math.min(rank - 3, 5));
        if (rank >= 6) enchant(bow, Enchantments.PUNCH_ARROWS, 1);
        e.setItemSlot(EquipmentSlot.MAINHAND, bow);
        e.setItemSlot(EquipmentSlot.OFFHAND,  new ItemStack(Items.ARROW));
        // Light armour — leather/chainmail
        e.setItemSlot(EquipmentSlot.HEAD,     new ItemStack(rank >= 3 ? Items.CHAINMAIL_HELMET : Items.LEATHER_HELMET));
        e.setItemSlot(EquipmentSlot.CHEST,    new ItemStack(rank >= 3 ? Items.CHAINMAIL_CHESTPLATE : Items.LEATHER_CHESTPLATE));
        e.setItemSlot(EquipmentSlot.LEGS,     new ItemStack(rank >= 3 ? Items.CHAINMAIL_LEGGINGS : Items.LEATHER_LEGGINGS));
        e.setItemSlot(EquipmentSlot.FEET,     new ItemStack(rank >= 3 ? Items.CHAINMAIL_BOOTS : Items.LEATHER_BOOTS));
    }

    private static void equipCrossbowman(LivingEntity e, int rank) {
        ItemStack xbow = new ItemStack(Items.CROSSBOW);
        if (rank >= 3) enchant(xbow, Enchantments.QUICK_CHARGE, Math.min(rank - 2, 3));
        if (rank >= 5) enchant(xbow, Enchantments.PIERCING, Math.min(rank - 4, 4));
        e.setItemSlot(EquipmentSlot.MAINHAND, xbow);
        e.setItemSlot(EquipmentSlot.HEAD,     tierHelmet(rank));
        e.setItemSlot(EquipmentSlot.CHEST,    tierChestplate(rank));
        e.setItemSlot(EquipmentSlot.LEGS,     tierLeggings(rank));
        e.setItemSlot(EquipmentSlot.FEET,     tierBoots(rank));
    }

    private static void equipShieldBearer(LivingEntity e, int rank) {
        e.setItemSlot(EquipmentSlot.MAINHAND, tierSword(rank));
        e.setItemSlot(EquipmentSlot.OFFHAND,  new ItemStack(Items.SHIELD));
        // Shield bearers get extra-heavy armour
        e.setItemSlot(EquipmentSlot.HEAD,     tierHelmet(Math.min(rank + 1, 7)));
        e.setItemSlot(EquipmentSlot.CHEST,    tierChestplate(Math.min(rank + 1, 7)));
        e.setItemSlot(EquipmentSlot.LEGS,     tierLeggings(Math.min(rank + 1, 7)));
        e.setItemSlot(EquipmentSlot.FEET,     tierBoots(Math.min(rank + 1, 7)));
    }

    private static void equipSpearman(LivingEntity e, int rank) {
        // Represent spear with enchanted sword (knockback = reach effect)
        ItemStack spear = tierSword(rank);
        enchant(spear, Enchantments.KNOCKBACK, rank >= 4 ? 2 : 1);
        e.setItemSlot(EquipmentSlot.MAINHAND, spear);
        e.setItemSlot(EquipmentSlot.HEAD,     tierHelmet(rank));
        e.setItemSlot(EquipmentSlot.CHEST,    tierChestplate(rank));
        e.setItemSlot(EquipmentSlot.LEGS,     tierLeggings(rank));
        e.setItemSlot(EquipmentSlot.FEET,     tierBoots(rank));
    }

    // ── Tiered item factories ─────────────────────────────────────────────────
    // rank 0-1 = iron, 2-3 = iron (enchanted), 4-5 = diamond, 6-7 = netherite

    private static ItemStack tierSword(int rank) {
        ItemStack s;
        if (rank >= 6)      s = new ItemStack(Items.NETHERITE_SWORD);
        else if (rank >= 4) s = new ItemStack(Items.DIAMOND_SWORD);
        else                s = new ItemStack(Items.IRON_SWORD);
        if (rank >= 2) enchant(s, Enchantments.SHARPNESS, Math.min(rank, 5));
        if (rank >= 5) enchant(s, Enchantments.LOOTING,   Math.min(rank - 4, 3));
        return s;
    }

    private static ItemStack tierAxe(int rank) {
        ItemStack a;
        if (rank >= 6)      a = new ItemStack(Items.NETHERITE_AXE);
        else if (rank >= 4) a = new ItemStack(Items.DIAMOND_AXE);
        else                a = new ItemStack(Items.IRON_AXE);
        if (rank >= 2) enchant(a, Enchantments.SHARPNESS, Math.min(rank, 5));
        if (rank >= 4) enchant(a, Enchantments.SWEEPING_EDGE, Math.min(rank - 3, 3));
        return a;
    }

    private static ItemStack tierHelmet(int rank) {
        if (rank >= 6) return new ItemStack(Items.NETHERITE_HELMET);
        if (rank >= 4) return new ItemStack(Items.DIAMOND_HELMET);
        if (rank >= 2) return new ItemStack(Items.IRON_HELMET);
        return new ItemStack(Items.LEATHER_HELMET);
    }

    private static ItemStack tierChestplate(int rank) {
        if (rank >= 6) return new ItemStack(Items.NETHERITE_CHESTPLATE);
        if (rank >= 4) return new ItemStack(Items.DIAMOND_CHESTPLATE);
        if (rank >= 2) return new ItemStack(Items.IRON_CHESTPLATE);
        return new ItemStack(Items.LEATHER_CHESTPLATE);
    }

    private static ItemStack tierLeggings(int rank) {
        if (rank >= 6) return new ItemStack(Items.NETHERITE_LEGGINGS);
        if (rank >= 4) return new ItemStack(Items.DIAMOND_LEGGINGS);
        if (rank >= 2) return new ItemStack(Items.IRON_LEGGINGS);
        return new ItemStack(Items.LEATHER_LEGGINGS);
    }

    private static ItemStack tierBoots(int rank) {
        if (rank >= 6) return new ItemStack(Items.NETHERITE_BOOTS);
        if (rank >= 4) return new ItemStack(Items.DIAMOND_BOOTS);
        if (rank >= 2) return new ItemStack(Items.IRON_BOOTS);
        return new ItemStack(Items.LEATHER_BOOTS);
    }

    /** Safely adds an enchantment to an item stack. */
    private static void enchant(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment ench, int level) {
        stack.enchant(ench, level);
    }
}
