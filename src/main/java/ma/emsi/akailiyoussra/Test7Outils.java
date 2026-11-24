package ma.emsi.akailiyoussra;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import ma.emsi.akailiyoussra.outil.meteo.MeteoTool;

import java.util.Scanner;

/**
 * Test 7 : utilisation d'outils.
 * Le LLM peut appeler les outils de MeteoTool (géolocalisation et prévisions de pluie).
 * Le logging des requêtes et réponses permet de voir les échanges JSON avec le LLM.
 */
public class Test7Outils {

    public static void main(String[] args) {
        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .logRequests(true)   // Logging des requêtes envoyées au LLM
                .logResponses(true)  // Logging des réponses du LLM
                .build();

        // Assistant qui peut utiliser les outils météo s'il le souhaite
        AssistantMeteo assistant =
                AiServices.builder(AssistantMeteo.class)
                        .chatModel(modele)
                        .chatMemory(MessageWindowChatMemory.withMaxMessages(20)) // L'utilisation des outils génère des messages
                        .tools(new MeteoTool())  // Ajout des outils
                        .build();

        conversationAvec(assistant);
    }

    /**
     * Boucle de conversation avec l'assistant.
     * Taper "fin" pour arrêter.
     *
     * @param assistant l'assistant IA.
     */
    private static void conversationAvec(AssistantMeteo assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.println("Posez votre question (ou \"fin\" pour arrêter) : ");
                String question = scanner.nextLine();
                if (question.isBlank()) {
                    continue;
                }
                if ("fin".equalsIgnoreCase(question.trim())) {
                    break;
                }
                System.out.println("==================================================");
                String reponse = assistant.chat(question);
                System.out.println("Assistant : " + reponse);
            }
        }
    }
}