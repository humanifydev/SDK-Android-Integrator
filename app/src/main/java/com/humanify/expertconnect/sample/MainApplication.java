package com.humanify.expertconnect.sample;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;

import com.humanify.expertconnect.ExpertConnect;
import com.humanify.expertconnect.ExpertConnectConfig;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainApplication extends Application {

    private static final String TOKEN_ENDPOINT = "http://api.ce03.humanify.com/identityDelegate/v1/tokens";
    private static final String TOKEN = "22e89580-a307-4e90-827d-2cae1009112e";
    private static final String API_ENDPOINT = "http://api.ce03.humanify.com";
    private static final String CLIENT_ID = "henry";
    private static final String CLIENT_SECRET = "secret123";

    @Override
    public void onCreate() {
        super.onCreate();

        // hard coded token
        // configureWithUserToken(TOKEN);

        // default auth server
        configureWithDefaultAuthServer();

        // identity delegate server
        // new RetrieveUserIdentityTokenTask().execute();
    }

    private void configureWithUserToken(String userToken) {
        ExpertConnect.getInstance(this).setConfig(new ExpertConnectConfig()
                .setMainNavigationClass(SampleActivity.class)
                .setEndpoint(API_ENDPOINT)
                .setUserIdentityToken(userToken));
    }

    public void configureWithDefaultAuthServer() {
        ExpertConnect.getInstance(this).setConfig(new ExpertConnectConfig()
                .setMainNavigationClass(SampleActivity.class)
                .setEndpoint(API_ENDPOINT)
                .setCredentials(MainApplication.CLIENT_ID, MainApplication.CLIENT_SECRET));
    }

    private class RetrieveUserIdentityTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String newAuthUrl = Uri.parse(TOKEN_ENDPOINT).buildUpon()
                    .appendQueryParameter("username", "f")
                    .toString();

            Request newAuthRequest = new Request.Builder()
                    .url(newAuthUrl)
                    .get()
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();

                Response newAuthResponse = client.newCall(newAuthRequest).execute();
                if (newAuthResponse.isSuccessful()) {
                    String userToken = newAuthResponse.body().string();
                    return userToken;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String userToken) {
            if (userToken == null) {
                configureWithDefaultAuthServer();
            } else {
                configureWithUserToken(userToken);
            }
        }
    }
}
