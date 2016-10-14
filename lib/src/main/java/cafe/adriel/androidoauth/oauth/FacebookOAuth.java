package cafe.adriel.androidoauth.oauth;

import android.app.Activity;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.model.Verb;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

import cafe.adriel.androidoauth.model.SocialUser;

public final class FacebookOAuth extends BaseOAuth {
    private static final String DEFAULT_SCOPES = "public_profile email";
    private static final String GET_ACCOUNT_URL = "https://graph.facebook.com/v2.6/me?fields=name,email,picture,cover";
    private static final String REVOKE_TOKEN_URL = "https://graph.facebook.com/v2.6/me/permissions?access_token=%s";
    private static final Verb REVOKE_TOKEN_VERB = Verb.DELETE;

    private FacebookOAuth(Activity activity) {
        super(activity, FacebookApi.instance(), GET_ACCOUNT_URL, REVOKE_TOKEN_URL,
                REVOKE_TOKEN_VERB);
    }

    public static LoginOAuth login(Activity activity) {
        return new LoginOAuth(new FacebookOAuth(activity), DEFAULT_SCOPES);
    }

    public static LogoutOAuth logout(Activity activity) {
        return new LogoutOAuth(new FacebookOAuth(activity));
    }

    @Override
    protected SocialUser toAccount(String json) {
        try {
            JSONObject accountJson = new JSONObject(json);
            SocialUser account = new SocialUser();
            account.setId(accountJson.getString("id"));
            account.setName(accountJson.getString("name"));
            if (accountJson.has("email")) {
                account.setEmail(accountJson.getString("email"));
            }
            if (accountJson.has("picture")) {
                account.setPictureUrl(accountJson
                        .getJSONObject("picture")
                        .getJSONObject("data")
                        .getString("url"));
            }
            if (accountJson.has("cover")) {
                account.setCoverUrl(accountJson
                        .getJSONObject("cover")
                        .getString("source"));
            }
            if(accountJson.has("birthday")){
                try {
                    account.setBirthday(new SimpleDateFormat("MM/dd/yyyy")
                            .parse(accountJson.getString("birthday")));
                } catch (Exception e){ }
            }
            account.setProvider(OAuthProvider.FACEBOOK);
            return account;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}