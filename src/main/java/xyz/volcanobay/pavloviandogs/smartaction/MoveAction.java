package xyz.volcanobay.pavloviandogs.smartaction;

import net.minecraft.world.entity.animal.Wolf;

public class MoveAction extends Action {
    public MoveAction(String string) {
        super(string);
    }

    @Override
    public void performAction(Wolf wolf) {
        wolf.setInSittingPose(false);
        if (!wolf.getNavigation().isInProgress())
            wolf.getNavigation().moveTo(pos.getX(),pos.getY(),pos.getZ(),1);
    }

    @Override
    public boolean shouldContinue(Wolf wolf) {
        return !wolf.getNavigation().isStuck() && !wolf.getNavigation().isDone();
    }
}
