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

package com.jungle.apps.photos.module.homepage.widget.hot;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.homepage.data.CommonHotTagAdapter;
import com.jungle.base.utils.MiscUtils;
import com.jungle.widgets.loading.JungleLoadingLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HotLayoutView extends FrameLayout {

    private LinearLayout mContentContainer;
    private JungleLoadingLayout mLoadingPageView;
    private List<HotGroupDisplayView.HotInfo> mHotInfoList = new ArrayList<>();


    public HotLayoutView(Context context) {
        super(context);
        initLayout(context);
    }

    public HotLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public HotLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_hot_view, this);
        mContentContainer = (LinearLayout) findViewById(R.id.hot_content_container);
        mLoadingPageView = (JungleLoadingLayout) findViewById(R.id.loading_page);
        mLoadingPageView.setLoading();

        loadLocalHotList();
    }

    private void loadLocalHotList() {
        AssetManager mgr = getContext().getAssets();
        String json = MiscUtils.getAssetContent("hotlist.json");
        updateLayout(json);
    }

    private void updateLayout(String json) {
        if (TextUtils.isEmpty(json)) {
            mLoadingPageView.setEmpty();
            return;
        }

        try {
            JSONArray array = new JSONArray(json);
            int count = array.length();
            for (int i = 0; i < count; ++i) {
                HotGroupDisplayView.HotInfo info = new HotGroupDisplayView.HotInfo();
                JSONObject item = array.getJSONObject(i);
                info.mName = item.getString("name");
                info.mImgUrl = item.getString("url");

                if (item.has("click")) {
                    info.mClickTag = item.getString("click");
                }

                if (item.has("gravity")) {
                    String gravity = item.getString("gravity");
                    info.mImgGravity = TextUtils.equals(gravity, "left")
                            ? HotGroupDisplayView.ImageGravity.Left
                            : HotGroupDisplayView.ImageGravity.Right;
                }

                JSONArray tags = item.getJSONArray("tags");
                int tagCount = tags.length();
                for (int index = 0; index < tagCount; ++index) {
                    String tag = tags.getString(index);
                    if (!TextUtils.isEmpty(tag)) {
                        info.mTagsList.add(tag);
                    }
                }

                mHotInfoList.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        buildHotViews();
        mLoadingPageView.setInvisible();
    }

    private void buildHotViews() {
        for (HotGroupDisplayView.HotInfo info : mHotInfoList) {
            HotGroupDisplayView view = new HotGroupDisplayView(getContext(), info.mImgGravity);
            view.setAdapter(new HotTagsDisplayAdapter(info));
            mContentContainer.addView(view);
        }
    }


    private class HotTagsDisplayAdapter extends CommonHotTagAdapter
            implements HotGroupDisplayView.HotGroupTagAdapter {

        private HotGroupDisplayView.HotInfo mHotInfo;

        HotTagsDisplayAdapter(HotGroupDisplayView.HotInfo info) {
            super(getContext(), info.mTagsList);
            mHotInfo = info;
        }

        @Override
        public HotGroupDisplayView.HotInfo getHotInfo() {
            return mHotInfo;
        }
    }
}
