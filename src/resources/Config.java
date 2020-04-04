package resources;

import java.util.ListResourceBundle;

public class Config extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"anti_captcha_key", "YOUR_ANTI_CAPTCHA_KEY_GOES_HERE"}
        };
    }
}