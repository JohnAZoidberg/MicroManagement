package de.struckmeierfliesen.ds.micromanagement;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FoodFragment extends Fragment {

    public static final String ARG_FOOD_TYPE = "arg_food_type";

    public int type;
    private FoodLoaderActivity activity;


    private OnListFragmentInteractionListener mListener;
    private MyFoodRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FoodFragment() {
    }

    @SuppressWarnings("unused")
    public static FoodFragment newInstance(int foodType) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FOOD_TYPE, foodType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);

        activity = (FoodLoaderActivity) getActivity();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_FOOD_TYPE)) {
                type = args.getInt(ARG_FOOD_TYPE);
            } else {
                throw new RuntimeException("You need to pass a food type");
            }
        }

        // Get the Food items
        List<Food> items = activity.loadFood(type);

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new MyFoodRecyclerViewAdapter(items, mListener);
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Food item);
        void onListFragmentLongInteraction(Food item);
    }

    public interface FoodLoaderActivity {
        List<Food> loadFood(int type);
    }

    public void updateList() {
        //items.add(items.size(), items.remove(0));
        // TODO: Only update what has changed
        List<Food> foods = activity.loadFood(type);
        adapter.newItems(foods);
    }
}
