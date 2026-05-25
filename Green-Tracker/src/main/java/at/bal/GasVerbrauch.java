package at.bal;


import java.util.List;
import java.util.Objects;

public class GasVerbrauch extends Verbrauch {

    // FixList
    public static final List<String> vorgeschlageneTaetigkeiten = List.of(
            "Kochen", "Heizen", "Warmwasser", "Trocknen"
    );

    private double aktuellerGasPreis;
    private double kubikmeternVerbrauchGas;

    public GasVerbrauch(String taetigkeit, double dauer, double aktuellerGasPreis, double kubikmeternVerbrauchGas) throws GreenTrackerException {
        super(taetigkeit, dauer);
        setAktuellerGasPreis(aktuellerGasPreis);
        setKubikmeternVerbrauchGas(kubikmeternVerbrauchGas);
    }

    public GasVerbrauch(String[] lineParts) throws GreenTrackerException {
        super(lineParts[1], Double.parseDouble(lineParts[2]));
        setAktuellerGasPreis(Double.parseDouble(lineParts[3]));
        setKubikmeternVerbrauchGas(Double.parseDouble(lineParts[4]));
    }

    public double getAktuellerGasPreis() {
        return aktuellerGasPreis;
    }

    public void setAktuellerGasPreis(double aktuellerGasPreis) throws GreenTrackerException {
        if (aktuellerGasPreis <= 0.0) {
            throw new GreenTrackerException("Fehler: Gas-Preis muss größer 0.0 sein");
        }
        this.aktuellerGasPreis = aktuellerGasPreis;
    }

    public double getKubikmeternVerbrauchGas() {
        return kubikmeternVerbrauchGas;
    }

    public void setKubikmeternVerbrauchGas(double kubikmeternVerbrauchGas) throws GreenTrackerException {
        if (kubikmeternVerbrauchGas <= 0.0) {
            throw new GreenTrackerException("Fehler: Gas-Kubikmetern muss größer 0.0 sein");
        }
        this.kubikmeternVerbrauchGas = kubikmeternVerbrauchGas;
    }

    @Override
    public double preis() {
        return aktuellerGasPreis * kubikmeternVerbrauchGas;
    }

    @Override
    public double ausstoss() {
        return kubikmeternVerbrauchGas * getDauerMin();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GasVerbrauch that = (GasVerbrauch) o;
        return Double.compare(aktuellerGasPreis, that.aktuellerGasPreis) == 0 && Double.compare(kubikmeternVerbrauchGas, that.kubikmeternVerbrauchGas) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), aktuellerGasPreis, kubikmeternVerbrauchGas);
    }

    @Override
    public String toString() {
        return super.toString() + ", akt. Gas Preis: " + aktuellerGasPreis + ", m³: " + kubikmeternVerbrauchGas;
    }

    public String toCsvString() {
        return super.toCsvString() + ";" + aktuellerGasPreis + ";" + kubikmeternVerbrauchGas;
    }

}
