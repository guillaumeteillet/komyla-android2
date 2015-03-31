package eip.com.lizz.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eip.com.lizz.Models.CreditCard;

public class UJsonToData {
    public static ArrayList<CreditCard> getCreditCardListFromJSON(String resultSet) throws JSONException {
        ArrayList<CreditCard> creditCards = new ArrayList<>();
        JSONArray jsonArray = null;

        if (resultSet == null) {
            return null;
        }
        jsonArray = new JSONArray(resultSet);
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject oneObject = jsonArray.getJSONObject(i);
            creditCards.add(new CreditCard(
                    oneObject.getString("id"),
                    "************" + oneObject.getString("cardNumberHidden"),
                    oneObject.getString("cardExpireMonth"),
                    oneObject.getString("cardExpireYear"),
                    "***",
                    oneObject.getString("cardHolder"),
                    oneObject.getString("cardName")
            ));
        }
        return creditCards;
    }
}
