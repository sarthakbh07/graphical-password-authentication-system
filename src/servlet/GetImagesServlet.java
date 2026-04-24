import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/getImages")
public class GetImagesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        String line;

        // Read the JSON body from the request
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // Parse the JSON string
        JsonObject jsonObject = new Gson().fromJson(sb.toString(), JsonObject.class);
        String username = jsonObject.get("username").getAsString();

        JsonObject jsonResponse = new JsonObject();

        if (username == null || username.isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Username is required.");
            out.println(new Gson().toJson(jsonResponse));
            return;
        }

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/graphical_password_auth?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC", "root", "ayush")) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT image_pattern FROM users WHERE username = ?");
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String imagePattern = resultSet.getString("image_pattern");
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("imagePattern", imagePattern); // Send back the image pattern
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "User not found.");
                }

                resultSet.close();
                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
        }

        out.println(new Gson().toJson(jsonResponse));
        out.close();
    }
}

