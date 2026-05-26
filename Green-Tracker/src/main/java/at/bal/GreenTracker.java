package at.bal;

import java.io.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GreenTracker {

    private String name;
    List<Verbrauch> verbrauche;

    public GreenTracker(String name) throws GreenTrackerException {
        setName(name);
        this.verbrauche = new LinkedList<>();
    }

    public Verbrauch getVerbauch(int index) throws GreenTrackerException {
        if (index < 0 || index > verbrauche.size()) throw new GreenTrackerException("Error index!");
        return verbrauche.get(index);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws GreenTrackerException {
        if (name == null || name.isBlank()) {
            throw new GreenTrackerException("Fehler: Name darf nicht null oder leer sein");
        }
        this.name = name;
    }

    public boolean hinzufuegen(Verbrauch verbrauch) throws GreenTrackerException {
        if (verbrauch != null && !verbrauche.contains(verbrauch)) {
            return verbrauche.add(verbrauch);
        } else {
            return false;
        }
    }

    public boolean entfernen(Verbrauch verbrauch) {
        if (verbrauch == null || verbrauche.isEmpty()) {
            return false;
        }
        return verbrauche.remove(verbrauch);
    }

    public int entfernen(String bezeichnung) throws GreenTrackerException {
        if (bezeichnung == null || bezeichnung.isBlank()) throw new GreenTrackerException("Error: entfernen");
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
        if (type == null) throw new GreenTrackerException("Error type ist null");
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
        if (type == null) throw new GreenTrackerException("Error null");
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

    public int anzahlTaetigkeiten(String taetigkeit) throws GreenTrackerException {
        if (taetigkeit == null || taetigkeit.isBlank()) throw new GreenTrackerException("Error: leer oder null");
        int anzahl = 0;
        for (Verbrauch v: verbrauche) {
            if (v.getTaetigkeit().equals(taetigkeit)) {
                anzahl++;
            }
        }
        return anzahl;
    }

    public void sortieren() {
        verbrauche.sort(null);
    }

    public void sortierenNachTaetigkeit() {
        verbrauche.sort((Comparator.comparing(Verbrauch::getTaetigkeit)));
    }

    public void save () {
        String filepath = "src/main/resources/greentracker.ser";
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(verbrauche);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeVerbraucheToCsv() {
        String filepath = "src/main/resources/greentracker.csv";
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            bw.write(toCsvString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readVerbraucheFromCsv() {
        String filepath = "src/main/resources/greentracker.csv";
        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine();
            while (line != null && !line.isBlank()) {
                String[] lineParts = line.split(";");
                if (lineParts.length >= 4) {
                    if (lineParts[0].trim().equals("WasserVerbrauch")) {
                        try {
                            hinzufuegen(new WasserVerbrauch(lineParts));
                        } catch (GreenTrackerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (lineParts[0].trim().equals("StromVerbrauch")) {
                        try {
                            hinzufuegen(new StromVerbrauch(lineParts));
                        } catch (GreenTrackerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (lineParts[0].trim().equals("GasVerbrauch")) {
                        try {
                            hinzufuegen(new GasVerbrauch(lineParts));
                        } catch (GreenTrackerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (lineParts[0].trim().equals("DuschVerbrauch")) {
                        try {
                            hinzufuegen(new DuschVerbrauch(lineParts));
                        } catch (GreenTrackerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    System.out.println("Datei ist leer");
                }
                line = br.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Greentracker ").append(name).append("\n");
        int anzahl = verbrauche.size();
        if (anzahl > 0) {
            sb.append("Anzahl der Verbauche: ").append(anzahl).append("\n");
            for (int i = 0; i < anzahl; i++)
                sb.append(verbrauche.get(i).toString()).append("\n");
        } else {
            sb.append("Keine Verbrauche vorhanden\n");
        }
        return sb.toString();
    }

    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        for(Verbrauch verbrauch : verbrauche) {
            sb.append(verbrauch.toCsvString()).append("\n");
        }
        return sb.toString();
    }



}
