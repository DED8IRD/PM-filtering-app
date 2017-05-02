package hcilab.pm_filtering_app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

public class RatingActivity extends Activity {

    RatingBar rb;
    TextView val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        rb = (RatingBar) findViewById(R.id.ratingBar);
        val = (TextView) findViewById(R.id.tvValue);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                val.setText("Value is " + v);
            }
        });

    }

}
