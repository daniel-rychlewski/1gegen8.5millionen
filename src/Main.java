import com.anti_captcha.Api.NoCaptchaProxyless;
import com.anti_captcha.Helper.DebugHelper;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Main {
    private static final String ZUSCHAUERWETTBEWERB = "http://w.srf.ch/1gegen100/1gegen100.php?mId=";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("resources.HansMüller");
    private static final ResourceBundle ANTI_CAPTCHA_BUNDLE = ResourceBundle.getBundle("resources.Config");

    private static final String SITE_CAPTCHA_KEY = "6LcPUHkUAAAAAGDgT_1QTkXEFjNeoFlHgwZPNLCS";
    private static final String IPHONE_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/70.0.3538.75 Mobile/15E148 Safari/605.1";

    public static void main(String[] args) {
        completePage(Optional.empty())
                .thenApplyAsync(Main::parseBodyForSId)
                .thenApplyAsync(sessionId -> completePage(Optional.of(sessionId)).join())
                .thenAcceptAsync(System.out::println)
                .join();
    }

    private static CompletableFuture<String> completePage(Optional<String> sId) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(sId.map(s -> constructSecondPageInput(sId.get())).orElseGet(Main::constructFirstPageInput)))
                .uri(URI.create(ZUSCHAUERWETTBEWERB))
                .header("User-Agent", IPHONE_USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded");

        if (sId.isPresent()) {
            requestBuilder = requestBuilder.header("Cookie", "sg_random_sample="+randomNumericString(2)+"; " +
                    "_pipe_c=do_not_track; " +
                    "ABTasty=uid="+generateAlphanumericString(16) +
                    "&fst="+getNumericTimestamp((int)(Math.random() * 1_000))+
                    "&pst="+getNumericTimestamp((int)(Math.random() * 1_000))+
                    "&cst="+getNumericTimestamp((int)(Math.random() * 1_000))+
                    "&ns="+randomNumericString(1)+
                    "&pvt="+randomNumericString(1)+
                    "&pvis="+randomNumericString(1)+
                    "&th="+randomNumericString(6)+"."+randomNumericString(1)+"."+randomNumericString(1)+
                    "."+randomNumericString(1)+"."+randomNumericString(1)+"."+randomNumericString(1)+
                    "."+getNumericTimestamp((int)(Math.random() * 1_000))+"."+getNumericTimestamp((int)(Math.random() * 1_000))+"."+randomNumericString(1)+"; " +
                    "_pipe_st="+getNumericTimestamp((int)(Math.random() * 1_000))+"; " +
                    "wt_cdbeid="+randomNumericString(1)+"; " +
                    "wt_geid="+randomNumericString(24)+"; " +
                    "wt_rla="+randomNumericString(15)+"%2C"+randomNumericString(1)+"%2C"+getNumericTimestamp((int)(Math.random() * 1_000))+"; " +
                    "PHPSESSID="+sId);
        }

        return client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body);
    }

    private static String constructFirstPageInput() {
        String sId = generateAlphanumericString(26);
        String captchaResponse = solveCaptcha();

        var body = Map.of("do", "contact",
        "ct_mobilnr", RESOURCE_BUNDLE.getString("natelnummer"),
        "g-recaptcha-response", captchaResponse, // e.g., "03AERD8XqOLPRvsG3UUr_9lXXD2tjf7vxRB8gPNlnTpigV8b90hHU76pe_zDugV74IXkodNmH9NgJDw9pO-SQF3Vd8Yad5Uc5Gymv17ev_tK4L7SbWsuNan435WNbx6z35Y3DAI56rzqRurOLXm9u26ke_xO14AShxt4Via3-CWq8AjsqjYNuh_V3KNO16mlAoMOtGxUKEGkOGPx2i7OasvKEPG80xX5Zf3CLYT5C6lO_pB4f_zgKhrmeymzZL_BD0aAV4O_0fKW-K5XTPd8SX_Y1dYiTngCjClA2UMhZPX6Fu7RiBdJKidV1oe8ckNukq1EhOpx1ywElNIy7pXEEbvHaBokQQBpkVx4LAMsUwwa9N1YTpaZ8kvtMhi8eJtElJclUf7vqO15SH" was valid on March 2nd, 2020
        "sId", sId, // e.g., "pq7tj0kn4lg9ggv8c32030rbce" was valid on March 2nd, 2020
        "sJavaScriptEnabled", "1");

        return convertToParameterList(body);
    }

    /**
     * Achieves document.getElementsByName("sId")[0].value in Java for the response body given.
     */
    private static String parseBodyForSId(String body) {
        // XML parser is extremely susceptible to errors (e.g., missing </img> tag), so parse in a simplistic manner
        String parseBegin = "<input type=\"hidden\" name=sId value=\"";
        int beginIndex = body.indexOf(parseBegin)+ parseBegin.length();
        int endIndex = body.indexOf("\"", beginIndex);
        return body.substring(beginIndex, endIndex);
    }

    /**
     * Uses the anti-captcha.com API for solving the ReCaptcha v2 on the page.
     */
    private static String solveCaptcha() {
        try {
            DebugHelper.setVerboseMode(true);

            NoCaptchaProxyless api = new NoCaptchaProxyless();
            api.setClientKey(ANTI_CAPTCHA_BUNDLE.getString("anti_captcha_key"));
            api.setWebsiteUrl(new URL(ZUSCHAUERWETTBEWERB));
            api.setWebsiteKey(SITE_CAPTCHA_KEY);

            if (!api.createTask()) {
                throw new UnsupportedOperationException("API v2 send failed. " + api.getErrorMessage());
            } else if (!api.waitForResult()) {
                throw new UnsupportedOperationException("Could not solve the captcha");
            } else {
                DebugHelper.out("Result: " + api.getTaskSolution().getGRecaptchaResponse(), DebugHelper.Type.SUCCESS);
                return api.getTaskSolution().getGRecaptchaResponse();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not solve captcha: "+e.getClass() + " "+ e.getMessage());
        }
    }

    private static String constructSecondPageInput(String sId) {
        var body = Map.ofEntries(
                Map.entry("do", "address"),
                Map.entry("ct_firstname", RESOURCE_BUNDLE.getString("vorname")),
                Map.entry("ct_lastname", RESOURCE_BUNDLE.getString("nachname")),
                Map.entry("ct_address", RESOURCE_BUNDLE.getString("strasse")),
                Map.entry("ct_zip", RESOURCE_BUNDLE.getString("plz")),
                Map.entry("ct_city", RESOURCE_BUNDLE.getString("stadt")),
                Map.entry("sId", sId), // e.g., "rdm0s87208l3rndb5bqbkpg0sm" was valid on March 2nd, 2020
                Map.entry("sJavaScriptEnabled", "1"),
                Map.entry("ct_mobilnr", RESOURCE_BUNDLE.getString("natelnummer")),
                Map.entry("ct_vote", ""),
                Map.entry("ct_captcha", "")
        );

        return convertToParameterList(body);
    }

    private static String convertToParameterList(Map<String, String> body) {
        StringBuilder postEntries = new StringBuilder();
        body.forEach((k, v) -> {
            if (postEntries.length() > 0) {
                postEntries.append("&");
            }
            postEntries.append(k+"="+v);
        });
        return postEntries.toString();
    }

    private static String generateAlphanumericString(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }

    private static String getNumericTimestamp(int lastThreeDigits) {
        return "" + ZonedDateTime.now().toEpochSecond() + String.format(String.format("%%0%dd", 3), lastThreeDigits);
    }

    private static String randomNumericString(int numberOfDigits) {
        StringBuilder myNumber = new StringBuilder();
        while (numberOfDigits > 0) {
            myNumber.append((int)(Math.random() * 10));
            numberOfDigits--;
        }
        return myNumber.toString();
    }
}
