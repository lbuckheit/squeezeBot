import static org.junit.Assert.assertNull;

import org.junit.Test;
public class LambdaHandlerTest {
  @Test 
  public void getTweets() {
      LambdaHandler lambdaHandler = new LambdaHandler();

      assertNull(lambdaHandler.handleRequest());
  }
}
