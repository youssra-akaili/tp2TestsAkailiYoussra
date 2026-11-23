package ma.emsi.akailiyoussra;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

/**
 * Test 1 : pour se chauffer.
 * Pose des questions simples à Gemini en passant par LangChain4j.
 */
public class Test1 {

    public static void main(String[] args) {
        // Clé secrète lue dans la variable d'environnement (jamais dans le code)
        String cle = System.getenv("GEMINI_KEY");

        // Création du modèle avec le pattern "builder"
        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        // 1. Question simple
        String question1 = "Quelle est la capitale du Maroc ?";
        System.out.println("Question : " + question1);
        System.out.println("Réponse : " + modele.chat(question1));
        System.out.println("----------------------------------------");

        // 2. L'heure qu'il est
        String question2 = "Quelle heure est-il ?";
        System.out.println("Question : " + question2);
        System.out.println("Réponse : " + modele.chat(question2));
        System.out.println("----------------------------------------");

        // 3. Se présenter, puis demander son nom (2 requêtes séparées)
        String question3 = "Bonjour, je m'appelle Youssra Akaili.";
        System.out.println("Question : " + question3);
        System.out.println("Réponse : " + modele.chat(question3));

        String question4 = "Comment je m'appelle ?";
        System.out.println("Question : " + question4);
        System.out.println("Réponse : " + modele.chat(question4));
    }
}