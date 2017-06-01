package com.shen.snote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shen on 2017/3/1.
 */

public class AdapterMain extends RecyclerView.Adapter {

    private List<NoteData> noteDatas;
    private Context context;

    interface OnItemClickListener{
        void click(int layoutPosition);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


    public AdapterMain(Context context, List<NoteData> noteDatas) {
        this.context = context;
        this.noteDatas = noteDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main_note, parent, false);
        MiViewHolder miViewHolder = new MiViewHolder(view);
        return miViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MiViewHolder miViewHolder = (MiViewHolder) holder;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.click(holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteDatas == null ? 0 : noteDatas.size();
    }

    private class MiViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MiViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.item_note_title);
        }
    }
}
