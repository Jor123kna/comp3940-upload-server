public class LoggingAspect {

   public static void before(String method) {
      System.out.println("Starting " + method + " request");
   }

   public static void after(String method) {
      System.out.println("Finished " + method + " request");
   }
}