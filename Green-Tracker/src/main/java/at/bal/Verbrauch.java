package at.bal;

// TODO
// toString
// CSVString
// compareTo
// fixTaetigkeiten
// AnzahlTaetigkeiten

import java.util.Objects;

public abstract class Verbrauch {

    private String taetigkeit; // Was man macht
    private double dauerMin; // Wie lang

    // Default-Konstruktor
    public Verbrauch() {

    }

    public Verbrauch(String taetigkeit, double dauerMin) throws GreenTrackerException {
        setTaetigkeit(taetigkeit);
        setDauerMin(dauerMin);
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
}
