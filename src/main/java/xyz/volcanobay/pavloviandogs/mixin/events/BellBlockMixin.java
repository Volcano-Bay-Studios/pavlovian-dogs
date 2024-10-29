package xyz.volcanobay.pavloviandogs.mixin.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.volcanobay.pavloviandogs.registries.Events;
import xyz.volcanobay.pavloviandogs.smartanimal.SmartAnimal;
import xyz.volcanobay.pavloviandogs.smartevents.BellTollEvent;
import xyz.volcanobay.pavloviandogs.util.SmartAnimalTypeTest;

import java.util.List;

@Mixin(BellBlock.class)
public class BellBlockMixin {
    @Inject(method = "attemptToRing(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"))
    public void ringBell(Entity entity, Level level, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        List<Entity> entities = level.getEntities((Entity) null, new AABB(pos).inflate(10), EntitySelector.NO_SPECTATORS);
        if (!level.isClientSide)
            for (Entity entity1 : entities) {
                if (entity1 instanceof SmartAnimal smartAnimal) {
                    smartAnimal.addSmartEvent(Events.BELL_TOLL.get().setPos(pos));
                }
            }
    }

    @Inject(method = "attemptToRing(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"))
    public void ringBell(Level level, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        List<Entity> entities = level.getEntities((Entity) null, new AABB(pos).inflate(10), EntitySelector.NO_SPECTATORS);
        if (!level.isClientSide)
            for (Entity entity1 : entities) {
                if (entity1 instanceof SmartAnimal smartAnimal) {
                    smartAnimal.addSmartEvent(Events.BELL_TOLL.get().setPos(pos));
                }
            }
    }
}
