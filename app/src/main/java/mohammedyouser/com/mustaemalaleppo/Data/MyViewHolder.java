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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.recyclerview.widget.RecyclerView;

import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.CircleTransform;

/**
 * Created by mohammed_youser on 10/23/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = "";
    private final TextView mItemUserName;
    private final EditText mTitle;
    private final EditText mItemPrice;
    private final TextView mCity;
    private final TextView mCategory;
    private final TextView mDate;
    private final EditText mItemDetails;


    private View mItemView;
    ProgressBar progressBar = itemView.findViewById(R.id.progressBar1);
    ImageButton imageButton = itemView.findViewById(R.id.img_btn_itemImage);

    public MyViewHolder(View itemView) {
        super(itemView);
        this.mItemView = itemView;
        mTitle = (EditText) mItemView.findViewById(R.id.et_item_title);
        mItemPrice = (EditText) mItemView.findViewById(R.id.et_item_price);
        mCity = (TextView) mItemView.findViewById(R.id.tv_item_city);
        mCategory = (TextView) mItemView.findViewById(R.id.tv_item_category);
        mDate = (TextView) mItemView.findViewById(R.id.tv_item_date);
        mItemDetails = (EditText) mItemView.findViewById(R.id.et_item_details);
        mItemUserName = (TextView) mItemView.findViewById(R.id.textView_username);


    }


    public View getMyItemView() {
        return mItemView;
    }


    public void setItemTitle(String title) {
        mTitle.setText(title);
    }

    public void setItemPrice(String itemPrice) {
        mItemPrice.setText(itemPrice);
        Log.d(TAG, "setItemImage: Price ");

    }

    public void setItemCity(String itemCity) {

        mCity.setText(itemCity);
    }

    public void setItemCategory(String itemCategory) {
        mCategory.setText(itemCategory);
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

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView_userImage);
        if (userImageURL != null) {
            setImage_circle(ctx, Uri.parse(userImageURL), 1, imageView);

        } else {
            imageView.setImageDrawable(ctx.getDrawable(R.drawable.ic_account_circle_white_24dp));
        }

    }

    public void setUserName(String userName) {

        if (userName != null) {
            mItemUserName.setText(userName);

        } else {
            mItemUserName.setText(R.string.default_val_user_name);
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

