package eip.com.lizz.Protocols;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eip.com.lizz.Utils.UCrypt;

/**
 * Created by guillaume on 20/03/15.
 */
public class PED {

    static String[] salts  = {"Neil Young", "The Mavericks", "Aaron Watson", "Poco", "Johnny Cash", "Calvin Russel", "Elton John", "Hank Williams",
                              "Marty Robbins", "Dierks Bentley", "Jordi Puig", "Clément Fournier", "Wild Sofia", "Eric Fischi", "Madame Poitrine",
                              "Maxime Lich", "Dian Hankson", "Camille Saferis", "Gilles Neret", "Frank De Mulder", "Horny Matches", "iHookup",
                              "Passion", "Adult Friend Finder", "Get It On", "Ashley Madison", "Friend Finder", "Friend Finder-X", "Plenty Of Fish",
                              "Match"};

    public static String cryptped(String id_payement, String PIN, String email_sender, String receiver, String id_user, String amount, Context context)
    {
        char[] pinArray = PIN.toCharArray();
        String key = "-1BF9K2"+id_payement+pinArray[1]+"X9IAJS02K"+email_sender+"S01NZEE2JFB"+pinArray[3]+"--HB2S"+pinArray[0]+"N1B234"+receiver+"  "+pinArray[2]+id_user+amount+"KQJ34HZ211";

        return sha256(key, id_user, amount, id_payement, receiver, context);
    }

    private static String sha256(String key, String id_user, String amount, String id_payement, String receiver, Context context)
    {
        int i = 0;
        while (i < 30) {
            key = UCrypt.bin2hex(UCrypt.getHash(key + salts[i]));
            i++;
        }
        return print_secure(key, id_user, amount, id_payement, receiver, context);
    }

    private static String print_secure(String key, String id_user, String amount, String id_payement, String receiver, final Context context)
    {
       try {
            key = UCrypt.RSAEncrypt(id_user + "-" + amount + "-" + key, context, "PED");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException | InvalidKeySpecException | PackageManager.NameNotFoundException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        return rsa_secure_format(key, id_payement, receiver);
    }

    private static String rsa_secure_format(String key_secure, String id_payement, String receiver) {
        String final_message = "=P="+id_payement+"="+UCrypt.random(2)+key_secure+UCrypt.random(1)+receiver+"=";

        return Base64.encodeToString(final_message.getBytes(), Base64.DEFAULT);
    }
}
