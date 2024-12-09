/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.UserDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author princ
 */
@WebServlet("/deleteUser")
public class DeleteUserController extends HttpServlet {

    private UserDao userDao; // Assuming you have a UserDao for database operations

    public void init() {
        userDao = new UserDao(); // Initialize UserDao
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            System.out.println("Attempting to delete user with ID: " + userId);

            boolean deleted = userDao.deleteUser(userId);

            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(Json.createObjectBuilder()
                        .add("success", true)
                        .add("message", "User deleted successfully")
                        .build());
            } else {
                System.out.println("Deletion failed for user ID: " + userId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(Json.createObjectBuilder()
                        .add("success", false)
                        .add("message", "Failed to delete user")
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Error processing request")
                    .build());
        } finally {
            out.close();
        }
    }

}
