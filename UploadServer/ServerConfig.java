public class ServerConfig {

   private static final ServerConfig instance = new ServerConfig();

   private final int port = 8082;
   private final String imagesFolder = "images";

   private ServerConfig() {
   }

   public static ServerConfig getInstance() {
      return instance;
   }

   public int getPort() {
      return port;
   }

   public String getImagesFolder() {
      return imagesFolder;
   }
}