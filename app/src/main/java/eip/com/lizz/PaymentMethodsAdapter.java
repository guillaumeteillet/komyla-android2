package eip.com.lizz;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eip.com.lizz.Models.CreditCard;


public class            PaymentMethodsAdapter
        extends         RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Attributes
    private Context     mContext;
    private ArrayList<CreditCard>    mCreditCards;


    // Constructors
    // Provide a suitable constructor (depends on the kind of dataset)
    public              PaymentMethodsAdapter(ArrayList<CreditCard> myDataset, Context context) {
        mCreditCards = myDataset;
        mContext = context;
    }


    // Methods
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // create a new view
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.card_payment_methods, viewGroup, false);

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ViewHolder vh = (PaymentMethodsAdapter.ViewHolder)viewHolder;
        vh.paymentMethodName.setText(mCreditCards.get(i).get_displayName());

        Drawable myDrawable = mContext.getResources().getDrawable(R.drawable.placeholdercb);
        vh.paymentMethodImage.setImageDrawable(myDrawable);
//        setImageDrawable(mContext.getDrawable(country.getImageResourceId(mContext)));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int  getItemCount() {
        if (mCreditCards == null)
            return 0;
        return mCreditCards.size();
    }


    // Classes
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Attributes
        public TextView paymentMethodName;
        public ImageView paymentMethodImage;

        // Constructor
        public ViewHolder(View v) {
            super(v);
            paymentMethodName = (TextView)v.findViewById(R.id.paymentMethodName);
            paymentMethodImage = (ImageView)v.findViewById(R.id.paymentMethodImage);
        }
    }
}

