package com.recruitprogression.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.fml.ModList;

/**
 * Utility methods for identifying recruit entities and their owners.
 *
 * Villager Recruits mod ID: "recruits"
 * Main entity class (Forge registry name): "recruits:recruit"
 *
 * We use the registry name string to stay compatible without a compile-time
 * hard dependency — if the Recruits mod is absent the checks simply return false.
 */
public final class RecruitHelper {

    private RecruitHelper() {}

    /** Returns true if the Villager Recruits mod is loaded. */
    public static boolean isRecruitsModLoaded() {
        return ModList.get().isLoaded("recruits");
    }

    /**
     * Returns true if the given entity is a recruit from the Villager Recruits mod.
     * Works by checking the entity's registry name.
     */
    public static boolean isRecruit(LivingEntity entity) {
        if (!isRecruitsModLoaded()) return false;
        var regName = entity.getType().getRegistryName();
        return regName != null
                && "recruits".equals(regName.getNamespace())
                && "recruit".equals(regName.getPath());
    }

    /**
     * Returns true if the entity is a vanilla hostile monster
     * (skeleton, zombie, creeper, etc.) but NOT a recruit.
     */
    public static boolean isHostileMonster(LivingEntity entity) {
        return entity instanceof Monster && !isRecruit(entity);
    }

    /**
     * Returns true if victim is an "enemy" recruit relative to killer.
     * Two recruits are enemies if they have different owners (OwnerId differs)
     * or if one has no owner (e.g. untamed / hostile recruit).
     *
     * We read the OwnerId from the entity NBT because we avoid a hard compile
     * dependency on the Recruits mod's internal Tameable implementation.
     */
    public static boolean areEnemyRecruits(LivingEntity killer, LivingEntity victim) {
        if (!isRecruit(killer) || !isRecruit(victim)) return false;

        var killerNbt = new net.minecraft.nbt.CompoundTag();
        var victimNbt = new net.minecraft.nbt.CompoundTag();
        killer.saveWithoutId(killerNbt);
        victim.saveWithoutId(victimNbt);

        // Recruits mod stores owner UUID under "Owner" key (same as vanilla tameable)
        String killerOwner = killerNbt.getString("Owner");
        String victimOwner = victimNbt.getString("Owner");

        if (killerOwner.isEmpty() || victimOwner.isEmpty()) return true; // unowned = enemy
        return !killerOwner.equals(victimOwner);
    }
}
