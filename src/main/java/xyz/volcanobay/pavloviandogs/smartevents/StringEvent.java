package xyz.volcanobay.pavloviandogs.smartevents;

import net.minecraft.core.BlockPos;

public class StringEvent extends SmartEvent {
    public StringEvent(String string) {
        super(string);
    }

    public StringEvent(BlockPos pos) {
        super(pos);
    }

    public String storedString;

    @Override
    public String getReference() {
        return reference + ":" + storedString;
    }

    public StringEvent setPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public StringEvent setString(String string) {
        storedString = string;
        return this;
    }
}
