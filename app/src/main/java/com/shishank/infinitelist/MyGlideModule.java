package com.shishank.infinitelist;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.module.AppGlideModule;

import timber.log.Timber;

import static com.bumptech.glide.load.engine.executor.GlideExecutor.newSourceExecutor;

@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return super.isManifestParsingEnabled();
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        GlideExecutor.UncaughtThrowableStrategy uncaughtThrowableStrategy = t -> {
            Timber.d("Exception" + t.getMessage());
        };
        int cores = Runtime.getRuntime().availableProcessors();
        int maxThread = 0;
        if (cores >= 4) {
            maxThread = 4;
        } else {
            maxThread = cores;
        }
        Timber.d("cores = " + maxThread + "" + cores);
        GlideExecutor sourceExecutor = newSourceExecutor(maxThread, "image",
                uncaughtThrowableStrategy);
        builder.setSourceExecutor(sourceExecutor);
    }
}