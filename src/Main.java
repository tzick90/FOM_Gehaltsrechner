import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // ALLE Variablen am Anfang deklarieren
        int jahr;
        Map<String, Double> svSaetze;
        Map<String, Abgabenrechner.KrankenkassenInfo> krankenkassen;
        Map<String, Double> einkommensteuerGrenzen;
        Map<String, CsvReader.BundeslandInfo> bundeslaender;
        Map<String, Double> pauschalen;
        Map<String, Double> steuersaetze;

        int steuerklasse;
        int anzahlKinder;  // ← NEU statt boolean hatKinder
        String gewähltesBundesland;
        boolean istKirchenmitglied;

        String gewählteKK;
        //String gewähltesBundesland;

        Scanner scanner = new Scanner(System.in);

        //int auswahl;




        jahr = 2026;
        try {
            // Sozialversicherung Basis laden
            svSaetze = CsvReader.lesenMitJahr("config/sozialversicherung_saetze.csv", jahr);

            // Krankenkassen laden
            krankenkassen = CsvReader.leseKrankenkassen("config/krankenkassen.csv");

            // Einkommensteuergrenzen laden
            einkommensteuerGrenzen = CsvReader.lesenMitJahr("config/einkommensteuer_grenzen.csv", jahr);

            // Bundesländer laden
            bundeslaender = CsvReader.leseBundeslaenderMitJahr("config/Bundesland_und_Kirchensteuer.csv", jahr);

            // Pauschalen laden
            pauschalen = CsvReader.lesenMitJahr("config/pauschalen.csv", jahr);

            // Steuersätze laden
            steuersaetze = CsvReader.lesenMitJahr("config/steuer_saetze.csv", jahr);

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

        // Pauschalen
        double werbekostenpauschale = pauschalen.get("werbungskosten");
        double sonderausgaben = pauschalen.get("sonderausgaben");
        double entlastungBasis = pauschalen.get("entlastungsbetrag_basis");
        double entlastungWeiteres = pauschalen.get("entlastungsbetrag_weiteres");

        // Einkommensteuer-Grenzen
        double grundfreibetrag = einkommensteuerGrenzen.get("grundfreibetrag");
        double zone1Ende = einkommensteuerGrenzen.get("zone1_ende");
        double zone2Ende = einkommensteuerGrenzen.get("zone2_ende");
        double zone3Ende = einkommensteuerGrenzen.get("zone3_ende");

        // Soli-Werte
        double soliSatz = steuersaetze.get("solidaritaetszuschlag_satz");
        double soliFreigrenze = steuersaetze.get("solidaritaetszuschlag_freigrenze");
        double soliObergrenze = steuersaetze.get("solidaritaetszuschlag_obergrenze");



        // ========================================
        // KRANKENKASSE EINGEBEN (String-basiert)
        // ========================================

        // Häufigste Abkürzungen manuell
        Map<String, String> kkAbkürzungen = new HashMap<>();
        kkAbkürzungen.put("tk", "Techniker Krankenkasse");
        kkAbkürzungen.put("dak", "DAK-Gesundheit");
        kkAbkürzungen.put("barmer", "BARMER");
        kkAbkürzungen.put("aok", "AOK");
        kkAbkürzungen.put("ikk", "IKK");
        kkAbkürzungen.put("bkk", "BKK");
        kkAbkürzungen.put("hkk", "hkk");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("KRANKENKASSE");
        System.out.println("=".repeat(60));
        System.out.println("Eingabe: z.B. 'TK', 'DAK', 'AOK Bayern', 'hkk'");
        System.out.println("Oder '?' für Liste aller Krankenkassen");

        while (true) {
            System.out.print("\nKrankenkasse: ");
            // scanner.nextLine(); // Buffer leeren
            String eingabe = scanner.nextLine().trim();

            // Liste anzeigen
            if (eingabe.equals("?")) {
                System.out.println("\nVerfügbare Krankenkassen:");
                int i = 1;
                for (String kk : krankenkassen.keySet()) {
                    Abgabenrechner.KrankenkassenInfo info = krankenkassen.get(kk);
                    System.out.println(i + ". " + kk +
                            " (Gesamt: " + info.getGesamtbeitrag() + "%, " +
                            "Zusatz: " + info.getZusatzbeitrag() + "%)");
                    i++;
                }
                continue;
            }

            // Fuzzy-Match
            gewählteKK = FuzzyMatcher.finde(eingabe, krankenkassen, kkAbkürzungen);

            if (gewählteKK != null) {
                System.out.println("✓ Gefunden: " + gewählteKK);
                break;
            } else {
                // Vorschläge geben
                List<String> vorschläge = FuzzyMatcher.gibVorschläge(eingabe, krankenkassen, 3);
                if (!vorschläge.isEmpty()) {
                    System.out.println("✗ Nicht gefunden. Meinten Sie:");
                    for (String v : vorschläge) {
                        System.out.println("  - " + v);
                    }
                } else {
                    System.out.println("✗ Nicht gefunden.");
                }
                System.out.println("Oder '?' für vollständige Liste.");
            }
        }

        //Bruttogehalt
        System.out.print("\nBruttogehalt (monatlich) in €: ");
        double brutto = scanner.nextDouble();

        // Steuerklasse wählen
        System.out.println("\n" + "=".repeat(60));
        System.out.println("STEUERKLASSE");
        System.out.println("=".repeat(60));
        System.out.println("1. I   - Ledig, geschieden, verwitwet");
        System.out.println("2. II  - Alleinerziehend mit Kind(ern)");
        System.out.println("3. III - Verheiratet (Partner hat V)");
        System.out.println("4. IV  - Verheiratet (beide berufstätig)");
        System.out.println("5. V   - Verheiratet (Partner hat III)");
        System.out.println("6. VI  - Zweitjob/Nebenjob");

        System.out.print("\nWählen Sie Ihre Steuerklasse (1-6): ");
        steuerklasse = scanner.nextInt();

        String[] steuerklassenNamen = {"I", "II", "III", "IV", "V", "VI"};
        System.out.println("✓ Gewählt: Steuerklasse " + steuerklassenNamen[steuerklasse - 1]);


        //Alter User
        System.out.print("\nBitte Alter eingeben:");
        int alter = scanner.nextInt();

        //Anzahl Kinder
        System.out.print("Anzahl Kinder unter 25 Jahren (0-5): ");
        anzahlKinder = scanner.nextInt();

        // Bundesland auswählen:
        // ========================================
        // BUNDESLAND EINGEBEN (String-basiert)
        // ========================================

        // Offizielle Kürzel
        Map<String, String> blAbkürzungen = Map.ofEntries(
                Map.entry("bw", "Baden-Württemberg"),
                Map.entry("by", "Bayern"),
                Map.entry("be", "Berlin"),
                Map.entry("bb", "Brandenburg"),
                Map.entry("hb", "Bremen"),
                Map.entry("hh", "Hamburg"),
                Map.entry("he", "Hessen"),
                Map.entry("mv", "Mecklenburg-Vorpommern"),
                Map.entry("ni", "Niedersachsen"),
                Map.entry("nrw", "Nordrhein-Westfalen"),
                Map.entry("rp", "Rheinland-Pfalz"),
                Map.entry("sl", "Saarland"),
                Map.entry("sn", "Sachsen"),
                Map.entry("st", "Sachsen-Anhalt"),
                Map.entry("sh", "Schleswig-Holstein"),
                Map.entry("th", "Thüringen")
        );

        System.out.println("\n" + "=".repeat(60));
        System.out.println("BUNDESLAND");
        System.out.println("=".repeat(60));
        System.out.println("Eingabe: z.B. 'Bayern', 'BW', 'NRW'");
        System.out.println("Oder '?' für Liste");

        while (true) {
            System.out.print("\nBundesland: ");
            String eingabe = scanner.nextLine().trim();

            // Liste anzeigen
            if (eingabe.equals("?")) {
                System.out.println("\nVerfügbare Bundesländer:");
                int i = 1;
                for (String bl : bundeslaender.keySet()) {
                    CsvReader.BundeslandInfo blInfo = bundeslaender.get(bl);
                    System.out.println(i + ". " + bl +
                            " (Kirchensteuer: " + blInfo.getKirchensteuer() + "%)");
                    i++;
                }
                continue;
            }

            // Fuzzy-Match
            gewähltesBundesland = FuzzyMatcher.finde(eingabe, bundeslaender, blAbkürzungen);

            if (gewähltesBundesland != null) {
                System.out.println("✓ Gefunden: " + gewähltesBundesland);
                break;
            } else {
                // Vorschläge geben
                List<String> vorschläge = FuzzyMatcher.gibVorschläge(eingabe, bundeslaender, 3);
                if (!vorschläge.isEmpty()) {
                    System.out.println("✗ Nicht gefunden. Meinten Sie:");
                    for (String v : vorschläge) {
                        System.out.println("  - " + v);
                    }
                } else {
                    System.out.println("✗ Nicht gefunden.");
                }
                System.out.println("Oder '?' für vollständige Liste.");
            }
        }


        // Kirchenmitgliedschaft abfragen
        System.out.print("\nSind Sie Kirchenmitglied? (true/false): ");
        istKirchenmitglied = scanner.nextBoolean();

        if (istKirchenmitglied) {
            System.out.println("✓ Kirchensteuer wird berechnet (" +
                    bundeslaender.get(gewähltesBundesland).getKirchensteuer() + "%)");
        } else {
            System.out.println("✓ Keine Kirchensteuer");
        }


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
        // STEUERN BERECHNEN
        // ========================================

        // Einkommensteuer-Grenzen laden
        //double grundfreibetrag = einkommensteuerGrenzen.get("grundfreibetrag");
        //double zone1Ende = einkommensteuerGrenzen.get("zone1_ende");
        //double zone2Ende = einkommensteuerGrenzen.get("zone2_ende");
        //double zone3Ende = einkommensteuerGrenzen.get("zone3_ende");

        // Lohnsteuer berechnen (nach §32a EStG) - JAHR
        // Hier wird sozialabgabenGesamt übergeben!
        double lohnsteuerJahr;

        switch (steuerklasse) {
            case 1:  // Klasse I
            case 4:  // Klasse IV (gleich wie I)
                lohnsteuerJahr = Gehaltsrechner.berechneLohnsteuerJahr(
                        brutto,
                        sozialabgabenGesamt,
                        werbekostenpauschale,
                        sonderausgaben,
                        grundfreibetrag,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahr
                );
                break;

            case 2:  // Klasse II (Alleinerziehend)
                lohnsteuerJahr = Gehaltsrechner.berechneLohnsteuerKlasseII(
                        brutto,
                        sozialabgabenGesamt,
                        werbekostenpauschale,
                        sonderausgaben,
                        entlastungBasis,
                        entlastungWeiteres,
                        anzahlKinder,
                        grundfreibetrag,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahr
                );
                break;

            case 3:  // Klasse III (Ehegattensplitting)
                lohnsteuerJahr = Gehaltsrechner.berechneLohnsteuerKlasseIII(
                        brutto,
                        sozialabgabenGesamt,
                        werbekostenpauschale,
                        sonderausgaben,
                        grundfreibetrag,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahr
                );
                break;

            case 5:  // Klasse V
                lohnsteuerJahr = Gehaltsrechner.berechneLohnsteuerKlasseV(
                        brutto,
                        sozialabgabenGesamt,
                        werbekostenpauschale,
                        sonderausgaben,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahr
                );
                break;

            case 6:  // Klasse VI (Zweitjob)
                lohnsteuerJahr = Gehaltsrechner.berechneLohnsteuerKlasseVI(
                        brutto,
                        sozialabgabenGesamt,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahr
                );
                break;

            default:
                System.err.println("Ungültige Steuerklasse!");
                return;
        }
        double lohnsteuerMonat = lohnsteuerJahr / 12;

        // Soli berechnen
        double soliJahr = Gehaltsrechner.berechneSolidaritaetszuschlag(
                lohnsteuerJahr,
                soliSatz,
                soliFreigrenze,
                soliObergrenze
        );
        double soliMonat = soliJahr / 12;

        // Kirchensteuer berechnen - JAHR
        CsvReader.BundeslandInfo blInfo = bundeslaender.get(gewähltesBundesland);
        double kirchensteuersatz = blInfo.getKirchensteuer();
        double kirchensteuerJahr = Gehaltsrechner.berechneKirchensteuerJahr(
                lohnsteuerJahr,
                kirchensteuersatz,
                istKirchenmitglied
        );
        double kirchensteuerMonat = kirchensteuerJahr / 12;

        // Steuern gesamt
        double steuernGesamtJahr = lohnsteuerJahr + kirchensteuerJahr;
        double steuernGesamtMonat = lohnsteuerMonat + kirchensteuerMonat;


        // ========================================
        // ERGEBNIS AUSGEBEN
        // ========================================

        System.out.println("\n" + "=".repeat(70));
        System.out.println("STEUERN (Jahr " + jahr + ")");
        System.out.println("=".repeat(70));

        //zVE berechnen für die Anzeige
        double bruttoJahr = brutto * 12;
        double sozialabgabenJahr = sozialabgabenGesamt * 12;
        double zvEJahr = bruttoJahr
                - sozialabgabenJahr
                - werbekostenpauschale
                - sonderausgaben;

        // bei Klasse II: Entlastungsbetrag abziehen
        double entlastungsbetrag = 0;
        if (steuerklasse == 2 && anzahlKinder >= 1) {
            entlastungsbetrag = entlastungBasis;
            if (anzahlKinder > 1) {
                entlastungsbetrag += (anzahlKinder -1) * entlastungWeiteres;
            }
        }

        // Entlastungsbetrag von zvE abziehen  ← NEU!
        zvEJahr -= entlastungsbetrag;

        System.out.println("\nZu versteuerndes Einkommen (zvE):");
        System.out.println("  Brutto (Jahr):             " + String.format("%,10.2f €", bruttoJahr));
        System.out.println("  - Sozialabgaben:           " + String.format("%,10.2f €", sozialabgabenJahr));
        System.out.println("  - Werbungskosten:          " + String.format("%,10.2f €", werbekostenpauschale));
        System.out.println("  - Sonderausgaben:          " + String.format("%,10.2f €", sonderausgaben));

        if (steuerklasse == 2 && anzahlKinder >= 1) {
            System.out.println("  - Entlastungsbetrag:       " + String.format("%,10.2f €", entlastungsbetrag));
        }

        System.out.println("  " + "-".repeat(68));
        System.out.println("  = zvE (Jahr):              " + String.format("%,10.2f €", zvEJahr));

        System.out.println("\nLohnsteuer (§32a EStG):");
        System.out.println("  Lohnsteuer (Jahr):         " + String.format("%,10.2f €", lohnsteuerJahr));
        System.out.println("  Lohnsteuer (Monat):        " + String.format("%,10.2f €", lohnsteuerMonat));

        System.out.println("  Soli-Satz:                 " + soliSatz + "%");
        if (lohnsteuerJahr <= soliFreigrenze) {
            System.out.println("  Status:                    Unter Freigrenze - keine Soli");
        } else if (lohnsteuerJahr <= soliObergrenze) {
            System.out.println("  Status:                    In Gleitzone");
        }
        System.out.println("  Soli (Jahr):               " + String.format("%,10.2f €", soliJahr));
        System.out.println("  Soli (Monat):              " + String.format("%,10.2f €", soliMonat));


        System.out.println("\nKirchensteuer:");
        System.out.println("  Bundesland:                " + gewähltesBundesland);
        System.out.println("  Kirchensteuersatz:         " + kirchensteuersatz + "%");
        System.out.println("  Kirchenmitglied:           " + (istKirchenmitglied ? "Ja" : "Nein"));
        System.out.println("  Kirchensteuer (Jahr):      " + String.format("%,10.2f €", kirchensteuerJahr));
        System.out.println("  Kirchensteuer (Monat):     " + String.format("%,10.2f €", kirchensteuerMonat));

        System.out.println("\n" + "-".repeat(70));
        System.out.println("SUMME STEUERN:");
        System.out.println("  Jahr:                      " + String.format("%,10.2f €", steuernGesamtJahr));
        System.out.println("  Monat:                     " + String.format("%,10.2f €", steuernGesamtMonat));
        System.out.println("=".repeat(70));

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

        // ========================================
        // NETTO BERECHNEN
        // ========================================

        double netto = brutto - sozialabgabenGesamt - steuernGesamtMonat;

        System.out.println("\n" + "=".repeat(70));
        System.out.println("NETTO-GEHALT");
        System.out.println("=".repeat(70));
        System.out.println("  Bruttogehalt:              " + String.format("%,10.2f €", brutto));
        System.out.println("  - Sozialabgaben:           " + String.format("%,10.2f €", sozialabgabenGesamt));
        System.out.println("  - Steuern:                 " + String.format("%,10.2f €", steuernGesamtMonat));
        System.out.println("  " + "-".repeat(68));
        System.out.println("  = NETTO:                   " + String.format("%,10.2f €", netto));
        System.out.println("=".repeat(70));

        scanner.close();
    }
}
