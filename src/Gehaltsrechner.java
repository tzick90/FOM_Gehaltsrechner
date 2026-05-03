public class Gehaltsrechner {

 /**
  * Verwenden einer inneren Klasse um die Steuerergebnisse zu sparen (Jahr + Monat)
  */

 public static class Steuererbnis {
   private double lohnsteuerJahr;
   private double lohnsteuerMonat;
   private double kirchensteuerJahr;
   private double kirchensteuerMonat;

   public Steuererbnis(
           double lohnsteuerJahr,
           double kirchensteuerJahr
   ) {
    this.kirchensteuerJahr = lohnsteuerJahr;
    this.lohnsteuerMonat = lohnsteuerJahr / 12;
    this.kirchensteuerJahr = kirchensteuerJahr;
    this.kirchensteuerMonat = kirchensteuerJahr / 12;
   }

   public double getLohnsteuerJahr() { return lohnsteuerJahr; }
   public double getLohnsteuerMonat() {return lohnsteuerMonat;}

   public double getKirchensteuerJahr() { return kirchensteuerJahr; }
   public double getKirchensteuerMonat() { return kirchensteuerMonat; }

   // Berechne die Summen
   public double getSteuernGesamtJahr() {
     return lohnsteuerJahr + kirchensteuerJahr;
   }
   public double getSteuernGesamtMonat() {
     return lohnsteuerMonat + kirchensteuerMonat;
   }

   @Override
   public String toString() {
     return String.format(
             "Lohnsteuer: %.2f€/Jahr (%.2f€/Monat), Kirchensteuer: %.2f€/Jahr (%.2f€/Monat)",
             lohnsteuerJahr, lohnsteuerMonat, kirchensteuerJahr, kirchensteuerMonat
     );
   }
 }

  /**
   *Berechnet Lohnsteuer nach §32a EStG (progressiv)
   *GIBT JAHRESWERT ZURÜCK!
   *@param bruttoMonat Bruttogehalt monatlich
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
          double grundfreibetrag,
          double zone1Ende,
          double zone2Ende,
          double zone3Ende,
          int jahr
  ) {
     // Monatliches Brutto → Jährliches zvE
     double bruttoJahr = bruttoMonat * 12;
     double sozialabgabenJahr = sozialabgabenMonat * 12;

     // Werbekostenpauschale (gilt für 2025, 2026) später durch user settings holbar
     final double WERBEKOSTEN_PAUSCHALE = 1230;

     // Sonderausgaben-Pauschale (gilt für 2025,2026) -> siehe werbekosten-p.
     final double SONDERAUSGABEN_PAUSCHALE = 36;

     // zvE = Brutto - Sozialabgaben - Werbungskosten - Sonderausgaben
     double zvE = bruttoJahr
             - sozialabgabenJahr
             - WERBEKOSTEN_PAUSCHALE
             - SONDERAUSGABEN_PAUSCHALE;

     // Sicherstellen, dass das zuversteuernde Einkommen (zVE) nicht negativ wird!
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
