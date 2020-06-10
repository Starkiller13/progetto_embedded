package com.corgilab.corgiOCR.HistoryManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.corgilab.corgiOCR.R;
import java.util.List;


public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder> {

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;
        private final TextView wordHeader;
        private final CardView cardView;
        private final Drawable initialColor;
        private boolean isSelected = false;
        @SuppressLint("ResourceType")
        private HistoryViewHolder(View itemView) {
            super(itemView);

            wordItemView = itemView.findViewById(R.id.textView);
            wordHeader = itemView.findViewById(R.id.tw_header);
            cardView = itemView.findViewById(R.id.cardview);
            initialColor = cardView.getBackground();

            itemView.setOnClickListener(v -> {
                if(mListener!=null){
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION)
                        mListener.onItemClick(position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                this.isSelected = !this.isSelected;
                cardView.setSelected(isSelected);
                if (mLongListener != null) {
                    if(isSelected)
                        cardView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.toolbar_gradient,itemView.getContext().getTheme()));
                    else
                        cardView.setBackground(initialColor);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        mLongListener.onItemLongClick(position);

                }
                return true;
            });
        }

        private void updateView(){
            this.isSelected=false;
            cardView.setBackground(initialColor);
        }
    }

    public interface OnItemClickListener{ void onItemClick(int position);}

    public interface OnItemLongClickListener{ void onItemLongClick(int position);}

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){ mLongListener = listener; notifyDataSetChanged(); }

    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;
    private final LayoutInflater mInflater;
    private List<History> mWords; // Cached copy of words
    public HistoryListAdapter(Context context) {
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

    public int getId(int position){ return  mWords.get(position).getId();}

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        if (mWords != null) {
            History current = mWords.get(position);
            holder.wordItemView.setText(current.getText());
            holder.wordHeader.setText(current.getData().substring(0,10));
            holder.updateView();
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No Word");
        }
    }

    public void setWords(List<History> words) {
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


