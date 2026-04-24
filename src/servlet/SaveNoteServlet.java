import com.google.gson.Gson;
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

@WebServlet("/saveNote")
public class SaveNoteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String username = (String) request.getSession().getAttribute("username");
        String noteText = request.getParameter("noteText");

        JsonObject jsonResponse = new JsonObject();

        if (username != null && noteText != null && !noteText.trim().isEmpty()) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/graphical_password_auth?useUnicode=true&characterEncoding=UTF-8", "root", "ayush")) {
                // Get user ID
                PreparedStatement userIdStmt = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
                userIdStmt.setString(1, username);
                ResultSet userIdResult = userIdStmt.executeQuery();

                if (userIdResult.next()) {
                    int userId = userIdResult.getInt("id");

                    // Insert new note
                    PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO user_notes (user_id, note_text) VALUES (?, ?)");
                    insertStmt.setInt(1, userId);
                    insertStmt.setString(2, noteText);
                    insertStmt.executeUpdate();

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Note saved successfully.");
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
            jsonResponse.addProperty("message", "Note text is required.");
        }

        out.println(new Gson().toJson(jsonResponse));
        out.close();
    }
}

