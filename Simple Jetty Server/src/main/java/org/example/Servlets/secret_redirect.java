package org.example.Servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;


/**
 * A secret redirect page that the students will inject into the page
 */
public class secret_redirect extends HttpServlet {
    /**
     * Sends a secret html code when this servlet's GET request is called
     *
     * @param req {@link HttpServletRequest} request
     * @param resp {@link HttpServletResponse} response
     * @throws IOException exception
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("i was here!") == null) {
            resp.sendRedirect("/a");
            return;
        }

        session.removeAttribute("i was here!");

        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>CS336 - Feedback</title>\n" +
                "    <style>\n" +
                "        /* Modern Reset */\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            background-color: #f0f2f5;\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            display: flex;\n" +
                "            justify-content: center; /* Horizontal centering */\n" +
                "            align-items: center;     /* Vertical centering */\n" +
                "            height: 100vh;           /* Full viewport height */\n" +
                "            color: #333;\n" +
                "        }\n" +
                "\n" +
                "        .highlight-container {\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            font-size: 2.5rem;\n" +
                "            position: relative;\n" +
                "            display: inline;\n" +
                "            z-index: 1;\n" +
                "        }\n" +
                "\n" +
                "        /* The Highlight Effect */\n" +
                "        h1::after {\n" +
                "            content: \"\";\n" +
                "            position: absolute;\n" +
                "            left: -5px;\n" +
                "            bottom: 5px;\n" +
                "            width: 105%;\n" +
                "            height: 40%;\n" +
                "            background-color: #ff99cc; /* Bocchi pink highlight */\n" +
                "            z-index: -1;\n" +
                "            transform: rotate(-1deg);\n" +
                "            opacity: 0.6;\n" +
                "            border-radius: 2px;\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 600px) {\n" +
                "            h1 { font-size: 1.5rem; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "    <div class=\"highlight-container\">\n" +
                "        <h1>What's your favorite thing about CS336 so far?</h1>\n" +
                "    </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }
}