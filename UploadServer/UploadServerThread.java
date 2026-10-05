import java.net.*;
import java.io.*;

public class UploadServerThread extends Thread {
   private Socket socket = null;

   public UploadServerThread(Socket socket) {
      super("DirServerThread");
      this.socket = socket;
   }

   public void run() {
      try {
         BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
         String firstLine = readLine(in);
         System.out.println(firstLine);
         String[] request = firstLine.split(" ");
         String method = request[0];
         String path = request[1];

         HttpServletRequest req = new HttpServletRequest(in);
         OutputStream baos = new ByteArrayOutputStream();
         HttpServletResponse res = new HttpServletResponse(baos);

         HttpServlet httpServlet = ServletFactory.createServlet("UploadServlet");
         
         if (method.equals("GET") && path.equals("/")) {
            LoggingAspect.before("GET");

            httpServlet.doGet(req, res);

            LoggingAspect.after("GET");
         } else if (method.equals("POST")) {
            LoggingAspect.before("POST");

            httpServlet.doPost(req, res);

            LoggingAspect.after("POST");
         }

         OutputStream out = socket.getOutputStream();

         out.write(((ByteArrayOutputStream) baos).toByteArray());
         out.flush();

         socket.close();

      } catch (Exception e) {
         e.printStackTrace();
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
