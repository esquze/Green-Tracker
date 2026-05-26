package at.bal;

import static org.junit.jupiter.api.Assertions.*;

class GreenTrackerTest {

    private GreenTracker greenTracker;
    private WasserVerbrauch w1;
    private GasVerbrauch g1;
    private StromVerbrauch s1;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        try {
            greenTracker = new GreenTracker("Greentracker 1");
            w1 = new WasserVerbrauch("Haende waschen",2, 0.5, 20);
            g1 = new GasVerbrauch("Heizen",120,1,50);
            s1 = new StromVerbrauch("PC", 300,3,150);

            greenTracker.hinzufuegen(w1);
            greenTracker.hinzufuegen(g1);

        } catch (GreenTrackerException e) {
            throw new RuntimeException(e);
        }
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void getVerbauch() throws GreenTrackerException {
        assertEquals(greenTracker.getVerbauch(0),w1);
        assertThrows(GreenTrackerException.class, () -> greenTracker.getVerbauch(100));
        assertThrows(GreenTrackerException.class, () -> greenTracker.getVerbauch(-1));
    }

    @org.junit.jupiter.api.Test
    void getName() {
        assertEquals("Greentracker 1", greenTracker.getName());
        assertNotEquals("Falscher Name", greenTracker.getName());
    }

    @org.junit.jupiter.api.Test
    void setName() {
        String falscherName = "          ";
        String leererName = "";
        String richtigerName = "Greentracker Richtig";
        assertThrows(GreenTrackerException.class, () -> greenTracker.setName(null));
        assertThrows(GreenTrackerException.class, () -> greenTracker.setName(falscherName));
        assertThrows(GreenTrackerException.class, () -> greenTracker.setName(leererName));
        assertDoesNotThrow(() -> greenTracker.setName(richtigerName));
    }

    @org.junit.jupiter.api.Test
    void hinzufuegen() throws GreenTrackerException {
        assertFalse(greenTracker.hinzufuegen(null));
        assertTrue(greenTracker.hinzufuegen(s1));
        assertFalse(greenTracker.hinzufuegen(s1));
        assertFalse(greenTracker.hinzufuegen(g1));
    }

    @org.junit.jupiter.api.Test
    void entfernen() throws GreenTrackerException {
        // Verbrauch entfernen
        GreenTracker greenTrackerLeer = new GreenTracker("Leer Greentracker");
        assertFalse(greenTracker.entfernen((Verbrauch) null));
        assertFalse(greenTrackerLeer.entfernen(w1));
        assertTrue(greenTracker.entfernen(w1));
        assertFalse(greenTracker.entfernen(w1));

        // String entfernen
        assertEquals(1, greenTracker.entfernen("Heizen"));
        assertThrows(GreenTrackerException.class, () -> greenTracker.entfernen((String) null));
    }

    @org.junit.jupiter.api.Test
    void berechneGesamtEmission() {
        assertEquals(6040.0, greenTracker.berechneGesamtEmission());
    }

    @org.junit.jupiter.api.Test
    void berechneVerbrauchEmisson() throws GreenTrackerException {
        assertEquals(40, greenTracker.berechneVerbrauchEmisson("WasserVerbrauch"));
        assertEquals(6000, greenTracker.berechneVerbrauchEmisson("GasVerbrauch"));
        assertEquals(0, greenTracker.berechneVerbrauchEmisson("StromVerbrauch"));
        assertThrows(GreenTrackerException.class, () -> greenTracker.berechneVerbrauchEmisson("Kein Type"));
        assertThrows(GreenTrackerException.class, () -> greenTracker.berechneVerbrauchEmisson(null));
    }

    @org.junit.jupiter.api.Test
    void berechneGesamtPreis() {
        assertEquals(60, greenTracker.berechneGesamtPreis());
    }

    @org.junit.jupiter.api.Test
    void berechneVerbrauchPreis() throws GreenTrackerException {
        assertEquals(10, greenTracker.berechneVerbrauchPreis("WasserVerbrauch"));
        assertEquals(50, greenTracker.berechneVerbrauchPreis("GasVerbrauch"));
        assertEquals(0, greenTracker.berechneVerbrauchPreis("StromVerbrauch"));
        assertThrows(GreenTrackerException.class, () -> greenTracker.berechneVerbrauchPreis("Kein Type"));
        assertThrows(GreenTrackerException.class, () -> greenTracker.berechneVerbrauchPreis(null));
    }

    @org.junit.jupiter.api.Test
    void anzahlTaetigkeiten() throws GreenTrackerException {
        String falscherName = "          ";
        String leererName = "";
        String richtigerName = "Greentracker Richtig";
        assertThrows(GreenTrackerException.class, () -> greenTracker.anzahlTaetigkeiten(null));
        assertThrows(GreenTrackerException.class, () -> greenTracker.anzahlTaetigkeiten(falscherName));
        assertThrows(GreenTrackerException.class, () -> greenTracker.anzahlTaetigkeiten(leererName));
        assertDoesNotThrow(() -> greenTracker.anzahlTaetigkeiten(richtigerName));
        assertEquals(1,greenTracker.anzahlTaetigkeiten("Heizen"));
        assertNotEquals(100,greenTracker.anzahlTaetigkeiten("Keine Taetigkeit"));
    }

    @org.junit.jupiter.api.Test
    void sortieren() {
        // TODO
    }

    @org.junit.jupiter.api.Test
    void sortierenNachTaetigkeit() {
        // TODO
    }

    @org.junit.jupiter.api.Test
    void save() {
        // Siehe Main
    }

    @org.junit.jupiter.api.Test
    void writeVerbraucheToCsv() {
        // Siehe Main
    }

    @org.junit.jupiter.api.Test
    void readVerbraucheFromCsv() {
        // Siehe Main
    }
}