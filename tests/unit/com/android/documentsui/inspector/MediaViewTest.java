/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.android.documentsui.inspector;

import android.media.ExifInterface;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.documentsui.R;
import com.android.documentsui.base.Shared;
import com.android.documentsui.testing.TestEnv;
import com.android.documentsui.testing.TestResources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MediaViewTest {

    private TestResources mResources;
    private TestTable mTable;
    private Bundle mMetadata;

    @Before
    public void setUp() {
        mResources = TestResources.create();
        mResources.strings.put(R.string.metadata_dimensions_format, "%d x %d, %.1fMP");
        mResources.strings.put(R.string.metadata_aperture_format, "f/%.1f");
        mResources.strings.put(R.string.metadata_coordinates_format, "%.3f, %.3f");
        mTable = new TestTable();
        mMetadata = new Bundle();
        TestMetadata.populateExifData(mMetadata);
        TestMetadata.populateVideoData(mMetadata);
    }

    /**
     * Test that the updateMetadata method is printing metadata for selected items found in the
     * bundle.
     */
    @Test
    public void testShowExifData() throws Exception {
        mResources.strings.put(R.string.metadata_iso_format, "ISO %s");
        mResources.strings.put(R.string.metadata_focal_format, "%d mm");
        mResources.strings.put(R.string.metadata_aperture_format, "f/%.1f");
        Bundle exif = mMetadata.getBundle(DocumentsContract.METADATA_EXIF);
        MediaView.showExifData(mTable, mResources, TestEnv.FILE_JPG, exif, null);

        mTable.assertHasRow(R.string.metadata_dimensions, "3840 x 2160, 8.3MP");
        mTable.assertHasRow(R.string.metadata_date_time, "Jan 01, 1970, 12:16 AM");
        mTable.assertHasRow(R.string.metadata_coordinates, "33.996, -118.475");
        mTable.assertHasRow(R.string.metadata_altitude, "1244.0");
        mTable.assertHasRow(R.string.metadata_make, "Google");
        mTable.assertHasRow(R.string.metadata_model, "Pixel");
        mTable.assertHasRow(R.string.metadata_shutter_speed, "1/100");
        mTable.assertHasRow(R.string.metadata_aperture, "f/2.0");
        mTable.assertHasRow(R.string.metadata_focal_length, "8 mm");
        mTable.assertHasRow(R.string.metadata_iso_speed_ratings, "ISO 120");
    }

    /**
     * Bundle only supplies half of the values for the pairs that print in printMetaData. No put
     * method should be called as the correct conditions have not been met.
     * @throws Exception
     */
    @Test
    public void testShowExifData_PartialGpsTags() throws Exception {
        Bundle data = new Bundle();
        data.putDouble(ExifInterface.TAG_GPS_LATITUDE, 37.7749);

        mMetadata.putBundle(DocumentsContract.METADATA_EXIF, data);
        MediaView.showExifData(mTable, mResources, TestEnv.FILE_JPG, mMetadata, null);
        mTable.assertEmpty();
    }

    /**
     * Bundle only supplies half of the values for the pairs that print in printMetaData. No put
     * method should be called as the correct conditions have not been met.
     * @throws Exception
     */
    @Test
    public void testShowExifData_PartialDimensionTags() throws Exception {
        Bundle data = new Bundle();
        data.putInt(ExifInterface.TAG_IMAGE_WIDTH, 3840);

        mMetadata.putBundle(DocumentsContract.METADATA_EXIF, data);
        MediaView.showExifData(mTable, mResources, TestEnv.FILE_JPG, mMetadata, null);
        mTable.assertEmpty();
    }

    /**
     * Test that the updateMetadata method is printing metadata for selected items found in the
     * bundle.
     */
    @Test
    public void testShowVideoData() throws Exception {
        Bundle data = mMetadata.getBundle(Shared.METADATA_KEY_VIDEO);
        MediaView.showVideoData(mTable, mResources, TestEnv.FILE_MP4, data, null);

        mTable.assertHasRow(R.string.metadata_duration, "01:12");
        mTable.assertHasRow(R.string.metadata_dimensions, "1920 x 1080, 2.1MP");
    }

    /**
     * Test that the updateMetadata method is printing metadata for selected items found in the
     * bundle.
     */
    @Test
    public void testShowVideoData_HourPlusDuration() throws Exception {
        Bundle data = mMetadata.getBundle(Shared.METADATA_KEY_VIDEO);
        data.putInt(MediaMetadata.METADATA_KEY_DURATION, 21660000);
        MediaView.showVideoData(mTable, mResources, TestEnv.FILE_MP4, data, null);

        mTable.assertHasRow(R.string.metadata_duration, "6:01:00");
    }
}