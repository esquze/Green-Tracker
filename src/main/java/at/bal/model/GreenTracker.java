package at.bal.model;
import java.io.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalDate;

public class GreenTracker {

    private String name;
    List<Verbrauch> verbrauche;
    private static final double REFERENZ_PRO_JAHR = 2700.0;

    public GreenTracker(String name) throws GreenTrackerException {
        setName(name);
        this.verbrauche = new LinkedList<>();
    }

    public Verbrauch getVerbrauch(int index) throws GreenTrackerException {
        if (index < 0 || index > verbrauche.size()) throw new GreenTrackerException("Error index!");
        return verbrauche.get(index);
    }

    public List<Verbrauch> getVerbrauche() {
        return verbrauche;
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

    public double berechneGesamtCo2() {
        double sum = 0;
        for (Verbrauch verbrauch : verbrauche) {
            sum += verbrauch.co2Fussabdruck();
        }
        return sum;
    }

    public String co2Bewertung(double gesamtCo2) throws GreenTrackerException {
        double anzTage = ChronoUnit.DAYS.between(LocalDate.of(2026, 1, 1), LocalDate.now());
        if (anzTage <= 0) {
            throw new GreenTrackerException("Division by 0 nicht möglich");
        }
        double proTag = gesamtCo2 / anzTage;
        double referenzProTag = REFERENZ_PRO_JAHR / 365.0;
        double verhaeltnis = proTag / referenzProTag;

        if (verhaeltnis < 0.7)  return "gut";
        if (verhaeltnis < 1.2)  return "durchschnittlich";
        return "schlecht";
    }

    public void sortieren() {
        verbrauche.sort(null);
    }

    public void sortierenNachTaetigkeit() {
        verbrauche.sort((Comparator.comparing(Verbrauch::getTaetigkeit)));
    }

    public void save () throws GreenTrackerException {
        String filepath = "data/greentracker.ser";
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(verbrauche);
        } catch (IOException e) {
            throw new GreenTrackerException("Error: Save + " + e.getMessage());
        }
    }

    public void load() throws GreenTrackerException {
        String filepath = "data/greentracker.ser";
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            verbrauche = (List<Verbrauch>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GreenTrackerException(e.getMessage());
        }
    }

    public void writeVerbraucheToCsv() throws GreenTrackerException {
        String filepath = "data/greentracker.csv";
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            bw.write(toCsvString());
        } catch (IOException e) {
            throw new GreenTrackerException("Error: Export Csv" + e.getMessage());
        }
    }

    public void readVerbraucheFromCsv() throws GreenTrackerException {
        String filepath = "data/greentracker.csv";
        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine();
            while (line != null && !line.isBlank()) {
                String[] lineParts = line.split(";");
                if (lineParts.length >= 4) {
                    try {
                        switch (lineParts[0].trim()) {
                            case "WasserVerbrauch" -> hinzufuegen(new WasserVerbrauch(lineParts));
                            case "StromVerbrauch" -> hinzufuegen(new StromVerbrauch(lineParts));
                            case "GasVerbrauch" -> hinzufuegen(new GasVerbrauch(lineParts));
                            case "DuschVerbrauch" -> hinzufuegen(new DuschVerbrauch(lineParts));
                        }
                    } catch (GreenTrackerException e) {
                        throw new GreenTrackerException(e.getMessage());
                    }
                } else {
                    System.out.println("Datei ist leer");
                }
                line = br.readLine();
            }

        } catch (IOException e) {
            throw new GreenTrackerException(e.getMessage());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Greentracker ").append(name).append("\n");
        int anzahl = verbrauche.size();
        if (anzahl > 0) {
            sb.append("Anzahl der Verbrauche: ").append(anzahl).append("\n");
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
