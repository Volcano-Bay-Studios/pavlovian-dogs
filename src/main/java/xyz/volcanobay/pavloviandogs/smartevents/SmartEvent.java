package xyz.volcanobay.pavloviandogs.smartevents;

import net.minecraft.core.BlockPos;

public abstract class SmartEvent {
    public String reference;
    public SmartEvent(String string) {
        this.reference = string;
    }
    public SmartEvent(BlockPos pos) {
        this.pos = pos;
    }
    public BlockPos pos;
    public int age;
    public int getMemoryLength() {
        return 240;
    }
}
