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

        //Alter User
        System.out.print("\nBitte Alter eingeben:");
        int alter = scanner.nextInt();

        //Anzahl Kinder
        System.out.print("\nHaben Sie Kinder?:");
        boolean hatKinder = scanner.hasNextBoolean();


        // KV-Basis aus HashMap holen
        Abgabenrechner.KrankenkassenInfo kkInfo = krankenkassen.get(gewählteKK);
        double kvBasis = svSaetze.get("krankenversicherung_basis");

        // KV-Zusatz der gewählten Krankenkasse holen
        double kvZusatz = kkInfo.getZusatzbeitrag();

        // Berechnung der Sozialabgaben
        // 1. Krankenversicherung
        double kvGesamt = kkInfo.getGesamtbeitrag();
        double kvBeitrag = brutto * (kvGesamt/2)/100;

        // 2. Rentenversicherung
        double rvSatz = svSaetze.get("rentenversicherung");
        double rvBeitrag = Abgabenrechner.berechneRentenversicherung(brutto, rvSatz);

        // 3. Arbeitslosenversicherung
        double avSatz = svSaetze.get("arbeitslosenversicherung");
        double avBeitrag = Abgabenrechner.berechneArbeitslosenversicherung(brutto,avSatz);


        // 4. Pflegeversicherung
        double pvStandard = svSaetze.get("pflegeversicherung_standard");
        double pvKinderlos = svSaetze.get("pflegeversicherung_kinderlos");
        boolean kinderlos = !hatKinder;
        double pvBeitrag = Abgabenrechner.berechnePflegeversicherung(
                brutto,
                pvStandard,
                pvKinderlos,
                kinderlos,
                alter
        );

        // Summe Sozialabgaben
        double sozialabgabenGesamt = kvBeitrag + rvBeitrag + avBeitrag + pvBeitrag;

        // ========================================
        // ERGEBNIS AUSGEBEN
        // ========================================

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ERGEBNIS - Sozialabgaben");
        System.out.println("=".repeat(60));
        System.out.println("Bruttogehalt:              " + String.format("%8.2f €", brutto));
        System.out.println("Krankenkasse:              " + gewählteKK);
        System.out.println("-".repeat(60));

        System.out.println("\nKrankenversicherung:");
        System.out.println("  Gesamtbeitrag:           " + kvGesamt + "%");
        System.out.println("  ..davon KV-Beitrag:      " + kvBasis + "%");
        System.out.println("  ..davon Zusatzbeitrag:   " + kvZusatz + "%");
        System.out.println("  AN-Anteil (50%):         " + String.format("%8.2f €", kvBeitrag));

        System.out.println("\nRentenversicherung:");
        System.out.println("  Beitragssatz:            " + rvSatz + "%");
        System.out.println("  AN-Anteil:               " + String.format("%8.2f €", rvBeitrag));

        System.out.println("\nArbeitslosenversicherung:");
        System.out.println("  Beitragssatz:            " + avSatz + "%");
        System.out.println("  AN-Anteil:               " + String.format("%8.2f €", avBeitrag));

        System.out.println("\nPflegeversicherung:");
        if (kinderlos && alter > 23) {
            System.out.println("  Beitragssatz:            " + pvKinderlos + "% (kinderlos >23)");
        } else {
            System.out.println("  Beitragssatz:            " + pvStandard + "%");
        }
        System.out.println("  AN-Anteil:               " + String.format("%8.2f €", pvBeitrag));

        System.out.println("\n" + "=".repeat(60));
        System.out.println("SUMME SOZIALABGABEN:       " + String.format("%8.2f €", sozialabgabenGesamt));
        System.out.println("=".repeat(60));

        scanner.close();
    }
}
