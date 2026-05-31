package util;

import core.CsvReader;
import core.Sozialabgabenrechner;
import core.Steuerabgabenrechner;


import java.io.IOException;
import java.time.Year;
import java.util.Map;

public class Abgabenrechner {
    /**
     * Berechnet Netto aus Brutto für GUI
     *
     * @param brutto Brutto-Gehalt
     * @param steuerklasse Steuerklasse (1-6)
     * @param gewähltesBundesland Bundesland (für Kirchensteuer)
     * @param kirchenmitglied true = Kirchenmitglied
     * @param jahr Jahr (aktuelles oder vorheriges Jahr)
     * @param gewählteKK die gewählte Krankenkasse
     * @param kinderanzahl die Anzahl der Kinder
     * @param alter die Angabe des Alters
     * @return Ergebnis-Objekt mit allen Berechnungen
     */

    public static Ergebnis berechneGehalt(
            double brutto,
            String steuerklasse,
            String kirchenmitglied,
            String gewähltesBundesland,
            int kinderanzahl,
            String jahr,
            String gewählteKK,
            int alter

    ) {
        // hier der Code von Main rein:

        // Calculate actual and previous year and store in vars
        int currentYear = Year.now().getValue();
        int previousYear = currentYear -1;

        //steuerrechner erwartet boolean kirchenmitglied, GUI liefert String -> Folge: in Boolean übersetzen
        boolean istKirchenmitglied = false;
        if (kirchenmitglied.equals("Ja")) {
            istKirchenmitglied = true;
        } else {
            istKirchenmitglied = false;
        }

        Map<String, Double> svSaetze;
        Map<String, Sozialabgabenrechner.KrankenkassenInfo> krankenkassen;
        Map<String, Double> einkommensteuerGrenzen;
        Map<String, CsvReader.BundeslandInfo> bundeslaender;
        Map<String, Double> pauschalen;
        Map<String, Double> steuersaetze;

        int jahrInt = Integer.parseInt(jahr);

        //-> Auslesen der CSVs
        try {
            // Sozialversicherung Basis laden
            svSaetze = CsvReader.lesenMitJahr("config/sozialversicherung_saetze.csv", jahrInt);

            // Krankenkassen laden
            krankenkassen = CsvReader.leseKrankenkassen("config/krankenkassen.csv");

            // Einkommensteuergrenzen laden
            einkommensteuerGrenzen = CsvReader.lesenMitJahr("config/einkommensteuer_grenzen.csv", jahrInt);

            // Bundesländer laden
            bundeslaender = CsvReader.leseBundeslaenderMitJahr("config/Bundesland_und_Kirchensteuer.csv", jahrInt); // Note:

            // Pauschalen laden
            pauschalen = CsvReader.lesenMitJahr("config/pauschalen.csv", jahrInt);

            // Steuersätze laden
            steuersaetze = CsvReader.lesenMitJahr("config/steuer_saetze.csv", jahrInt);

            System.out.println("✓ CSVs erfolgreich geladen!");

        } catch (IOException e) {
            System.err.println("✗ Fehler beim Laden der CSVs: " + e.getMessage());
            return null; // Programm beenden
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
        // SOZIALABGABEN BERECHNEN
        // ========================================

        // 1. Krankenversicherung
        Sozialabgabenrechner.KrankenkassenInfo kkInfo = krankenkassen.get(gewählteKK);
        double kvZusatz = kkInfo.getZusatzbeitrag();  // z.B. 2.69%

        double kvBeitrag = Sozialabgabenrechner.berechneKrankenversicherung(
                brutto,
                kvBasis,   // 14.6%
                kvZusatz,  // z.B. 2.69%
                bbgKV
        );

        // 2. Rentenversicherung
        double rvBeitrag = Sozialabgabenrechner.berechneRentenversicherung(
                brutto,
                rvSatzGesamt,  // 18.6%
                bbgRV
        );

        // 3. Arbeitslosenversicherung
        double avBeitrag = Sozialabgabenrechner.berechneArbeitslosenversicherung(
                brutto,
                avSatzGesamt,  // 2.6%
                bbgRV
        );

        // 4. Pflegeversicherung
        double pvBeitrag = Sozialabgabenrechner.berechnePflegeversicherung(
                brutto,
                pvSatzGesamt,  // 3.6%
                pvZuschlag,    // 0.6%
                pvAbschlag,    // 0.25%
                kinderanzahl,
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
            case "I":  // Klasse I
            case "IV":  // Klasse IV (gleich wie I)
                lohnsteuerJahr = Steuerabgabenrechner.berechneLohnsteuerJahr(
                        brutto,
                        sozialabgabenGesamt,
                        werbekostenpauschale,
                        sonderausgaben,
                        grundfreibetrag,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahrInt
                );
                break;

            case "II":  // Klasse II (Alleinerziehend)
                lohnsteuerJahr = Steuerabgabenrechner.berechneLohnsteuerKlasseII(
                        brutto,
                        sozialabgabenGesamt,
                        werbekostenpauschale,
                        sonderausgaben,
                        entlastungBasis,
                        entlastungWeiteres,
                        kinderanzahl,
                        grundfreibetrag,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahrInt
                );
                break;

            case "III":  // Klasse III (Ehegattensplitting)
                lohnsteuerJahr = Steuerabgabenrechner.berechneLohnsteuerKlasseIII(
                        brutto,
                        sozialabgabenGesamt,
                        werbekostenpauschale,
                        sonderausgaben,
                        grundfreibetrag,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahrInt
                );
                break;

            case "V":  // Klasse V
                lohnsteuerJahr = Steuerabgabenrechner.berechneLohnsteuerKlasseV(
                        brutto,
                        sozialabgabenGesamt,
                        werbekostenpauschale,
                        sonderausgaben,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahrInt
                );
                break;

            case "VI":  // Klasse VI (Zweitjob)
                lohnsteuerJahr = Steuerabgabenrechner.berechneLohnsteuerKlasseVI(
                        brutto,
                        sozialabgabenGesamt,
                        zone1Ende,
                        zone2Ende,
                        zone3Ende,
                        jahrInt
                );
                break;

            default:
                System.err.println("Ungültige Steuerklasse!");
                return null;
        }
        double lohnsteuerMonat = lohnsteuerJahr / 12;

        // Soli berechnen
        double soliJahr = Steuerabgabenrechner.berechneSolidaritaetszuschlag(
                lohnsteuerJahr,
                soliSatz,
                soliFreigrenze,
                soliObergrenze
        );
        double soliMonat = soliJahr / 12;

        // Kirchensteuer berechnen - JAHR
        CsvReader.BundeslandInfo blInfo = bundeslaender.get(gewähltesBundesland);
        double kirchensteuersatz = blInfo.getKirchensteuer();
        double kirchensteuerJahr = Steuerabgabenrechner.berechneKirchensteuerJahr(
                lohnsteuerJahr,
                kirchensteuersatz,
                istKirchenmitglied
        );
        double kirchensteuerMonat = kirchensteuerJahr / 12;

        // Steuern gesamt
        double steuernGesamtJahr = lohnsteuerJahr + kirchensteuerJahr;
        double steuernGesamtMonat = lohnsteuerMonat + kirchensteuerMonat;

        double nettoMonat = brutto - steuernGesamtMonat - sozialabgabenGesamt;
        //double nettoJahr =


        // ========================================
        // GGF PRÜFEN: ERGEBNIS AUSGEBEN
        // ========================================


        return new Ergebnis(
                brutto,
                nettoMonat,
                lohnsteuerJahr,
                lohnsteuerMonat,
                kirchensteuerJahr,
                kirchensteuerMonat,
                soliJahr,
                soliMonat,
                //kirchensteuer,
                kvBeitrag,
                rvBeitrag,
                avBeitrag,
                pvBeitrag

        );
    }
}
