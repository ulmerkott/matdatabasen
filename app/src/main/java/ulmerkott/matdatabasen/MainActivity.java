package ulmerkott.matdatabasen;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.widget.SearchView;
import android.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AlphabetIndexer;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;

// TODO:
// * Långsam start ibland
// * Infosida om livsmedel
// * expandable listview för att snabbt visa infon?

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    private ListView MatListView;
    private DatabaseAccess LivsmedelsDB;
    private Cursor DBCursor;
    private SearchView DBSearchView;
    private CursorAdapter DBCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Create a toolbar instead of the SearchVIew we have currently.. see:
                //http://stackoverflow.com/questions/32751024/fab-to-search-bar
                DBSearchView.setIconified(false);
                DBSearchView.requestFocus();
                DBSearchView.setVisibility(View.VISIBLE);
            }
        });

        DBSearchView = (SearchView) findViewById(R.id.searchView);

        MatListView = (ListView) findViewById(R.id.listView);
        LivsmedelsDB = DatabaseAccess.getInstance(this);
        LivsmedelsDB.open();
        DBCursor = LivsmedelsDB.getLivsmedel();
        DBCursorAdapter = new CursorAdapter(getApplicationContext(),
                R.layout.container_list_layout, DBCursor,
                new String[] {DatabaseAccess.KEY_NAME, "Energi (kcal)"},
                new int[] { R.id.list_item, R.id.sub_list_item });

        MatListView.setAdapter(DBCursorAdapter);

        DBSearchView.setIconifiedByDefault(true);
        DBSearchView.setOnQueryTextListener(this);
        DBSearchView.setSubmitButtonEnabled(true);
        DBSearchView.setQueryHint("Search Here");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LivsmedelsDB.close();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            MatListView.clearTextFilter();
        } else {
            DBCursorAdapter.getFilter().filter(newText);
        }
        return true;
    }

    class CursorAdapter extends SimpleCursorAdapter implements SectionIndexer {

        AlphabetIndexer alphaIndexer;

        public CursorAdapter(Context context, int layout, Cursor c,
                             String[] from, int[] to) {
            super(context, layout, c, from, to, 0);

            alphaIndexer = new AlphabetIndexer(c,
                    c.getColumnIndex(DatabaseAccess.KEY_NAME),
                    " ABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ");

            setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {
                    Cursor cursor = LivsmedelsDB.searchLivsmedel(constraint.toString());
                    alphaIndexer.setCursor(cursor);
                    return cursor;
                }
            });
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
