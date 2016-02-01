package fi.qvik.android.qvikpuzzlechecker.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import fi.qvik.android.qvikpuzzlechecker.Config;
import fi.qvik.android.qvikpuzzlechecker.QvikPuzzleApplication;
import fi.qvik.android.qvikpuzzlechecker.R;
import fi.qvik.android.qvikpuzzlechecker.SolutionChecker;
import fi.qvik.android.qvikpuzzlechecker.game.BoardView;
import fi.qvik.android.qvikpuzzlechecker.game.animation.MoveFrame;
import fi.qvik.android.qvikpuzzlechecker.game.animation.OrientationFrame;
import fi.qvik.android.qvikpuzzlechecker.game.animation.PlayerAnimator;
import fi.qvik.android.qvikpuzzlechecker.model.Level;
import fi.qvik.android.qvikpuzzlechecker.view.FunctionInput;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, SolutionChecker.PlayerListener, FunctionInput.Listener, PlayerAnimator.AnimationListener {

    SolutionChecker checker;
    @Bind(R.id.board) BoardView board;
    Level level;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Bind(R.id.inputFunctionMain) FunctionInput inputFunctionMain;
    @Bind(R.id.inputFunction1) FunctionInput inputFunction1;
    @Bind(R.id.inputFunction2) FunctionInput inputFunction2;

    @Bind(R.id.bt_fwd) ImageButton buttonFwd;
    @Bind(R.id.bt_back) ImageButton buttonBack;
    @Bind(R.id.bt_left) ImageButton buttonLeft;
    @Bind(R.id.bt_right) ImageButton buttonRight;
    @Bind(R.id.bt_f1) ImageButton buttonF1;
    @Bind(R.id.bt_f2) ImageButton buttonF2;

    @Bind(R.id.buttonCheck) Button buttonCheck;
    @Bind(R.id.buttonErase) Button buttonErase;
    @Bind(R.id.buttonReset) Button buttonReset;
    @Bind(R.id.buttonAnimate) Button buttonAnimate;

    PlayerAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        inputFunctionMain.setListener(this);
        inputFunction1.setListener(this);
        inputFunction2.setListener(this);

        buttonFwd.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
        buttonRight.setOnClickListener(this);
        buttonF1.setOnClickListener(this);
        buttonF2.setOnClickListener(this);
        buttonCheck.setOnClickListener(this);
        buttonErase.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
        buttonAnimate.setOnClickListener(this);


        // See if we are to edit an existing level
        if(getIntent().hasExtra("level")){
            String levelName = getIntent().getStringExtra("level");
            level = QvikPuzzleApplication.getInstance().getLevelUtil().loadLevelFromDisk(levelName);
        }else{ // or are we creating a new level.
            // use the original/default
            level = QvikPuzzleApplication.getInstance().getLevelUtil().getDefaultLevel();
        }

        if(level != null){
            setTitle(level.getName());
            board.setLevel(level);

            // Move to boardView?
            int[] start = level.getStart();
            board.setPlayerStart(start[1], start[0], level.getStartOrientation());

            checker = new SolutionChecker(level);
            checker.setListener(this);

            animator = new PlayerAnimator(board);
            animator.setAnimationListener(this);

        }else{
            Toast.makeText(GameActivity.this, "Level load failed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }

    private void doCheck(){

        animator.reset(true);

        checker.setF_main(inputFunctionMain.getCommands());
        checker.setF_1(inputFunction1.getCommands());
        checker.setF_2(inputFunction2.getCommands());

        int score = inputFunctionMain.getCommands().length + inputFunction1.getCommands().length + inputFunction2.getCommands().length;

        if(checker.check()){
            Toast.makeText(GameActivity.this, "Solution works! Score: "+ score +", Total steps: " + animator.getStepCount(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(GameActivity.this, "Solution doesn't work.", Toast.LENGTH_SHORT).show();
        }

        buttonAnimate.setEnabled(true);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonCheck.getId()) {
            doCheck();
        }else if (v.getId() == buttonErase.getId()) {
            FunctionInput focused = getFocusedInput();
            if (focused != null) {
                focused.erase();
            }
        }else if (v.getId() == buttonReset.getId()) {
            inputFunctionMain.reset();
            inputFunction1.reset();
            inputFunction2.reset();
            board.reset();
            animator.reset(true);
            checker.reset();
        }else if(v.getId() == buttonAnimate.getId()){
            if(animator.isAnimating()){
                animator.reset(false);
            }else{
                animator.animateFromStart();
                buttonAnimate.setText("stop anim.");
            }
        }else{
            FunctionInput focused = getFocusedInput();
            if(focused != null){
                switch (v.getId()){
                    case R.id.bt_fwd: focused.input(Config.CMD_FWD); break;
                    case R.id.bt_back: focused.input(Config.CMD_BWDS); break;
                    case R.id.bt_left: focused.input(Config.CMD_TURN_LEFT); break;
                    case R.id.bt_right: focused.input(Config.CMD_TURN_RIGHT); break;
                    case R.id.bt_f1: focused.input(Config.CMD_F1); break;
                    case R.id.bt_f2: focused.input(Config.CMD_F2); break;
                }
            }
        }
    }

    private FunctionInput getFocusedInput(){
        if(inputFunctionMain.isFocused()){
            return inputFunctionMain;
        }else if(inputFunction1.isFocused()){
            return inputFunction1;
        }else if(inputFunction2.isFocused()){
            return inputFunction2;
        }
        return null; // nothing is focused
    }

    @Override
    public void onPlayerChanged(SolutionChecker.Player player, boolean isOrientationChange) {

        if(isOrientationChange) {
            animator.addFrame(new OrientationFrame(player.orientation));
        }else {
            animator.addFrame(new MoveFrame(player.x, player.y));
        }
    }

    @Override
    public void onFocused(int viewId) {
        // some input is focused: defocus others.
        if(viewId == inputFunctionMain.getId()){
            inputFunction1.setFocused(false);
            inputFunction2.setFocused(false);
        }else if(viewId == inputFunction1.getId()){
            inputFunctionMain.setFocused(false);
            inputFunction2.setFocused(false);
        }else if(viewId == inputFunction2.getId()){
            inputFunction1.setFocused(false);
            inputFunctionMain.setFocused(false);
        }
    }

    @Override
    public void onInputChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonAnimate.setEnabled(false);
            }
        });
    }

    @Override
    public void onAnimationEnd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonAnimate.setText("Animate");
            }
        });

    }
}
