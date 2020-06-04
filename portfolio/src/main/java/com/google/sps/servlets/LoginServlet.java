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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;  
import java.util.HashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    // struggling a lot to create custom jsons in java so I'm using ArrayList
    // ArrayList<String> loginData = new ArrayList<>();
    // can I append logInOutUrl and loginStatus to arraylist after initializing but before declaring? 
    // in java, strings are nonprimitive and immutable, so does the arraylist hold mutable pointers instead?
    String logInOutUrl;
    int loginStatus;

    Gson gsonBuilder = new GsonBuilder().create();
    HashMap loginDataMap = new HashMap();

    // generate login/logout url & login status as 0/1
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      logInOutUrl = userService.createLogoutURL("/");
      loginStatus = 1;
    } else {
      logInOutUrl = userService.createLoginURL("/");
      loginStatus = 0;
    }

    // loginData.add(loginStatus);
    // loginData.add(logInOutUrl);
    // remember to lookup line 40 & 41
    // JsonObject loginDataJson = new JSONObject();

    


 loginDataMap.put("loginStatus", loginStatus);
 loginDataMap.put("logInOutUrl", logInOutUrl);
 String loginDataJson = gsonBuilder.toJson(loginDataMap);
 



// String loginDataJson = new Gson().toJson(loginData);
    response.getWriter().println(loginDataJson);

  }
}