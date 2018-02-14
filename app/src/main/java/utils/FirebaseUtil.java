package utils;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by Admin on 30/12/2017.
 */

public class FirebaseUtil {

    public static String TEST_PHONE = "+917276856723";

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();



    //Docs
    //public static final String DOC_USERS = "users";
    public static final String DOC_USERS = "usersdemo";
    public static final String DOC_APPOINTMENTS = "appointments";
    public static final String DOC_CONFIG = "configuration";


    public static String getUid() {
        if(mAuth == null || mAuth.getCurrentUser() == null) {
            return null;
        }
        return mAuth.getCurrentUser().getUid();
    }

    public static String getMobile() {
        if(mAuth == null || mAuth.getCurrentUser() == null) {
            return null;
            //return TEST_PHONE;
        }
        return mAuth.getCurrentUser().getPhoneNumber();
    }

    public static boolean taskExists(@NonNull Task<DocumentSnapshot> task) {
        return task != null && task.isSuccessful() && task.getResult() != null && task.getResult().exists();
    }

    public static boolean resultExists(@NonNull Task<QuerySnapshot> task) {
        return task != null && task.getResult() != null && task.getResult().size() > 0;
    }

}
