package com.lucidworks.jseval;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class JSEvalTestServer {

  private static Integer findRandomOpenPortOnAllLocalInterfaces() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }
  public static void main(String[] args) throws Exception {
    JSEvalTestServer jsEvalTestServer = new JSEvalTestServer();
    int port;
    if (args.length == 0) {
      port = findRandomOpenPortOnAllLocalInterfaces();
    } else {
      port = Integer.parseInt(args[0]);
    }
    jsEvalTestServer.run(port);
  }

  public void run(int portToUse) {
    final Server server = new Server(portToUse);
    Thread httpServerThread = new Thread(() -> {
      try {
        server.setHandler(new AbstractHandler() {
          @Override
          public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            System.out.println(target + " is the request");
            if (target.contains("infinite")) {
              // page will infinite hard cpu javascript loop
              response.getWriter().println("<!DOCTYPE html>\n" +
                  "<html>\n" +
                  "<body>\n" +
                  "<div id=\"container\">hard javascript loop</div>\n" +
                  "<script>\n" +
                  "    while (true) {}\n" +
                  "  </script>\n" +
                  "</body>\n" +
                  "</html>\n");
            } else if (target.toLowerCase().contains("pagetimeout")) {
              try {
                Thread.sleep(1000000);
                response.getWriter().println("should time out");
              } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
              }
            } else if (target.contains("page")) {
              // vary between xhtml manual ajax and jquery ajax
              if (new Random().nextBoolean()) {
                response.getWriter().println("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" +
                    "<script>\nvar nowMs = Date.now();\n" +
                    "$(function() {\n" +
                      "$.get(\"ajax1\").done(function() {\n" +
                      "  $('#demo1').html('done loading 1 time taken ms =' + (Date.now() - nowMs));\n" +
                      "});" +
                      "$.get(\"ajax2\").done(function() {\n" +
                      "  $('#demo2').html('done loading 2 time taken ms =' + (Date.now() - nowMs));\n" +
                      "});" +
                      "$.get(\"ajax3\").done(function() {\n" +
                      "  $('#demo3').html('done loading 3 time taken ms =' + (Date.now() - nowMs));\n" +
                      "});" +
                    "});" +
                    "</script>\n" +
                    "<body>\n" +
                    "\n" +
                    "<div id=\"demo1\">\n" +
                    "  <h2>still waiting for reload 1</h2>\n" +
                    "</div>\n" +
                    "<div id=\"demo2\">\n" +
                    "  <h2>still waiting for reload 2</h2>\n" +
                    "</div>\n" +
                    "<div id=\"demo3\">\n" +
                    "  <h2>still waiting for reload 3</h2>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>");
              } else {
                response.getWriter().println("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<script>\nvar nowMs = Date.now();\n" +
                    "function loadDoc(idx) {\n" +
                    "  var xhttp = new XMLHttpRequest();\n" +
                    "  xhttp.onreadystatechange = function() {\n" +
                    "    if (this.readyState == 4 && this.status == 200) {\n" +
                    "     document.getElementById(\"demo\" + idx).innerHTML = this.responseText + ' time taken ms = ' + (Date.now() - nowMs);\n" +
                    "    }\n" +
                    "  };\n" +
                    "  xhttp.open(\"GET\", \"ajaxCall\" + idx, true);\n" +
                    "  xhttp.send();\n" +
                    "}\n" +
                    "function doLoads() {\n" +
                    "  setTimeout(function () { loadDoc(1) }, 100);\n" +
                    "  setTimeout(function () { loadDoc(2) }, 100);\n" +
                    "  setTimeout(function () { loadDoc(3) }, 100);\n" +
                    "}\n" +
                    "</script>\n" +
                    "<body onload=\"doLoads()\">\n" +
                    "<div>\n" +
                    "  <div id=\"demo1\">still waiting for reload 1</div>\n" +
                    "  <div id=\"demo2\">still waiting for reload 2</div>\n" +
                    "  <div id=\"demo3\">still waiting for reload 3</div>\n" +
                    "</div>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>");
              }
            } else if (target.contains("ajaxtimeout")) {
              // vary between xhtml manual ajax and jquery ajax
              if (new Random().nextBoolean()) {
                response.getWriter().println("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" +
                    "<script>\n" +
                    "$(function() {\n" +
                    "$.get(\"pagetimeout\").done(function() {\n" +
                    "  $('#demo2').html('done loading 2');\n" +
                    "});" +
                    "});" +
                    "</script>\n" +
                    "<body>\n" +
                    "\n" +
                    "<div id=\"demo2\">\n" +
                    "  <h2>still waiting for reload 2</h2>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>");
              } else {
                response.getWriter().println("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<script>\n" +
                    "function loadDoc(idx) {\n" +
                    "  var xhttp = new XMLHttpRequest();\n" +
                    "  xhttp.onreadystatechange = function() {\n" +
                    "    if (this.readyState == 4 && this.status == 200) {\n" +
                    "     document.getElementById(\"demo\" + idx).innerHTML = this.responseText;\n" +
                    "    }\n" +
                    "  };\n" +
                    "  xhttp.open(\"GET\", \"pagetimeout\" + idx, true);\n" +
                    "  xhttp.send();\n" +
                    "}\n" +
                    "function doLoads() {\n" +
                    "  setTimeout(function () { loadDoc(1) }, 100);\n" +
                    "}\n" +
                    "</script>\n" +
                    "<body onload=\"doLoads()\">\n" +
                    "<div>\n" +
                    "  <div id=\"demo1\">still waiting for reload 1</div>\n" +
                    "</div>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>");
              }
            } else if (target.toLowerCase().contains("ajax")) {
              int typeOfDelay = new Random().nextInt(10);
              int jsDelay;
              if (typeOfDelay >= 8) { // long wait
                jsDelay = new Random().nextInt(20);
              } else if (typeOfDelay >= 6) { // medium wait
                jsDelay = new Random().nextInt(10);
              } else {
                jsDelay = new Random().nextInt(3); // small wait
              }
              try {
                Thread.sleep(jsDelay * 1000);
              } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
              }
              response.getWriter().println(" successfully loaded ajax call.");
            } else { // index.html
              response.getWriter().println("<html>\n" +
                  "<body>");
              for (int i=0; i<10000; ++i) {
                response.getWriter().println("<p><a href=\"page" + i + ".html\">page " + i + "</a></p>");
              }
              response.getWriter().println("</body></html>");
            }
          }
        });
        server.start();
        server.join();
      } catch (Exception e) {
        System.err.println("Couldn't create embedded jetty server");
      }
    });
    httpServerThread.start();
  }
}
