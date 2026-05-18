package at.bal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// TODO
// Logik von Emission besprechen

public class GreenTracker {

    private String name;
    List<Verbrauch> verbrauche;

    public GreenTracker(String name, List<Verbrauch> verbrauche) throws GreenTrackerException {
        setName(name);
        verbrauche = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws GreenTrackerException {
        if (name == null || name.isEmpty()) {
            throw new GreenTrackerException("Fehler: Name darf nicht null oder leer sein");
        }
        this.name = name;
    }

    public void hinzufuegen(Verbrauch verbrauch) throws GreenTrackerException {
        if (verbrauch != null && !verbrauche.contains(verbrauch)) {
            verbrauche.add(verbrauch);
        } else {
            throw new GreenTrackerException("Fehler: Verbrauch ist null oder exestiert schon");
        }
    }

    public boolean entfernen(Verbrauch verbrauch) {
        if (verbrauch == null || verbrauche.isEmpty()) {
            return false;
        }
        return verbrauche.add(verbrauch);
    }

    public int entfernen(String bezeichnung) {
        int anzahl = 0;
        Iterator<Verbrauch> iterator = verbrauche.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getTaetigkeit().equals(bezeichnung)) {
                iterator.remove();
                anzahl++;
            }
        }
        return anzahl;
    }

    public double berechneGesamtEmission() {
        double gesamt = 0.0;
        for (Verbrauch v: verbrauche) {
            gesamt += v.ausstoss();
        }
        return gesamt;
    }

    public double berechneVerbrauchEmisson(String bezeichnung) {
        return 0.0;
    }


}
