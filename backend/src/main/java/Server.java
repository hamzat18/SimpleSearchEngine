import static spark.Spark.*;
import com.google.gson.Gson;
import javax.servlet.MultipartConfigElement;
import java.io.InputStream;
import java.io.File;

//main backend server
public class Server {
    public static void main(String[] args) {
        InvertedIndex index = new InvertedIndex();

        // Server Config
        port(5500);

        // PATH Confi
        File frontendDir = new File("../frontend");
        if (!frontendDir.exists()) {
            frontendDir = new File("frontend");
        }

        staticFiles.externalLocation(frontendDir.getAbsolutePath());

        // Upload
        post("/api/upload", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            int count = 0;
            // loop through all the uploaded files
            for (javax.servlet.http.Part part : req.raw().getParts()) {
                if (part.getSubmittedFileName() != null && part.getSize() > 0) {
                    // parse document
                    try (InputStream is = part.getInputStream()) {
                        String content = DocumentParser.parse(is, part.getSubmittedFileName());
                        index.addDocument(part.getSubmittedFileName(), content);
                        count++;
                    }
                }
            }
            return "Successfully indexed " + count + " documents.";
        });

        // Search
        get("/api/search", (req, res) -> {
            res.type("application/json");
            String query = req.queryParams("q");
            if (query == null || query.trim().isEmpty())
                return "[]";
            // convert to JSON
            return new Gson().toJson(index.search(query));
        });

        System.out.println("=======================================");
        System.out.println("Server running at http://localhost:5500");
        System.out.println("Frontend loaded from: " + frontendDir.getAbsolutePath());
        System.out.println("=======================================");
    }
}