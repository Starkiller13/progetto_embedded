package com.example.progetto_embedded;

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder> {

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;
        private boolean isSelected;
        private HistoryViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION)
                            mListener.onItemClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mLongListener != null) {
                        isSelected = !itemView.isSelected();
                        itemView.setSelected(isSelected);
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            mLongListener.onItemLongClick(position);
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(int position);
    }



    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        mLongListener = listener;
    }
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;
    private final LayoutInflater mInflater;
    private List<History> mWords; // Cached copy of words
    HistoryListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new HistoryViewHolder(itemView);
    }

    public String getText(int position){
        return mWords.get(position).getText();
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        if (mWords != null) {
            History current = mWords.get(position);
            holder.wordItemView.setText(current.getData() + "\n" + current.getText());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No Word");
        }
    }

    void setWords(List<History> words) {
        mWords = words;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mWords != null)
            return mWords.size();
        else return 0;
    }
}


