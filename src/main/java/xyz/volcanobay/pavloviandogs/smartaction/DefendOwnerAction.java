package xyz.volcanobay.pavloviandogs.smartaction;

import net.minecraft.world.entity.animal.Wolf;

public class DefendOwnerAction extends Action {
    public DefendOwnerAction(String string) {
        super(string);
    }

    @Override
    public void performAction(Wolf wolf) {
        if (wolf.getOwner() != null)
            wolf.setTarget(wolf.getOwner().getLastAttacker());
    }

    @Override
    public boolean shouldContinue(Wolf wolf) {
        if (wolf.getTarget() != null)
            return wolf.getTarget().isAlive();
        return false;
    }
}
