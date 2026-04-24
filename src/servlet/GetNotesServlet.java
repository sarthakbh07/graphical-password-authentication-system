import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/getNotes")
public class GetNotesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Retrieve username from session
        String username = (String) request.getSession().getAttribute("username");

        JsonObject jsonResponse = new JsonObject();

        if (username != null) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/graphical_password_auth?useUnicode=true&characterEncoding=UTF-8", "root", "ayush")) {
                // Get user ID
                PreparedStatement userIdStmt = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
                userIdStmt.setString(1, username);
                ResultSet userIdResult = userIdStmt.executeQuery();

                if (userIdResult.next()) {
                    int userId = userIdResult.getInt("id");

                    // Fetch notes for the user
                    PreparedStatement notesStmt = connection.prepareStatement("SELECT note_text FROM user_notes WHERE user_id = ?");
                    notesStmt.setInt(1, userId);
                    ResultSet notesResult = notesStmt.executeQuery();

                    List<JsonObject> notesList = new ArrayList<>();

                    // Populate the notes list
                    while (notesResult.next()) {
                        JsonObject note = new JsonObject();
                        note.addProperty("note_text", notesResult.getString("note_text"));
                        notesList.add(note);
                    }

                    // Prepare JSON response
                    jsonResponse.addProperty("success", true);
                    jsonResponse.add("notes", new Gson().toJsonTree(notesList));
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "User not found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Database error: " + e.getMessage());
            }
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "User not logged in.");
        }

        out.println(new Gson().toJson(jsonResponse));
        out.close();
    }
}

