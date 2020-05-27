// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  private ArrayList<String> quotes;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (quotes == null) {
        quotes = new ArrayList<>();
        quotes.add("Beep Boop - Ben Lin");
    }
    
    // Convert the quotes to JSON using Gson library
    String json = new Gson().toJson(quotes);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
    
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
  
    // Get the input from the form and add it to json on /data
    String quoteSubmission = getQuoteSubmission(request);
    quotes.add(quoteSubmission);
    // 

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  private String getQuoteSubmission(HttpServletRequest request) {
    // Get the input from the form.
    String quote = request.getParameter("quote-submission");
    String author = request.getParameter("author");

    return (quote + " - " + author);
  }

}