package fi.qvik.android.qvikpuzzlechecker.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fi.qvik.android.qvikpuzzlechecker.QvikPuzzleApplication;
import fi.qvik.android.qvikpuzzlechecker.R;
import fi.qvik.android.qvikpuzzlechecker.model.Level;
import fi.qvik.android.qvikpuzzlechecker.view.BoardImageView;

public class MainActivity extends AppCompatActivity {

    RecyclerView list;
    ArrayList<Level> levels;
    LevelsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = (RecyclerView) findViewById(R.id.recycler);
        list.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        adapter = new LevelsListAdapter();

        list.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapMakerActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        levels = QvikPuzzleApplication.getInstance().getLevelUtil().loadAll();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true; // TODO: ADD SETTINGS ACTIVITY?
        }
        return super.onOptionsItemSelected(item);
    }

    class LevelsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LevelViewHolder holder = new LevelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_level, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof LevelViewHolder){
                ((LevelViewHolder) holder).bind(levels.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return levels != null ? levels.size() : 0;
        }

        class LevelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            Level level;

            TextView name;
            BoardImageView preview;
            View editButton;

            public LevelViewHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.name);
                preview = (BoardImageView) itemView.findViewById(R.id.previewImage);
                editButton = itemView.findViewById(R.id.edit);

                itemView.setOnClickListener(this);
                editButton.setOnClickListener(this);
            }

            public void bind(Level level){

                this.level = level;

                name.setText(level.getName());
                preview.setBitmap(level.getPreviewImage());

                if(level.isEditable()){
                    editButton.setVisibility(View.VISIBLE);
                }else{
                    editButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onClick(View v) {
                if(level != null){

                    Class c = GameActivity.class;

                    if(v.getId() == editButton.getId()){
                        c = MapMakerActivity.class;
                    }

                    Intent i = new Intent(MainActivity.this, c);
                    if(level.isEditable()) {
                        i.putExtra("level", level.getName());
                    }
                    startActivity(i);
                }
            }
        }
    }
}
