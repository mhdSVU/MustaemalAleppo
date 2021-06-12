package mohammedyouser.com.mustaemalaleppo.Data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.CircleTransform;

/**
 * Created by mohammed_youser on 10/23/2017.
 */

public class ViewHolder_Item_Display_Edit extends RecyclerView.ViewHolder {
    public static final String TAG = "TAG";
    final TextView mItemUserName;
    private final EditText mTitle;
    private final EditText mItemPrice;
    private final TextView mCity;
    private final TextView mDate;
    private final EditText mItemDetails;
    private final Context context;



    private View mItemView;
    ProgressBar progressBar = itemView.findViewById(R.id.progressBar1);
    ImageButton imageButton = itemView.findViewById(R.id.img_btn_itemImage);

    public ViewHolder_Item_Display_Edit(View itemView,Context context) {
        super(itemView);
        this.context = context;
        this.mItemView = itemView;
        mTitle =  mItemView.findViewById(R.id.et_item_title);
        mItemPrice =  mItemView.findViewById(R.id.et_item_price);
        mCity =  mItemView.findViewById(R.id.tv_item_city);
        mDate =  mItemView.findViewById(R.id.tv_item_date);
        mItemDetails =mItemView.findViewById(R.id.et_item_details);
        mItemUserName = mItemView.findViewById(R.id.textView_username);


    }


    public View getMyItemView() {
        return mItemView;
    }


    public void setItemTitle(String title) {
        if(String.valueOf(title).equals("")){
            mTitle.setText(context.getString(R.string.no_title));
            return;

        }
        mTitle.setText(title);
    }

    public void setItemPrice(String itemPrice) {
        if(String.valueOf(itemPrice).equals("")){
            mItemPrice.setText(context.getString(R.string.no_price));
            return;

        }
        mItemPrice.setText(itemPrice);
        Log.d(TAG, "setItemImage: Price ");

    }

    public void setItemCity(String itemCity) {

        mCity.setText(itemCity);
    }

    public void setItemCategory(String itemCategory) {
    }

    public void setItemDateAndTime(String dateandtime) {

        mDate.setText(dateandtime);
    }

    public void setItemImage(final Context ctx, final String itemImageURL) {


        if (itemImageURL != null) {
            final ImageView finalImageView = imageButton;


            Log.d(TAG, "setItemImage: Picasso " + itemImageURL);
            setImage_circle(ctx, Uri.parse(itemImageURL), 1, finalImageView);


/*
            Picasso.with(ctx).load(Uri.parse(itemImageURL)).networkPolicy(NetworkPolicy.OFFLINE).into(imageButton, new Callback() {
                @Override
                public void onSuccess() {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(itemImageURL).into(finalImageView);

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }


                }
            });
*/


        }


    }

    public void setItemDetails(String itemDetails) {
        mItemDetails.setText(itemDetails);
    }

    public void setUserImage(final Context ctx, final String userImageURL) {

        ImageView imageView = itemView.findViewById(R.id.imageView_userImage);
        if (userImageURL != null) {
            setImage_circle(ctx, Uri.parse(userImageURL), 1, imageView);

        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.ic_account_circle_white_24dp));
        }

    }

    public void setUserName(String userName) {

        if (userName != null) {
            mItemUserName.setText(userName);

        } else {
            mItemUserName.setText(R.string.empty);
        }
    }


    public String getItemTitle() {
        return mTitle.getText().toString();
    }

    public String getItemPrice() {
        return mItemPrice.getText().toString();
    }

    public String getItemDetails() {
        return mItemDetails.getText().toString();
    }


    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {

        Glide.with(context).load(imageURL)//TODO
                //.bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CircleTransform(context))
                .into(imageView);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

}

