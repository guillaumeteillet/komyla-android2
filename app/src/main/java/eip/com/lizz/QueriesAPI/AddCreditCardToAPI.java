package eip.com.lizz.QueriesAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
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


public class                    AddCreditCardToAPI extends AsyncTask<CreditCard, Void, String> {
    private final String        _tokenCSRF;
    private final Context       _context;
    private List<Cookie>        _cookies;

    public AddCreditCardToAPI(String _tokenCSRF, Context _context) {
        this._tokenCSRF = _tokenCSRF;
        this._context = _context;
    }



    private String uploadCreditCard(CreditCard creditCard) throws Exception {

        JSONObject jsonObject = null; // !!! Utilité ?

        String url = _context.getResources().getString(R.string.url_api_final_v1)
                + _context.getResources().getString(R.string.url_api_add_creditCard);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        _cookies = Cookies.loadSharedPreferencesCookie(_context);
        httpClient.getCookieStore().addCookie(_cookies.get(0));
        HttpPost httppost = new HttpPost(url);

        String json;
        InputStream inputStream = null;

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("_csrf", _tokenCSRF);
        dataToSend.put("cardName", creditCard.get_displayName());
        dataToSend.put("cardType", "PlaceHolder");
        dataToSend.put("cardHolder", creditCard.get_cardHolder());
        dataToSend.put("cardNumber", creditCard.get_cardNumber());
        dataToSend.put("cardVerificationValue", creditCard.get_cryptogram());
        dataToSend.put("cardExpireMounth", creditCard.get_expirationDateMonth());
        dataToSend.put("cardExpireYear", creditCard.get_expirationDateYear());
        dataToSend.put("cardMounthLimit", "0");

        json = dataToSend.toString();

        StringEntity stringEntity = new StringEntity(json);

        httppost.setEntity(stringEntity);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");

        HttpResponse httpResponse = httpClient.execute(httppost);
        inputStream = httpResponse.getEntity().getContent();

        int responseCode = httpResponse.getStatusLine().getStatusCode();
        Log.d("GetPaymentMethods", "Le code de retour est: " + responseCode +
                " et le contenu est: " + API.convertStreamToString(inputStream));

        return Integer.toString(responseCode);
    }

    @Override
    protected String doInBackground(CreditCard... params) {
        try {
            return uploadCreditCard(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
