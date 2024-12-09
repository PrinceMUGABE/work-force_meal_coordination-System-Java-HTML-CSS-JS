package controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dao.FoodDAO;
import dao.UserDao;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import modal.Food;
import modal.User;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebServlet("/lecturer/food/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class LecturerManageFoods extends HttpServlet {

    private FoodDAO foodDAO;
    private UserDao userDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        foodDAO = new FoodDAO();
        userDAO = new UserDao();
        gson = new Gson();
    }

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    // Debug logging
    System.out.println("Request Path Info: " + request.getPathInfo());
    System.out.println("Request Servlet Path: " + request.getServletPath());
    System.out.println("Request Request URI: " + request.getRequestURI());

    String pathInfo = request.getPathInfo();

    // Ensure user is logged in
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
        sendErrorResponse(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    User currentUser = (User) session.getAttribute("user");

    // Modified to handle both "/create" and empty path cases
    if (pathInfo == null || "/create".equals(pathInfo)) {
         if (pathInfo == null || "/create".equals(pathInfo)) {
            createFood(request, response, currentUser);
        } else {
            sendErrorResponse(response, "Invalid endpoint", HttpServletResponse.SC_BAD_REQUEST);
        }
    } else {
        sendErrorResponse(response, "Invalid endpoint", HttpServletResponse.SC_BAD_REQUEST);
    }
}




      @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if ("/list".equals(pathInfo)) {
            listFoods(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/image/")) {
            getFoodImage(request, response);
        } else {
            sendErrorResponse(response, "Invalid endpoint", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void getFoodImage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String[] pathParts = request.getPathInfo().split("/");
            if (pathParts.length < 3) {
                sendErrorResponse(response, "Invalid food ID", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int foodId = Integer.parseInt(pathParts[2]);
            Food food = foodDAO.getFoodbyId(foodId);

            if (food == null || food.getImage() == null) {
                sendErrorResponse(response, "Food or image not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("image/jpeg");  // Adjust based on actual image type
            try (OutputStream out = response.getOutputStream()) {
                out.write(food.getImage());
            }
        } catch (Exception e) {
            sendErrorResponse(response, "Error retrieving food image: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

     private void listFoods(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Food> foods = foodDAO.getAllFood();
        JsonArray jsonArray = new JsonArray();

        for (Food food : foods) {
            JsonObject jsonFood = new JsonObject();
            jsonFood.addProperty("id", food.getId());
            jsonFood.addProperty("name", food.getName());
            jsonFood.addProperty("amount", food.getAmmount());
            jsonFood.addProperty("createdBy", food.getCreatedBy().getUserId());
            
            // Add image as Base64 if present
            if (food.getImage() != null) {
                jsonFood.addProperty("image", Base64.getEncoder().encodeToString(food.getImage()));
            }

            jsonArray.add(jsonFood);
        }

        response.getWriter().write(jsonArray.toString());
    }

    

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        // Ensure user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        if ("/update".equals(pathInfo)) {
            updateFood(request, response, currentUser);
        } else {
            sendErrorResponse(response, "Invalid endpoint", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        // Ensure user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        if ("/delete".equals(pathInfo)) {
            deleteFood(request, response, currentUser);
        } else {
            sendErrorResponse(response, "Invalid endpoint", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void createFood(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {

        try {
            String foodName = request.getParameter("name");
            Part imagePart = request.getPart("image");
            float amount = Integer.parseInt(request.getParameter("amount"));

            if (foodName == null || imagePart == null) {
                sendErrorResponse(response, "Missing food name or image", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Read image bytes - compatible method
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (InputStream input = imagePart.getInputStream()) {
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = input.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] imageBytes = buffer.toByteArray();

                // Create Food object
                Food food = new Food();
                food.setName(foodName);
                food.setCreatedBy(currentUser);
                food.setImage(imageBytes);
                food.setAmmount(amount);


                // Save food
                foodDAO.createFood(food);

                // Send success response
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_CREATED);
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Food created successfully");
                responseMap.put("foodId", food.getId());
                response.getWriter().write(gson.toJson(responseMap));
//                response.sendRedirect("manageFood.html");
            }
        } catch (Exception e) {
            sendErrorResponse(response, "Error creating food: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void updateFood(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        try {
            int foodId = Integer.parseInt(request.getParameter("id"));
            Food existingFood = foodDAO.getFoodbyId(foodId);

            if (existingFood == null) {
                sendErrorResponse(response, "Food not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Ensure only the creator can update
            if (existingFood.getCreatedBy().getUserId() != currentUser.getUserId()) {
                sendErrorResponse(response, "Unauthorized to update this food", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String foodName = request.getParameter("name");
            Part imagePart = request.getPart("image");

            if (foodName != null) {
                existingFood.setName(foodName);
            }

            if (imagePart != null) {
                // Read image bytes with compatibility for older Java versions
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                try (InputStream input = imagePart.getInputStream()) {
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = input.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    byte[] imageBytes = buffer.toByteArray();

                    existingFood.setImage(imageBytes);
                }
            }

            boolean updated = foodDAO.updateFood(existingFood);

            if (updated) {
                response.setContentType("application/json");
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("message", "Food updated successfully");
                response.getWriter().write(gson.toJson(responseMap));
            } else {
                sendErrorResponse(response, "Failed to update food", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            sendErrorResponse(response, "Error updating food: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void deleteFood(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        try {
            int foodId = Integer.parseInt(request.getParameter("id"));
            Food existingFood = foodDAO.getFoodbyId(foodId);

            if (existingFood == null) {
                sendErrorResponse(response, "Food not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Ensure only the creator can delete
            if (existingFood.getCreatedBy().getUserId() != currentUser.getUserId()) {
                sendErrorResponse(response, "Unauthorized to delete this food", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            boolean deleted = foodDAO.deleteFood(foodId);

            if (deleted) {
                response.setContentType("application/json");
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("message", "Food deleted successfully");
                response.getWriter().write(gson.toJson(responseMap));
            } else {
                sendErrorResponse(response, "Failed to delete food", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            sendErrorResponse(response, "Error deleting food: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode)
            throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", message);

        response.getWriter().write(gson.toJson(errorMap));
    }


}
