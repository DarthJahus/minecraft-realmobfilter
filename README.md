# Real Mob Filter
Server-side filter for **natural** spawns, per entity type.

No Fabric API, no commands. No `/reload`, restart after editing.

Works for any entity, vanilla or not, as long as the spawn goes through the natural spawner (`NaturalSpawner`).

## Config
`config/realmobfilter.json` (written on first launch)

`enabled` defaults to **false**. While it stays false, the filter does nothing.

```json
{
  "enabled": false,
  "entities": {
    "minecraft:phantom": 0.0,
    "minecraft:creeper": 0.25
  }
}
```

The value is the **chance to allow** the natural spawn. The roll is uniform in `[0, 1)`.

| Value       | Effect                                 |
| ----------- | -------------------------------------- |
| `0.0`       | the attempt is cancelled every time    |
| `0.1`       | allowed 10% of the time, cancelled 90% |
| `1.0`       | always allowed                         |
| missing key | always allowed                         |

An invalid id is ignored. A value outside `[0, 1]` is clamped.

## Mobcap
Cancellation happens **after** Vanilla has picked the type. The attempt **is not replaced** and there is no second roll.

The mobcap therefore **fills more slowly**, but it still fills: only with rolls that pass, plus anything that is not in the config.

### ToDo
Make the filter point configurable:
- **after** (current): Vanilla picks, we cancel. Throughput drops.
- **before**: remove or reweight the type in the biome list, before the roll. Throughput stays at the mobcap rate; only the mix changes.

However, not now; plenty of mods already rewrite that list. Feel free to test and submit a PR.

## Where it cuts
Mixin on `NaturalSpawner.getMobForSpawn`, at the head of the method, **before** `EntityType.create`.

Vanilla already does `if (mob == null) return;`, so the attempt stops before the entity is created, before the collision test, and before `finalizeSpawn`.

Eggs, spawners, trial spawners, commands and bosses do not go through this method.

## Compatibility
- ✅ **[Real Mobcap](https://github.com/darthjahus/minecraft-realmobcap)**: fine. The cap and the filter stack.
- ✅ **Carpet**: fine.
- ✅ **Serilum** spawn mods: tested, no issue.
- 🔜 **MobFilter**: not tested yet.

## Build
```text
./gradlew build
```

Minecraft `26.3`, Fabric Loader `0.19.5`, Java 25.

## License
Apache 2.0
