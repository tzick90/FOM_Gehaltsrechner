import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // ALLE Variablen am Anfang deklarieren
        int jahr;
        Map<String, Double> svSaetze;
        Map<String, Abgabenrechner.KrankenkassenInfo> krankenkassen;

        int anzahlKinder;  // ← NEU statt boolean hatKinder

        jahr = 2026;
        try {
            // Sozialversicherung Basis laden
            svSaetze = CsvReader.lesenMitJahr("config/sozialversicherung_saetze.csv", jahr);

            // Krankenkassen laden
            krankenkassen = CsvReader.leseKrankenkassen("config/krankenkassen.csv");

            System.out.println("✓ CSVs erfolgreich geladen!");

        } catch (IOException e) {
            System.err.println("✗ Fehler beim Laden der CSVs: " + e.getMessage());
            return; // Programm beenden
        }

        // BBG-Werte aus CSV holen
        double bbgKV = svSaetze.get("bbg_kranken_pflege");
        double bbgRV = svSaetze.get("bbg_renten_arbeitslosen");

        // Sozialversicherungs-Gesamtsätze aus CSV holen
        double kvBasis = svSaetze.get("krankenversicherung_basis");  // 14.6%
        double rvSatzGesamt = svSaetze.get("rentenversicherung");    // 18.6%
        double avSatzGesamt = svSaetze.get("arbeitslosenversicherung");  // 2.6%
        double pvSatzGesamt = svSaetze.get("pflegeversicherung_basis");  // 3.6%
        double pvZuschlag = svSaetze.get("pflegeversicherung_zuschlag_kinderlos");  // 0.6
        double pvAbschlag = svSaetze.get("pflegeversicherung_abschlag_pro_kind");   // 0.25

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
        System.out.print("Anzahl Kinder unter 25 Jahren (0-5): ");
        anzahlKinder = scanner.nextInt();


        // ========================================
        // SOZIALABGABEN BERECHNEN
        // ========================================

        // 1. Krankenversicherung
        Abgabenrechner.KrankenkassenInfo kkInfo = krankenkassen.get(gewählteKK);
        double kvZusatz = kkInfo.getZusatzbeitrag();  // z.B. 2.69%

        double kvBeitrag = Abgabenrechner.berechneKrankenversicherung(
                brutto,
                kvBasis,   // 14.6%
                kvZusatz,  // z.B. 2.69%
                bbgKV
        );

        // 2. Rentenversicherung
        double rvBeitrag = Abgabenrechner.berechneRentenversicherung(
                brutto,
                rvSatzGesamt,  // 18.6%
                bbgRV
        );

        // 3. Arbeitslosenversicherung
        double avBeitrag = Abgabenrechner.berechneArbeitslosenversicherung(
                brutto,
                avSatzGesamt,  // 2.6%
                bbgRV
        );

        // 4. Pflegeversicherung
        double pvBeitrag = Abgabenrechner.berechnePflegeversicherung(
                brutto,
                pvSatzGesamt,  // 3.6%
                pvZuschlag,    // 0.6%
                pvAbschlag,    // 0.25%
                anzahlKinder,
                alter,
                bbgKV
        );

        // Summe Sozialabgaben
        double sozialabgabenGesamt = kvBeitrag + rvBeitrag + avBeitrag + pvBeitrag;

        // ========================================
        // ERGEBNIS AUSGEBEN
        // ========================================

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ERGEBNIS - Sozialabgaben (Jahr " + jahr +")");
        System.out.println("=".repeat(60));
        System.out.println("Bruttogehalt:              " + String.format("%8.2f €", brutto));
        System.out.println("Krankenkasse:              " + gewählteKK);
        System.out.println("-".repeat(60));

        System.out.println("\nKrankenversicherung:");
        System.out.println("  Gesamtbeitrag:           " + (kvBasis + kvZusatz) + "%");
        System.out.println("  ..davon KV-Beitrag:      " + kvBasis + "%");
        System.out.println("  ..davon Zusatzbeitrag:   " + kvZusatz + "%");
        System.out.println("  AN-Anteil (50%):         " + String.format("%8.2f €", kvBeitrag));

        System.out.println("\nRentenversicherung:");
        System.out.println("  Beitragssatz:            " + rvSatzGesamt + "%");
        System.out.println("  AN-Anteil:               " + String.format("%8.2f €", rvBeitrag));

        System.out.println("\nArbeitslosenversicherung:");
        System.out.println("  Beitragssatz:            " + avSatzGesamt + "%");
        System.out.println("  AN-Anteil:               " + String.format("%8.2f €", avBeitrag));

        System.out.println("\nPflegeversicherung:");
        double pvSatz = (pvBeitrag / brutto) * 100;  // Rückrechnung des tatsächlichen Satzes

        if (anzahlKinder == 0 && alter > 23) {
            System.out.println("  Status:                  Kinderlos >23 Jahre");
            System.out.println("  Beitragssatz:            " + String.format("%.2f", pvSatz) + "% (Basis 1.8% + Zuschlag 0.6%)");
        } else if (anzahlKinder >= 2) {
            System.out.println("  Status:                  " + anzahlKinder + " Kinder");
            double abschlag = Math.min(anzahlKinder - 1, 4) * 0.25;
            System.out.println("  Beitragssatz:            " + String.format("%.2f", pvSatz) + "% (Basis 1.8% - Abschlag " + String.format("%.2f", abschlag) + "%)");
        } else if (anzahlKinder == 1) {
            System.out.println("  Status:                  1 Kind");
            System.out.println("  Beitragssatz:            " + String.format("%.2f", pvSatz) + "% (Basis-Satz)");
        } else {
            System.out.println("  Status:                  ≤23 Jahre");
            System.out.println("  Beitragssatz:            " + String.format("%.2f", pvSatz) + "% (Basis-Satz)");
        }
        System.out.println("  AN-Anteil:               " + String.format("%8.2f €", pvBeitrag));

        System.out.println("\n" + "=".repeat(60));
        System.out.println("SUMME SOZIALABGABEN:       " + String.format("%8.2f €", sozialabgabenGesamt));
        System.out.println("=".repeat(60));

        scanner.close();
    }
}
