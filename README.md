# 🔐 Graphical Password Authentication System

A Java-based web application that replaces traditional passwords with graphical image-based authentication. Users sign up by selecting images in a specific sequence and authenticate themselves by reproducing that sequence.

---

##  Features

- 👤 Graphical Sign-Up and Login
- 🖼️ Dynamic image grid with randomization
- 🔐 Session-based user management
- 🗄️ MySQL database integration
- 🧪 Lightweight and easy to test

---

## 📁 Project Structure

graphical-auth/
├── css/ # Stylesheets
├── images/ # Contains images for grid (1.jpg - 30.jpg)
├── index.html # Main page with login and signup UI
├── js/ # JavaScript logic for image grid
│ └── imageGrid.js
├── notes.html # Dummy dashboard post-login
├── WEB-INF/ # Servlet and web.xml config
│ ├── web.xml
│ └── classes/
│ ├── LoginServlet.java
│ └── SignupServlet.java
└── database/
└── schema.sql # MySQL schema and table structure

---

## 🛠️ Requirements

- Java JDK 8+
- Apache Tomcat 9+
- MySQL 5.7+ or 8+
- MySQL Connector/J (`mysql-connector-java-x.x.xx.jar`)
- Gson Library for JSON parsing
- Web Browser (Chrome/Firefox)
- Git

---

## 🚀 Setup Instructions

### 1. Clone the Repository


git clone https://github.com/ayush528/graphical-auth.git
cd graphical-auth
### 2. Configure the Database

Start MySQL and create the database:

mysql -u root -p

Then in the MySQL prompt:

CREATE DATABASE graphical_password_auth;
USE graphical_password_auth;
SOURCE database/schema.sql;
### 3. Configure Backend (Java Servlets)

    Update DB credentials in both LoginServlet.java and SignupServlet.java:

DriverManager.getConnection("jdbc:mysql://localhost:3306/graphical_password_auth", "root", "your_mysql_password");

Compile the servlets:

javac -cp ".:/path/to/tomcat/lib/servlet-api.jar:/path/to/mysql-connector-java.jar" WEB-INF/classes/*.java

    Note: On Windows, use ; instead of : for classpath separators.

Place compiled .class files back into WEB-INF/classes/.

## 4. Deploy to Tomcat

    Copy the graphical-auth/ folder into Tomcat’s webapps/ directory.

    Restart Tomcat.

## 5. Run the App

Visit:

http://localhost:8080/graphical-auth/index.html

🧪 How It Works

    Sign-Up: User enters username and selects 3 images.

    Login: User enters username, system fetches stored images and mixes them with random ones. The user must pick the original images in any order.

    Authentication: If selections match stored data, login is successful.

🛡️ Notes

    Image patterns are stored as comma-separated values in the database.

    Avoid using this method in real-world secure systems — it's a demo.

    You may add a favicon.ico to remove browser warnings.


