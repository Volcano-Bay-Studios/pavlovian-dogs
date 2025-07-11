package xyz.volcanobay.pavloviandogs.smartaction;

import net.minecraft.world.entity.animal.Wolf;

public class MoveAction extends Action {
    private int ticksToRecalculate = 0;

    public MoveAction(String string, String[] fullText) {
        super(string, fullText);
    }

    @Override
    public void performAction(Wolf wolf) {
        wolf.setInSittingPose(false);
        wolf.setTarget(null);
        if (!wolf.getNavigation().isInProgress() || ticksToRecalculate < 1) {
            wolf.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1);
            ticksToRecalculate = 20;
        }
        ticksToRecalculate--;
    }

    @Override
    public boolean shouldContinue(Wolf wolf) {
        return !wolf.getNavigation().isStuck();
    }
}
