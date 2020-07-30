// package com.googl.sps.servlets;

// import com.google.appengine.api.blobstore.BlobInfo;
// import com.google.appengine.api.blobstore.BlobInfoFactory;
// import com.google.appengine.api.blobstore.BlobKey;
// import com.google.appengine.api.blobstore.BlobstoreService;
// import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
// import com.google.appengine.api.images.ImagesService;
// import com.google.appengine.api.images.ImagesServiceFactory;
// import com.google.appengine.api.images.ServingUrlOptions;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.net.MalformedURLException;
// import java.net.URL;
// import java.util.List;
// import java.util.Map;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.PreparedQuery;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.api.datastore.Query.SortDirection;
// import com.google.appengine.api.users.UserService;
// import com.google.appengine.api.users.UserServiceFactory;
// import com.google.appengine.api.datastore.FetchOptions;

// /**
//  * When the user submits the form, Blobstore processes the file upload and then forwards the request
//  * to this servlet. This servlet can then process the request using the file URL we get from
//  * Blobstore.
//  */
// @WebServlet("/my-form-handler")
// public class serveBlobClips extends HttpServlet {

//   @Override
//   public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


//     // Get the URL of the image that the user uploaded to Blobstore.
//     BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
//     Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
//     List<BlobKey> blobKeys = blobs.get("image");

//     // User submitted form without selecting a file, so we can't get a URL. (devserver)
//     if (blobKeys == null || blobKeys.isEmpty()) {
//         response.sendRedirect("/");
//     } else {
//         response.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
//     }
//     out.println("<img src=\"" + imageUrl + "\" />");
//   }

// //   public void doGet(HttpServletRequest req, HttpServletResponse res){}
//     // throws IOException {
//     //     BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
//     //     blobstoreService.serve(blobKey, res);
//     // }
//   }

//   // check Group Entity 
  
// import com.google.cloud.storage.BlobId;
// import com.google.cloud.storage.BlobInfo;
// import com.google.cloud.storage.Storage;
// import com.google.cloud.storage.StorageOptions;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;

// public class UploadObject {
//   public static void uploadObject(
//       String projectId, String bucketName, String objectName, String filePath) throws IOException {
//     // The ID of your GCP project
//     // String projectId = "your-project-id";

//     // The ID of your GCS bucket
//     // String bucketName = "your-unique-bucket-name";

//     // The ID of your GCS object
//     // String objectName = "your-object-name";

//     // The path to your file to upload
//     // String filePath = "path/to/your/file"

//     Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//     BlobId blobId = BlobId.of(bucketName, objectName);
//     BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//     storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

//     System.out.println(
//         "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
//   }
// }