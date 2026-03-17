# =============================================================
# RECRUIT PROGRESSION — LOAD
# Runs automatically when the world loads or /reload is used.
# =============================================================

# ── Scoreboard Setup (silently ignored if already exists) ──
scoreboard objectives add rp_xp dummy "Recruit XP"
scoreboard objectives add rp_rank dummy "Recruit Rank"
scoreboard objectives add rp_class dummy "Recruit Class"
scoreboard objectives add rp_prev_monsters dummy
scoreboard objectives add rp_cur_monsters dummy
scoreboard objectives add rp_prev_enemies dummy
scoreboard objectives add rp_cur_enemies dummy
scoreboard objectives add rp_max_enemy_rank dummy
scoreboard objectives add rp_kill_delta dummy
scoreboard objectives add rp_misc dummy
scoreboard objectives add rp_config dummy
scoreboard objectives add rp_timer dummy

# ── Static Config Values ──
scoreboard players set #neg1            rp_config -1
scoreboard players set #mod5            rp_config 5

# XP awarded per event
scoreboard players set #xp_monster      rp_config 3
scoreboard players set #xp_enemy        rp_config 10
scoreboard players set #xp_rank_bonus   rp_config 5

# ── Rank XP Thresholds ──
# Rank 0 Apprentice     → Rank 1 Veteran           : 30  XP
# Rank 1 Veteran        → Rank 2 Squad Leader      : 80  XP
# Rank 2 Squad Leader   → Rank 3 Captain           : 150 XP
# Rank 3 Captain        → Rank 4 Battalion Leader  : 250 XP
# Rank 4 Battalion Ldr  → Rank 5 General           : 400 XP
# Rank 5 General        → Rank 6 Master            : 600 XP
# Rank 6 Master         → Rank 7 Grandmaster       : 900 XP
scoreboard players set #rank_threshold_0 rp_config 30
scoreboard players set #rank_threshold_1 rp_config 80
scoreboard players set #rank_threshold_2 rp_config 150
scoreboard players set #rank_threshold_3 rp_config 250
scoreboard players set #rank_threshold_4 rp_config 400
scoreboard players set #rank_threshold_5 rp_config 600
scoreboard players set #rank_threshold_6 rp_config 900

# ── Reset tick timer ──
scoreboard players set #timer rp_timer 0

tellraw @a [{"text":"[","color":"dark_gray"},{"text":"★ Recruit Progression","color":"gold","bold":true},{"text":"] ","color":"dark_gray"},{"text":"Loaded! Summon recruits and run ","color":"green"},{"text":"/function recruit_progression:utils/tag_friendly","color":"aqua","clickEvent":{"action":"suggest_command","value":"/function recruit_progression:utils/tag_friendly"}},{"text":" to register them.","color":"green"}]
