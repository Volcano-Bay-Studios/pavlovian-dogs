package xyz.volcanobay.pavloviandogs.mixin;

import com.google.common.collect.HashBiMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
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

import java.util.HashMap;

@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements SmartAnimal {
    @Shadow
    protected abstract void actuallyHurt(DamageSource damageSource, float damageAmount);

    @Shadow
    public abstract boolean isFood(ItemStack stack);

    @Shadow
    public abstract void tick();

    private AnimalBrain brain = new AnimalBrain();

    protected WolfMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    public void registerGoals(CallbackInfo ci) {
        goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        goalSelector.addGoal(2, new FloatGoal(this));
        goalSelector.addGoal(3, new SmartAnimalGoal(self()));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        ci.cancel();
    }

    @Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
    public void consumeFood(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.level().isClientSide) {
            ItemStack itemStack = player.getItemInHand(hand);
            Item item = itemStack.getItem();
            FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
            float f = foodProperties != null ? (float) foodProperties.nutrition() : 1.0F;
            if (this.isFood(itemStack) && !(this.getHealth() < this.getMaxHealth())) {
                itemStack.consume(1, player);
                brain.encourage(f / 4f);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
            if (itemStack.is(Items.POTION)) {
                brain.discourage(2f);
                itemStack.consume(1,player);
                player.gameEvent(GameEvent.DRINK);
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        } else {
            ItemStack itemStack = player.getItemInHand(hand);
            if (this.isFood(itemStack) && !(this.getHealth() < this.getMaxHealth())) {
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ((SmartAnimal) self()).addSmartEvent(Events.INTERACT.get().setPos(player.blockPosition()));
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!self().level().isClientSide && source.getEntity() != null && source.getEntity() instanceof Player) {
            brain.discourage(amount*4);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        CompoundTag tag = new CompoundTag();
        AnimalBrain brain1 = getAnimalBrain();
        for (String string : brain1.neuralNetwork.keySet()) {
            CompoundTag tag1 = new CompoundTag();
            HashBiMap<Float, String> hashBiMap = brain1.neuralNetwork.get(string);
            for (String s : hashBiMap.values()) {
                tag1.putFloat(s, hashBiMap.inverse().get(s));
            }
            tag.put(string, tag1);
        }
        compound.put("AnimalBrain", tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        HashMap<String, HashBiMap<Float, String>> neuralNetwork = new HashMap<>();
        CompoundTag compoundTag = compound.getCompound("AnimalBrain");
        if (compoundTag != null) {
            for (String s1 : compoundTag.getAllKeys()) {
                neuralNetwork.put(s1, HashBiMap.create());
                CompoundTag tag = compoundTag.getCompound(s1);
                for (String s2 : tag.getAllKeys()) {
                    float f = tag.getFloat(s2);
                    neuralNetwork.get(s1).put(f, s2);
                }
            }
        }
        getAnimalBrain().neuralNetwork = neuralNetwork;
    }

    public Wolf self() {
        return ((Wolf) (Object) this);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return self().getBreedOffspring(level, otherParent);
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
