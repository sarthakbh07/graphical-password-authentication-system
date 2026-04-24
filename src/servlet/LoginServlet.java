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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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
        String imagePattern = jsonObject.get("imagePattern").getAsString();

        JsonObject jsonResponse = new JsonObject();

        if (username == null || imagePattern == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Username and image pattern are required.");
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
                    String dbImagePattern = resultSet.getString("image_pattern");

                    if (imagePattern.equals(dbImagePattern)) {
                        // Set session attribute for username
                        request.getSession().setAttribute("username", username);

                        // Respond with success message and redirect URL
                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("redirect", "notes.html");
                    } else {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Invalid image pattern.");
                    }
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

