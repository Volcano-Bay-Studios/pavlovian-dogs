package xyz.volcanobay.pavloviandogs.smartaction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Wolf;

public abstract class Action {
    public String reference;
    public BlockPos pos;
    public Action(String string, String[] fullText) {
        this.reference = string;
    }
    public void setPos(BlockPos pos) {
        this.pos = pos;
    }
    public abstract void performAction(Wolf wolf);
    public abstract boolean shouldContinue(Wolf wolf);
}
