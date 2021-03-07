import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class LambdaHandlerTest {
  @Test 
  public void getTweets() {
      LambdaHandler lambdaHandler = new LambdaHandler();

      assertEquals("abc", lambdaHandler.handleRequest());
  }
}
