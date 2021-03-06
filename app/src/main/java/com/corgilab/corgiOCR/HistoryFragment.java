package com.corgilab.corgiOCR;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.corgilab.corgiOCR.HistoryManagement.History;
import com.corgilab.corgiOCR.HistoryManagement.HistoryListAdapter;
import com.corgilab.corgiOCR.HistoryManagement.HistoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

/**
 * Frammento atto alla visualizzazione e alla gestione degli elementi salvati nel database room
 */
public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private View view;
    private List<Integer> del_list = new ArrayList<Integer>();
    private HistoryViewModel mHistoryViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        Context context = view.getContext();

        RecyclerView recyclerView = view.findViewById(R.id.hist_rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final HistoryListAdapter adapter = new HistoryListAdapter(context);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new HistoryListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position){
                Log.v(TAG,"clicked");
                String txt = adapter.getText(position);
                Bundle bundle = new Bundle();
                bundle.putString("text", txt);
                bundle.putBoolean("hb_visible",false);
                T2SFragment frag = new T2SFragment();
                frag.setArguments(bundle);
                FragmentTransaction manager =  getParentFragmentManager().beginTransaction();
                manager.setCustomAnimations(R.anim.enter_right,R.anim.exit_left,R.anim.enter_left,R.anim.exit_right);
                manager.replace(R.id.fragment_container,frag).addToBackStack(null).commit();
                del_list = new ArrayList<>();
            }
        });
        adapter.setOnItemLongClickListener(new HistoryListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                view.findViewById(R.id.hist_fab).setVisibility(View.VISIBLE);
                int i=adapter.getId(position);
                if(del_list.contains(i))
                    del_list.remove(Integer.valueOf(i));
                else
                    del_list.add(i);
                if(del_list.isEmpty())
                    view.findViewById(R.id.hist_fab).setVisibility(View.INVISIBLE);
                else
                    view.findViewById(R.id.hist_fab).setVisibility(View.VISIBLE);
                Log.v(TAG,"Long Clicked");
            }
        });
        FloatingActionButton a = view.findViewById(R.id.hist_fab);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!del_list.isEmpty())
                    mHistoryViewModel.deleteList(del_list);
                del_list = new ArrayList<>();
                view.findViewById(R.id.hist_fab).setVisibility(View.INVISIBLE);
                requireActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        mHistoryViewModel.getAllTexts().observe(getViewLifecycleOwner(), new Observer<List<History>>() {

            @Override
            public void onChanged(@Nullable final List<History> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setWords(words);
            }
        });
        Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.fragment_container), R.string.snackbar, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(R.color.secondaryTextColor, requireActivity().getTheme()));
        snackbar.show();
        return view;
    }
}
