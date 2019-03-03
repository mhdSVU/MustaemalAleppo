package mohammedyouser.com.mustaemalaleppo.Data;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.CircleTransform;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by mohammed_youser on 10/23/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG ="";


    private View view;
    ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
    ImageButton imageView = (ImageButton) itemView.findViewById(R.id.img_btn_itemImage);

    public MyViewHolder(View itemView) {
        super(itemView);
        this.view=itemView;



    }

    public void setView(View view) {
        this.view = view;
    }


    public void setItemTitle(String title) {
        EditText mTitle= (EditText) view.findViewById(R.id.et_item_title);
        mTitle.setText(title);
    }

    public void setItemPrice(String itemPrice) {
        EditText mItemPrice=(EditText) view.findViewById(R.id.et_item_price);
        mItemPrice.setText(itemPrice);
        Log.d(TAG, "setItemImage: Price ");

    }

    public void setItemCity(String itemCity) {

        TextView mCity= (TextView) view.findViewById(R.id.tv_item_city);
        mCity.setText(itemCity);
    }

    public void setItemCategory(String itemCategory) {
        TextView mCategory= (TextView) view.findViewById(R.id.tv_item_category);
        mCategory.setText(itemCategory);
    }

    public void setItemDateAndTime(String dateandtime)

    {

        TextView mDate= (TextView) view.findViewById(R.id.tv_item_date);
        mDate.setText(dateandtime);
    }

    public void setItemImage(final Context ctx, final String itemImageURL) {


        if (itemImageURL != null) {
            final ImageView finalImageView = imageView;


            Log.d(TAG, "setItemImage: Picasso "+ itemImageURL);

            Picasso.with(ctx).load(Uri.parse(itemImageURL)).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    if(progressBar!=null) {
                        progressBar.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onError()

                {
                    Picasso.with(ctx).load(itemImageURL).into(finalImageView);

                    if(progressBar!=null) {
                        progressBar.setVisibility(View.GONE);
                    }



                }
            });


        }


    }

    public void setItemDetails(String itemDetails) {
        EditText mItemDetails=(EditText)view.findViewById(R.id.et_item_details);
        mItemDetails.setText(itemDetails);
    }
    public void setUserImage(final Context ctx, final String userImageURL) {

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView_userImage);
        if(userImageURL!=null)
        {
            setImage_circle(ctx,Uri.parse(userImageURL),1,imageView);

        }
        else {
            imageView.setImageDrawable(ctx.getDrawable(R.drawable.ic_account_circle_white_24dp));
        }

    }
    public void setUserName(String userName) {
        TextView mItemUserName=(TextView)view.findViewById(R.id.textView_username);

        if(userName!=null)
        {
            mItemUserName.setText(userName);

        }
        else {
            mItemUserName.setText("My name.");
           }
    }

    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView)
    {
        Glide.with(context).load(imageURL)//TODO
                /*.bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)*/
                .into(imageView)


        ;


    }




    public String getItemTitle() {
        EditText mTitle= (EditText) view.findViewById(R.id.et_item_title);
       return mTitle.getText().toString();
    }

    public String getItemPrice() {
        EditText mItemPrice=(EditText) view.findViewById(R.id.et_item_price);
        return mItemPrice.getText().toString();
    }

    public String getItemDetails() {
        EditText mItemDetails=(EditText)view.findViewById(R.id.et_item_details);
        return mItemDetails.getText().toString();
    }




}

