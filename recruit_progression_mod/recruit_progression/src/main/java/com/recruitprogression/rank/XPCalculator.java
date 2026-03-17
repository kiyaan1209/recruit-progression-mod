package com.recruitprogression.rank;

/**
 * Calculates XP rewards for kills.
 *
 * Kill types and base XP:
 *   Monster (non-recruit hostile)  →  5 XP
 *   Enemy recruit (same rank)      →  20 XP
 *   Enemy recruit (1 rank higher)  →  35 XP
 *   Enemy recruit (2+ ranks higher)→  50 + 10 per extra rank above 2
 *
 * "Enemy recruit" means a recruit owned by a different player/team,
 * or any recruit that was flagged as hostile.
 */
public class XPCalculator {

    /** XP for killing a regular monster (skeleton, zombie, etc.). */
    public static final int XP_MONSTER = 5;

    /** XP for killing a recruit of the same rank. */
    public static final int XP_RECRUIT_SAME_RANK = 20;

    /** Extra XP added per rank the victim is above the killer. */
    public static final int XP_PER_RANK_ABOVE = 15;

    /**
     * Returns XP gained when a recruit kills a regular monster.
     */
    public static int forMonsterKill() {
        return XP_MONSTER;
    }

    /**
     * Returns XP gained when a recruit kills another recruit.
     *
     * @param killerRank  the rank of the killing recruit
     * @param victimRank  the rank of the killed recruit
     */
    public static int forRecruitKill(RecruitRank killerRank, RecruitRank victimRank) {
        int rankDiff = victimRank.ordinalRank - killerRank.ordinalRank;

        if (rankDiff < 0) {
            // Victim was lower rank — still reward but reduced
            return Math.max(5, XP_RECRUIT_SAME_RANK + (rankDiff * 5));
        } else if (rankDiff == 0) {
            return XP_RECRUIT_SAME_RANK;
        } else {
            // Victim was higher rank — bonus XP
            return XP_RECRUIT_SAME_RANK + (rankDiff * XP_PER_RANK_ABOVE);
        }
    }

    /**
     * Checks whether the given total XP should trigger a rank-up,
     * and returns the new rank if so.
     *
     * @param currentRank  the recruit's current rank
     * @param totalXP      the recruit's accumulated XP
     * @return the new rank if a rank-up happened, otherwise the same rank
     */
    public static RecruitRank checkRankUp(RecruitRank currentRank, int totalXP) {
        if (currentRank.isMaxRank()) return currentRank;

        RecruitRank next = currentRank.next();
        // Keep checking if recruit skipped multiple thresholds (e.g. killing a much higher rank)
        while (next != null && totalXP >= next.xpRequired) {
            currentRank = next;
            next = currentRank.next();
        }
        return currentRank;
    }
}
