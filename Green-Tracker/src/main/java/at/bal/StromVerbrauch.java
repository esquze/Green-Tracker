package at.bal;

// TODO
// toString
// CSVString

import java.util.List;
import java.util.Objects;

public class StromVerbrauch extends Verbrauch {

    // FixList
    public static final List<String> vorgeschlageneTaetigkeiten = List.of(
            "Computer", "Fernseher", "Waschmaschine", "Licht", "Kühlschrank"
    );

    private double aktuellerStromPreis;
    private double kilowattProStunden;

    public StromVerbrauch(String taetigkeit, double dauer, double aktuellerStromPreis, double kilowattProStunden) throws GreenTrackerException {
        super(taetigkeit, dauer);
        setAktuellerStromPreis(aktuellerStromPreis);
        setKilowattProStunden(kilowattProStunden);
    }

    public double getAktuellerStromPreis() {
        return aktuellerStromPreis;
    }

    public void setAktuellerStromPreis(double aktuellerStromPreis) throws GreenTrackerException {
        if (aktuellerStromPreis <= 0.0) {
            throw new GreenTrackerException("Fehler: Strom-Preis muss größer 0.0 sein");
        }
        this.aktuellerStromPreis = aktuellerStromPreis;
    }

    public double getKilowattProStunden() {
        return kilowattProStunden;
    }

    public void setKilowattProStunden(double kilowattProStunden) throws GreenTrackerException {
        if (kilowattProStunden <= 0.0) {
            throw new GreenTrackerException("Fehler: Kilowatt-pro-Stunden muss größer 0.0 sein");
        }
        this.kilowattProStunden = kilowattProStunden;
    }

    @Override
    public double preis() {
        return aktuellerStromPreis * kilowattProStunden;
    }

    @Override
    public double ausstoss() {
        return kilowattProStunden * getDauerMin();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StromVerbrauch that = (StromVerbrauch) o;
        return Double.compare(aktuellerStromPreis, that.aktuellerStromPreis) == 0 && Double.compare(kilowattProStunden, that.kilowattProStunden) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), aktuellerStromPreis, kilowattProStunden);
    }

}
