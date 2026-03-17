package com.recruitprogression.events;

import com.recruitprogression.capability.RecruitCapability;
import com.recruitprogression.util.RecruitHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Handles NBT serialization of recruit capability data so that
 * rank and XP persist across world saves / server restarts.
 *
 * Forge automatically calls serializeNBT / deserializeNBT on the
 * capability provider, so persistent storage is handled for entities
 * that stay loaded. This handler adds explicit copy-on-respawn logic
 * in case any future mechanic resurrects recruits.
 */
public class RecruitNBTHandler {

    /** Call this from the main mod constructor to register. */
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new RecruitNBTHandler());
    }

    /**
     * When an entity is cloned (player respawn / dimension travel),
     * copy recruit capability data to the new instance.
     * Recruits themselves don't typically clone, but this is a safety net.
     */
    @SubscribeEvent
    public void onEntityClone(net.minecraftforge.event.entity.EntityEvent.Clone event) {
        if (!(event.getEntity() instanceof LivingEntity newEntity)) return;
        if (!(event.getOriginal() instanceof LivingEntity oldEntity)) return;
        if (!RecruitHelper.isRecruit(newEntity)) return;

        var oldCap = RecruitCapability.get(oldEntity);
        var newCap = RecruitCapability.get(newEntity);

        oldCap.ifPresent(old -> newCap.ifPresent(nw -> {
            CompoundTag tag = new CompoundTag();
            old.saveNBT(tag);
            nw.loadNBT(tag);
        }));
    }
}
