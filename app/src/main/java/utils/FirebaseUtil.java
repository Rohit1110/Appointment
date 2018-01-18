package utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Admin on 30/12/2017.
 */

public class FirebaseUtil {

    public static String TEST_PHONE = "+918956711498";

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Docs
    public static final String DOC_USERS = "users";
    public static final String DOC_APPOINTMENTS = "appointments";


    public static String getUid() {
        if(mAuth == null || mAuth.getCurrentUser() == null) {
            return null;
        }
        return mAuth.getCurrentUser().getUid();
    }

    public static String getMobile() {
        if(mAuth == null || mAuth.getCurrentUser() == null) {
            return TEST_PHONE;
        }
        return mAuth.getCurrentUser().getPhoneNumber();
    }

}
