package ma.emsi.akailiyoussra;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
// Mis en commentaire pour le test 5 (dépendance du modèle intégré en commentaire dans pom.xml)
// import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;

/**
 * Test 4 : embeddings.
 * Calcule la similarité cosinus entre les embeddings de couples de phrases,
 * avec 2 modèles : le modèle intégré (local) et un modèle externe de Google.
 */
public class Test4Embeddings {

    /**
     * Couples de phrases à comparer.
     */
    private static final String[][] COUPLES = {
            // 1. Même sens
            {"Le chat dort sur le canapé.", "Le félin fait la sieste sur le sofa."},
            // 2. Sens opposés
            {"Il fait très chaud aujourd'hui.", "Il fait très froid aujourd'hui."},
            // 3. Sujets sans rapport
            {"J'aime programmer en Java.", "La tour Eiffel se trouve à Paris."},
            // 4. Même sens, langues différentes
            {"Bonjour, comment vas-tu ?", "Hello, how are you?"},
            // 5. Même domaine, sens différent
            {"Spring Boot simplifie le développement d'API REST.", "Angular est un framework pour le frontend."},
            // 6. Même sens, en anglais
            {"The weather is nice today.", "It is a beautiful sunny day."}
    };

    public static void main(String[] args) {
        // 1. Modèle d'embeddings intégré (local, gratuit, entraîné surtout sur de l'anglais)
        // Mis en commentaire pour le test 5 : sa dépendance est en commentaire dans pom.xml
        // (conflit avec le modèle d'embeddings utilisé par easy-rag).
        // EmbeddingModel modeleIntegre = new BgeSmallEnV15EmbeddingModel();
        // System.out.println("===== Modèle intégré : BgeSmallEnV15 =====");
        // afficherSimilarites(modeleIntegre);

        // 2. Modèle d'embeddings externe de Google
        EmbeddingModel modeleGoogle = GoogleGenAiEmbeddingModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-embedding-001")
                // Type de tâche : on veut calculer une similarité
                .taskType(GoogleGenAiEmbeddingModel.TaskTypeEnum.SEMANTIC_SIMILARITY)
                // Le prof conseille de commencer à 100 ms puis d'augmenter si ça ne suffit pas
                .timeout(Duration.ofSeconds(10))
                .build();
        System.out.println();
        System.out.println("===== Modèle externe : gemini-embedding-001 =====");
        afficherSimilarites(modeleGoogle);
    }

    /**
     * Affiche la similarité cosinus de chaque couple de phrases avec le modèle donné.
     *
     * @param modele le modèle d'embeddings utilisé.
     */
    private static void afficherSimilarites(EmbeddingModel modele) {
        for (String[] couple : COUPLES) {
            // content() récupère l'embedding dans la réponse du modèle
            Embedding embedding1 = modele.embed(couple[0]).content();
            Embedding embedding2 = modele.embed(couple[1]).content();

            double similarite = CosineSimilarity.between(embedding1, embedding2);

            System.out.printf("%.4f  |  %s  <->  %s%n", similarite, couple[0], couple[1]);
        }
    }
}