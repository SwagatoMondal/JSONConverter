package com.swag.jsonconverter;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.swag.jsonconverter.sample.Entity;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class JSONConverterTest {

    private static final String TAG = JSONConverterTest.class.getSimpleName();

    @Test
    public void testFeature() {
        Entity entity = new Entity();
        entity.innerEntity1.innerEntity2 = new Entity.InnerEntity2("Test JSON string");
        final JSONObject result = JSONConverter.toJSON(entity);
        Assert.assertNotNull("Found result null", result);
        Log.d(TAG, "Result : " + result.toString());
        final Object conversion = JSONConverter.fromJSON(result, Entity.class);
        Assert.assertNotNull("Found conversion null", conversion);
        Assert.assertEquals(entity, conversion);
    }
}
