package core;

public class Steuerabgabenrechner {

 /**
  * Verwenden einer inneren Klasse um die Steuerergebnisse zu sparen (Jahr + Monat)
  */



  /**
   * Berechnet die Lohnsteuer für die Klasse I und IV.
   * Berechnet Lohnsteuer nach §32a EStG (progressiv)
   * GIBT JAHRESWERT ZURÜCK!
   * @param bruttoMonat Bruttogehalt monatlich
   * @param grundfreibetrag Grundfreibetrag
   * @param zone1Ende Ende Zone 1
   * @param zone2Ende Ende Zone 2
   * @param zone3Ende Ende Zone 3
   * @param jahr Steuerjahr
   * @return Lohnsteuer PRO JAHR in EUR
   */

  public static double berechneLohnsteuerJahr(
          double bruttoMonat,
          double sozialabgabenMonat,
          double werbekostenpauschale,
          double sonderausgaben,
          double grundfreibetrag,
          double zone1Ende,
          double zone2Ende,
          double zone3Ende,
          int jahr
  ) {
     // Monatliches Brutto → Jährliches zvE
     double bruttoJahr = bruttoMonat * 12;
     double sozialabgabenJahr = sozialabgabenMonat * 12;

     // zvE = Brutto - Sozialabgaben - Werbungskosten - Sonderausgaben
     double zvE = bruttoJahr
             - sozialabgabenJahr
             - werbekostenpauschale
             - sonderausgaben;

     // Sicherstellen, dass das zu versteuernde Einkommen (zVE) nicht negativ wird!
     if (zvE < 0) {
         zvE = 0;
     }

     // Einkommensteuer nach §32a berechnen
     return berechneEinkommensteuer(
             zvE,
             grundfreibetrag,
             zone1Ende,
             zone2Ende,
             zone3Ende,
             jahr
     );
  }

  /*
  Berechnet Lohnsteuer für die Steuerklasse II (Alleinerziehend).
  Mit Entlastungsbetrag für Alleinerziehende nach §24b EStG
   */
  public static double berechneLohnsteuerKlasseII(
          double bruttoMonat,
          double sozialabgabenMonat,
          double werbekostenpauschale,
          double sonderausgaben,
          double entlastungsbetragBasis,
          double entlastungsbetragWeiteres,
          int anzahlKinder,
          double grundfreibetrag,
          double zone1Ende,
          double zone2Ende,
          double zone3Ende,
          int jahr
  ) {
      // Jahreswerte
      double bruttoJahr = bruttoMonat * 12;
      double sozialabgabenJahr = sozialabgabenMonat * 12;

      // Entlastungsbetrag berechnen
      double entlastungsbetrag = 0;
      if (anzahlKinder >= 1) {
          entlastungsbetrag = entlastungsbetragBasis;
          if (anzahlKinder > 1) {
              entlastungsbetrag += (anzahlKinder - 1) * entlastungsbetragWeiteres;
          }
      }

      // zvE mit Entlastungsbetrag
      double zvE = bruttoJahr
              - sozialabgabenJahr
              - werbekostenpauschale
              - sonderausgaben
              - entlastungsbetrag;

      if (zvE < 0)
          zvE = 0;

      return berechneEinkommensteuer(
              zvE,
              grundfreibetrag,
              zone1Ende,
              zone2Ende,
              zone3Ende,
              jahr
      );

  }


  /*
  Berechnet die Lohnsteuer für Steuerklasse III (Ehegattensplitting)
  Das zu versteuernde Einkommen (zvE) wird hier verdoppelt;
  die Steuer halbiert (Splittingtabelle)
   */
  public static double berechneLohnsteuerKlasseIII(
          double bruttoMonat,
          double sozialabgabenMonat,
          double werbekostenpauschale,
          double sonderausgaben,
          double grundfreibetrag,
          double zone1Ende,
          double zone2Ende,
          double zone3Ende,
          int jahr
  )  {
      // Jahreswerte
      double bruttoJahr = bruttoMonat * 12;
      double sozialabgabenJahr = sozialabgabenMonat * 12;

      // zvE berechnen
      double zvE = bruttoJahr
              - sozialabgabenJahr
              - werbekostenpauschale
              - sonderausgaben;

      // Sicherstellen, dass das zvE nicht negativ ist
      if (zvE < 0)
          zvE = 0;

      /*
      Splittingverfahren:
      zvE erst halbieren, Steuer berechnen, dann wieder verdoppeln
       */
      double zvESplitting = zvE / 2;

      // Steuer auf verdoppeltes zvE berechnen
      double steuerSplitting = berechneEinkommensteuer(
              zvESplitting,
              grundfreibetrag,
              zone1Ende,
              zone2Ende,
              zone3Ende,
              jahr
      );

      // Ergebnisse halbieren
      return steuerSplitting * 2;
  }

