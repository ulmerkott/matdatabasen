package ulmerkott.matdatabasen;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    private ListView MatListView;
    private DatabaseAccess LivsmedelsDB;
    private Cursor DBCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.search, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        MatListView = (ListView)findViewById(R.id.listView);
        LivsmedelsDB = DatabaseAccess.getInstance(this);
        LivsmedelsDB.open();
        DBCursor = LivsmedelsDB.getLivsmedel();

        MatListView.setAdapter(
                new CursorAdapter(getApplicationContext(),
                        R.layout.container_list_layout, DBCursor,
                        new String[] {DatabaseAccess.KEY_NAME, "Energi (kcal)"},
                        new int[] { R.id.list_item, R.id.sub_list_item })
        );

        LivsmedelsDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class CursorAdapter extends SimpleCursorAdapter implements SectionIndexer {

        AlphabetIndexer alphaIndexer;

        public CursorAdapter(Context context, int layout, Cursor c,
                             String[] from, int[] to) {
            super(context, layout, c, from, to, 0);

            alphaIndexer = new AlphabetIndexer(c,
                    c.getColumnIndex(DatabaseAccess.KEY_NAME),
                    " ABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ");
        }

        @Override
        public int getPositionForSection(int section) {
            // TODO Auto-generated method stub
            return alphaIndexer.getPositionForSection(section);
        }

        @Override
        public int getSectionForPosition(int position) {
            // TODO Auto-generated method stub
            return alphaIndexer.getSectionForPosition(position);
        }

        @Override
        public Object[] getSections() {
            // TODO Auto-generated method stub
            return alphaIndexer.getSections();
        }

    }
}
