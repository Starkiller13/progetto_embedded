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

/**
 * Classe Adattatore per recyclerView specifica per il frammento LanguageFragment orientata alla
 * selezione della lingua del motore t2s
 */
public class LanguageViewAdapter extends RecyclerView.Adapter<LanguageViewAdapter.LanguageViewHolder>
{
    //Variabili
    private OnScrollListener onScrollListener;
    private OnItemClickListener mListener;
    private final LayoutInflater mInflater;
    private List<String> mLang;
    private List<String> mLangTag;
    private int lastPos = 1;

    //iInterfacce per i listener, la seconda non è utilizzata ma può servire per sviluppi futuri
    public interface OnItemClickListener{ void onItemClick(int position);}
    public void setOnClickListener(OnItemClickListener listener){mListener=listener;}
    public interface  OnScrollListener{ void onScroll(int position);}
    public void setOnScrollListener(OnScrollListener listener){ onScrollListener = listener;}

    /**
     * Costruttore dell'adapter
     * @param context
     */
    public LanguageViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Metodo per l'accesso alla lista delle lingue
     * @param list
     */
    public void setLanguages(List<String> list){
        mLang =list;
    }

    /**
     * Metodo per l'accesso alla lista di Tag
     * @param list
     */
    public void setLanguagesTag(List<String> list){
        mLangTag =list;
    }

    /** Metodo standard
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.rw_language_item, parent, false);
        return new LanguageViewAdapter.LanguageViewHolder(itemView);
    }

    /**
     * Metodo per capire quale elemento visualizzare nel recyclerview in alto
     * @param lastPos
     */
    public void setLastPos(int lastPos) {
        this.lastPos = lastPos;
    }

    /** Metodo standard ber il binding dell'holder
     * Se l'holder rappresenta la lingua selezionata la colora di arancione
     *
     * @param holder
     * @param position
     */
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

    /**
     * Metodo che ritorna la lunghezza della lista con le lingue disponibili
     * @return
     */
    @Override
    public int getItemCount() {
        if (mLang != null)
            return mLang.size();
        else return 0;
    }

    /**
     * Classe Holder
     */
    class LanguageViewHolder extends RecyclerView.ViewHolder{
        private final TextView language;
        private final TextView languageTag;
        private boolean isClicked =false;
        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.language = itemView.findViewById(R.id.language);
            this.languageTag = itemView.findViewById(R.id.language_tag);
            /**
             * Metodo onScroll non implementato
             */
            itemView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int position = getAdapterPosition();
                    onScrollListener.onScroll(position);
                }
            });
            /**
             * Metodo che gestisce il click sull'oggetto da parte dell'user
             */
            itemView.setOnClickListener(v -> {
                if(mListener!=null){
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION)
                        mListener.onItemClick(position);
                }
            });

        }

        /**
         * Mi serve per cambiare colore all'elemento se selezionato
         */
        public void setSelectedColor(){
            itemView.findViewById(R.id.cardview_lang).setBackground(itemView.getContext().getResources().getDrawable(R.drawable.toolbar_gradient,itemView.getContext().getTheme()));
        }

        /**
         * Mi serve per assegnare la tinta di default agli elementi non selezionati
         */
        public void setDefaultColor(){
            itemView.findViewById(R.id.cardview_lang).setBackground(itemView.getContext().getResources().getDrawable(R.drawable.background,itemView.getContext().getTheme()));
        }

    }

}
