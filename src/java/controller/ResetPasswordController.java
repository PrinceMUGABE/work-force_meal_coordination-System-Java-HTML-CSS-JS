package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modal.User;
import dao.UserDao;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "ResetPasswordController", urlPatterns = {"/resetPassword"})
public class ResetPasswordController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         
        String email = request.getParameter("email");
        String newPassword = request.getParameter("new_password");
        String confirmPassword = request.getParameter("confirm_password");

        // Check for empty email, new password, and confirm password fields
        if (email == null || newPassword == null || confirmPassword == null ||
            email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"All fields are required.\"}");
            return;
        }

        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Passwords do not match.\"}");
            return;
        }

        // Retrieve the user by email
        UserDao userDao = new UserDao();  // assuming you have a valid constructor for UserDao
        User user = userDao.getUserByEmail(email);

        if (user == null) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"User not found.\"}");
            return;
        }

        // Hash the new password and update it in the database
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // Set the new password in the user object
        user.setPassword(hashedPassword);

        // Update the user in the database
        boolean updated = userDao.updateUser(user);

        if (updated) {
            // Password updated successfully, redirect to login page
            response.sendRedirect("login.html"); // Send user to login page
        } else {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Failed to reset password.\"}");
        }
    }

    @Override
    public String getServletInfo() {
        return "Reset Password Controller";
    }
}
