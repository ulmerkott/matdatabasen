package ulmerkott.matdatabasen;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.animation.AlphaAnimation;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toolbar;
import android.view.View;
import android.widget.AlphabetIndexer;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;

// TODO:
// * Långsam start ibland
// * Infosida om livsmedel
// * Klickar man på sök medan den animerar så kan man hamna i ett läge där FAB är osynlig i ej sökläge.
//   --> I onclick för FAB så ska man nog kolla om sök är aktivt...

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    private ListView MatListView;
    private DatabaseAccess LivsmedelsDB;
    private Cursor DBCursor;
    private SearchView DBSearchView;
    private CursorAdapter DBCursorAdapter;
    private FloatingActionButton Fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);


        Fab = (FloatingActionButton) findViewById(R.id.fab);
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSearchView(true);
            }
        });
        DBSearchView = (SearchView) findViewById(R.id.searchView);

        DBSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (DBSearchView.getQuery().toString().isEmpty()) {
                    ShowSearchView(false);
                }
                else {
                    DBSearchView.setQuery("",true);
                }
                return true;
            }
        });

        MatListView = (ListView) findViewById(R.id.listView);
        LivsmedelsDB = DatabaseAccess.getInstance(this);
        LivsmedelsDB.open();
        DBCursor = LivsmedelsDB.GetLivsmedel();
        DBCursorAdapter = new CursorAdapter(getApplicationContext(),
                R.layout.container_list_layout, DBCursor,
                new String[] {DatabaseAccess.KEY_NAME, DatabaseAccess.KEY_ENERGY},
                new int[] { R.id.list_item, R.id.sub_list_item });

        MatListView.setAdapter(DBCursorAdapter);

        DBSearchView.setIconifiedByDefault(true);
        DBSearchView.setOnQueryTextListener(this);
        MatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long row_id) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, MatInfoActivity.class);
                intent.putExtra(DatabaseAccess.INTENT_ROW_ID, String.valueOf(row_id));
                startActivity(intent, options.toBundle());
            }
        });

        Transition exitTrans = new Fade();
        getWindow().setExitTransition(exitTrans);

        Transition reenterTrans = new Slide();
        getWindow().setReenterTransition(reenterTrans);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LivsmedelsDB.close();
    }

    @Override
    public void onBackPressed() {
        if (DBSearchView.getVisibility() == View.VISIBLE) {
            ShowSearchView(false);
        } else {
            super.onBackPressed();
        }
    }

    private void ShowSearchView(boolean show) {
        if (show) {
            AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
            animation1.setDuration(500);
            animation1.setFillAfter(true);
            DBSearchView.startAnimation(animation1);
            DBSearchView.setIconified(false);
            DBSearchView.requestFocus();
            DBSearchView.setVisibility(View.VISIBLE);
            Fab.animate()
                    .setStartDelay(500)
                    .scaleX(0)
                    .scaleY(0)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
            Fab.hide();
        }
        else {
            AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
            animation1.setDuration(500);
            animation1.setFillAfter(false);
            DBSearchView.startAnimation(animation1);
            DBSearchView.setVisibility(View.GONE);
            Fab.show();
            Fab.animate()
                    .setStartDelay(500)
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        DBCursorAdapter.getFilter().filter(newText);
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
                    Cursor cursor = LivsmedelsDB.SearchLivsmedel(constraint.toString());
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
