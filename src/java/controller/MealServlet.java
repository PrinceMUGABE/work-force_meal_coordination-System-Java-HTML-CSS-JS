/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.google.gson.Gson;
import dao.MealDao;
import modal.Meal;
import modal.MealType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/restaurantManager/meal")
public class MealServlet extends HttpServlet {

    private MealDao mealDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        mealDao = new MealDao();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");

        try {
            if ("create".equalsIgnoreCase(action)) {
                createMeal(request, response);
            } else if ("update".equalsIgnoreCase(action)) {
                updateMeal(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"Invalid action\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getById".equalsIgnoreCase(action)) {
            getMealById(request, response);
        } else if ("getAll".equalsIgnoreCase(action)) {
            getAllMeals(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"Invalid action\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        deleteMeal(request, response);
    }

    private void createMeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        Meal meal = gson.fromJson(reader, Meal.class);

        // Ensure type is set
        if (meal.getType() == null) {
            throw new IllegalArgumentException("Meal type is required");
        }

        boolean isCreated = mealDao.createMeal(meal);
        if (isCreated) {
            response.getWriter().write("{\"message\": \"Meal created successfully\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Failed to create meal\"}");
        }
    }

    private void getMealById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Meal meal = mealDao.getMealById(id);
        response.setContentType("application/json");
        if (meal != null) {
            response.getWriter().write(gson.toJson(meal));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"message\": \"Meal not found\"}");
        }
    }

    private void getAllMeals(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Meal> meals = mealDao.getAllMeals();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(meals));
    }

    private void updateMeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        Meal meal = gson.fromJson(reader, Meal.class);

        boolean isUpdated = mealDao.updateMeal(meal);
        response.setContentType("application/json");
        if (isUpdated) {
            response.getWriter().write("{\"message\": \"Meal updated successfully\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Failed to update meal\"}");
        }
    }

    private void deleteMeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean isDeleted = mealDao.deleteMeal(id);
        response.setContentType("application/json");
        if (isDeleted) {
            response.getWriter().write("{\"message\": \"Meal deleted successfully\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Failed to delete meal\"}");
        }
    }
}
