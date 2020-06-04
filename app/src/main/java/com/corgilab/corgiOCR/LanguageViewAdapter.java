package com.corgilab.corgiOCR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class LanguageViewAdapter extends RecyclerView.Adapter<LanguageViewAdapter.LanguageViewHolder>
{
    private OnScrollListener onScrollListener;
    private OnItemClickListener mListener;
    private final LayoutInflater mInflater;
    private List<String> mLang;
    private List<String> mLangTag;
    private int lastPos = 1;
    public interface OnItemClickListener{ void onItemClick(int position);}
    public void setOnClickListener(OnItemClickListener listener){mListener=listener;}
    public interface  OnScrollListener{ void onScroll(int position);}
    public void setOnScrollListener(OnScrollListener listener){ onScrollListener = listener;}
    public LanguageViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setLanguages(List<String> list){
        mLang =list;
    }

    public void setLanguagesTag(List<String> list){
        mLangTag =list;
    }


    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.rw_language_item, parent, false);
        return new LanguageViewAdapter.LanguageViewHolder(itemView);
    }

    public void setLastPos(int lastPos) {
        this.lastPos = lastPos;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        if(mLang!=null){
            holder.language.setText(mLang.get(position));
            holder.languageTag.setText(mLangTag.get(position));
        }
        if(holder.languageTag.getText().equals(mLangTag.get(lastPos)))
            holder.setSelectedColor();
        else
            holder.setDefaultColor();

    }

    @Override
    public int getItemCount() {
        if (mLang != null)
            return mLang.size();
        else return 0;
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder{
        private final TextView language;
        private final TextView languageTag;
        private boolean isClicked =false;
        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.language = itemView.findViewById(R.id.language);
            this.languageTag = itemView.findViewById(R.id.language_tag);

            itemView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int position = getAdapterPosition();
                    onScrollListener.onScroll(position);
                }
            });
            itemView.setOnClickListener(v -> {
                if(mListener!=null){
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION)
                        mListener.onItemClick(position);
                }
            });

        }
        public void setSelectedColor(){
            itemView.findViewById(R.id.cardview_lang).setBackground(itemView.getContext().getResources().getDrawable(R.drawable.toolbar_gradient,itemView.getContext().getTheme()));
        }

        public void setDefaultColor(){
            itemView.findViewById(R.id.cardview_lang).setBackground(itemView.getContext().getResources().getDrawable(R.drawable.background,itemView.getContext().getTheme()));
        }

    }

}
