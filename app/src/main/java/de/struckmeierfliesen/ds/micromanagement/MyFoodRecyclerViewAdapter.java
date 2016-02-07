package de.struckmeierfliesen.ds.micromanagement;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import de.struckmeierfliesen.ds.micromanagement.FoodFragment.OnListFragmentInteractionListener;

import static de.struckmeierfliesen.ds.micromanagement.Util.*;
import static de.struckmeierfliesen.ds.micromanagement.Util.GREEN;
import static de.struckmeierfliesen.ds.micromanagement.Util.WHITE;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Food} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyFoodRecyclerViewAdapter extends RecyclerView.Adapter<MyFoodRecyclerViewAdapter.ViewHolder> {

    private final List<Food> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyFoodRecyclerViewAdapter(List<Food> items, OnListFragmentInteractionListener listener) {
        Collections.reverse(items);
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mLastEatenView.setText(holder.mItem.getLastEatenDateString());
        holder.mFoodView.setText(holder.mItem.getEatenThisWeek() + " " + holder.mItem.getName());
        switch (holder.mItem.getEatenThisWeek()) {
            case 0:
                holder.mContainer.setBackgroundColor(WHITE);
                holder.mFoodView.setTextColor(TEXT_BLACK);
                holder.mLastEatenView.setTextColor(TEXT_BLACK);
                break;
            case 1:
                holder.mContainer.setBackgroundColor(GREEN);
                holder.mFoodView.setTextColor(WHITE);
                holder.mLastEatenView.setTextColor(WHITE);
                break;
            case 2:
                holder.mContainer.setBackgroundColor(LIGHT_GREEN);
                holder.mFoodView.setTextColor(TEXT_BLACK);
                holder.mLastEatenView.setTextColor(TEXT_BLACK);
                break;
            case 3:
                holder.mContainer.setBackgroundColor(LIME);
                holder.mFoodView.setTextColor(TEXT_BLACK);
                holder.mLastEatenView.setTextColor(TEXT_BLACK);
                break;
            case 4:
                holder.mContainer.setBackgroundColor(YELLOW);
                holder.mFoodView.setTextColor(TEXT_BLACK);
                holder.mLastEatenView.setTextColor(TEXT_BLACK);
                break;
            case 5:
                holder.mContainer.setBackgroundColor(AMBER);
                holder.mFoodView.setTextColor(TEXT_BLACK);
                holder.mLastEatenView.setTextColor(TEXT_BLACK);
                break;
            case 6:
                holder.mContainer.setBackgroundColor(ORANGE);
                holder.mFoodView.setTextColor(TEXT_BLACK);
                holder.mLastEatenView.setTextColor(TEXT_BLACK);
                break;
            case 7:
                holder.mContainer.setBackgroundColor(DEEP_ORANGE);
                holder.mFoodView.setTextColor(WHITE);
                holder.mLastEatenView.setTextColor(WHITE);
                break;
            default:
                holder.mContainer.setBackgroundColor(RED);
                holder.mFoodView.setTextColor(WHITE);
                holder.mLastEatenView.setTextColor(WHITE);
                break;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.onListFragmentLongInteraction(holder.mItem);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void newItems(List<Food> foods) {
        Collections.reverse(foods);
        mValues.clear();
        mValues.addAll(foods);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFoodView;
        public final TextView mLastEatenView;
        public final RelativeLayout mContainer;
        public Food mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContainer = (RelativeLayout) view.findViewById(R.id.container);
            mFoodView = (TextView) view.findViewById(R.id.food);
            mLastEatenView = (TextView) view.findViewById(R.id.last_eaten);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFoodView.getText() + "'";
        }
    }
}
