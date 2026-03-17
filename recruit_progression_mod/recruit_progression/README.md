# Recruit Progression Mod
**Minecraft 1.20.1 · Forge 47.x · Requires: Villager Recruits mod**

Adds a full **rank progression system** and **auto class-selection** to the
[Villager Recruits](https://www.curseforge.com/minecraft/mc-mods/villager-recruits) mod.

---

## ⚔️ Rank System

Recruits earn XP by fighting and automatically rank up across **9 tiers**:

| Tier       | Rank             | XP Required |
|------------|------------------|-------------|
| 🟢 Beginner | Apprentice       | 0 (start)   |
| 🟢 Beginner | Veteran          | 100         |
| 🟢 Beginner | Squad Leader     | 250         |
| 🟡 Mid      | Captain          | 500         |
| 🟡 Mid      | Battalion Leader | 900         |
| 🟡 Mid      | General          | 1,500       |
| 🔴 Advanced | Master           | 2,500       |
| 🔴 Advanced | Grandmaster      | 4,000       |

### XP Rewards
| Kill Type                              | XP Gained                         |
|----------------------------------------|-----------------------------------|
| Regular monster (zombie, skeleton…)    | +5 XP                             |
| Enemy recruit (same rank)              | +20 XP                            |
| Enemy recruit (1 rank higher)          | +35 XP                            |
| Enemy recruit (2 ranks higher)         | +50 XP                            |
| Enemy recruit (N ranks higher, N≥2)    | +20 + (N × 15) XP                 |

Recruits can **skip multiple ranks** at once if they beat a significantly
higher-ranked enemy.

---

## 🗡️ Auto Class Selection

When a recruit is first summoned it **automatically picks its own class**
at random. The choice is permanent and stored in NBT.

| Class         | Weapon        | Playstyle                              |
|---------------|---------------|----------------------------------------|
| Swordsman     | Sword         | Fast aggressive melee                  |
| Axeman        | Axe           | Heavy hitter, breaks shields           |
| Archer        | Bow           | Ranged, keeps distance                 |
| Crossbowman   | Crossbow      | Slower but hits harder at range        |
| Shield Bearer | Sword+Shield  | Defensive frontline, tanks for allies  |
| Spearman      | Sword (KB)    | Reach fighter, knockback attacks       |

### Equipment Scaling with Rank
Equipment upgrades automatically on rank-up:

| Rank Ordinal | Armour/Weapon Tier |
|--------------|--------------------|
| 0–1          | Leather / Iron     |
| 2–3          | Iron (enchanted)   |
| 4–5          | Diamond            |
| 6–7          | Netherite          |

Archers and crossbowmen also receive relevant enchantments (Power, Punch,
Quick Charge, Piercing) at higher ranks.

---

## 🛠️ Building & Installing

### Prerequisites
- Java 17 JDK
- Minecraft Forge MDK for 1.20.1

### Steps

1. **Download ForgeGradle MDK** from https://files.minecraftforge.net and
   extract it into a new folder.

2. **Copy these mod sources** into the MDK folder, replacing the example mod:
   ```
   src/main/java/com/recruitprogression/  ← all Java files
   src/main/resources/                    ← pack.mcmeta, META-INF/mods.toml
   build.gradle                           ← replace the example one
   ```

3. **Update the Villager Recruits file ID** in `build.gradle`:
   ```groovy
   compileOnly fg.deobf('curse.maven:villager-recruits-397452:YOUR_FILE_ID')
   ```
   Find the correct file ID on CurseForge → Villager Recruits → Files tab
   → pick the 1.20.1 build → the number in the URL is the file ID.

4. **Run setup:**
   ```bash
   ./gradlew genEclipseRuns   # or genIntellijRuns
   ./gradlew build
   ```

5. **Output JAR** will be in `build/libs/recruit_progression-1.0.0.jar`.
   Drop it into your `.minecraft/mods/` folder alongside Villager Recruits.

---

## 📁 File Structure

```
src/main/java/com/recruitprogression/
├── RecruitProgressionMod.java          ← Mod entry point
├── rank/
│   ├── RecruitRank.java                ← All 9 ranks (enum)
│   └── XPCalculator.java               ← XP rewards + rank-up logic
├── classselect/
│   ├── RecruitClass.java               ← 6 weapon classes (enum)
│   └── ClassSelector.java              ← Equips recruit on spawn + rank-up
├── capability/
│   └── RecruitCapability.java          ← Stores rank/XP/class in entity NBT
├── events/
│   ├── RecruitEventHandler.java        ← Spawn & kill event listeners
│   └── RecruitNBTHandler.java          ← Persists data across restarts
└── util/
    └── RecruitHelper.java              ← Entity type detection utilities
```

---

## 🔧 Compatibility Notes

- This mod uses **only Forge events and capability APIs** — it does not
  import Villager Recruits classes at compile time, so it won't crash if the
  Recruits mod updates.
- Recruit identification is done via the entity **registry name**
  (`recruits:recruit`). If the Recruits mod changes this name in a future
  update, edit `RecruitHelper.isRecruit()`.
- Owner detection reads the `"Owner"` NBT key, same as vanilla tameable
  entities. This should be stable across Recruits mod versions.
