package ma.emsi.akailiyoussra;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.Scanner;

/**
 * Test 6 : RAG 2.
 * Le LLM répond en utilisant le contenu d'un PDF (support de cours sur le machine learning).
 * Conversation avec plusieurs questions dans la même session.
 */
public class Test6Rag2 {

    /**
     * Nom du fichier PDF, à la racine du projet (A ADAPTER si besoin).
     */
    private static final String FICHIER_PDF = "ml.pdf";

    /**
     * Service IA : l'implémentation de cette interface est fournie par LangChain4j (AiServices).
     */
    interface Assistant {
        String chat(String question);
    }

    public static void main(String[] args) {
        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        // 1. Chargement du PDF (easy-rag utilise Apache Tika pour lire le PDF)
        System.out.println("Chargement et découpage du PDF, patientez...");
        Document document = FileSystemDocumentLoader.loadDocument(FICHIER_PDF);

        // 2. Base de données vectorielle en mémoire
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 3. Ingestion : le PDF est découpé en morceaux (segments),
        //    les embeddings des segments sont calculés et enregistrés dans la base
        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        // 4. Assistant avec mémoire (pour la conversation) et RAG
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(modele)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        // 5. Conversation : plusieurs questions dans la même session
        conversationAvec(assistant);
    }

    /**
     * Boucle de conversation avec l'assistant.
     * Taper "fin" pour arrêter.
     *
     * @param assistant l'assistant IA.
     */
    private static void conversationAvec(Assistant assistant) {
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