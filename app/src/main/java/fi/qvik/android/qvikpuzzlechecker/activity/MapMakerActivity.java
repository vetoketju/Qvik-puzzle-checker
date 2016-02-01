package fi.qvik.android.qvikpuzzlechecker.activity;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import fi.qvik.android.qvikpuzzlechecker.Config;
import fi.qvik.android.qvikpuzzlechecker.LevelUtil;
import fi.qvik.android.qvikpuzzlechecker.QvikPuzzleApplication;
import fi.qvik.android.qvikpuzzlechecker.R;
import fi.qvik.android.qvikpuzzlechecker.game.BoardView;
import fi.qvik.android.qvikpuzzlechecker.model.Level;

public class MapMakerActivity extends AppCompatActivity implements View.OnClickListener {

    Level level;

    @Bind(R.id.board) BoardView board;

    @Bind(R.id.radioEmpty) RadioButton radioEmpty;
    @Bind(R.id.radioWall) RadioButton radioWall;
    @Bind(R.id.radioStart) RadioButton radioStart;
    @Bind(R.id.radioGoal) RadioButton radioGoal;

    @Bind(R.id.saveButton) Button save;

    @Bind(R.id.bt_fill_empty) Button fill_empty;
    @Bind(R.id.bt_fill_wall) Button fill_wall;
    @Bind(R.id.bt_playerorientation) Button playerOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_maker);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        board.setEditMode(true);

        radioEmpty.setChecked(true);

        radioEmpty.setButtonTintList(ColorStateList.valueOf(Config.COLOR_BACKGROUND));
        radioWall.setButtonTintList(ColorStateList.valueOf(Config.COLOR_WALL));
        radioStart.setButtonTintList(ColorStateList.valueOf(Config.COLOR_START));
        radioGoal.setButtonTintList(ColorStateList.valueOf(Config.COLOR_GOAL));



        // See if we are to edit an existing level
        if(getIntent().hasExtra("level")){
            String levelName = getIntent().getStringExtra("level");
            level = QvikPuzzleApplication.getInstance().getLevelUtil().loadLevelFromDisk(levelName);
        }else{ // or are we creating a new level.
            // use the original/default
            level = QvikPuzzleApplication.getInstance().getLevelUtil().getDefaultLevel();
            if(level == null){
                level = new Level();
            }
            // Generate name with timestamp
            level.setName("Level_" + new Date().getTime());
        }

        if(level != null){
            board.setLevel(level);
            int[] start = level.getStart();
            board.setPlayerStart(start[1], start[0], level.getStartOrientation());
        }else{
            Toast.makeText(MapMakerActivity.this, "Level load failed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fill_empty.setOnClickListener(this);
        save.setOnClickListener(this);
        fill_wall.setOnClickListener(this);
        playerOrientation.setOnClickListener(this);

    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();
        if(checked){
            switch(view.getId()) {
                case R.id.radioEmpty:
                    board.setBrush(Config.SQUARE_EMPTY);
                    break;
                case R.id.radioWall:
                    board.setBrush(Config.SQUARE_WALL);
                    break;
                case R.id.radioStart:
                    board.setBrush(Config.SQUARE_START);
                    break;
                case R.id.radioGoal:
                    board.setBrush(Config.SQUARE_GOAL);
                    break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == save.getId()){
            AlertDialog.Builder builder = new AlertDialog.Builder(MapMakerActivity.this);
            builder.setTitle("Confirmation");

            final EditText input = new EditText(MapMakerActivity.this);
            input.setText(level.getName());

            builder.setView(input);

            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();
                    level.setName(name);
                    boolean success = QvikPuzzleApplication.getInstance().getLevelUtil().saveLevelToDisk(level);

                    if(success){
                        Toast.makeText(MapMakerActivity.this, "Level saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MapMakerActivity.this, "Level save failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }else if(v.getId() == fill_empty.getId()){
            board.fill(Config.SQUARE_EMPTY);
        }else if(v.getId() == fill_wall.getId()){
            board.fill(Config.SQUARE_WALL);
        }else if(v.getId() == playerOrientation.getId()){
            int tmp = board.getPlayerOrientation() + 1;
            if(tmp > 3) tmp = 0;
            board.setPlayerOrientation(tmp);
        }
    }
}
