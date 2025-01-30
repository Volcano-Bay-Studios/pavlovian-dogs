package xyz.volcanobay.pavloviandogs.wolf;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import xyz.volcanobay.pavloviandogs.smartanimal.AnimalBrain;
import xyz.volcanobay.pavloviandogs.smartanimal.SmartAnimal;

public class SmartAnimalGoal extends Goal {
    private Wolf wolf;

    public SmartAnimalGoal(Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public void tick() {
        AnimalBrain brain = ((SmartAnimal) wolf).getAnimalBrain();
        brain.tick();
        if (wolf.hasEffect(MobEffects.WEAKNESS)) {
            brain.neuralNetwork.clear();
            brain.fillBrain();
            wolf.removeEffect(MobEffects.WEAKNESS);
        }
        if (brain.action == null && brain.smartEventList.isEmpty())
            wolf.setInSittingPose(true);
        if (brain.getMostRecentEvent() != null) {
            BlockPos eventPos = brain.getMostRecentEvent().pos;
            if (brain.eventsThisSecond > 1)
                wolf.getLookControl().setLookAt(eventPos.getX(), eventPos.getY(), eventPos.getZ());
        }
        if (brain.action != null) {
            brain.action.performAction(wolf);
            if (!brain.action.shouldContinue(wolf))
                brain.stopAction();
        }
        super.tick();
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public boolean canUse() {
        return true;
    }

}
