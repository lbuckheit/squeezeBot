  
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

// import service.DynamoService;
import service.ParameterStoreService;
import service.SlackService;
import service.TwitterService;
public class LambdaHandler implements RequestHandler<ScheduledEvent, Void> {
    private TwitterService twitterService;
    private SlackService slackService;
    // private DynamoService dynamoService;
    private ParameterStoreService parameterStoreService;
    // private static final String BOT_NAME = System.getenv("BOT_NAME");
    // private static final String SECRET_NAME = System.getenv("SECRET_NAME");
    // private static final String REGION = System.getenv("REGION");
    // private static final String REGION = System.getenv("us-east-2");

    // private static final String SEARCH_STRING = System.getenv("SEARCH_STRING");
    private static final String SEARCH_STRING = System.getenv("aws");

    public Void handleRequest(ScheduledEvent event, Context context) {
        ParameterStoreService parameterStoreService = new ParameterStoreService();
        if (parameterStoreService != null) {
            setupSecrets();
            
            List<String> tweetIds = null;
            try {
                tweetIds = twitterService.getTweets(SEARCH_STRING != null ? SEARCH_STRING : "aws", getTime());
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }

            if (null != tweetIds && tweetIds.size() > 0) {
                for (String id : tweetIds) {
                    String url = tweetAsUrl(id);

                    // if (null != dynamoService) ...

                    if (null != slackService) {
                        slackService.postMessage(url);
                    }
                }
            }
        }
        return null;
    }

    private void setupSecrets() {
        twitterService = new TwitterService(parameterStoreService.getParaValue("twitter-api-key"), parameterStoreService.getParaValue("twitter-api-secret"));
        slackService = new SlackService(parameterStoreService.getParaValue("slack-webhook-url"), "squeezeBot");
        // dynamoService = new DynamoService(parameterStoreService.getParaValue("dynamo-table-name"), parameterStoreService.getParaValue("dynamo-table-pk"), REGION != null ? REGION :  "us-east-2");
    }

    private String tweetAsUrl(String id) {
        return String.format("https://twitter.com/s/status/%s", id);
    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date(System.currentTimeMillis() - 3600 * 1000));
    }
}
