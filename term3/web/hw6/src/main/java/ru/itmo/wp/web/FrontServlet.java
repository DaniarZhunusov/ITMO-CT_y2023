package ru.itmo.wp.web;

import com.google.common.base.Strings;
import freemarker.template.*;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.web.exception.NotFoundException;
import ru.itmo.wp.web.exception.RedirectException;
import ru.itmo.wp.web.page.IndexPage;
import ru.itmo.wp.web.page.NotFoundPage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FrontServlet extends HttpServlet {
    private static final String BASE_PAGE_PACKAGE = FrontServlet.class.getName().substring(
            0, FrontServlet.class.getName().length() - FrontServlet.class.getSimpleName().length()
    ) + "page";

    private Configuration sourceFreemarkerConfiguration;
    private Configuration targetFreemarkerConfiguration;

    private Configuration newFreemarkerConfiguration(
            String templateDirName, boolean debug) throws ServletException {
        File templateDir = new File(templateDirName);
        if (!templateDir.isDirectory()) {
            return null;
        }

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        try {
            configuration.setDirectoryForTemplateLoading(templateDir);
        } catch (IOException e) {
            throw new ServletException("Can't create freemarker configuration [templateDir=" + templateDir + "].", e);
        }

        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(debug
                ? TemplateExceptionHandler.HTML_DEBUG_HANDLER
                : TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setFallbackOnNullLoopVariable(false);

        return configuration;
    }

    @Override
    public void init() throws ServletException {
        sourceFreemarkerConfiguration = newFreemarkerConfiguration(getServletContext().getRealPath(".") + "/../../src/main/webapp/WEB-INF/templates", true);
        targetFreemarkerConfiguration = newFreemarkerConfiguration(getServletContext().getRealPath("WEB-INF/templates"), false);
    }

    private Template newTemplate(String name) throws ServletException {
        Template template = null;
        try {
            template = sourceFreemarkerConfiguration.getTemplate(name);
        } catch (TemplateNotFoundException ignored) {
            // No operations.
        } catch (IOException e) {
            throw new ServletException("Can't load template [name=" + name + "].", e);
        }

        if (template == null) {
            try {
                template = targetFreemarkerConfiguration.getTemplate(name);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [name=" + name + "].", e);
            }
        }

        if (template == null) {
            throw new ServletException("Unable to find template [template=" + name + "].");
        }

        return template;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Route route = Route.newRoute(request);
        try {
            process(route, request, response);
        } catch (NotFoundException e) {
            try {
                process(Route.newNotFoundRoute(), request, response);
            } catch (NotFoundException e1) {
                throw new ServletException(e1);
            }
        }
    }

    private void process(Route route,
                         HttpServletRequest request,
                         HttpServletResponse response) throws NotFoundException, ServletException, IOException {
        Class<?> pageClass;
        try {
            pageClass = Class.forName(route.getClassName());
        } catch (ClassNotFoundException e) {
            throw new NotFoundException();
        }

        Method beforeMethod = null;
        Method afterMethod = null;
        Method actionMethod = null;

        for (Class<?> clazz = pageClass; (beforeMethod == null || afterMethod == null || actionMethod == null) && clazz != null; clazz = clazz.getSuperclass()) {
            if (beforeMethod == null) {
                beforeMethod = getMethod(clazz, "before", HttpServletRequest.class, Map.class);
            }
            if (afterMethod == null) {
                afterMethod = getMethod(clazz, "after", HttpServletRequest.class, Map.class);
            }
            if (actionMethod == null) {
                actionMethod = getMethod(clazz, route.getAction(), HttpServletRequest.class, Map.class);
            }
        }

        if (actionMethod == null) {
            throw new NotFoundException();
        }

        Object page;
        try {
            page = pageClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ServletException("Can't create page [pageClass=" + pageClass + "].", e);
        }

        Map<String, Object> view = new HashMap<>();
        putUser(request, view);

        if (invokeMethod(request, response, pageClass, beforeMethod, page, view)) {
            return;
        }

        if (invokeMethod(request, response, pageClass, actionMethod, page, view)) {
            return;
        }

        if (invokeMethod(request, response, pageClass, afterMethod, page, view)) {
            return;
        }

        renderTemplate(response, pageClass, view);
    }

    private Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private boolean invokeMethod(HttpServletRequest request, HttpServletResponse response, Class<?> pageClass, Method method, Object page, Map<String, Object> view) throws ServletException, IOException {
        if (method == null) {
            return false;
        }

        try {
            method.setAccessible(true);
            method.invoke(page, request, view);
        } catch (IllegalAccessException e) {
            throw new ServletException("Unable to run action [pageClass=" + pageClass + ", method=" + method + "].", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RedirectException) {
                response.sendRedirect(((RedirectException) cause).getLocation());
                return true;
            } else if (cause instanceof ValidationException) {
                handleValidationException(request, view, (ValidationException) cause);
            } else {
                throw new ServletException("Unable to run action [pageClass=" + pageClass + ", method=" + method + "].", e);
            }
        }
        return false;
    }

    private void handleValidationException(HttpServletRequest request, Map<String, Object> view, ValidationException validationException) {
        view.put("error", validationException.getMessage());
        for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet()) {
            String key = param.getKey();
            if (param.getValue() != null && param.getValue().length == 1) {
                view.put(key, param.getValue()[0]);
            }
        }
    }

    private void renderTemplate(HttpServletResponse response, Class<?> pageClass, Map<String, Object> view) throws ServletException, IOException {
        Template template = newTemplate(pageClass.getSimpleName() + ".ftlh");

        response.setContentType("text/html");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            template.process(view, response.getWriter());
        } catch (TemplateException e) {
            throw new ServletException("Can't render template [pageClass=" + pageClass + "].", e);
        }
    }

    private void putUser(HttpServletRequest request, Map<String, Object> view) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            view.put("user", user);
        }
    }

    private static final class Route {
        private static final String DEFAULT_ACTION = "action";

        private final String className;
        private final String action;

        private Route(String className, String action) {
            this.className = className;
            this.action = action;
        }

        private static Route newNotFoundRoute() {
            return new Route(
                    NotFoundPage.class.getName(),
                    DEFAULT_ACTION
            );
        }

        private static Route newIndexRoute() {
            return new Route(
                    IndexPage.class.getName(),
                    DEFAULT_ACTION
            );
        }

        private String getClassName() {
            return className;
        }

        private String getAction() {
            return action;
        }

        private static Route newRoute(HttpServletRequest request) {
            String uri = request.getRequestURI();

            StringBuilder className = new StringBuilder(BASE_PAGE_PACKAGE);
            Arrays.stream(uri.split("/")).filter(s -> !s.isEmpty()).forEach(s -> {
                className.append('.');
                className.append(s);
            });

            if (className.toString().equals(BASE_PAGE_PACKAGE)) {
                return newIndexRoute();
            }

            int lastPeriodPos = className.lastIndexOf(".");
            className.setCharAt(lastPeriodPos + 1, Character.toUpperCase(className.charAt(lastPeriodPos + 1)));
            className.append("Page");

            String action = request.getParameter("action");
            if (Strings.isNullOrEmpty(action)) {
                action = DEFAULT_ACTION;
            }

            return new Route(className.toString(), action);
        }
    }
}
