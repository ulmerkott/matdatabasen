package ulmerkott.matdatabasen;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MatInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mat_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String matRowId = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            matRowId = extras.getString(DatabaseAccess.INTENT_ROW_ID);
        }

        DatabaseAccess matDbAccess = DatabaseAccess.getInstance(this);
        final Food food = matDbAccess.GetFood(matRowId);

        MatInfoActivity.this.setTitle(food.Name);
        TextView extendedInfo = (TextView) findViewById(R.id.extentedInfo);
        extendedInfo.setText(food.Info);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        registerForContextMenu(fab);

        final PopupMenu popup = new PopupMenu(MatInfoActivity.this, fab);
        popup.getMenuInflater().inflate(R.menu.menu_portions, popup.getMenu());
        popup.getMenu().add(R.string.custom_portion);
        for (String key: food.Portions.keySet() ) {
            popup.getMenu().add(key);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Setup menu item selection
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = (String) item.getTitle();
                        Toast.makeText(MatInfoActivity.this, title + " " + food.Portions.get(title).toString() + " g", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                // Handle dismissal with: popup.setOnDismissListener(...);
                // Show the menu
                popup.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Transition enterTrans = new Slide();
        getWindow().setEnterTransition(enterTrans);

        Transition returnTrans = new Fade();
        getWindow().setReturnTransition(returnTrans);
        getWindow().setExitTransition(returnTrans);

    }
}
