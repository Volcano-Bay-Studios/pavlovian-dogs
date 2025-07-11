package xyz.volcanobay.pavloviandogs.smartevents;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Wolf;

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

    @Override
    public void tick(Wolf wolf) {
        setPos(BlockPos.containing(wolf.getOwner().position()));
        super.tick(wolf);
    }
}
