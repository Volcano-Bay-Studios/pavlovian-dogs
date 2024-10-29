package xyz.volcanobay.pavloviandogs.wolf;

import net.minecraft.core.BlockPos;
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
