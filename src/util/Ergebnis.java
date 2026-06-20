package util;

/**
 * Daten-Klasse zur Kapselung aller Berechnungsergebnisse eines Gehaltsrechnungslaufs.
 * Wird als einheitlicher Rückgabetyp sowohl vom Vollmodus (Abgabenrechner)
 * als auch vom Projektmodus (FOMGehaltsrechner) verwendet, sodass die GUI-Ausgabe
 * unabhängig vom gewählten Modus identisch implementiert werden kann.
 */

public class Ergebnis {
    private double brutto;
    private double nettoMonat;
    private double lohnsteuerJahr;
    private double lohnsteuerMonat;
    private double kirchensteuerJahr;
    private double kirchensteuerMonat;
    private double soliJahr;
    private double soliMonat;
    private double kvBeitrag;
    private double kvZusatz;
    private double kvBasisBetrag;
    private double kvZusatzBetrag;
    private double rvBeitrag;
    private double avBeitrag;
    private double pvBeitrag;
    // Konstruktor – exakt 12 Parameter, exakt diese Reihenfolge
    public Ergebnis(
            double brutto,
            double nettoMonat,
            double lohnsteuerJahr,
            double lohnsteuerMonat,
            double kirchensteuerJahr,
            double kirchensteuerMonat,
            double soliJahr,
            double soliMonat,
            double kvBeitrag,
            double kvZusatz,
            double kvBasisBetrag,
            double kvZusatzBetrag,
            double rvBeitrag,
            double avBeitrag,
            double pvBeitrag
    ) {
        this.brutto             = brutto;
        this.nettoMonat         = nettoMonat;
        this.lohnsteuerJahr     = lohnsteuerJahr;
        this.lohnsteuerMonat    = lohnsteuerMonat;
        this.kirchensteuerJahr  = kirchensteuerJahr;
        this.kirchensteuerMonat = kirchensteuerMonat;
        this.soliJahr           = soliJahr;
        this.soliMonat          = soliMonat;
        this.kvBeitrag          = kvBeitrag;
        this.kvZusatz           = kvZusatz;
        this.kvBasisBetrag      = kvBasisBetrag;
        this.kvZusatzBetrag     = kvZusatzBetrag;
        this.rvBeitrag          = rvBeitrag;
        this.avBeitrag          = avBeitrag;
        this.pvBeitrag          = pvBeitrag;
    }
    // Getter für jeden Wert
    public double getBrutto()             { return brutto; }
    public double getNettoMonat()         { return nettoMonat; }
    public double getLohnsteuerJahr()     { return lohnsteuerJahr; }
    public double getLohnsteuerMonat()    { return lohnsteuerMonat; }
    public double getKirchensteuerJahr()  { return kirchensteuerJahr; }
    public double getKirchensteuerMonat() { return kirchensteuerMonat; }
    public double getSoliJahr()           { return soliJahr; }
    public double getSoliMonat()          { return soliMonat; }
    public double getKvBeitrag()          { return kvBeitrag; }
    public double getKvZusatz()           { return kvZusatz; }
    public double getKvBasisBetrag()      { return kvBasisBetrag; }
    public double getKvZusatzBetrag()     { return kvZusatzBetrag; }
    public double getRvBeitrag()          { return rvBeitrag; }
    public double getAvBeitrag()          { return avBeitrag; }
    public double getPvBeitrag()          { return pvBeitrag; }

}
