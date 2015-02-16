package eip.com.lizz.QueriesAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import eip.com.lizz.API;
import eip.com.lizz.Cookies;
import eip.com.lizz.Models.CreditCard;
import eip.com.lizz.R;


public class                    DeleteCreditCardFromAPI extends AsyncTask<CreditCard, Void, String> {
    private final String        _tokenCSRF;
    private final Context       _context;
    private List<Cookie>        _cookies;

    public DeleteCreditCardFromAPI(String _tokenCSRF, Context _context) {
        this._tokenCSRF = _tokenCSRF;
        this._context = _context;
    }

    private String removeCreditCard(CreditCard creditCard) throws Exception {

        String url = _context.getResources().getString(R.string.url_api_final_v1)
                + _context.getResources().getString(R.string.url_api_delete_creditCard)
                + creditCard.get_id();

        Log.d("RemovePaymentMethods", "Envoie de la requête à l'url : " + url);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        _cookies = Cookies.loadSharedPreferencesCookie(_context);
        httpClient.getCookieStore().addCookie(_cookies.get(0));
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        HttpResponse httpResponse = httpClient.execute(httpGet);
        InputStream inputStream = httpResponse.getEntity().getContent();

        int responseCode = httpResponse.getStatusLine().getStatusCode();
        Log.d("RemovePaymentMethods", "Le code de retour est: " + responseCode +
                " et le contenu est: " + API.convertStreamToString(inputStream));

        return Integer.toString(responseCode);
    }

    @Override
    protected String doInBackground(CreditCard... params) {
        try {
            return removeCreditCard(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
