package com.shishank.infinitelist;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView;
import com.shishank.infinitelist.utils.CustomLinearLayoutManager;
import com.shishank.infinitelist.utils.LocalStorage;
import com.shishank.infinitelist.utils.OnFlingChangeListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * @author shishank
 */

public class InfiniteListActivity extends AppCompatActivity implements View.OnTouchListener {

    @BindView(R.id.rvImages)
    RecyclerView rvImages;

    private ImagesAdapter imagesAdapter;
    private CustomLinearLayoutManager linearLayoutManager;
    private boolean isUpdating;
    private static final int PAGE_SIZE = 500;
    private List<Target> targets;
    private Target<Drawable> target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infinite_list);
        ButterKnife.bind(this);
        initView();
        rvImages.setOnTouchListener(this);
        rvImages.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if (rvImages.getLayoutManager() instanceof OnFlingChangeListener) {
                    int pos = ((OnFlingChangeListener) rvImages.getLayoutManager())
                            .getPositionForVelocity(velocityY);
                    int finalPos;
                    if (pos > imagesAdapter.getItemCount()) {
                        finalPos = imagesAdapter.getItemCount();
                    } else {
                        finalPos = pos;
                    }
                    initialCall(finalPos);
                    rvImages.smoothScrollToPosition(finalPos);
                    imagesAdapter.notifyItemRangeChanged(finalPos, imagesAdapter.getItemCount());
                    return true;
                }
                return false;
            }
        });

        rvImages.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (rvImages.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                    cancelCalls();
                }
            }
        });
    }

    private void initialCall(int pos) {
        targets = new ArrayList<>();
        int child = linearLayoutManager.getChildCount();

        for (int i = 0; i < child; i++) {
            pos++;
            RequestManager requestManager = GlideApp.with(this);
            target = requestManager
                    .load(String.format("http://dummyimage.com/300&text=%s", pos))
                    .preload(150, 150);
            targets.add(target);

        }
    }

    private void cancelCalls() {
        if (targets != null && targets.size() > 0) {
            for (Target target : targets) {
                if (target.getRequest() != null) {
                    target.getRequest().clear();
                }
            }
        }
    }

    private void initView() {
        linearLayoutManager = new CustomLinearLayoutManager(this,
                CustomLinearLayoutManager.VERTICAL, false);
        imagesAdapter = new ImagesAdapter(this);
        rvImages.setLayoutManager(linearLayoutManager);
        rvImages.setAdapter(imagesAdapter);
        imagesAdapter.notifyDataSetChanged();
        loadImages();
        RxRecyclerView.scrollEvents(rvImages)
                .filter(event -> shouldUpdate())
                .filter(event1 -> hasScrolledToLast())
                .subscribe(
                        recyclerViewScrollEvent -> {
                            loadImages();
                            rvImages.post(() -> imagesAdapter.notifyDataSetChanged());
                        },
                        Timber::e);

    }

    private void loadImages() {
        isUpdating = true;
        for (int i = 0; i < PAGE_SIZE; i++) {
            imagesAdapter.addImage();
        }
        isUpdating = false;
    }

    private boolean hasScrolledToLast() {
        int pastVisibleItems, visibleItemCount, totalItemCount;
        visibleItemCount = linearLayoutManager.getChildCount();
        totalItemCount = linearLayoutManager.getItemCount();
        pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
        return (visibleItemCount + pastVisibleItems) >= totalItemCount;
    }

    private boolean shouldUpdate() {
        return !isUpdating;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        rvImages.performClick();
        final boolean ret = super.onTouchEvent(event);
        final RecyclerView.LayoutManager lm = rvImages.getLayoutManager();

        if (lm instanceof OnFlingChangeListener
                && (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL)
                && rvImages.getScrollState() == SCROLL_STATE_IDLE) {
            // The scroll state is idle-
            // no fling was performed, so the view may be in an unaligned state
            // and will not be flung to a proper state.
            rvImages.smoothScrollToPosition(((OnFlingChangeListener) lm).getFixScrollPos());
        }

        return ret;
    }

    @OnClick(R.id.btn_image_count)
    public void onViewClicked() {
        int count = LocalStorage.getInstance().getImageCount();
        String imageCount = String.format(getString(R.string.images_downloaded), count);
        Snackbar.make(rvImages, imageCount, Snackbar.LENGTH_SHORT).show();
    }
}

