package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = request.getSession();

        if (isCaptchaPassed(session) || request.getRequestURI().equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }

        if (isCaptchaValid(request, session)) {
            session.setAttribute("captcha-passed", Boolean.TRUE);
            //response.sendRedirect(request.getRequestURI());
            response.sendRedirect("http://localhost:8080/index.html");
        } else {
            generateCaptcha(session);
            request.getRequestDispatcher("/static/js/captcha.jsp").forward(request, response);
        }
    }

    private boolean isCaptchaPassed(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("captcha-passed"));
    }

    private boolean isCaptchaValid(HttpServletRequest request, HttpSession session) {
        String userInput = request.getParameter("actual-answer");
        String expected = (String) session.getAttribute("expected-answer");
        return userInput != null && userInput.equals(expected);
    }
    
    private void generateCaptcha(HttpSession session) {
        if (session.getAttribute("captcha-image") == null) {
            String newAnswer = String.valueOf(100 + new Random().nextInt(900));
            session.setAttribute("expected-answer", newAnswer);
            session.setAttribute("captcha-image", Base64.getEncoder().encodeToString(ImageUtils.toPng(newAnswer)));
        }
    }
}
