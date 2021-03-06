/**
 * Android photos application project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.apps.photos.module.homepage.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.base.utils.MiscUtils;
import com.jungle.base.utils.NetworkUtils;

public class NetworkTipsView extends FrameLayout {

    public NetworkTipsView(Context context) {
        super(context);
        initLayout(context, null);
    }

    public NetworkTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
    }

    public NetworkTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_network_tips, this);

        if (isInEditMode()) {
            return;
        }

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.NetworkTipsView);
            String invalidTips = arr.getString(R.styleable.NetworkTipsView_invalidTips);
            if (!TextUtils.isEmpty(invalidTips)) {
                setInvalidNetworkTips(invalidTips);
            }

            arr.recycle();
        }

        View tipsView = findViewById(R.id.network_invalid_tips);
        tipsView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MiscUtils.openNetworkSetting(getContext());
            }
        });

        updateTipsVisibility();
        NetworkUtils.addNetworkStateListener(mNetworkListener);
    }

    public void setInvalidNetworkTips(int invalidTipsResId) {
        setInvalidNetworkTips(getContext().getString(invalidTipsResId));
    }

    public void setInvalidNetworkTips(String invalidTips) {
        TextView invalidTextView = (TextView) findViewById(R.id.network_invalid_text);
        invalidTextView.setText(invalidTips);
    }

    public void updateTipsVisibility() {
        setVisibility(NetworkUtils.hasNetwork() ? View.GONE : View.VISIBLE);
    }

    private NetworkUtils.NetworkStateListener mNetworkListener =
            new NetworkUtils.SimpleNetworkStateListener() {
                @Override
                public void onNone2Mobile() {
                    updateTipsVisibility();
                }

                @Override
                public void onNone2Wifi() {
                    updateTipsVisibility();
                }

                @Override
                public void onMobile2None() {
                    updateTipsVisibility();
                }

                @Override
                public void onWifi2None() {
                    updateTipsVisibility();
                }
            };
}
