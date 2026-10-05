import java.io.*;
import java.util.Arrays;

public class UploadServlet extends HttpServlet {
   protected void doPost(HttpServletRequest request,
         HttpServletResponse response) {
      try {
         InputStream in = request.getInputStream();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         int contentLength = 0;
         String contentType = "";

         String line;

         // Read the HTTP headers
         while (!(line = readLine(in)).isEmpty()) {

            if (line.startsWith("Content-Length:")) {
               contentLength = Integer.parseInt(
                     line.substring("Content-Length:".length()).trim());
            }

            if (line.startsWith("Content-Type:")) {
               contentType = line.substring("Content-Type:".length()).trim();
            }
         }

         // Read exactly the multipart body
         byte[] content = new byte[1];
         int bytesRead;
         int totalBytesRead = 0;

         while (totalBytesRead < contentLength) {

            bytesRead = in.read(content);

            if (bytesRead == -1) {
               break;
            }

            baos.write(content, 0, bytesRead);
            totalBytesRead += bytesRead;
         }

         String boundary = "";

         if (contentType.contains("boundary=")) {
            boundary = "--" +
                  contentType.substring(
                        contentType.indexOf("boundary=") + 9);
         }

         String body = baos.toString("ISO-8859-1");

         String caption = "";

         String captionMarker = "name=\"caption\"\r\n\r\n";

         int captionStart = body.indexOf(captionMarker);

         if (captionStart != -1) {
            captionStart += captionMarker.length();

            int captionEnd = body.indexOf("\r\n" + boundary, captionStart);

            caption = body.substring(captionStart, captionEnd);
         }

         String date = "";

         String dateMarker = "name=\"date\"\r\n\r\n";

         int dateStart = body.indexOf(dateMarker);

         if (dateStart != -1) {
            dateStart += dateMarker.length();

            int dateEnd = body.indexOf("\r\n" + boundary, dateStart);

            date = body.substring(dateStart, dateEnd);
         }

         String fileName = "";

         String fileMarker = "name=\"fileName\"; filename=\"";

         int fileNameStart = body.indexOf(fileMarker);
         int fileNameEnd = -1;

         if (fileNameStart != -1) {
            fileNameStart += fileMarker.length();

            fileNameEnd = body.indexOf("\"", fileNameStart);

            fileName = body.substring(fileNameStart, fileNameEnd);
         }

         if (caption.isEmpty() || date.isEmpty() || fileName.isEmpty()) {
            throw new InvalidUploadException(
                  "Caption, date and file are required.");
         }

         int fileStart = body.indexOf("\r\n\r\n", fileNameEnd) + 4;

         int fileEnd = body.indexOf("\r\n" + boundary, fileStart);

         byte[] allBytes = baos.toByteArray();

         File imagesFolder = new File(
               ServerConfig.getInstance().getImagesFolder());

         if (!imagesFolder.exists()) {
            imagesFolder.mkdir();
         }

         String savedFileName = caption + "_" + date + "_" + fileName;

         FileOutputStream outputStream = new FileOutputStream(
               new File(imagesFolder, savedFileName));

         outputStream.write(
               allBytes,
               fileStart,
               fileEnd - fileStart);

         outputStream.close();

         PrintWriter out = new PrintWriter(response.getOutputStream(), true);

         String[] fileList = imagesFolder.list();

         if (fileList != null) {
            Arrays.sort(fileList, String.CASE_INSENSITIVE_ORDER);
         }

         out.println("HTTP/1.1 200 OK");
         out.println("Content-Type: text/html");
         out.println();
         out.println("<html>");
         out.println("<body>");
         out.println("<h2>Uploaded Files</h2>");
         out.println("<ul>");

         if (fileList != null) {
            Arrays.stream(fileList)
                  .forEach(file -> out.println("<li>" + file + "</li>"));
         }

         out.println("</ul>");
         out.println("</body>");
         out.println("</html>");

      } catch (InvalidUploadException ex) {

         try {
            PrintWriter out = new PrintWriter(response.getOutputStream(), true);

            out.println("HTTP/1.1 400 Bad Request");
            out.println("Content-Type: text/html");
            out.println("Connection: close");
            out.println();

            out.println("<html>");
            out.println("<body>");
            out.println("<h2>Upload Error</h2>");
            out.println("<p>" + ex.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");

         } catch (Exception e) {
            System.err.println(e);
         }

      } catch (Exception ex) {
         System.err.println(ex);
      }
   }

   protected void doGet(HttpServletRequest request,
         HttpServletResponse response) {
      try {
         PrintWriter out = new PrintWriter(response.getOutputStream(), true);

         String html = "<!DOCTYPE html>" +
               "<html>" +
               "<body>" +

               "<h2>HTML Forms</h2>" +

               "<form action=\"http://localhost:8082\" " +
               "method=\"post\" " +
               "enctype=\"multipart/form-data\">" +

               "Caption: " +
               "<input type=\"text\" name=\"caption\"/><br/><br/>" +

               "Date: " +
               "<input type=\"date\" name=\"date\"/><br/>" +

               "<input type=\"file\" name=\"fileName\"/><br/><br/>" +

               "<input type=\"submit\" value=\"Submit\"/>" +

               "</form>" +

               "</body>" +
               "</html>";

         out.println("HTTP/1.1 200 OK");
         out.println("Content-Type: text/html");
         out.println("Connection: close");
         out.println();
         out.println(html);

      } catch (Exception ex) {
         System.err.println(ex);
      }
   }

   private String readLine(InputStream in) throws IOException {
      StringBuilder line = new StringBuilder();

      int charector;

      while ((charector = in.read()) != -1) {
         if (charector == '\n') {
            break;
         }
         if (charector != '\r') {
            line.append((char) charector);
         }
      }
      return line.toString();
   }
}