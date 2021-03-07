import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import service.ParameterStoreService;
import service.TwitterService;

import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TwitterServiceTest {

  private ParameterStoreService parameterStoreService = new ParameterStoreService();
  private TwitterService twitterService = new TwitterService(parameterStoreService.getParaValue("twitter-api-key"), parameterStoreService.getParaValue("twitter-api-secret"));
  @Test 
  public void notNull() {
      assertNotNull(twitterService);
  }

  @Test 
  public void getTweets() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

    List<String> tweetIds = null;
    try {
      tweetIds = twitterService.getTweets("aws", sdf.format(new Date(System.currentTimeMillis() - 540 * 1000)));
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
    }

    assertNotNull(tweetIds);
  }
}
