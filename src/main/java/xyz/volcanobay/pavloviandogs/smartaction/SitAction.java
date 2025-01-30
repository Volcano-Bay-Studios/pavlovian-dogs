package xyz.volcanobay.pavloviandogs.smartaction;

import net.minecraft.world.entity.animal.Wolf;

public class SitAction extends Action {
    public SitAction(String string) {
        super(string);
    }

    @Override
    public void performAction(Wolf wolf) {
//        wolf.getNavigation().stop();
        wolf.setInSittingPose(true);
    }

    @Override
    public boolean shouldContinue(Wolf wolf) {
        return false;
    }
}