  /*
  Berechnet Lohnsteuer für die Steuerklasse V
  -> Kein Grundfreibetrag, höhere Vorauszahlung
   */
  public static double berechneLohnsteuerKlasseV(
          double bruttoMonat,
          double sozialabgabenMonat,
          double werbekostenpauschale,
          double sonderausgaben,
          double zone1Ende,
          double zone2Ende,
          double zone3Ende,
          int jahr
  ) {
      // Jahreswerte
      double bruttoJahr = bruttoMonat * 12;
      double sozialabgabenJahr = sozialabgabenMonat * 12;

      // zvE berechnen
      double zvE = bruttoJahr
              - sozialabgabenJahr
              - werbekostenpauschale
              - sonderausgaben;

      // Sicherstellen, dass das zvE nicht <0 ist.
      if (zvE < 0)
          zvE = 0;

      // KEIN Grundfreibetrag bei Klasse V
      return berechneEinkommensteuer(
              zvE,
              0,  // Grundfreibetrag = 0!
              zone1Ende,
              zone2Ende,
              zone3Ende,
              jahr
      );
  }



  /*
  Berechnet Lohnsteuer für Steuerklasse VI (Zweitjob)
  Keine Pauschalen, kein Grundfreibetrag
  */
  public static double berechneLohnsteuerKlasseVI(
          double bruttoMonat,
          double sozialabgabenMonat,
          double zone1Ende,
          double zone2Ende,
          double zone3Ende,
          int jahr
  ) {
      // Jahreswerte
      double bruttoJahr = bruttoMonat * 12;
      double sozialabgabenJahr = sozialabgabenMonat * 12;

      // Keine Pauschalen beim Zweitjob!
      double zvE = bruttoJahr - sozialabgabenJahr;

      if (zvE < 0)
          zvE = 0;

      // Vereinfachung: Flat 42% ab erstem Euro
      return zvE * 0.42;
  }

  /**
   * Berechnet Solidaritätszuschlag mit Freigrenze
   * Seit 2021: Freigrenze + Gleitzone
   *
   * @param lohnsteuerJahr Lohnsteuer pro Jahr
   * @param soliSatz Soli-Satz (5.5%)
   * @param freigrenze Soli Freigrenze (17.543€)
   * @param obergrenze Soli Obergrenze Gleitzone (33.183€)
   * @return Solidaritätszuschlag pro Jahr
   */
  public static double berechneSolidaritaetszuschlag(
          double lohnsteuerJahr,
          double soliSatz,
          double freigrenze,
          double obergrenze
  ) {
      if (lohnsteuerJahr <= freigrenze) {
          return 0;  // Keine Soli

      } else if (lohnsteuerJahr <= obergrenze) {
          // Gleitzone: Ansteigend von 0% bis 5.5%
          double mehrbetrag = lohnsteuerJahr - freigrenze;
          return mehrbetrag * 0.119;  // ~11.9% des Mehrbetrags

      } else {
          // Voller Soli: 5.5%
          return lohnsteuerJahr * soliSatz / 100;
      }
  }



  /**
  * Einkommensteuer-Berechnung nach §32a EStG
  * (Bleibt gleich - gibt Jahreswert zurück)
  */
 private static double berechneEinkommensteuer(
         double zvE,
         double grenze0,
         double grenze1,
         double grenze2,
         double grenze3,
         int jahr
 ) {
  double steuer;

  if (zvE <= grenze0) {
   steuer = 0;

  } else if (zvE <= grenze1) {
   double y = (zvE - grenze0) / 10000;
   if (jahr == 2025) {
    steuer = (883.74 * y + 1400) * y;
   } else {
    steuer = (922.98 * y + 1400) * y;
   }

  } else if (zvE <= grenze2) {
   double z = (zvE - grenze1) / 10000;
   if (jahr == 2025) {
    steuer = (181.19 * z + 2397) * z + 991.51;
   } else {
    steuer = (181.19 * z + 2397) * z + 1025.38;
   }

  } else if (zvE <= grenze3) {
   if (jahr == 2025) {
    steuer = 0.42 * zvE - 10447.12;
   } else {
    steuer = 0.42 * zvE - 10602.13;
   }

  } else {
   if (jahr == 2025) {
    steuer = 0.45 * zvE - 18781.84;
   } else {
    steuer = 0.45 * zvE - 18936.88;
   }
  }

  return steuer;
 }

 /**
  * Berechnet Kirchensteuer (Jahr)
  *
  * @param lohnsteuerJahr Lohnsteuer pro Jahr
  * @param kirchensteuersatz Kirchensteuersatz (8 oder 9)
  * @param istKirchenmitglied true= zahlt Kirchensteuer, false = nicht
  * @return Kirchensteuer PRO JAHR in Euro
  */
 public static double berechneKirchensteuerJahr(
         double lohnsteuerJahr,
         double kirchensteuersatz,
         boolean istKirchenmitglied
 ) {
     if (!istKirchenmitglied) {
         return 0; //= Keine Kirchensteuer
     }
     return lohnsteuerJahr * kirchensteuersatz / 100;
 }



}
