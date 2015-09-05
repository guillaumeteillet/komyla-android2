package eip.com.lizz.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eip.com.lizz.R;


public class ParametersListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Context mContext;

    public static ParametersListFragment newInstance(Context context) {
        ParametersListFragment fragment = new ParametersListFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        //return fragment;
        return fragment;
    }

    public ParametersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parameters_list, container, false);

        String[] placeholderParameters = {"Adresses de livraison", "Moyens de paiement",
                "Alerte de dépense", "Code PIN", "Mot de passe", "Scanner"};

        mRecyclerView = (RecyclerView) view.findViewById(R.id.parameters_recycler_view);

        mAdapter = new ParameterListAdapter(placeholderParameters);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    private class ParameterListAdapter extends RecyclerView.Adapter<ParameterListAdapter.ViewHolder> {

        private String[] mPlaceholderParameters;

        public class ViewHolder extends RecyclerView.ViewHolder {

            // Image
            public TextView parameterName;

            public ViewHolder(View itemView) {
                super(itemView);
                parameterName = (TextView) itemView.findViewById(R.id.rvitem_parameter_title);
            }
        }

        public ParameterListAdapter(String[] placeholderParameters) {

            mPlaceholderParameters = placeholderParameters;
        }

        @Override
        public ParameterListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvitem_parameter,
                    parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.parameterName.setText(mPlaceholderParameters[i]);
        }

        @Override
        public int getItemCount() {
            return mPlaceholderParameters.length;
        }
    }
}

