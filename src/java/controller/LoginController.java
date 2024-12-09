package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.UserDao;
import modal.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;


@WebServlet("/login")
public class LoginController extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Parse form data
        String phoneOrEmail = request.getParameter("phoneOrEmail");
        String password = request.getParameter("password");

        if (phoneOrEmail == null || password == null || phoneOrEmail.isEmpty() || password.isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Phone/email or password is missing.\"}");
            return;
        }

        // Retrieve user based on input
        User user = phoneOrEmail.contains("@") ? userDao.getUserByEmail(phoneOrEmail) : userDao.getUserByPhone(phoneOrEmail);

        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid phone/email or password.\"}");
            return;
        }

        // Set session attributes if needed (e.g., user role)
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        // Get context path dynamically
        String contextPath = request.getContextPath();

        // Determine redirection based on user role
        String redirectUrl = null;
        switch (user.getRole()) {
            case ADMINISTRATOR:
                redirectUrl = contextPath + "/admin/adminManageSchedules.html";  // Relative to the context
                break;
            case LECTURER:
                redirectUrl = contextPath + "/aucaLecture/lecturerDashboard.html";
                break;
            case ASSISTANT_LECTURER:
                redirectUrl = contextPath + "/assistantLecturer/assistantLecturerDashboard.html";
                break;
            case FOOD_DISTRIBUTOR:
                redirectUrl = contextPath + "/restaurantEmployee/foodDistributorDashboard.html";
                break;
            case RESTAURANT_MANAGER:
                redirectUrl = contextPath + "/restaurantManager/restaurantManagerDashboard.html";
                break;
            default:
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Role not recognized.\"}");
                return;
        }

        // Redirect the user to their role-based dashboard
        response.sendRedirect(redirectUrl);
    }
}
