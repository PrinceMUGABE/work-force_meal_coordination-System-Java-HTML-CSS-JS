package controller;

import com.google.gson.Gson;
import com.google.gson.*;
import modal.MealSchedule;
import modal.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Optional;
import java.util.stream.Collectors;
import modal.Food;
import modal.MealType;
import dao.FoodDAO;
import java.io.BufferedReader;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import dao.MealScheduleDAO;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import modal.MealSchedule;
import modal.Meal;
import dao.MealDao;
import java.util.logging.Logger;
import dao.UserDao;
import static org.hibernate.annotations.common.util.impl.LoggerFactory.logger;

@WebServlet("/restaurantManager/schedule/*")
public class MealScheduleServlet extends HttpServlet {
//    private static final Logger logger = Logger.getLogger(RestaurantManagerServlet.class.getName());

    private final List<MealSchedule> mealSchedules = new ArrayList<>();
    private final Gson gson = new Gson();
    private final AtomicInteger idGenerator = new AtomicInteger(1); // Simple ID generator for the schedules.
    private final FoodDAO foodDao = new FoodDAO();
    private final MealScheduleDAO mealScheduleDAO = new MealScheduleDAO();
    private final Meal meal = new Meal();
    private final MealDao mealDao = new MealDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Comprehensive logging and error tracking
        System.out.println("=== Servlet Method Called ===");
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Path Info: " + request.getPathInfo());
        System.out.println("Request URI: " + request.getRequestURI());

        String pathInfo = request.getPathInfo();

