package com.recruitprogression.capability;

import com.recruitprogression.RecruitProgressionMod;
import com.recruitprogression.classselect.RecruitClass;
import com.recruitprogression.classselect.ClassSelector;
import com.recruitprogression.rank.RecruitRank;
import com.recruitprogression.util.RecruitHelper;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Forge Capability that stores per-recruit data:
 *   - Current rank
 *   - Accumulated XP
 *   - Chosen weapon class
 *   - Whether the class has been initialised (to avoid double-applying)
 */
public class RecruitCapability {

    // ── Capability key ────────────────────────────────────────────────────────
    public static final Capability<IRecruitData> RECRUIT_DATA =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation ID =
            new ResourceLocation(RecruitProgressionMod.MOD_ID, "recruit_data");

    public static void register() {
        // Registration is automatic via @AutoRegisterCapability on the interface.
        // This method is kept as a hook for any additional setup.
    }

    // ── Interface ─────────────────────────────────────────────────────────────
    @AutoRegisterCapability
    public interface IRecruitData {
        RecruitRank  getRank();
        void         setRank(RecruitRank rank);
        int          getXP();
        void         addXP(int amount);
        RecruitClass getRecruitClass();
        void         setRecruitClass(RecruitClass cls);
        boolean      isInitialised();
        void         setInitialised(boolean value);

        void saveNBT(CompoundTag tag);
        void loadNBT(CompoundTag tag);
    }

    // ── Implementation ────────────────────────────────────────────────────────
    public static class RecruitDataImpl implements IRecruitData {

        private RecruitRank  rank        = RecruitRank.APPRENTICE;
        private int          xp          = 0;
        private RecruitClass recruitClass = RecruitClass.SWORDSMAN;
        private boolean      initialised  = false;

        @Override public RecruitRank  getRank()          { return rank; }
        @Override public void         setRank(RecruitRank r) { this.rank = r; }
        @Override public int          getXP()            { return xp; }
        @Override public void         addXP(int amount)  { this.xp = Math.max(0, this.xp + amount); }
        @Override public RecruitClass getRecruitClass()  { return recruitClass; }
        @Override public void         setRecruitClass(RecruitClass c) { this.recruitClass = c; }
        @Override public boolean      isInitialised()    { return initialised; }
        @Override public void         setInitialised(boolean v) { this.initialised = v; }

        @Override
        public void saveNBT(CompoundTag tag) {
            tag.putInt("Rank",       rank.ordinalRank);
            tag.putInt("XP",         xp);
            tag.putString("Class",   recruitClass.name());
            tag.putBoolean("Init",   initialised);
        }

        @Override
        public void loadNBT(CompoundTag tag) {
            rank        = RecruitRank.fromOrdinal(tag.getInt("Rank"));
            xp          = tag.getInt("XP");
            try {
                recruitClass = RecruitClass.valueOf(tag.getString("Class"));
            } catch (IllegalArgumentException e) {
                recruitClass = RecruitClass.SWORDSMAN;
            }
            initialised = tag.getBoolean("Init");
        }
    }

    // ── Capability Provider ───────────────────────────────────────────────────
    public static class Provider implements ICapabilitySerializable<CompoundTag> {

        private final RecruitDataImpl data = new RecruitDataImpl();
        private final LazyOptional<IRecruitData> optional = LazyOptional.of(() -> data);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == RECRUIT_DATA ? optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            data.saveNBT(tag);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.loadNBT(nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }
    }

    // ── Attach to recruit entities ────────────────────────────────────────────
    public static class AttachCapabilitiesHandler {

        @SubscribeEvent
        public void onAttachCapabilities(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
            if (event.getObject() instanceof LivingEntity living) {
                if (RecruitHelper.isRecruit(living)) {
                    Provider provider = new Provider();
                    event.addCapability(ID, provider);
                    event.addListener(provider::invalidate);
                }
            }
        }
    }

    // ── Static helper ─────────────────────────────────────────────────────────
    /** Convenience getter — returns empty optional if entity has no capability. */
    public static LazyOptional<IRecruitData> get(LivingEntity entity) {
        return entity.getCapability(RECRUIT_DATA);
    }
}
