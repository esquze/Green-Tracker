package at.bal.model;
public class Main {
    static void main() {

        testSaveVerbrauch();
        testWriteVerbraucheCsv();
        testReadVerbraucheCsv();

    }


    public static void testSaveVerbrauch() {
        try {
            System.out.println("----Test Save Verbrauch----");
            GreenTracker greenTracker = new GreenTracker("Greentracker 1");
            WasserVerbrauch w1 = new WasserVerbrauch("Haende waschen",2, 0.5, 20);
            GasVerbrauch g1 = new GasVerbrauch("Heizen",120,1,50);

            greenTracker.hinzufuegen(w1);
            greenTracker.hinzufuegen(g1);

            greenTracker.save();
            System.out.println("Verbrauch gespeichert");
        } catch (GreenTrackerException e) {
            System.out.println("Unerwartete Exception: " + e.getMessage());
        }
    }

    public static void testWriteVerbraucheCsv() {
        try {
            System.out.println("----Test Write Verbrauche Csv----");
            GreenTracker greenTracker = new GreenTracker("Greentracker 1");
            WasserVerbrauch w1 = new WasserVerbrauch("Haende waschen",2, 0.5, 20);
            GasVerbrauch g1 = new GasVerbrauch("Heizen",120,1,50);
            StromVerbrauch s1 = new StromVerbrauch("PC", 300,3,150);
            DuschVerbrauch d1 = new DuschVerbrauch("Dusche", 15,3,150,g1);


            greenTracker.hinzufuegen(w1);
            greenTracker.hinzufuegen(g1);
            greenTracker.hinzufuegen(s1);
            greenTracker.hinzufuegen(d1);

            greenTracker.writeVerbraucheToCsv();
            System.out.println("Export erfolgreich");
        } catch (GreenTrackerException e) {
            System.out.println("Unerwartete Exception: " + e.getMessage());
        }
    }



    public static void testReadVerbraucheCsv() {
        try {
            System.out.println("----Test Read Verbrauche Csv----");

            GreenTracker g2 = new GreenTracker("Greentracker 2");

            g2.readVerbraucheFromCsv();
            System.out.println(g2);

            System.out.println("Import erfolgreich");
        } catch (GreenTrackerException e) {
            System.out.println("Unerwartete Exception: " + e.getMessage());
        }
    }


}