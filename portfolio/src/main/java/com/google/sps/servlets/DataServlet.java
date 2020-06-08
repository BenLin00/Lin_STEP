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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  private List<String> comments = new ArrayList<>(); 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Send the JSON as the response
    response.setContentType("application/json;");
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
    // add a limit

    List<String> commentsPopulate = new ArrayList<>();

    for (Entity entity : results) {
      String text = (String) entity.getProperty("text");
      String email = (String) entity.getProperty("email");
      commentsPopulate.add(email + ": " + text);
    }

    String json = new Gson().toJson(commentsPopulate);
    response.getWriter().println(json);

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
  
    // Get the input from the form and add it to json on /data
    String commentSubmission = request.getParameter("comment-submission");
    comments.add(commentSubmission);

    UserService userService = UserServiceFactory.getUserService();

    // Only logged-in users can post messages
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String text = request.getParameter("comment-submission");
    String email = userService.getCurrentUser().getEmail();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity messageEntity = new Entity("Message");
    messageEntity.setProperty("text", text);
    messageEntity.setProperty("email", email);
    messageEntity.setProperty("timestamp", System.currentTimeMillis());
    datastore.put(messageEntity);

    response.sendRedirect("/index.html");
  }

}