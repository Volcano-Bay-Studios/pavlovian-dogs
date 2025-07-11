package xyz.volcanobay.pavloviandogs.smartanimal;

import com.google.common.collect.HashBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Wolf;
import xyz.volcanobay.pavloviandogs.PavlovianDogs;
import xyz.volcanobay.pavloviandogs.registries.Actions;
import xyz.volcanobay.pavloviandogs.registries.Events;
import xyz.volcanobay.pavloviandogs.smartaction.Action;
import xyz.volcanobay.pavloviandogs.smartevents.SmartEvent;
import xyz.volcanobay.pavloviandogs.smartevents.StringEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimalBrain {
    private Wolf wolf;
    public AnimalBrain(Wolf wolf) {
        this.wolf = wolf;
        fillBrain();
    }

    public void fillBrain() {
        for (String event : Events.events.keySet()) {
            if (!neuralNetwork.containsKey(event))
                addNewEvent(event);
        }
        sortBrain();
    }

    public List<SmartEvent> smartEventList = new ArrayList<>();
    private Action lastAction;
    private int eventTimer;
    public int eventsThisSecond;
    private float avgEventsPerSecond;
    public HashMap<String, HashBiMap<Float, String>> neuralNetwork = new HashMap<>();

    public Action action;

    public SmartEvent getMostRecentEvent() {
        if (!smartEventList.isEmpty())
            return smartEventList.get(smartEventList.size() - 1);
        return null;
    }

    public void addNewEvent(String event) {
        neuralNetwork.put(event, HashBiMap.create(Actions.actions.size()));
        for (String action : Actions.actions.keySet()) {
            neuralNetwork.get(event).put((float) Math.random(), action);
        }
    }

    public void tick() {
        eventTimer++;
        List<SmartEvent> forgottenMemories = new ArrayList<>();
        for (SmartEvent smartEvent : smartEventList) {
            smartEvent.age++;
            if (smartEvent.age > smartEvent.getMemoryLength()) {
                forgottenMemories.add(smartEvent);
            }
            smartEvent.tick(wolf);
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
        float strongestWeight = (float) neuralNetwork.get(smartEvent.getReference()).keySet().toArray()[neuralNetwork.get(smartEvent.getReference()).size() - 1];
            pickAction(smartEvent);
    }

    public void pickAction(SmartEvent event) {
        Action pickedAction = null;
        while (pickedAction == null) {
            int step = (int) Math.ceil(Math.sqrt(Math.random()) * neuralNetwork.get(event.getReference()).size() - 1);
            float weight = (float) neuralNetwork.get(event.getReference()).keySet().toArray()[step];
            String[] text = new String[0];
            if (event instanceof StringEvent stringEvent) {
                text = stringEvent.storedString;
            }
            if (weight > Math.random()) {
                pickedAction = Actions.actions.get(neuralNetwork.get(event.getReference()).get(weight)).apply(text);
            }
        }
        updateAction(pickedAction, event.pos);
        if (PavlovianDogs.debug) {
            System.out.println(pickedAction);
            System.out.println(neuralNetwork);
        }
    }

    public void updateAction(Action action, BlockPos pos) {
        action.setPos(pos);
        this.action = action;
        this.lastAction = action;
    }

    public void stopAction() {
        this.action = null;
    }

//    public Action getBestAction(SmartEvent event) {
//        float best = (float) neuralNetwork.get(event.getReference()).keySet().toArray()[0];
//        return Actions.actions.get(neuralNetwork.get(event.getReference()).get(best)).get();
//    }

    public void sortBrain() { //TODO: sort brain on change
        for (String event : neuralNetwork.keySet()) {
            List<Float> weights = neuralNetwork.get(event).keySet().stream().sorted().toList();
            HashBiMap<Float, String> oldMap = HashBiMap.create(neuralNetwork.get(event));
            neuralNetwork.get(event).clear();
            for (float f : weights) {
                neuralNetwork.get(event).put(f, oldMap.get(f));
            }
        }
    }

    public void sendPhrase(String text) {

    }

    public void encourage(float amount, SmartEvent event, String action) {
        if (event != null) {
            float oldValue = neuralNetwork.get(event.getReference()).inverse().get(action);
            float newValue = Mth.clamp(oldValue + (amount / 20f),0,1f);
            if (neuralNetwork.get(event.getReference()).containsKey(newValue)) {
                String actionString = neuralNetwork.get(event.getReference()).get(newValue);
                neuralNetwork.get(event.getReference()).remove(newValue);
                neuralNetwork.get(event.getReference()).put(oldValue, actionString);
            } else {
                neuralNetwork.get(event.getReference()).remove(oldValue);
            }
            neuralNetwork.get(event.getReference()).put(newValue, action);
            sortBrain();
            if (PavlovianDogs.debug)
                System.out.println(neuralNetwork);
        }
    }

    public void encourage(float amount) {
        if (lastAction == null)
            return;
        SmartEvent lastEvent = getMostRecentEvent();
        encourage(amount, lastEvent, lastAction.reference);
        for (String string : new ArrayList<>(neuralNetwork.get(lastEvent.getReference()).values())) {
            if (string != lastAction.reference) {
                discourage(0.05f, lastEvent, string);
            }
        }
    }


    public void discourage(float amount, SmartEvent event, String action) {
        if (event != null) {
            float oldValue = neuralNetwork.get(event.getReference()).inverse().get(action);
            float newValue = Math.min(1f, oldValue - (amount / 20f));
            if (neuralNetwork.get(event.getReference()).containsKey(newValue)) {
                String actionString = neuralNetwork.get(event.getReference()).get(newValue);
                neuralNetwork.get(event.getReference()).remove(newValue);
                neuralNetwork.get(event.getReference()).put(oldValue, actionString);
            } else {
                neuralNetwork.get(event.getReference()).remove(oldValue);
            }
            neuralNetwork.get(event.getReference()).put(newValue, action);
            sortBrain();
            if (PavlovianDogs.debug)
                System.out.println(neuralNetwork);
        }
    }

    public void discourage(float amount) {
        if (lastAction == null)
            return;
        SmartEvent lastEvent = getMostRecentEvent();
        discourage(amount, lastEvent, lastAction.reference);
        for (String string : new ArrayList<>(neuralNetwork.get(lastEvent.getReference()).values())) {
            if (string != lastAction.reference) {
                encourage(0.05f, lastEvent, string);
            }
        }
    }
}
