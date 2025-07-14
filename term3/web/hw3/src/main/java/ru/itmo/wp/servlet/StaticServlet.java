package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StaticServlet extends HttpServlet {
    private File getFile(String uri) {
        File file = new File("./src/main/webapp/static/" + uri);
        if (!file.isFile()) {
            return new File(getServletContext().getRealPath("/static/" + uri));
        }
        return file;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] urisArr = request.getRequestURI().split("\\+");
        List<File> files = new ArrayList<>();
        for (String uri : urisArr) {
            File file = getFile(uri);
            if (!file.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                files.add(file);
            }
        }
        response.setContentType(getContentTypeFromName(urisArr[0]));
        for (File file : files) {
            OutputStream outputStream = response.getOutputStream();
            Files.copy(file.toPath(), outputStream);
            outputStream.flush();
        }
    }

    private String getContentTypeFromName(String name) {
        name = name.toLowerCase();

        if (name.endsWith(".png")) {
            return "image/png";
        }

        if (name.endsWith(".jpg")) {
            return "image/jpeg";
        }

        if (name.endsWith(".html")) {
            return "text/html";
        }

        if (name.endsWith(".css")) {
            return "text/css";
        }

        if (name.endsWith(".js")) {
            return "application/javascript";
        }

        throw new IllegalArgumentException("Can't find content type for '" + name + "'.");
    }
}
