package ma.emsi.akailiyoussra;

/**
 * Service IA pour le test 7 : l'implémentation est fournie par LangChain4j (AiServices).
 * Le LLM pourra utiliser l'outil météo (MeteoTool) s'il en a besoin.
 */
public interface AssistantMeteo {
    String chat(String question);
}