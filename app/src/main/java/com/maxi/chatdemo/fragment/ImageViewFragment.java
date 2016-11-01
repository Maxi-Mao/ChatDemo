package com.maxi.chatdemo.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.maxi.chatdemo.R;
import com.polites.android.GestureImageView;

public class ImageViewFragment extends Fragment {
    private String imageUrl;
    private ProgressBar loadBar;
    private GestureImageView imageGiv;

    public View onCreateView(android.view.LayoutInflater inflater,
                             android.view.ViewGroup container,
                             android.os.Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_images_view_item, container,
                false);
        init(view);
        loadImage(imageUrl);
        return view;
    }

    private void init(View mView) {
        loadBar = (ProgressBar) mView.findViewById(R.id.imageView_loading_pb);
        imageGiv = (GestureImageView) mView
                .findViewById(R.id.imageView_item_giv);
        imageGiv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    public void loadImage(String url) {
        if (url.startsWith("http://")) {
            Glide.with(this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        imageGiv.setImageBitmap(resource);
                        loadBar.setVisibility(View.GONE);
                        imageGiv.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            imageGiv.setImageBitmap(BitmapFactory.decodeFile(url));
            loadBar.setVisibility(View.GONE);
            imageGiv.setVisibility(View.VISIBLE);
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
