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

/**
 * Test 5 : RAG 1.
 * Le LLM répond en utilisant les informations du fichier infos.txt.
 */
public class Test5Rag1 {

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

        // 1. Chargement du fichier (à la racine du projet)
        Document document = FileSystemDocumentLoader.loadDocument("infos.txt");

        // 2. Base de données vectorielle en mémoire
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 3. Ingestion : découpage en segments, calcul des embeddings, enregistrement dans la base
        //    (easy-rag fournit un découpage et un modèle d'embeddings par défaut)
        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        // 4. Création de l'assistant avec une mémoire et un "content retriever"
        //    qui va chercher dans la base les segments les plus proches de la question
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(modele)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        // 5. Question
        // Lancement 1 : "Comment s'appelle le chat de Pierre ?"
        // Lancement 2 : "Pierre appelle son chat. Qu'est-ce qu'il pourrait dire ?"
        // Lancement 3 (avec Marseille dans infos.txt) : "Quelle est la capitale de la France ?"
        String question = "Comment s'appelle le chat de Pierre ?";
        System.out.println("Question : " + question);
        System.out.println("Réponse : " + assistant.chat(question));
    }
}