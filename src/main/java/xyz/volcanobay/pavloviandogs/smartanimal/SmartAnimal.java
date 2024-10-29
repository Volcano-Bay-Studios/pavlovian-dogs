package xyz.volcanobay.pavloviandogs.smartanimal;

import xyz.volcanobay.pavloviandogs.smartevents.SmartEvent;

public interface SmartAnimal {
    public void addSmartEvent(SmartEvent smartEvent);
    public AnimalBrain getAnimalBrain();
}
