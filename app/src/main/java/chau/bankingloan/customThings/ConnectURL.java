package chau.bankingloan.customThings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by com08 (21-Sep-16).
 */

public class ConnectURL {
    public final static int GET = 1;
    private final static int POST = 2;
    private static String response = null;

    public ConnectURL()
    {}

    public String makeServiceCall(String url, int method)
    {
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setAllowUserInteraction(false);
            connection.setInstanceFollowRedirects(true);

            int CONNECTION_TIMEOUT = 5000;
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            int SOCKET_TIMEOUT = 5000;
            connection.setReadTimeout(SOCKET_TIMEOUT);

            if(method == GET) {
                connection.setRequestMethod("GET");
                connection.connect();
                int resCode = connection.getResponseCode();

                StringBuilder sb = new StringBuilder();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(
                                    connection.getInputStream()));
                    while((response = bufferedReader.readLine()) != null)
                    {
                        sb.append(response);
                    }
                }
                connection.disconnect();
                response = sb.toString();
            }
            else if(method == POST)
            {
                connection.setRequestMethod("POST");
                connection.connect();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
