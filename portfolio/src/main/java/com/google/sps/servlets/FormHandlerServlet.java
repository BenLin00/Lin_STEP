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

package com.googl.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the user submits the form, Blobstore processes the file upload and then forwards the request
 * to this servlet. This servlet can then process the request using the file URL we get from
 * Blobstore.
 */
@WebServlet("/my-form-handler")
public class FormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the message entered by the user.
    String message = request.getParameter("message");

    // Get the URL of the image that the user uploaded to Blobstore.
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a URL. (devserver)
    if (blobKeys == null || blobKeys.isEmpty()) {
        response.sendRedirect("/");
    } else {
        response.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
    }

    // Output some HTML that shows the data the user entered.
    // A real codebase would probably store these in Datastore.
    PrintWriter out = response.getWriter();
    out.println("<p>Here's the image you uploaded:</p>");
    // out.println("<a href=\"" + imageUrl + "\">");
    // out.println("<img src=\"" + imageUrl + "\" />");
    out.println("</a>");
    out.println("<p>Here's the text you entered:</p>");
    out.println(message);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        // blobstoreService.serve(blobKey, res);
    }
  }

