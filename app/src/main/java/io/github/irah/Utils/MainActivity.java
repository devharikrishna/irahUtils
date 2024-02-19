package io.github.irah.Utils;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(irahUtils.isConnected(this)){
            /// Connection is Available
        }else{
            irahUtils.show_no_network_toast(this);
        }


        irahUtils.isValidPhoneNumber("phone_number");


    }
}
