package cz.milandufek.dluzniceklite.repository;

import java.util.Observable;
import java.util.Observer;

public class SummarySettleUpRepository extends Observable {

    public SummarySettleUpRepository() {
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
    }
}
