/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.UserDao;
import modal.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;


@WebServlet("/allUsers")
public class GetAllUsersController extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        // Initialize the UserDao
        userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Fetch all users from the database
        List<User> users = userDao.getAllUsers();

        // Convert the list of users to JSON format using Gson
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        // Convert each user to JSON and add to the JsonArray
        for (User user : users) {
            JsonObject jsonUser = new JsonObject();
            jsonUser.addProperty("userId", user.getUserId());
            jsonUser.addProperty("email", user.getEmail());
            jsonUser.addProperty("phone", user.getPhone());
            jsonUser.addProperty("role", user.getRole().toString());
            jsonArray.add(jsonUser);
        }

        // Write the JSON array to the response
        response.getWriter().write(jsonArray.toString());
    }

    @Override
    public void destroy() {
        // Clean up resources (if needed)
        userDao = null;
    }
}