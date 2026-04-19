import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Map<String, Abgabenrechner.KrankenkassenInfo> krankenkassen;
        Map<String, Double> svSaetze;

        try {
            // Sozialversicherung Basis laden
            svSaetze = CsvReader.lesen("config/sozialversicherung_saetze.csv");

            // Krankenkassen laden
            krankenkassen = CsvReader.leseKrankenkassen("config/krankenkassen.csv");

            System.out.println("✓ CSVs erfolgreich geladen!");

        } catch (IOException e) {
            System.err.println("✗ Fehler beim Laden der CSVs: " + e.getMessage());
            return; // Programm beenden
        }

        System.out.println("\nVerfügbare Krankenkassen:");
        System.out.println("==========================");

        int i = 1;
        // Schleife über krankenkassen.keySet()
        for (String kkName: krankenkassen.keySet()) {
            Abgabenrechner.KrankenkassenInfo info = krankenkassen.get(kkName);
            System.out.println(i + ". " + kkName +
                    " (Gesamt: " + info.getGesamtbeitrag() + "%, " +
                    "Zusatz: " + info.getZusatzbeitrag() + "%)");
            i++;

        }

        // User-Eingabe
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nWählen Sie eine Krankenkasse (1-" + krankenkassen.size() + "):");
        int auswahl = scanner.nextInt();

        //Deklariere die Variable
        String[] kkNamen = krankenkassen.keySet().toArray(new String[0]);
        String gewählteKK = kkNamen[auswahl -1];

        System.out.println("✓ Gewählt: " + gewählteKK);

        //Bruttogehalt
        System.out.print("\nBruttogehalt (monatlich) in €: ");
        double brutto = scanner.nextDouble();


        // KV-Basis aus HashMap holen
        Abgabenrechner.KrankenkassenInfo kkInfo = krankenkassen.get(gewählteKK);
        double kvBasis = svSaetze.get("krankenversicherung_basis");

        // KV-Zusatz der gewählten Krankenkasse holen
        double kvZusatz = kkInfo.getZusatzbeitrag();

        // Berechnung
        double kvBeitrag = Abgabenrechner.berechneKrankenversicherung(
                brutto,
                kvBasis,
                kvZusatz
        );

        System.out.println("Bruttogehalt:        " + String.format("%.2f €", brutto));
        System.out.println("Krankenkasse:        " + gewählteKK);
        System.out.println("KV-Basis:            " + kvBasis + "%");
        System.out.println("KV-Zusatzbeitrag:    " + kvZusatz + "%");
        System.out.println("KV-Gesamtbeitrag:    " + kkInfo.getGesamtbeitrag() + "%");  // ← NEU
        System.out.println("-".repeat(50));
        System.out.println("KV-Beitrag (AN):     " + String.format("%.2f €", kvBeitrag));
    }
}
