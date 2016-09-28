package com.maxi.chatdemo.fragment;

import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
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
		loadImage(imageGiv, imageUrl);
		return view;
	};

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
//		imageLoader = ImageLoader.getInstance();
//		builder = new DisplayImageOptions.Builder();
//		options = builder.cacheInMemory(true).cacheOnDisk(true)
//				.considerExifParams(true).delayBeforeLoading(1000)
//				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//				.resetViewBeforeLoading(false)
//				.displayer(new SimpleBitmapDisplayer()).build();
//		imgLoadListener = new SimpleImageLoadingListener() {
//			final List<String> displayedImages = Collections
//					.synchronizedList(new LinkedList<String>());
//
//			@Override
//			public void onLoadingComplete(String imageUri, View view,
//					Bitmap loadedImage) {
//				if (loadedImage != null) {
//					loadBar.setVisibility(View.GONE);
//					imageGiv.setVisibility(View.VISIBLE);
//					boolean firstDisplay = !displayedImages.contains(imageUri);
//					if (firstDisplay) {
//						FadeInBitmapDisplayer.animate(imageGiv, 0);
//						displayedImages.add(imageUri);
//					}
//				}
//			}
//		};
	}

	public void loadImage(ImageView imageView, String url) {
		if (url.startsWith("http://")) {
//			imageLoader.displayImage(url, imageView, options, imgLoadListener);
			Glide.with(this).load(url).into(imageView);
		} else {
			imageView.setImageBitmap(BitmapFactory.decodeFile(url));
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
