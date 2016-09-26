package il.ac.huji.ins;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by nimoshe on 9/25/2016.
 */
public class AboutActivity extends Activity {

    public static final int ABOUT_ACTIVITY_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView aboutTxt = (TextView) findViewById(R.id.about_txt);
        aboutTxt.setText(Html.fromHtml(getString(R.string.about)));

        Button quit = (Button) findViewById(R.id.ok_btn_about);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });
    }
}
