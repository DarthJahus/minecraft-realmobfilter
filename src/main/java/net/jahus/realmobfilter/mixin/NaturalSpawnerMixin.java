package net.jahus.realmobfilter.mixin;

import net.jahus.realmobfilter.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Natural attempts only. {@code getMobForSpawn} runs after the weighted pick
 * and before {@code EntityType.create}, collision tests and {@code finalizeSpawn}.
 * Vanilla already aborts the attempt when this returns null, so eggs, spawners,
 * trial spawners and boss summons (other call sites) are never touched.
 */
@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    @Inject(method = "getMobForSpawn", at = @At("HEAD"), cancellable = true)
    private static void realmobfilter$roll(ServerLevel level, EntityType<?> type, CallbackInfoReturnable<Mob> cir) {
        if (!ModConfig.allow(type)) {
            cir.setReturnValue(null);
        }
    }
}
