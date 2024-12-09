package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.UserDao;
import modal.User;
import modal.Role;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/updateUser")
public class UpdateUserController extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
    }

    // Add support for PUT method
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read JSON from request body
            User updatedUser = gson.fromJson(reader, User.class);

            // Validate email and phone uniqueness
            User existingUser = userDao.getUserById(updatedUser.getUserId());
            
            // Check email uniqueness (if changed)
            if (!existingUser.getEmail().equals(updatedUser.getEmail()) && 
                userDao.isEmailExists(updatedUser.getEmail())) {
                responseJson.addProperty("success", false);
                responseJson.addProperty("message", "Email already exists");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(responseJson.toString());
                return;
            }

            // Check phone uniqueness (if changed)
            if (!existingUser.getPhone().equals(updatedUser.getPhone()) && 
                userDao.isPhoneExists(updatedUser.getPhone())) {
                responseJson.addProperty("success", false);
                responseJson.addProperty("message", "Phone number already exists");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(responseJson.toString());
                return;
            }

            // Update user details while keeping the existing password
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setRole(updatedUser.getRole());

            // Perform update
            boolean updateSuccess = userDao.updateUser(existingUser);
            if (updateSuccess) {
                responseJson.addProperty("success", true);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                responseJson.addProperty("success", false);
                responseJson.addProperty("message", "Failed to update user");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            response.getWriter().write(responseJson.toString());
        } catch (Exception e) {
            responseJson.addProperty("success", false);
            responseJson.addProperty("message", "Error processing request");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(responseJson.toString());
            e.printStackTrace();
        }
    }

    // Optional: Add doPost method to handle POST requests if needed
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPut(request, response);
    }
}