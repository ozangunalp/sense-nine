package com.mechanitis.demo.sense.twitter.connector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import static java.net.URLEncoder.encode;

@SuppressWarnings("unused")
public class TwitterOAuth {
    private static final String HMAC_SHA1 = "HMAC-SHA1";
    private long nonce;
    private long timestampInSeconds;

    //from properties file
    private String accessTokenSecret;
    private String token;
    private String consumerKey;
    private String consumerSecret;

//    private String _ = null;

    public TwitterOAuth() {
        this.timestampInSeconds = System.currentTimeMillis() / 1000;
        this.nonce = timestampInSeconds + (new Random()).nextInt();
        loadProperties();
    }

    private void loadProperties() {
        Map<String, String> prop = System.getenv();
        consumerSecret = prop.get("CONSUMER_SECRET");
        accessTokenSecret = prop.get("ACCESS_TOKEN_SECRET");
        token = prop.get("TOKEN");
        consumerKey = prop.get("CONSUMER_KEY");
        if (!validateProperties()) {
            //if there aren't any environment variables, looks for the properties file
            loadFromPropertiesFile();
        }
        if (!validateProperties()) {
            throw new RuntimeException("You must define the Twitter security tokens either as environment " +
                                       "variables or in oauth.properties");
        }
    }

    private boolean validateProperties() {
        return !(consumerKey == null ||
                 accessTokenSecret == null ||
                 token == null ||
                 consumerSecret == null);
    }

    private void loadFromPropertiesFile() {
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("oauth.properties");
        try {
            prop.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not load oauth.properties", e);
        }
        consumerSecret = prop.getProperty("consumerSecret");
        accessTokenSecret = prop.getProperty("accessTokenSecret");
        token = prop.getProperty("token");
        consumerKey = prop.getProperty("consumerKey");
    }

    private String getBaseParams() {
        return "oauth_consumer_key=" + consumerKey + "&" +
               "oauth_nonce=" + String.valueOf(nonce) + "&" +
               "oauth_signature_method=" + HMAC_SHA1 + "&" +
               "oauth_timestamp=" + String.valueOf(timestampInSeconds) + "&" +
               "oauth_token=" + token + "&" +
               "oauth_version=1.0";
    }

    public String getAuthParams() {
        return "oauth_consumer_key=\"" + consumerKey + "\", " +
               "oauth_nonce=\"" + String.valueOf(nonce) + "\", " +
               "oauth_signature_method=\"" + HMAC_SHA1 + "\", " +
               "oauth_timestamp=\"" + String.valueOf(timestampInSeconds) + "\", " +
               "oauth_token=\"" + token + "\", " +
               "oauth_version=\"1.0\"";
    }

    private String getBaseString() {
        try {
            return "GET" + "&"
                   + encode("https://stream.twitter.com/1.1/statuses/sample.json", "UTF-8") + "&"
                   + encode(getBaseParams(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateSignature() {
        byte[] byteHMAC;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");

            String oauthSignature = encode(consumerSecret, "UTF-8")
                                    + "&" + encode(accessTokenSecret, "UTF-8");

            SecretKeySpec spec = new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
            mac.init(spec);
            byteHMAC = mac.doFinal(getBaseString().getBytes());
            return encode(Base64.getEncoder().encodeToString(byteHMAC), "UTF-8");
        } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to authenticate against Twitter", e);
        }
    }
}
