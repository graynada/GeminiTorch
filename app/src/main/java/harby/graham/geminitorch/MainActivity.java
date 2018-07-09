package harby.graham.geminitorch;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    Context context;
    NotificationManager mNotificationManager;
    Method method;
    String TAG = "GeminiTorch";
    Button button;
    int on;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        reflect(context);

        sp = getApplicationContext().getSharedPreferences("harby.graham.geminitorch", MODE_PRIVATE);
        button = (Button) findViewById(R.id.button);
        if(sp.contains("ON")){
            on = sp.getInt("ON", 0);
        }
        else{
            on = 0;
        }
        button.setText(setButtonText());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(on == 0){
                    on = 1;
                    button.setText(setButtonText());
                }
                else{
                    on = 0;
                    button.setText(setButtonText());
                }
                for(int i = 1; i <= 5; i++){
                    onOne(i);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("ON", on);
        editor.apply();
    }

    String setButtonText(){
        if(on == 0){
            return "On";
        }
        else {
            return "Off";
        }
    }

    public void reflect(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            method = mNotificationManager.getClass().getMethod("openLed", int.class, int.class, int.class, int.class, int.class);
        } catch (SecurityException e) {
            Log.e(TAG, "Security prevents reflection");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "No method");
        }

    }

    void onOne(int led){
        try {
            method.invoke(mNotificationManager, led, on, on, on, 0);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException");
        }
    }

}
