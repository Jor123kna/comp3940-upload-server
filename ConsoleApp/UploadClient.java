import java.io.*;
import java.net.*;

public class UploadClient {

    public UploadClient() {
    }

    public String uploadFile() {

        String listing = "";

        try {

            String caption = "Android Logo";
            String date = "2026-10-04";
            String fileName = "AndroidLogo.png";

            String boundary = "----MyBoundary123";


            // Build multipart body
            ByteArrayOutputStream body =
                    new ByteArrayOutputStream();


            // Caption
            body.write(("--" + boundary + "\r\n").getBytes());

            body.write(
                    "Content-Disposition: form-data; name=\"caption\"\r\n\r\n"
                            .getBytes()
            );

            body.write(caption.getBytes());

            body.write("\r\n".getBytes());


            // Date
            body.write(("--" + boundary + "\r\n").getBytes());

            body.write(
                    "Content-Disposition: form-data; name=\"date\"\r\n\r\n"
                            .getBytes()
            );

            body.write(date.getBytes());

            body.write("\r\n".getBytes());


            // File
            body.write(("--" + boundary + "\r\n").getBytes());

            body.write(
                    ("Content-Disposition: form-data; name=\"fileName\"; filename=\""
                            + fileName + "\"\r\n").getBytes()
            );

            body.write(
                    "Content-Type: image/png\r\n\r\n".getBytes()
            );

            FileInputStream fis =
                    new FileInputStream(fileName);

            byte[] fileBytes = fis.readAllBytes();

            fis.close();

            body.write(fileBytes);

            body.write("\r\n".getBytes());


            // End multipart data
            body.write(
                    ("--" + boundary + "--\r\n").getBytes()
            );


            // Connect to UploadServer
            Socket socket =
                    new Socket("localhost", 8082);

            OutputStream out =
                    socket.getOutputStream();

            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );


            // HTTP request headers
            String headers =
                    "POST / HTTP/1.1\r\n" +
                    "Host: localhost:8082\r\n" +
                    "Content-Type: multipart/form-data; boundary="
                            + boundary + "\r\n" +
                    "Content-Length: " + body.size() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";


            // Send request
            out.write(headers.getBytes());

            out.write(body.toByteArray());

            out.flush();


            // Read server response
            String line;

            while ((line = in.readLine()) != null) {
                listing += line + "\n";
            }


            socket.close();

        } catch (Exception e) {

            System.err.println(e);
        }

        return listing;
    }
}