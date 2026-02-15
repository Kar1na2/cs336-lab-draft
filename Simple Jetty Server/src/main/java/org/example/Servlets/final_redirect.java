package org.example.Servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Last TCP connection will be sent here that will return a html file
 */
public class final_redirect extends HttpServlet {
    /**
     * Sends a simple html code when the server reaches this path
     *
     * @param req {@link HttpServletRequest} request
     * @param resp {@link HttpServletResponse} response
     * @throws IOException exception
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("/a");
            return;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Bocchi ASCII</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            background-color: #1a1a1a; /* Dark background */\n" +
                "            color: #ff99cc;           /* Pink text */\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "            margin: 0;\n" +
                "            overflow: hidden;\n" +
                "            font-family: monospace;\n" +
                "        }\n" +
                "\n" +
                "        pre {\n" +
                "            font-size: 8px;           /* Adjusted size to fit screen */\n" +
                "            line-height: 1.1;\n" +
                "            text-shadow: 0 0 5px rgba(255, 153, 204, 0.3);\n" +
                "            white-space: pre;\n" +
                "        }\n" +
                "\n" +
                "        /* Responsive scaling */\n" +
                "        @media (max-width: 600px) {\n" +
                "            pre { font-size: 4px; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<pre>\n" +
                "⠀⠙⢎⠉⠢⡀⠀⠀⠀⠀⠉⠓⢤⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡬⢲⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠑⠶⣬⣕⣂⡶⠁⠀⣠⠎⢀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⣅⢣⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⡠⠛⢉⡿⠎⢁⣼⡥⢊⠁⠀⠀⡀⠀⠀⠀⠀⡀⢀⣀⠀⠀⠈⢷⣖⡠⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢦⠣⡀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⣀⠴⣪⠤⠒⠁⡀⠀⡼⠋⠀⠸⡀⠀⢤⠰⠀⢀⠀⠀⢣⡀⠙⣷⣦⣠⢞⡥⠛⣯⣍⣀⠀⠀⠀⢠⠀⠀⠀⠀⠀⠀⠈⠳⡑⢄⠀⠀⠀⠀⠀⠀\n" +
                "⠛⠓⠒⡲⠒⠒⢺⠁⢀⡞⠀⢀⠀⣇⠀⢸⣆⢇⠸⡄⠀⠘⡕⢄⠈⢮⣛⣷⣺⠽⠒⣛⢿⢿⣦⡀⠈⡆⠀⢠⠀⠀⠀⠀⠀⠱⣤⣹⣶⣤⣤⠴⠄\n" +
                "⣠⠔⠋⠀⠀⣠⠏⠀⢸⡇⣠⣿⣰⠹⡀⠀⡏⢾⡄⢷⡀⠀⢳⠘⣕⣤⣿⣞⢍⠓⠷⣾⣓⣕⢮⡉⠑⢺⡤⣼⢄⠀⠀⠀⣄⠀⠘⠮⠥⠜⠛⠀⠀\n" +
                "⠓⠒⠒⢒⡽⠃⣠⠞⡏⣷⢁⡧⣬⣀⠱⡀⢱⡈⢷⡘⣿⣄⠴⢯⣥⠞⣛⡮⣷⣧⣾⣷⣿⠏⠓⠺⢶⣼⡷⣼⠀⢧⠀⠀⠸⡀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⣠⡴⠭⠒⠉⡞⡸⡴⢻⡋⣏⠓⠢⠭⣽⣙⣧⠈⣷⠹⡎⣢⡈⢧⢙⣿⣛⣏⣿⣿⣤⠷⠀⠀⠀⠀⠀⣇⣧⠀⠛⣗⢄⠀⢣⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⢀⠞⣠⣟⡵⢃⢷⣿⢲⣼⡿⢷⣿⣿⣌⢢⢳⣷⣱⡙⢦⣣⡈⢿⣟⡾⠳⠉⠁⠀⠀⠀⠀⠀⡿⢸⠀⢘⢼⡀⠉⠢⠷⢤⡀⠀⠀⠀⠀\n" +
                "⠀⠀⢀⣤⡋⡠⠾⠏⢠⡾⢸⣿⣿⣕⣻⣽⣿⣿⡿⣸⠀⠻⣯⡓⢧⡙⠻⣄⠈⠉⠀⠀⠀⠀⠀⠀⠀⢸⠃⢸⠀⢠⢸⠈⠉⠉⠉⠉⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⠀⠀⣸⡴⠁⠀⣼⠀⢻⠛⠉⠛⠉⠉⠀⡿⡄⠀⠈⠳⠀⢳⡀⠈⠀⠀⠀⠀⠀⠀⠀⠀⠀⡜⠀⢸⡀⢜⡼⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⠀⠀⡟⠀⠀⠀⢻⡀⡇⠀⠀⠀⠀⠀⠀⢙⠇⠀⠀⠀⠰⡜⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⡳⡆⢾⠛⢝⢄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣧⢃⠀⠀⠀⠀⠀⠀⢸⠀⠀⠀⠀⠀⠉⠻⡄⠀⠀⠀⠀⠀⠀⠀⣠⢳⠃⡇⢸⡉⠑⠒⠳⠆⠀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣾⡄⠀⠀⠀⠀⠀⢯⣤⣀⣀⣤⣖⣪⠽⠇⠀⠀⠀⠀⠀⠀⢰⠃⡎⠀⢱⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣢⢄⡀⠀⠀⠀⠈⠑⡦⡏⠁⠀⠀⠀⠀⠀⠀⠀⡀⠀⣸⡼⠀⠀⣸⠧⢸⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⢿⢷⣄⠀⠀⠀⠀⡀⡇⢀⡀⢀⣀⡀⢀⣠⠞⠁⠀⣿⠃⠀⣰⣯⡄⡘⣿⣷⣶⣤⣀⠀⠀⠀⠀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠳⣽⢶⣠⠋⠉⠙⠛⠁⣀⣀⣀⡤⢖⠏⠀⠀⠀⠁⠀⡰⢹⠇⡇⡇⣿⣿⣿⣿⣿⣿⣶⣤⣀⠀⠀⠀\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠳⡙⣏⠀⠉⠉⠁⠀⠀⠀⣠⠎⠀⠀⠀⠀⠀⢠⣿⠃⣾⠃⡇⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣤\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣴⣾⣾⣾⠓⠤⠤⣶⠟⠒⠚⠁⠀⠀⠀⠀⠀⢠⣿⠃⠀⣿⠀⠁⠀⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣤⢶⣻⣿⣿⣿⣿⣿⣧⠀⢸⠁⠀⠀⠀⠀⠀⠀⠀⠀⣰⡿⠃⠀⢠⢹⠀⠁⢰⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣴⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠸⡀⠀⠀⠀⠀⠀⠀⣠⣾⡿⠃⠀⠀⢸⡀⠀⠁⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
                "⣀⣤⣤⣤⣤⣴⣶⣶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡬⣐⠒⠒⠒⣆⡼⠟⠉⠀⠀⠀⠀⡇⡇⠀⢠⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣄⣉⣀⣀⣀⣀⣀⣀⣀⣔⣀⣰⠁⠁⣠⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
                "</pre>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }
}