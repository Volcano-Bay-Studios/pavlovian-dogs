package xyz.volcanobay.pavloviandogs.registries;

import xyz.volcanobay.pavloviandogs.smartevents.BellTollEvent;
import xyz.volcanobay.pavloviandogs.smartevents.InteractEvent;
import xyz.volcanobay.pavloviandogs.smartevents.SmartEvent;
import xyz.volcanobay.pavloviandogs.smartevents.StringEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class Events {
    public static HashMap<String,Supplier<SmartEvent>> events = new HashMap<>();
    public static Supplier<BellTollEvent> BELL_TOLL = register("bell_toll",() -> new BellTollEvent("bell_toll"));
    public static Supplier<InteractEvent> INTERACT = register("interact",() -> new InteractEvent("interact"));
    public static Supplier<StringEvent> VOICE = register("voice",() -> new StringEvent("voice"));
    public static <T extends SmartEvent> Supplier<T> register(String name,Supplier<SmartEvent> eventHolder) {
        events.put(name,eventHolder);
        return (Supplier<T>) eventHolder;
    };
    public static void registerAll() {}
}
