package com.recruitprogression.events;

import com.recruitprogression.capability.RecruitCapability;
import com.recruitprogression.capability.RecruitCapability.IRecruitData;
import com.recruitprogression.classselect.ClassSelector;
import com.recruitprogression.classselect.RecruitClass;
import com.recruitprogression.rank.RecruitRank;
import com.recruitprogression.rank.XPCalculator;
import com.recruitprogression.util.RecruitHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens to two key events:
 *
 * 1. EntityJoinLevelEvent  → when a recruit first enters the world,
 *    assign it a random class and equip it accordingly.
 *
 * 2. LivingDeathEvent      → when something dies, check if a recruit
 *    was the killer. If so, award XP and check for rank-up.
 */
public class RecruitEventHandler {

    // ── 1. Class assignment on spawn ──────────────────────────────────────────

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        // Only run on the server
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!RecruitHelper.isRecruit(entity)) return;

        RecruitCapability.get(entity).ifPresent(data -> {
            if (!data.isInitialised()) {
                // First time this recruit joins — pick a random class
                RecruitClass chosen = ClassSelector.assignRandomClass(entity, entity.getRandom());
                data.setRecruitClass(chosen);
                data.setRank(RecruitRank.APPRENTICE);
                data.setInitialised(true);

                // Announce class to nearby players (optional flavour)
                broadcastClassChoice(entity, chosen);
            }
        });
    }

    // ── 2. XP + rank-up on kill ───────────────────────────────────────────────

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();

        // Find the killer — must be a recruit
        if (!(event.getSource().getEntity() instanceof LivingEntity killerEntity)) return;
        if (!RecruitHelper.isRecruit(killerEntity)) return;

        // Determine XP reward
        int xpGain;

        if (RecruitHelper.isRecruit(victim) && RecruitHelper.areEnemyRecruits(killerEntity, victim)) {
            // Killed an enemy recruit — use rank-based XP
            RecruitRank victimRank = getRecruitRank(victim);
            RecruitRank killerRank = getRecruitRank(killerEntity);
            xpGain = XPCalculator.forRecruitKill(killerRank, victimRank);
        } else if (RecruitHelper.isHostileMonster(victim)) {
            // Killed a regular monster
            xpGain = XPCalculator.forMonsterKill();
        } else {
            return; // no XP for killing passive mobs / allied recruits
        }

        // Apply XP and check for rank-up
        RecruitCapability.get(killerEntity).ifPresent(data -> {
            RecruitRank oldRank = data.getRank();
            data.addXP(xpGain);
            RecruitRank newRank = XPCalculator.checkRankUp(oldRank, data.getXP());

            if (newRank != oldRank) {
                data.setRank(newRank);
                // Upgrade equipment to match new rank
                ClassSelector.upgradeEquipment(killerEntity, data.getRecruitClass(), newRank.ordinalRank);
                // Notify owner
                notifyOwnerRankUp(killerEntity, oldRank, newRank);
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private RecruitRank getRecruitRank(LivingEntity recruit) {
        return RecruitCapability.get(recruit)
                .map(IRecruitData::getRank)
                .orElse(RecruitRank.APPRENTICE);
    }

    /**
     * Sends a chat message to all nearby players when a recruit picks its class.
     * Range: 32 blocks.
     */
    private void broadcastClassChoice(LivingEntity recruit, RecruitClass chosen) {
        String msg = "§8[Recruits] §rA new recruit has chosen the path of "
                + chosen.getFormattedName() + "§r!";

        recruit.getCommandSenderWorld().players().forEach(p -> {
            if (p.distanceTo(recruit) < 32) {
                p.sendSystemMessage(Component.literal(msg));
            }
        });
    }

    /**
     * Notifies the recruit's owner when it ranks up.
     * Looks up the owner UUID from NBT (avoids hard dependency on Recruits mod).
     */
    private void notifyOwnerRankUp(LivingEntity recruit, RecruitRank oldRank, RecruitRank newRank) {
        var nbt = new net.minecraft.nbt.CompoundTag();
        recruit.saveWithoutId(nbt);
        String ownerUUID = nbt.getString("Owner");
        if (ownerUUID.isEmpty()) return;

        recruit.getCommandSenderWorld().players().forEach(p -> {
            if (p.getStringUUID().equals(ownerUUID)) {
                String msg = "§8[Recruits] §rOne of your recruits "
                        + "(" + oldRank.getFormattedName() + "§r) has ranked up to "
                        + newRank.getFormattedName() + "§r! §7(XP earned: "
                        + RecruitCapability.get(recruit)
                              .map(IRecruitData::getXP).orElse(0)
                        + ")";
                p.sendSystemMessage(Component.literal(msg));
            }
        });
    }
}
