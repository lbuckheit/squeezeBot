package service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class TwitterService {
  private String apiKey;
  private String apiSecret;

  public TwitterService(String key, String secret) {
    apiKey = key;
    apiSecret = secret;
  }

  public <List>String getTweets(String searchString, String startTime) throws IOException, URISyntaxException {
    List<String> tweets = new ArrayList<>();
    if (validParameters(apiKey, apiSecret)) {
      Map<String, Object> map = search(searchString, startTime);
      if (null != map) {
        ArrayList<Object> data = (ArrayList<Object>) map.get("data");
        for (int i = 0; i < data.size(); i++) {
          Map<String, Object> item = (Map<String, Object>) data.get(i);
          String id = (String) item.get("id");
          tweets.add(id)
        }
      }
    }
    return tweets;
  }
}