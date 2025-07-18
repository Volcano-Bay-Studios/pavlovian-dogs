package xyz.volcanobay.pavloviandogs.smartevents;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Wolf;

public abstract class SmartEvent {
    public String reference;
    public SmartEvent(String string) {
        this.reference = string;
    }
    public SmartEvent(BlockPos pos) {
        this.pos = pos;
    }
    public String getReference() {
        return reference;
    }
    public BlockPos pos;
    public int age;
    public int getMemoryLength() {
        return 240;
    }
    public void tick(Wolf wolf) {}
}
