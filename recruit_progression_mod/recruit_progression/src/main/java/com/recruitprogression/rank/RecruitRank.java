package com.recruitprogression.rank;

/**
 * Defines all recruit ranks in order of progression.
 *
 * BEGINNER TIER:  Apprentice → Veteran → Squad Leader
 * MID TIER:       Captain → Battalion Leader → General
 * ADVANCED TIER:  Master → Grandmaster
 */
public enum RecruitRank {

    // ── Beginner Tier ──────────────────────────────────────────────────────────
    APPRENTICE(0, "Apprentice", Tier.BEGINNER,
            0,      // XP needed to reach this rank (starting rank)
            "§7",   // gray
            "A fresh recruit still learning the ropes."),

    VETERAN(1, "Veteran", Tier.BEGINNER,
            100,
            "§a",   // green
            "A battle-tested soldier with real experience."),

    SQUAD_LEADER(2, "Squad Leader", Tier.BEGINNER,
            250,
            "§2",   // dark green
            "Commands a small unit with authority."),

    // ── Mid Tier ───────────────────────────────────────────────────────────────
    CAPTAIN(3, "Captain", Tier.MID,
            500,
            "§e",   // yellow
            "A respected officer who leads by example."),

    BATTALION_LEADER(4, "Battalion Leader", Tier.MID,
            900,
            "§6",   // gold
            "Commands an entire battalion in the field."),

    GENERAL(5, "General", Tier.MID,
            1500,
            "§c",   // red
            "A decorated general, feared on any battlefield."),

    // ── Advanced Tier ──────────────────────────────────────────────────────────
    MASTER(6, "Master", Tier.ADVANCED,
            2500,
            "§d",   // light purple
            "A near-legendary warrior of exceptional skill."),

    GRANDMASTER(7, "Grandmaster", Tier.ADVANCED,
            4000,
            "§5",   // dark purple
            "The pinnacle of recruit achievement. Few ever reach this.");

    // ──────────────────────────────────────────────────────────────────────────

    public enum Tier { BEGINNER, MID, ADVANCED }

    public final int ordinalRank;
    public final String displayName;
    public final Tier tier;
    public final int xpRequired;      // total XP needed to reach this rank
    public final String colorCode;
    public final String description;

    RecruitRank(int ordinalRank, String displayName, Tier tier,
                int xpRequired, String colorCode, String description) {
        this.ordinalRank = ordinalRank;
        this.displayName = displayName;
        this.tier = tier;
        this.xpRequired = xpRequired;
        this.colorCode = colorCode;
        this.description = description;
    }

    /** Formatted name with rank colour. */
    public String getFormattedName() {
        return colorCode + "[" + displayName + "]§r";
    }

    /** Returns the next rank, or null if already Grandmaster. */
    public RecruitRank next() {
        RecruitRank[] values = values();
        int next = this.ordinal() + 1;
        return next < values.length ? values[next] : null;
    }

    /** Whether this rank is the maximum. */
    public boolean isMaxRank() {
        return this == GRANDMASTER;
    }

    /** Get rank from its ordinal integer (0–7). */
    public static RecruitRank fromOrdinal(int ordinal) {
        for (RecruitRank r : values()) {
            if (r.ordinalRank == ordinal) return r;
        }
        return APPRENTICE;
    }
}
