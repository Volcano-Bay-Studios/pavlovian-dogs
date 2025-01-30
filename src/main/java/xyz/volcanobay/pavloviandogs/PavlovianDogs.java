package xyz.volcanobay.pavloviandogs;

import net.fabricmc.api.ModInitializer;
import xyz.volcanobay.pavloviandogs.registries.Actions;
import xyz.volcanobay.pavloviandogs.registries.Events;
import xyz.volcanobay.pavloviandogs.smartaction.Action;
import xyz.volcanobay.pavloviandogs.voice.AttentionManager;

public class PavlovianDogs implements ModInitializer {
    public static final boolean debug = false;

    @Override
    public void onInitialize() {
        Actions.registerAll();
        Events.registerAll();
        AttentionManager.register();
    }
}
