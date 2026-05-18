package at.bal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// TODO

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

    public double berechneVerbrauchEmisson(String type) throws GreenTrackerException {
        double gesamt = 0.0;
        switch (type) {
            case "WasserVerbrauch", "GasVerbrauch", "StromVerbrauch", "DuschVerbrauch":
                for (Verbrauch v: verbrauche) {
                    if(v.getClass().getSimpleName().equals(type)) {
                        gesamt += v.ausstoss();
                    }
                } break;
            default:
                throw new GreenTrackerException("Unexpected value" + type);
        }
        return gesamt;
    }

    public double berechneGesamtPreis() {
        double summe = 0.0;
        for (Verbrauch v: verbrauche) {
            summe += v.preis();
        }
        return summe;
    }

    public double berechneVerbrauchPreis(String type) throws GreenTrackerException {
        double summe = 0.0;
        switch (type) {
            case "WasserVerbrauch", "GasVerbrauch", "StromVerbrauch", "DuschVerbrauch":
                for (Verbrauch v: verbrauche) {
                    if(v.getClass().getSimpleName().equals(type)) {
                        summe += v.preis();
                    }
                } break;
            default:
                throw new GreenTrackerException("Unexpected value" + type);
        }
        return summe;
    }

    public int anzahlTaetigkeiten(String taetigkeit) {
        int anzahl = 0;
        for (Verbrauch v: verbrauche) {
            if (v.getTaetigkeit().equals(taetigkeit)) {
                anzahl++;
            }
        }
        return anzahl;
    }

}
