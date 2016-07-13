package ulmerkott.matdatabasen;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.PathMotion;
import android.transition.SidePropagation;
import android.transition.Slide;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MatInfoActivity extends AppCompatActivity {

    private Food FoodInfo;
    private PieChart MatChart;
    private TextView PortionText;

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

        PortionText = (TextView) findViewById(R.id.portionInfo);

        DatabaseAccess matDbAccess = DatabaseAccess.getInstance(this);
        FoodInfo = matDbAccess.GetFood(matRowId);

        MatInfoActivity.this.setTitle(FoodInfo.Name);
        final TextView extendedInfo = (TextView) findViewById(R.id.extentedInfo);
        extendedInfo.setText(FoodInfo.Info);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        registerForContextMenu(fab);

        final PopupMenu popup = new PopupMenu(MatInfoActivity.this, fab);
        popup.getMenuInflater().inflate(R.menu.menu_portions, popup.getMenu());
        // TODO: Add choice for customizable portion
        //popup.getMenu().add(R.string.custom_portion);
        for (String key: FoodInfo.Portions.keySet() ) {
            popup.getMenu().add(key);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Setup menu item selection
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = (String) item.getTitle();
                        FoodInfo.setPortion(FoodInfo.Portions.get(title));
                        //setChartData();
                        PortionText.setText(title);
                        CreatePieChart();

                        return true;
                    }
                });
                // Handle dismissal with: popup.setOnDismissListener(...);
                // Show the menu
                popup.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Transition enterTrans = new Fade();
        getWindow().setEnterTransition(enterTrans);

        Transition returnTrans = new Slide();
        getWindow().setReturnTransition(returnTrans);
        getWindow().setExitTransition(returnTrans);
        CreatePieChart();
    }

    private void CreatePieChart() {
        // Color same as default activity background color.
        int BACKGROUND_COLOR = Color.parseColor("#fafafa");

        MatChart = (PieChart) findViewById(R.id.chart);

        MatChart.setBackgroundColor(BACKGROUND_COLOR);

        MatChart.setUsePercentValues(true);
        MatChart.setDescription("");

        MatChart.setDrawHoleEnabled(true);
        MatChart.setHoleColor(BACKGROUND_COLOR);
        MatChart.setHoleRadius(60);
        MatChart.setTransparentCircleRadius(70);

        MatChart.setTransparentCircleColor(BACKGROUND_COLOR);
        MatChart.setTransparentCircleAlpha(110);

        MatChart.setDrawCenterText(true);

        MatChart.setRotationEnabled(false);
        MatChart.setHighlightPerTapEnabled(false);

        // Use half pie
        MatChart.setMaxAngle(180f);
        MatChart.setRotationAngle(180f);
        MatChart.setCenterTextOffset(0, -20);
        MatChart.setCenterTextSize(18);

        Legend legend = MatChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setYOffset(-50f);
        legend.setMaxSizePercent(50);
        legend.setFormSize(14);
        legend.setTextSize(12);

        setChartData();

        MatChart.animateY(1000, Easing.EasingOption.EaseOutCubic);

    }

    private void setChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (FoodInfo.Carb > 0) {
            entries.add(new PieEntry(FoodInfo.getCarbKcal(), "Kolhydrater " + RoundKcal(FoodInfo.getCarbKcal())));
        }
        if (FoodInfo.Fat > 0) {
            entries.add(new PieEntry(FoodInfo.getFatKcal(), "Fett " + RoundKcal(FoodInfo.getFatKcal())));
        }
        if (FoodInfo.Protein > 0) {
            entries.add(new PieEntry(FoodInfo.getProteinKcal(), "Protein " + RoundKcal(FoodInfo.getProteinKcal())));
        }
        if (FoodInfo.Alcohol > 0) {
            entries.add(new PieEntry(FoodInfo.getAlkoholKcal(), "Alkohol " + RoundKcal(FoodInfo.getAlkoholKcal())));
        }
        if (FoodInfo.Fiber > 0) {
            entries.add(new PieEntry(FoodInfo.getFiberKcal(), "Fiber " + RoundKcal(FoodInfo.getFiberKcal())));
        }

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);
        dataset.setDrawValues(false);
        PieData data = new PieData(dataset);
        MatChart.setDrawEntryLabels(false);
        MatChart.setCenterText(RoundKcal(FoodInfo.getTotalKcal()));
        MatChart.setData(data);
        MatChart.notifyDataSetChanged();
    }

    private String RoundKcal(float value) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(value) + " kcal";
    }
}

