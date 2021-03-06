  
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

import service.DynamoService;
import service.SlackService;
import service.TwitterService;
public class LambdaHandler {
    private TwitterService twitterService;
    private SlackService slackService;
    private static final String BOT_NAME = System.getenv("BOT_NAME");
    private static final String SECRET_NAME = System.getenv("SECRET_NAME");
    private static final String REGION = System.getenv("REGION");
    private static final String SEARCH_STRING = System.getenv("SEARCH_STRING");

    public Void handleRequest(ScheduledEvent event, Context context) {
        Se
    }
}
