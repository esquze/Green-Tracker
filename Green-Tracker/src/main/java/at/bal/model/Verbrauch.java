package at.bal.model;
// TODO
// compareTo

import java.io.Serializable;
import java.util.Objects;

public abstract class Verbrauch implements Serializable, Comparable<Verbrauch> {

    private String taetigkeit; // Was man macht
    private double dauerMin; // Wie lang

    // Default-Konstruktor
    public Verbrauch() {
    }

    public Verbrauch(String taetigkeit, double dauerMin) throws GreenTrackerException {
        setTaetigkeit(taetigkeit);
        setDauerMin(dauerMin);
    }

    public Verbrauch(String[] lineParts) throws GreenTrackerException {
        try {
            setTaetigkeit(lineParts[1]);
            setDauerMin(Double.parseDouble(lineParts[2]));
        } catch (NumberFormatException e) {
            System.out.println("Error beim Verbrauch import: " + e.getMessage());
        }
    }

    public String getTaetigkeit() {
        return taetigkeit;
    }

    public void setTaetigkeit(String taetigkeit) throws GreenTrackerException {
        if (taetigkeit == null || taetigkeit.isEmpty()) {
            throw new GreenTrackerException("Fehler: Tätigkeit darf nicht null oder leer sein");
        }
        this.taetigkeit = taetigkeit;
    }

    public double getDauerMin() {
        return dauerMin;
    }

    public void setDauerMin(double dauerMin) throws GreenTrackerException {
        if (dauerMin <= 0.0) {
            throw new GreenTrackerException("Fehler: Dauer muss größer 0.0 sein");
        }
        this.dauerMin = dauerMin;
    }

    public abstract double preis();

    public abstract double ausstoss();

    public abstract double co2Fussabdruck();

    public int compareTo(Verbrauch other) {
        return this.getTaetigkeit().compareTo(other.getTaetigkeit());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Verbrauch verbrauch = (Verbrauch) o;
        return Double.compare(dauerMin, verbrauch.dauerMin) == 0 && Objects.equals(taetigkeit, verbrauch.taetigkeit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taetigkeit, dauerMin);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");
        sb.append("Taetigkeit: ").append(taetigkeit);
        sb.append("Dauer: ").append(dauerMin);
        return sb.toString();
    }

    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(";");
        sb.append(taetigkeit).append(";");
        sb.append(dauerMin);
        return sb.toString();
    }
}
