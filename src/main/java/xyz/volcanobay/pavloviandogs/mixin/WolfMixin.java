package xyz.volcanobay.pavloviandogs.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.volcanobay.pavloviandogs.registries.Events;
import xyz.volcanobay.pavloviandogs.smartanimal.AnimalBrain;
import xyz.volcanobay.pavloviandogs.smartanimal.SmartAnimal;
import xyz.volcanobay.pavloviandogs.smartevents.SmartEvent;
import xyz.volcanobay.pavloviandogs.wolf.SmartAnimalGoal;

@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements SmartAnimal {
    @Shadow protected abstract void actuallyHurt(DamageSource damageSource, float damageAmount);

    private AnimalBrain brain = new AnimalBrain();

    protected WolfMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"))
    public void registerGoals(CallbackInfo ci) {
        goalSelector.addGoal(1, new SmartAnimalGoal(self()));
}
    @Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
    public void consumeFood(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();
        FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
        float f = foodProperties != null ? (float)foodProperties.nutrition() : 1.0F;
        if (this.isFood(itemStack) && !(this.getHealth() < this.getMaxHealth())) {
            itemStack.consume(1, player);
            brain.reward(f);
            cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide()));
        }
    }
    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
            ((SmartAnimal)self()).addSmartEvent(Events.INTERACT.get().setPos(player.blockPosition()));
    }
    @Inject(method = "hurt", at = @At("HEAD"))
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() != null && self().getOwner() != null && source.getEntity().equals(self().getOwner())) {
            brain.discourage(amount*20f);
        }
    }
    public Wolf self() {
        return ((Wolf)(Object)this);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return self().getBreedOffspring(level,otherParent);
    }

    @Override
    public void addSmartEvent(SmartEvent smartEvent) {
        brain.addSmartEvent(smartEvent);
    }

    @Override
    public AnimalBrain getAnimalBrain() {
        return brain;
    }
}
