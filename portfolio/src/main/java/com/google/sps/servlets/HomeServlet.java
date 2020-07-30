
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

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// import com.google.cloud.storage.Storage;
// import com.google.cloud.storage.StorageOptions;

// import com.google.appengine.api.blobstore.BlobInfo;
import com.google.cloud.storage.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.*;
import com.google.appengine.api.blobstore.*;
// import com.google.appengine.api.blobstore.BlobInfo.BlobId;
// import com.google.appengine.api.blobstore.BlobId;
import com.google.cloud.storage.Blob.Builder;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.google.cloud.storage.*;
import com.google.cloud.storage.BlobId;

import com.google.appengine.api.blobstore.BlobKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This servlet prints out the HTML for the homepage. You wouldn't do this in a real codebase, but
 * this is meant to demonstrate getting a Blobstore URL and using it in a form to allow a user to
 * upload a file.
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

  /**
   * Returns HTML that contains a form. The form submits to Blobstore, which redirects to our
   * /my-form-handler, which is handled by FormHandlerServlet.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the Blobstore URL
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl("/my-form-handler");

    response.setContentType("text/html");

    // This demonstrates creating a form that uses the Blobstore URL.
    // This is not how you'd do this in a real codebase!
    // See the hello-world-jsp or hello-world-fetch examples for more info.
    PrintWriter out = response.getWriter();
    out.println(
        "<form method=\"POST\" enctype=\"multipart/form-data\" action=\"" + uploadUrl + "\">");

    out.println("<p>Type some text:</p>");
    out.println("<textarea name=\"message\"></textarea>");
    out.println("<br/>");

    out.println("<p>Upload an image:</p>");
    out.println("<input type=\"file\" name=\"image\">");
    out.println("<br/><br/>");

    out.println("<button>Submit</button>");
    out.println("</form>");
    uploadObject("ben-step-2020", "test_submission_bucket", "weewoo", "/headshot.png");
  }

    public static void uploadObject(
      String projectId, String bucketName, String objectName, String filePath) throws IOException {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-objet-name";

    // The path to your file to upload
    // String filePath = "path/to/your/file"

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, objectName);
    // BlobInfo blobInfo = BlobInfo.Builder().setBlobId(blobId).build();
    // BlobInfo blobInfo = blobInfo.toBuilder().setBlobId(blobId).build();
    // BlobInfo blobInfo = BlobInfo.Builder.setBlobId(blobId).build();
    // BlobInfo blobInfo = new BlobInfo.Builder().setBlobId(blobId).build();
    // BlobInfo blobInfo = Blob.Builder.setBlobId(blobId).build();
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    // BlobInfo blobInfo = new BlobInfo.Builder().setBlobId(blobId).build();
    // BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

    System.out.println(
        "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
  }
}
