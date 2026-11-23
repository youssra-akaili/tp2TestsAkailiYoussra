package ma.emsi.akailiyoussra;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.output.TokenUsage;

/**
 * Test 2 : coût en tokens d'une requête.
 * Affiche la réponse, le nombre de tokens utilisés et le coût en dollars.
 */
public class Test2Couts {

    // Prix en dollars pour 1 million de tokens (A VERIFIER sur la page indiquée dans le TP)
    private static final double PRIX_INPUT_PAR_MILLION = 0.10;
    private static final double PRIX_OUTPUT_PAR_MILLION = 0.40;

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // Même question que pour le test 1
        String question = "Quelle est la capitale du Maroc ?";

        // chat(ChatMessage...) retourne un ChatResponse qui contient les nombres de tokens
        ChatResponse reponse = modele.chat(UserMessage.from(question));

        System.out.println("Question : " + question);
        System.out.println("Réponse : " + reponse.aiMessage().text());
        System.out.println("----------------------------------------");

        // Nombres de tokens
        TokenUsage tokens = reponse.tokenUsage();
        int tokensInput = tokens.inputTokenCount();
        int tokensOutput = tokens.outputTokenCount();
        System.out.println("Tokens en entrée (input)  : " + tokensInput);
        System.out.println("Tokens en sortie (output) : " + tokensOutput);
        System.out.println("Total des tokens          : " + tokens.totalTokenCount());

        // Coût en dollars de la requête
        double cout = tokensInput * PRIX_INPUT_PAR_MILLION / 1_000_000
                + tokensOutput * PRIX_OUTPUT_PAR_MILLION / 1_000_000;
        System.out.printf("Coût de la requête : %.8f $%n", cout);

        // Nombre de requêtes similaires pour dépenser 1 dollar
        long nbRequetes = (long) (1.0 / cout);
        System.out.println("Nombre de requêtes similaires pour dépenser 1 $ : " + nbRequetes);
    }
}