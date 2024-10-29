package xyz.volcanobay.pavloviandogs.smartevents;

import net.minecraft.core.BlockPos;

public class InteractEvent extends SmartEvent {
    public InteractEvent(String string) {
        super(string);
    }

    public InteractEvent(BlockPos pos) {
        super(pos);
    }
    public InteractEvent setPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }
}
