package com.shishank.infinitelist;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.shishank.infinitelist.utils.LocalStorage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author shishank
 */

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> imageList;
    private LayoutInflater layoutInflater;
    private int imageCount = 1;
    private boolean isScrolling;

    ImagesAdapter(Context context) {
        this.context = context;
        imageList = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void addImage() {
        imageList.add(String.format("http://dummyimage.com/300&text=%s", imageCount));
        imageCount++;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_image_item, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ImageViewHolder) holder).bindViews(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        GlideApp.with(context).clear(((ImageViewHolder) holder).ivImage);
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage)
        ImageView ivImage;

        ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindViews(String s) {
            GlideApp.with(context).load(s)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            LocalStorage.getInstance().storeImageCount(LocalStorage.getInstance().getImageCount() + 1);
                            return false;
                        }
                    }).into(ivImage);
        }
    }
}
