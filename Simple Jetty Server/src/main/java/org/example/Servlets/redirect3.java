package org.example.Servlets;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class redirect3 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_FOUND);
        Cookie cookie = new Cookie("FlowState", "StageC");
        cookie.setMaxAge(10);
        resp.addCookie(cookie);

        resp.setHeader("Location", "/d");
    }
}