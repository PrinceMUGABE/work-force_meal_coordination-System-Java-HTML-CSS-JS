package controller;

import dao.UserDao;
import modal.User;
import modal.Role;
import utils.PasswordGenerator;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/restaurantManager/createUser")
public class RestaurantManagerAddUser extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() {
        userDao = new UserDao(); // Initialize DAO
    }

@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    System.out.println("Request received at /restaurantManager/createUser");

    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String roleString = request.getParameter("role");

    // Display the values on the terminal (console)
    System.out.println("Email: " + email);
    System.out.println("Phone: " + phone);
    System.out.println("Role: " + roleString);

    // Null checks for email and phone
    if (email == null || phone == null || roleString == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Missing required fields.\"}");
        return;
    }

    // Validate email and phone for duplicates
    if (userDao.isEmailExists(email)) {
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Email already in use. Please choose another.\"}");
        return;
    }

    if (userDao.isPhoneExists(phone)) {
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Phone number already in use. Please choose another.\"}");
        return;
    }

    // Generate secure password
    String generatedPassword = PasswordGenerator.generatePassword(8);
    String hashedPassword = BCrypt.hashpw(generatedPassword, BCrypt.gensalt());
    Role role = Role.valueOf(roleString.toUpperCase());

    // Create user object
    User user = new User();
    user.setEmail(email);
    user.setPhone(phone);
    user.setPassword(hashedPassword);
    user.setRole(role);

    // Save user to database
    userDao.createUser(user);
    
    response.sendRedirect("manageUsers.html");


    
    
}

    // Utility method to hash a password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
