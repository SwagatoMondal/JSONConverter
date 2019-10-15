package com.swag.jsonconverter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.runner.AndroidJUnit4;

import com.swag.jsonconverter.rules.ListRule;
import com.swag.jsonconverter.rules.MapRule;
import com.swag.jsonconverter.rules.RuleKey;
import com.swag.jsonconverter.sample.Child;
import com.swag.jsonconverter.sample.Entity;
import com.swag.jsonconverter.sample.InnerEntity1;
import com.swag.jsonconverter.sample.Parent;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class JSONConverterTest {

    private static final String TAG = JSONConverterTest.class.getSimpleName();

    @Before
    public void clear() {
        JSONConverter.loggingEnabled(false);
    }

    @Test
    public void testFeature() {
        JSONConverter.loggingEnabled(true);
        final JSONConverter<Entity> converter = new JSONConverter<Entity>().addRule(new RuleKey("map", Entity.class),
                new MapRule<>(new Constructor<Map<String, Entity.InnerStaticEntity>>() {
            @NonNull
            @Override
            public Map<String, Entity.InnerStaticEntity> construct() {
                return new HashMap<>();
            }
        }, Entity.InnerStaticEntity.class)).addRule(new RuleKey("map", InnerEntity1.class),
                new MapRule<>(new Constructor<Map<String, Boolean>>() {
            @NonNull
            @Override
            public Map<String, Boolean> construct() {
                return new HashMap<>();
            }
        }, Boolean.class));
        Entity entity = new Entity();
        entity.innerEntity1.innerStaticEntity = new Entity.InnerStaticEntity("Test JSON string");
        entity.map.put("key1", new Entity.InnerStaticEntity("Map entry 1"));
        entity.map.put("key2", new Entity.InnerStaticEntity("Map entry 2"));
        entity.innerEntity1.map.put("key1", true);
        entity.innerEntity1.map.put("key2", false);
        final JSONObject result = converter.toJSON(entity);
        Assert.assertNotNull("Found result null", result);
        Log.d(TAG, "Result : " + result.toString());
        final Entity newEntity = converter.fromJSON(result, Entity.class);
        Assert.assertNotNull("Found conversion null", newEntity);
        Assert.assertEquals(entity, newEntity);
        Log.i(TAG, "Test Map Key1 : " + newEntity.map.get("key1"));
        Log.i(TAG, "Test Map Key2 : " + newEntity.map.get("key2"));
    }

    @Test
    public void testInheritance() {
        JSONConverter.loggingEnabled(true);
        final JSONConverter<Child> converter = new JSONConverter<Child>().addRule(
                new RuleKey("list", Parent.class), new ListRule<>(new Constructor<List<Boolean>>() {
                    @NonNull
                    @Override
                    public List<Boolean> construct() {
                        return new LinkedList<>();
                    }
                }, Boolean.class));
        Child child = new Child();
        child.addToList(true);
        child.addToList(false);
        child.addToList(true);
        final JSONObject result = converter.toJSON(child);
        Assert.assertNotNull("Found result null", result);
        Log.d(TAG, "Result : " + result.toString());
        final Child newChild = converter.fromJSON(result, Child.class);
        Assert.assertNotNull("Found conversion null", newChild);
        Assert.assertEquals(child, newChild);
        Assert.assertEquals(child.size(), newChild.size());
    }
}
