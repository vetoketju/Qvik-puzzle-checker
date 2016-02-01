package fi.qvik.android.qvikpuzzlechecker.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import fi.qvik.android.qvikpuzzlechecker.Config;
import fi.qvik.android.qvikpuzzlechecker.R;

/**
 * Created by villevalta on 21/01/16.
 */
public class FunctionInput extends RecyclerView{

    ArrayList<Byte> commands = new ArrayList<>();

    // TODO: Feikkaa caret niin että se onkin oikeasti tollasessa Command viewholderissa aina takana ja blinkkaa sitä. jos on tyhjää niin ??? näytä tyhjä komento jossa on caret tai jos caret on ulkoisimpana niin sama
    int caretIndex = 0;
    float fontSize;

    boolean focused;

    Listener listener;

    Caret caret;

    Adapter adapter = new Adapter();
    // Save stuff to bundle.


    public FunctionInput(Context context) {
        super(context);
        init();
    }

    public FunctionInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FunctionInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    void init(){
        LayoutManager mgr = new GridLayoutManager(getContext(), 10);
        this.setLayoutManager(mgr);
        this.setAdapter(adapter);
        setBackgroundColor(Color.GRAY); // Todo add states...
    }

    public void setFocused(boolean focused){
        this.focused = focused;
        if(focused && listener != null){
            listener.onFocused(this.getId());
        }
        if(focused){
            setBackgroundColor(Color.LTGRAY);

        }else{
            setBackgroundColor(Color.GRAY);
        }
        if(caret != null) caret.setVisible(focused);
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        View touchedChild = findChildViewUnder(event.getX(), event.getY());
        if (!focused && event.getAction() == MotionEvent.ACTION_UP && touchedChild == null)
        {
            setFocused(true);
        }else if(focused && event.getAction() == MotionEvent.ACTION_DOWN && touchedChild != null){
            int touchedChildPosition = getChildAdapterPosition(touchedChild);
            int old = caretIndex;
            caretIndex = touchedChildPosition;
            adapter.notifyItemMoved(old, caretIndex);
            //adapter.notifyDataSetChanged();
        }
        return super.dispatchTouchEvent(event);
    }

    public void input(byte command) {
        commands.add(caretIndex, command);
        caretIndex++;
        adapter.notifyItemInserted(caretIndex);
        if(listener != null) listener.onInputChanged();
    }

    public void erase(){
        if(caretIndex > 0){
            caretIndex--;
            commands.remove(caretIndex);
            adapter.notifyItemRemoved(caretIndex);
            if(listener != null) listener.onInputChanged();
        }
    }

    public void reset(){
        commands.clear();
        caretIndex = 0;
        if(listener != null) listener.onInputChanged();
        adapter.notifyDataSetChanged();
    }

    public byte[] getCommands() {
        byte[] res = new byte[commands.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = commands.get(i);
        }
        return res;
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int TYPE_CARET = 0;
        private static final int TYPE_COMMAND = 1;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if(viewType == TYPE_CARET){

                if(caret == null){
                    caret = new Caret(LayoutInflater.from(parent.getContext()).inflate(R.layout.function_input_caret, parent, false));
                }

                return caret;

            }else{

                return new Command(LayoutInflater.from(parent.getContext()).inflate(R.layout.function_input_command, parent, false));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == caretIndex) return TYPE_CARET;
            else return TYPE_COMMAND;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(holder instanceof Caret){
                ((Caret) holder).bind();
            }else if(holder instanceof Command){
                ((Command) holder).bind(position);
            }
        }

        @Override
        public int getItemCount() {
            return commands.size() + 1; // caret
        }

    }

    class Caret extends RecyclerView.ViewHolder{

        View line;
        boolean visible = false;
        Handler h = new Handler();

        public Caret(View itemView) {
            super(itemView);
            line = itemView.findViewById(R.id.line);
        }

        public void setVisible(boolean isVisible){
            visible = isVisible;
            if(visible){
                h.removeCallbacks(null);
                h.postDelayed(blink(),500);
            }else {
                h.removeCallbacks(null);
            }
            line.setVisibility(isVisible ? VISIBLE : GONE);
        }

        private Runnable blink(){
            return new Runnable() {
                @Override
                public void run() {
                    boolean v = line.getVisibility() == VISIBLE;
                    line.setVisibility(v ? GONE : VISIBLE);
                    if(visible) h.postDelayed(blink(),500);
                    else line.setVisibility(GONE);
                }
            };
        }

        public void bind(){

        }

    }

    class Command extends RecyclerView.ViewHolder{

        ImageView image;

        public Command(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image_cmd);
        }

        public void bind(int position){

            if(caretIndex < position) position--;

            byte cmd = commands.get(position);
            int drawableResId = 0;
            switch (cmd){
                case Config.CMD_FWD: drawableResId = R.drawable.forward; break;
                case Config.CMD_BWDS: drawableResId = R.drawable.back; break;
                case Config.CMD_TURN_LEFT: drawableResId = R.drawable.left; break;
                case Config.CMD_TURN_RIGHT: drawableResId = R.drawable.right; break;
                case Config.CMD_F1: drawableResId = R.drawable.f1; break;
                case Config.CMD_F2: drawableResId = R.drawable.f2; break;
            }

            if(drawableResId != 0){
                image.setImageResource(drawableResId);
            }
        }

    }

    public interface Listener {
        void onFocused(int viewId);
        void onInputChanged();
    }
}
