package xyz.volcanobay.pavloviandogs.smartevents;

import net.minecraft.core.BlockPos;

public class BellTollEvent extends SmartEvent {
    public BellTollEvent(String string) {
        super(string);
    }

    public BellTollEvent(BlockPos pos) {
        super(pos);
    }
    public BellTollEvent setPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }
}
