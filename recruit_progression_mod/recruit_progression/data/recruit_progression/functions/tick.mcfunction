# =============================================================
# RECRUIT PROGRESSION — TICK  (runs every game tick)
# =============================================================
#
# ⚠ IMPORTANT: If your Villager Recruits mod uses a different
#   entity type ID, replace ALL instances of:
#       villagerrecruits:villager_recruit
#   with the correct entity type from your mod version.
#   Common alternatives: villagerrecruits:recruit
# =============================================================

# ── Detect & initialise newly summoned recruits ──
execute as @e[type=villagerrecruits:villager_recruit,tag=!rp_init] run function recruit_progression:class/assign

# ── Tick timer: heavy logic runs every 20 ticks (1 second) ──
scoreboard players add #timer rp_timer 1

execute if score #timer rp_timer matches 20.. run function recruit_progression:kill_tracking/scan
execute if score #timer rp_timer matches 20.. run scoreboard players set #timer rp_timer 0
