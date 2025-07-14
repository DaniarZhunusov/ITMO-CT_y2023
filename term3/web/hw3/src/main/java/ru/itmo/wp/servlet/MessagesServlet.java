package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MessagesServlet extends HttpServlet {
    private static class Pair {
        public final String user;
        public final String text;

        Pair(final String user, final String text) {
            this.user = user;
            this.text = text;
        }
    }

    List<Pair> userToText = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        OutputStream outputStream = response.getOutputStream();
        if ("/message/auth".equals(uri)) {
            String user = request.getParameter("user");
            if (user != null && !user.isBlank()) session.setAttribute("user", user);
            else user = "";
            outputStream.write(new Gson().toJson(user).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } else if ("/message/findAll".equals(uri)
                && session.getAttribute("user") != null) {
            outputStream.write(new Gson().toJson(userToText).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } else if ("/message/add".equals(uri)
                && session.getAttribute("user") != null) {
            String user = (String) session.getAttribute("user");
            String text = request.getParameter("text");
            if (!text.isBlank()) {
                userToText.add(new Pair(user, text));
            }
        }
    }
}
