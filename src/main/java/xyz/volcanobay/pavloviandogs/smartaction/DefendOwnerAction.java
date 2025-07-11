package xyz.volcanobay.pavloviandogs.smartaction;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.phys.AABB;
import xyz.volcanobay.pavloviandogs.util.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;

public class DefendOwnerAction extends Action {
    private String[] text;
    public DefendOwnerAction(String string, String[] fullText) {
        super(string, fullText);
        this.text = fullText;
    }

    @Override
    public void performAction(Wolf wolf) {
        if (text.length > 2) {
            String target = text[2];
            float distance = 9999999;
            LivingEntity foundTarget = null;
            for (Entity entity : wolf.level().getEntities(wolf,new AABB(wolf.blockPosition()).inflate(20f))) {
                if (entity instanceof LivingEntity livingEntity && livingEntity != wolf.getOwner()) {
                    Component customName = entity.getCustomName();
                    String name = null;
                    if (customName == null) {
                        name = livingEntity.getName().getString();
                    } else {
                        name = customName.getString();
                    }
                    if (name != null) {
                        float newDistance = (float) (LevenshteinDistance.calculate(name, target) + (wolf.position().distanceTo(livingEntity.position()))/100f);
                        if (newDistance < distance) {
                            newDistance = distance;
                            foundTarget = livingEntity;
                        }
                    }
                }
            }
            if (foundTarget != null ) {
                wolf.setTarget(foundTarget);
                wolf.getLookControl().setLookAt(foundTarget);
                wolf.setInSittingPose(false);
                return;
            }
        }

        if (wolf.getOwner() != null) {
            wolf.setTarget(wolf.getOwner().getLastAttacker());
        }
        wolf.setInSittingPose(false);
    }

    @Override
    public boolean shouldContinue(Wolf wolf) {
        return wolf.getOwner().distanceTo(wolf) < 40;
    }
}
