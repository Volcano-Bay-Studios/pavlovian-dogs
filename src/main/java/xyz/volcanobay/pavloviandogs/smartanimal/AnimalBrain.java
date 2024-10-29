package xyz.volcanobay.pavloviandogs.smartanimal;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.BlockPos;
import xyz.volcanobay.pavloviandogs.registries.Actions;
import xyz.volcanobay.pavloviandogs.registries.Events;
import xyz.volcanobay.pavloviandogs.smartaction.Action;
import xyz.volcanobay.pavloviandogs.smartevents.SmartEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimalBrain {
    public AnimalBrain() {
        for (String event : Events.events.keySet()) {
            neuralNetwork.put(event, HashBiMap.create(Actions.actions.size()));
            for (String action : Actions.actions.keySet()) {
                neuralNetwork.get(event).put((float) Math.random(), action);
            }
        }
        sortBrain();
    }

    public List<SmartEvent> smartEventList = new ArrayList<>();
    private Action lastAction;
    private int eventTimer;
    public int eventsThisSecond;
    private float avgEventsPerSecond;
    public HashMap<String, HashBiMap<Float, String>> neuralNetwork = new HashMap<>();

    public SmartEvent getMostRecentEvent() {
        if (!smartEventList.isEmpty())
            return smartEventList.get(smartEventList.size() - 1);
        return null;
    }

    public Action action;

    public void tick() {
        eventTimer++;
        List<SmartEvent> forgottenMemories = new ArrayList<>();
        for (SmartEvent smartEvent : smartEventList) {
            smartEvent.age++;
            if (smartEvent.age > smartEvent.getMemoryLength()) {
                forgottenMemories.add(smartEvent);
            }
        }
        if (eventTimer > 20) {
            avgEventsPerSecond = (avgEventsPerSecond + eventsThisSecond) / 2f;
            eventsThisSecond = 0;
            eventTimer = 0;
        }
        smartEventList.removeAll(forgottenMemories);
    }

    public void addSmartEvent(SmartEvent smartEvent) {
        smartEventList.add(smartEvent);
        eventsThisSecond++;
        float strongestWeight = (float) neuralNetwork.get(smartEvent.reference).keySet().toArray()[neuralNetwork.get(smartEvent.reference).size() - 1];
        if (strongestWeight > (Math.random() - avgEventsPerSecond - .05f)) {
            pickAction(smartEvent);
        }
    }

    public void pickAction(SmartEvent event) {
        Action pickedAction = null;
        while (pickedAction == null) {
            int step = (int) Math.ceil(Math.sqrt(Math.random()) * neuralNetwork.get(event.reference).size() - 1);
            float weight = (float) neuralNetwork.get(event.reference).keySet().toArray()[step];
            if (weight > Math.random()) {
                pickedAction = Actions.actions.get(neuralNetwork.get(event.reference).get(weight)).get();
            }
        }
        updateAction(pickedAction, event.pos);
        System.out.println(pickedAction);
        System.out.println(neuralNetwork);
    }

    public void updateAction(Action action, BlockPos pos) {
        action.setPos(pos);
        this.action = action;
        this.lastAction = action;
    }

    public void stopAction() {
        this.action = null;
    }

    public Action getBestAction(SmartEvent event) {
        float best = (float) neuralNetwork.get(event.reference).keySet().toArray()[0];
        return Actions.actions.get(neuralNetwork.get(event.reference).get(best)).get();
    }

    public void sortBrain() { //TODO: sort brain on change
        for (String event : Events.events.keySet()) {
            List<Float> weights = neuralNetwork.get(event).keySet().stream().sorted().toList();
            HashBiMap<Float, String> oldMap = HashBiMap.create(neuralNetwork.get(event));
            neuralNetwork.get(event).clear();
            for (float f : weights) {
                neuralNetwork.get(event).put(f, oldMap.get(f));
            }
        }
    }

    public void reward(float amount) {
        SmartEvent lastEvent = getMostRecentEvent();
        if (lastEvent != null) {
            float oldValue = neuralNetwork.get(lastEvent.reference).inverse().get(lastAction.reference);
            float newValue = Math.min(1f, oldValue + (amount / 20f));
            if (neuralNetwork.get(lastEvent.reference).containsKey(newValue)) {
                String action = neuralNetwork.get(lastEvent.reference).get(newValue);
                neuralNetwork.get(lastEvent.reference).remove(newValue);
                neuralNetwork.get(lastEvent.reference).put(oldValue, action);
            } else {
                neuralNetwork.get(lastEvent.reference).remove(oldValue);
            }
            neuralNetwork.get(lastEvent.reference).put(newValue, lastAction.reference);
            sortBrain();
            System.out.println(neuralNetwork);
        }
    }

    public void discourage(float amount) {
        SmartEvent lastEvent = getMostRecentEvent();
        if (lastEvent != null) {
            float oldValue = neuralNetwork.get(lastEvent.reference).inverse().get(lastAction.reference);
            float newValue = Math.max(0f, oldValue - (amount / 20f));
            neuralNetwork.get(lastEvent.reference).remove(oldValue);
            neuralNetwork.get(lastEvent.reference).put(newValue, lastAction.reference);
            sortBrain();
            if (Math.random() > .5f) {
                stopAction();
            }
            System.out.println(neuralNetwork);
        }
    }
}
