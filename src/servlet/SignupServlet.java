import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.sql.SQLException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        // Read the JSON data from the request body
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        // Parse the JSON string
        String jsonString = jsonBuilder.toString();
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Extract username and image pattern from JSON
        String username = jsonObject.get("username").getAsString();
        String imagePattern = jsonObject.get("imagePattern").getAsString();

        JsonObject jsonResponse = new JsonObject();
        
        // Validate inputs
        if (username == null || username.isEmpty() || imagePattern == null || imagePattern.isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Username and image pattern are required.");
            out.println(new Gson().toJson(jsonResponse));
            return;
        }

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a database connection
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/graphical_password_auth?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC", "root", "ayush")) {
                // Check if username already exists
                String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
                try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                    checkStatement.setString(1, username);
                    try (ResultSet resultSet = checkStatement.executeQuery()) {
                        if (resultSet.next() && resultSet.getInt(1) > 0) {
                            // If username exists, return an error
                            jsonResponse.addProperty("success", false);
                            jsonResponse.addProperty("message", "Username already exists. Please choose a different username.");
                            out.println(new Gson().toJson(jsonResponse));
                            return;
                        }
                    }
                }

                // Prepare the SQL query for user insertion
                String insertQuery = "INSERT INTO users (username, image_pattern) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, imagePattern);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("message", "User created successfully.");
                    } else {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Failed to create user.");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "JDBC Driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
        } finally {
            out.println(new Gson().toJson(jsonResponse));
            out.close();
        }
    }
}

