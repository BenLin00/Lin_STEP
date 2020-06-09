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
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    // logInOutUrl is either the login or logout url depending on user status
    String logInOutUrl;
    boolean isLoggedIn;

    // generate login/logout url & login status as 0/1
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      logInOutUrl = userService.createLogoutURL("/");
      isLoggedIn = true;
    } else {
      logInOutUrl = userService.createLoginURL("/");
      isLoggedIn = false;
    }

    Gson gsonBuilder = new GsonBuilder().create();
    Map<String, Object> loginDataMap = new HashMap<>();

    loginDataMap.put("isLoggedIn", isLoggedIn);
    loginDataMap.put("logInOutUrl", logInOutUrl);

    String loginDataJson = gsonBuilder.toJson(loginDataMap);
    response.getWriter().println(loginDataJson);
  }
}