        // Explicitly check for /create path
        if ("/create".equals(pathInfo)) {
            // Always send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Robust session handling
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                sendErrorResponse(response, "User authentication required", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Read request body
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            // Log raw request body for debugging
            String rawPayload = requestBody.toString();
            System.out.println("Raw Payload: " + rawPayload);

            // Validate request body
            if (rawPayload.trim().isEmpty()) {
                sendErrorResponse(response, "Empty request payload", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Parse JSON payload
            JsonObject payload;
            try {
                payload = new Gson().fromJson(rawPayload, JsonObject.class);
            } catch (JsonSyntaxException e) {
                System.err.println("JSON Parsing Error: " + e.getMessage());
                sendErrorResponse(response, "Invalid JSON format", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Validate payload structure
            if (payload == null || !payload.has("mealId") || !payload.has("foodIds")) {
                sendErrorResponse(response, "Missing required fields: mealId, foodIds", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Extract and validate mealId
            int mealId;
            MealType mealType;
            try {
                mealId = payload.get("mealId").getAsInt();
                mealType = MealType.values()[mealId];
                System.out.println("Selected Meal Type: " + mealType);
            } catch (Exception e) {
                System.err.println("Meal ID Validation Error: " + e.getMessage());
                sendErrorResponse(response, "Invalid meal type", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Extract and validate foodIds
            JsonArray foodIdsArray = payload.getAsJsonArray("foodIds");
            if (foodIdsArray == null || foodIdsArray.size() == 0) {
                sendErrorResponse(response, "No food items selected", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Convert and validate food IDs
            List<Integer> foodIds = new ArrayList<>();
            for (JsonElement elem : foodIdsArray) {
                try {
                    int foodId = elem.getAsInt();
                    foodIds.add(foodId);
                } catch (Exception e) {
                    System.err.println("Invalid food ID: " + elem);
                    sendErrorResponse(response, "Invalid food ID format", HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }

            // Resolve and validate foods
            List<Food> resolvedFoods = new ArrayList<>();
            try {
                for (int foodId : foodIds) {
                    Food food = foodDao.getFoodbyId(foodId);
                    if (food == null) {
                        System.err.println("Food not found: ID " + foodId);
                        sendErrorResponse(response, "Food with ID " + foodId + " not found", HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    resolvedFoods.add(food);
                    System.out.println("Resolved Food: " + food.getName() + " (ID: " + food.getId() + ")");
                }
            } catch (Exception e) {
                System.err.println("Error resolving foods: " + e.getMessage());
                sendErrorResponse(response, "Error resolving food items", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // Create meal schedule
            MealSchedule schedule = new MealSchedule();
            User currentUser = (User) session.getAttribute("user");

            schedule.setUser(currentUser);
            schedule.setMealType(mealType);
            schedule.setFoods(resolvedFoods);

            // Calculate total amount
            float totalAmount = (float) resolvedFoods.stream()
                    .mapToDouble(Food::getAmmount)
                    .sum();

//    compare total amount with meal amount
            List<Meal> allMeals = mealDao.getMealsByType(mealType);
            float mealAmount = meal.getAmount();

            if (!allMeals.isEmpty()) {
                System.out.println("Meals of type " + mealType + ":");
                for (Meal foundmeal : allMeals) {
                    System.out.println("ID: " + foundmeal.getId() + ", Amount: " + foundmeal.getAmount());
                    if (totalAmount > foundmeal.getAmount()) {
                        System.out.println("");
                        sendErrorResponse(response, "Total food amount exceeds meal amount limit", HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                    schedule.setMealAmount(foundmeal.getAmount());
                    System.out.println("Setled Meal Total Amount: " + foundmeal.getAmount());
                }
            }
            schedule.setTotalAmount(totalAmount);

            // Attempt to create schedule
            try {
                MealSchedule created_schedules = mealScheduleDAO.createSchedule(schedule);
                if (created_schedules != null) {
                    System.out.println("Meal Scdulle created successfully");
                }
                // Prepare and send successful response
                response.setStatus(HttpServletResponse.SC_CREATED);
                String jsonResponse = new Gson().toJson(schedule);

                System.out.println("=== Meal Schedule Created Successfully ===");
                System.out.println("Created Schedule Details:");
                System.out.println("  - Schedule ID: " + schedule.getId());
                System.out.println("  - Meal Type: " + schedule.getMealType());
                System.out.println("  - Total Amount: " + schedule.getTotalAmount());
                System.out.println("  - Number of Foods: " + resolvedFoods.size());
               

                response.getWriter().write(jsonResponse);

            } catch (Exception e) {
                System.err.println("Failed to create Meal Schedule:");
                e.printStackTrace();
                sendErrorResponse(response, "Failed to create meal schedule: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            System.out.println("Invalid end point");
            sendErrorResponse(response, "Invalid endpoint", HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Request Path Info: " + request.getPathInfo());
        System.out.println("Request Servlet Path: " + request.getServletPath());
        System.out.println("Request Request URI: " + request.getRequestURI());

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if ("/list".equals(pathInfo)) {
            listAllSchedules(request, response);
        } else {
            sendErrorResponse(response, "Invalid endpoint", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void listAllSchedules(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            List<MealSchedule> schedules = mealScheduleDAO.getAllSchedules();

            if (schedules == null || schedules.isEmpty()) {
                System.out.println("No schedules found in the database");
                sendErrorResponse(response, "No schedules found", HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            System.out.println("Total Schedules Retrieved: " + schedules.size());

            for (MealSchedule schedule : schedules) {
                System.out.println("Schedule ID: " + schedule.getId());
                System.out.println("User: " + (schedule.getUser() != null ? schedule.getUser().getEmail() : "No User"));
                System.out.println("Foods: " + (schedule.getFoods() != null ? schedule.getFoods().size() : "No Foods"));
                System.out.println("Meal Type: " + schedule.getMealType());
                System.out.println("Total Amount: " + schedule.getTotalAmount());
                System.out.println("Creation Date: " + schedule.getCreationDate());
                System.out.println("---");
            }

            response.getWriter().write(gson.toJson(schedules));
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "Error retrieving schedules: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Request Path Info: " + request.getPathInfo());
        System.out.println("Request Servlet Path: " + request.getServletPath());
        System.out.println("Request Request URI: " + request.getRequestURI());

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("^/update/\\d+$")) {
            sendErrorResponse(response, "Invalid endpoint or missing scheduleId in path", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Extract scheduleId from the path
        int scheduleId = Integer.parseInt(pathInfo.split("/")[2]);

        // Pass the extracted ID and request to the update handler
        updateSchedule(request, response, scheduleId);
    }

    private void updateSchedule(HttpServletRequest request, HttpServletResponse response, int scheduleId) throws IOException {
        System.out.println("=== MealSchedule Update Request === for Schedule ID: " + scheduleId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "User authentication required", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String rawPayload = requestBody.toString();
        System.out.println("Raw Payload: " + rawPayload);

        if (rawPayload.trim().isEmpty()) {
            sendErrorResponse(response, "Empty request payload", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JsonObject payload;
        try {
            payload = new Gson().fromJson(rawPayload, JsonObject.class);
        } catch (JsonSyntaxException e) {
            sendErrorResponse(response, "Invalid JSON format", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!payload.has("scheduleId") || !payload.has("mealId") || !payload.has("foodIds")) {
            sendErrorResponse(response, "Missing required fields: scheduleId, mealId, foodIds", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

//    int scheduleId = payload.get("scheduleId").getAsInt();
        int mealId = payload.get("mealId").getAsInt();
        JsonArray foodIdsArray = payload.getAsJsonArray("foodIds");

        if (foodIdsArray == null || foodIdsArray.size() == 0) {
            sendErrorResponse(response, "No food items selected", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        MealSchedule existingSchedule = mealScheduleDAO.getScheduleById(scheduleId);
        if (existingSchedule == null) {
            sendErrorResponse(response, "Schedule not found", HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<Integer> foodIds = new ArrayList<>();
        for (JsonElement elem : foodIdsArray) {
            foodIds.add(elem.getAsInt());
        }

        List<Food> resolvedFoods = new ArrayList<>();
        try {
            for (int foodId : foodIds) {
                Food food = foodDao.getFoodbyId(foodId);
                if (food == null) {
                    sendErrorResponse(response, "Food with ID " + foodId + " not found", HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                resolvedFoods.add(food);
            }
        } catch (Exception e) {
            sendErrorResponse(response, "Error resolving food items: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        float totalAmount = (float) resolvedFoods.stream()
                .mapToDouble(Food::getAmmount)
                .sum();

        List<Meal> allMeals = mealDao.getMealsByType(MealType.values()[mealId]);
        if (allMeals.isEmpty() || totalAmount > allMeals.get(0).getAmount()) {
            sendErrorResponse(response, "Total food amount exceeds meal amount limit", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        existingSchedule.setMealType(MealType.values()[mealId]);
        existingSchedule.setFoods(resolvedFoods);
        existingSchedule.setTotalAmount(totalAmount);
        existingSchedule.setMealAmount(allMeals.get(0).getAmount());

        try {
            MealSchedule updatedSchedule = mealScheduleDAO.updateSchedule(existingSchedule);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(new Gson().toJson(updatedSchedule));
        } catch (Exception e) {
            sendErrorResponse(response, "Failed to update schedule: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Request Path Info: " + request.getPathInfo());
        System.out.println("Request Servlet Path: " + request.getServletPath());
        System.out.println("Request Request URI: " + request.getRequestURI());

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("^/delete/\\d+$")) {
            sendErrorResponse(response, "Invalid endpoint or missing scheduleId in path", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Extract scheduleId from the path
        int scheduleId = Integer.parseInt(pathInfo.split("/")[2]);

        // Pass the extracted ID to the delete handler
        deleteSchedule(request, response, scheduleId);
    }

    private void deleteSchedule(HttpServletRequest request, HttpServletResponse response, int scheduleId) throws IOException {
        System.out.println("=== MealSchedule Delete Request === for Schedule ID: " + scheduleId);

        // Fetch the schedule by ID
        MealSchedule schedule = mealScheduleDAO.getScheduleById(scheduleId);
        if (schedule == null) {
            sendErrorResponse(response, "Schedule not found", HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            mealScheduleDAO.deleteSchedule(scheduleId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            sendErrorResponse(response, "Failed to delete schedule: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
