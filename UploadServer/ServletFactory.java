public class ServletFactory {

   public static HttpServlet createServlet(String className) throws Exception {

      Class<?> servletClass = Class.forName(className);

      return (HttpServlet) servletClass
            .getDeclaredConstructor()
            .newInstance();
   }
}