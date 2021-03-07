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
public class LambdaHandler/* implements RequestHandler<ScheduledEvent, Void>*/ {
    private TwitterService twitterService;
    private SlackService slackService;
    // private DynamoService dynamoService;
    private ParameterStoreService parameterStoreService;
    // private static final String BOT_NAME = System.getenv("BOT_NAME");
    // private static final String SECRET_NAME = System.getenv("SECRET_NAME");
    // private static final String REGION = System.getenv("REGION");
    // private static final String REGION = System.getenv("us-east-2");

    // private static final String SEARCH_STRING = System.getenv("SEARCH_STRING");
    private static final String SEARCH_STRING = "awstest from:lukebuckheit";

    public Void handleRequest(/*ScheduledEvent event, Context context*/) {
        try {
            parameterStoreService = new ParameterStoreService();
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

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
        Date now = new Date();

        SimpleDateFormat twitterTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        twitterTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat usEasternTime = new SimpleDateFormat("HH:mm");
        usEasternTime.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEE");

        String currentDayOfWeek = dayOfWeek.format(now);
        Boolean isTradingDay = !currentDayOfWeek.equals("Sat") && !currentDayOfWeek.equals("Sun");

        Boolean isPreMarket = false;
        Boolean isAfterHours = false;
        try {
            Date currentEasternTime = usEasternTime.parse(usEasternTime.format(now));
            isPreMarket = currentEasternTime.before(usEasternTime.parse("09:00"));
            isAfterHours = currentEasternTime.after(usEasternTime.parse("16:30"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean isMarketOpen = isTradingDay && !isPreMarket && !isAfterHours;

        Integer checkInterval = 900; // AWS is calling this every 15min during market hours
        if (!isMarketOpen) {
            checkInterval = 64800; // If the market's closed check every 18 hours

            if (currentDayOfWeek == "Mon" && isPreMarket) {
                checkInterval = 216000; // If it's Monday premarket, we need to scan the entire weekend
            }
        }

        return twitterTimeFormat.format(new Date(System.currentTimeMillis() - checkInterval * 1000));
    }
}
