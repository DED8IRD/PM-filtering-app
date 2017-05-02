package hcilab.pm_filtering_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class RatingActivity extends Activity {

    RatingBar rb;
    TextView val;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        rb = (RatingBar) findViewById(R.id.ratingBar);
        val = (TextView) findViewById(R.id.tvValue);
        confirm = (Button) findViewById(R.id.btnConfirm);

        final double[] rate = {-1};

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                val.setText("Value is " + v);
                rate[0] = v;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("key_rating", rate[0]);
                setResult(RESULT_OK, i);
                finish();
            }
        });



    }

}
