package hcilab.pm_filtering_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

    Button buttonDiscover = null;
    Button buttonConnect = null;
    Button btnNext = null;
    EditText participantID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        participantID = (EditText) findViewById(R.id.txtParticipant);

        btnNext = (Button) findViewById(R.id.btnNext);


        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String participant = participantID.getText().toString();
                Intent i = new Intent(getApplicationContext(), CameraIntentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("send_participant", participant);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        });
    }
}

