package ma.emsi.akailiyoussra;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Test 3 : un traducteur.
 * Utilise PromptTemplate (modèle de prompt avec des variables) et Prompt (le prompt rempli).
 */
public class Test3Traducteur {

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        // ===== 1. Template avec 1 variable : le texte à traduire =====
        PromptTemplate template1 = PromptTemplate.from(
                "Traduis le texte suivant en anglais : {{texte}}");

        Map<String, Object> variables1 = new HashMap<>();
        variables1.put("texte", "Bonjour, je suis étudiante en ingénierie informatique à Rabat.");

        // Remplace {{texte}} par sa valeur
        Prompt prompt1 = template1.apply(variables1);

        System.out.println("Prompt envoyé : " + prompt1.text());
        System.out.println("Réponse : " + modele.chat(prompt1.text()));
        System.out.println("----------------------------------------");

        // ===== 2. Template avec 2 variables : la langue et le texte =====
        PromptTemplate template2 = PromptTemplate.from(
                "Traduis le texte suivant en {{langue}} : {{texte}}");

        Map<String, Object> variables2 = new HashMap<>();
        variables2.put("langue", "espagnol");
        variables2.put("texte", "Le café du matin est le meilleur moment de la journée.");

        Prompt prompt2 = template2.apply(variables2);

        System.out.println("Prompt envoyé : " + prompt2.text());
        System.out.println("Réponse : " + modele.chat(prompt2.text()));
    }
}