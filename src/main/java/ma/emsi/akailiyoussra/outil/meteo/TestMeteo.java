package ma.emsi.akailiyoussra.outil.meteo;

/**
 * Teste la classe MeteoTool sans passer par le LLM.
 * Ville choisie : Rabat (latitude 34.02, longitude -6.83).
 */
public class TestMeteo {

    public static void main(String[] args) {
        MeteoTool meteo = new MeteoTool();

        double latitude = 34.02;
        double longitude = -6.83;

        // Période de 3 jours commençant aujourd'hui
        boolean pluie1 = meteo.isPluiePrevue(latitude, longitude, 0, 3);
        System.out.println("Rabat, à partir d'aujourd'hui, 3 jours : pluie prévue ? " + pluie1);

        // Période de 3 jours commençant demain
        boolean pluie2 = meteo.isPluiePrevue(latitude, longitude, 1, 3);
        System.out.println("Rabat, à partir de demain, 3 jours : pluie prévue ? " + pluie2);

        // Seulement aujourd'hui
        boolean pluie3 = meteo.isPluiePrevue(latitude, longitude, 0, 1);
        System.out.println("Rabat, aujourd'hui seulement : pluie prévue ? " + pluie3);
    }
}