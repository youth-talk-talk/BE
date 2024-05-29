package com.server.youthtalktalk.util;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FilterResponseUtils {

    public static void unAuthorized(HttpServletResponse response, Exception e) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    public static void forbidden(HttpServletResponse response, Exception e) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }
}
