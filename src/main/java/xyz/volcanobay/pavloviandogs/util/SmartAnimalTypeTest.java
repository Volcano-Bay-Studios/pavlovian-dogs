package xyz.volcanobay.pavloviandogs.util;

import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;
import xyz.volcanobay.pavloviandogs.smartanimal.SmartAnimal;

public class SmartAnimalTypeTest implements EntityTypeTest {
    @Nullable
    @Override
    public Object tryCast(Object entity) {
        return (SmartAnimal) entity;
    }

    @Override
    public Class getBaseClass() {
        return SmartAnimal.class;
    }
}
