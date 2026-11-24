package ma.emsi.akailiyoussra.outil.meteo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

/**
 * Outils météo pour le LLM :
 * - isPluiePrevue : risque de pluie pendant une période, pour un lieu donné par sa latitude et sa longitude ;
 * - getCoordonnees : latitude et longitude d'une ville donnée par son nom.
 */
public class MeteoTool {

    @Tool("""
    Retourne true si la probabilité d'avoir de la pluie pour la ville dont on donne la latitude et la longitude
    est importante durant une période de nbJours jours. La période commence nbJourDebut jours après aujourd'hui.
    Par exemple, si nbJoursDebut = 1 et nbJours = 2, la période de 2 jours commence demain (1 jour après aujourd'hui).
    Si nbJoursDebut = 0 et nbJours = 3, la période de 3 jours commence aujourd'hui.
    Retourne false sinon.
    """)
    public boolean isPluiePrevue(
            @P(value = "Latitude", name = "latitude") double latitude,
            @P(value = "Longitude", name = "longitude") double longitude,
            @P(value = "Nombre de jours après aujourd'hui pour indiquer le début de la période", name = "nbJoursDebut") int nbJoursDebut,
            @P(value = "Nombre de jours de la période", name = "nbJours") int nbJours) {
        // Seuil au-delà duquel la probabilité d'avoir de la pluie est suffisante pour prendre un parapluie.
        // A modifier éventuellement, en particulier pour faire des tests.
        double seuilPluie = 25.0;

        try {
            // Construire l'URL de l'API météo en utilisant les coordonnées et la période spécifiée
            String baseUrl = "https://api.open-meteo.com/v1/forecast";
            int finPeriode = Math.min(nbJoursDebut + nbJours, 7); // L'API fournit des prévisions pour 7 jours maximum
            String apiUri = String.format(Locale.US,
                    "%s?latitude=%f&longitude=%f&daily=precipitation_probability_max&forecast_days=%d",
                    baseUrl, latitude, longitude, finPeriode);

            // Ouvrir une connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            // Lire la réponse
            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Parser la réponse JSON
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

//            System.out.println("Réponse de l'API météo : " + response);

            // Extraire le tableau precipitation_probability_max
            JsonObject daily = jsonResponse.getAsJsonObject("daily");
            int[] precipitationProbabilities = gson.fromJson(
                    daily.get("precipitation_probability_max"),
                    int[].class
            );

            // Vérifier si au moins une probabilité dépasse le seuil pendant la période spécifiée
            for (int i = nbJoursDebut; i < finPeriode; i++) {
                if (precipitationProbabilities[i] > seuilPluie) {
                    return true;
                }
            }
            // Si aucune probabilité de pluie ne dépasse le seuil, on retourne false
            return false;
        } catch (IOException e) {
            // En cas d'erreur, on considère qu'on ne peut pas déterminer la météo
            throw new RuntimeException("Erreur lors de la récupération de la météo pour ("
                    + latitude + ", " + longitude + ") : " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Tool("""
    Retourne les coordonnées géographiques d'une ville dont on donne le nom :
    un tableau de 2 nombres, la latitude puis la longitude.
    Utilise cet outil quand l'utilisateur donne le nom d'une ville et que tu as besoin
    de sa latitude et de sa longitude (par exemple pour connaître la météo).
    Pour lever une ambiguïté, le nom peut être suivi du pays, par exemple "Rabat,Morocco".
    """)
    public double[] getCoordonnees(
            @P(value = "Nom de la ville, éventuellement suivi d'une virgule et du nom du pays en anglais",
                    name = "ville") String ville) {
        try {
            String baseUrl = "https://geocoding-api.open-meteo.com/v1/search?name=";
            String encodedVille = URLEncoder.encode(ville, StandardCharsets.UTF_8);
            String apiUri = baseUrl + encodedVille;

            HttpURLConnection connection = (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8);
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

//            System.out.println("Réponse de l'API de géocodage : " + response);

            // Parser la réponse JSON (on prend la première correspondance trouvée pour la ville)
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            // Récupérer le tableau "results"
            if (jsonResponse.has("results")) {
                JsonArray results = jsonResponse.getAsJsonArray("results");

                if (!results.isEmpty()) {
                    // Récupérer le 1er élément du tableau results
                    JsonObject premierResultat = results.get(0).getAsJsonObject();

                    double latitude = premierResultat.get("latitude").getAsDouble();
                    double longitude = premierResultat.get("longitude").getAsDouble();
                    return new double[]{latitude, longitude};
                }
            }

            throw new RuntimeException("Aucune coordonnée trouvée pour la ville : " + ville);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la récupération des coordonnées pour la ville : " + ville, e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}