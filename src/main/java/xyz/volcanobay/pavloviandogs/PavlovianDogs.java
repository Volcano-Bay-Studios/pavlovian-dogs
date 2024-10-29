package xyz.volcanobay.pavloviandogs;

import net.fabricmc.api.ModInitializer;
import xyz.volcanobay.pavloviandogs.registries.Actions;
import xyz.volcanobay.pavloviandogs.registries.Events;
import xyz.volcanobay.pavloviandogs.smartaction.Action;

public class PavlovianDogs implements ModInitializer {

    @Override
    public void onInitialize() {
        Actions.registerAll();
        Events.registerAll();
    }
}
