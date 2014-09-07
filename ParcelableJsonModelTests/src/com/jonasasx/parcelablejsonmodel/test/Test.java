package com.jonasasx.parcelablejsonmodel.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.text.TextUtils;

public class Test extends TestCase {

	public void testSimpleInt() throws NoSuchFieldException, JSONException {
		TestModel model = new TestModel();
		model.setAttributes(new JSONObject("{\"id\": 1}"));
		assertEquals(model.getId(), 1);
	}

	public void testStringInt() throws NoSuchFieldException, JSONException {
		TestModel model = new TestModel();
		model.setAttributes(new JSONObject("{\"id\": \"1\"}"));
		assertEquals(model.getId(), 1);
	}

	public void testNullInt() throws NoSuchFieldException, JSONException {
		TestModel model = new TestModel();
		model.setAttributes(new JSONObject("{\"id\": \"x\"}"));
		assertEquals(model.getId(), 0);

		model = new TestModel();
		model.setAttributes(new JSONObject());
		assertEquals(model.getId(), 0);
	}

	public void testList() throws NoSuchFieldException, JSONException {
		TestModel model = new TestModel();
		model.setAttributes(new JSONObject("{\"list\": [{}, {\"id\": 1}]}"));
		assertEquals(model.getList().get(1).getId(), 1);
	}

	public void testSetGet() throws NoSuchFieldException, JSONException {
		TestModel model = new TestModel();
		TestModel model2 = new TestModel();
		model.setId(1);
		List<TestModel> list = new ArrayList<TestModel>();
		list.add(model);
		list.add(model2);
		model.setList(list);
		model2.setList(list);
		assertEquals(model.getId(), new TestModel(model.getAttributes()).getId());
	}

	public void testToString() throws NoSuchFieldException, JSONException {
		TestModel model = new TestModel();
		List<TestModel> list = new ArrayList<TestModel>();
		list.add(model);
		model.setList(list);

		TestModel model2 = new TestModel();
		list = new ArrayList<TestModel>();
		list.add(model2);
		model2.setList(list);
		assertTrue(!TextUtils.isEmpty(model.toString()));
		assertTrue(!TextUtils.isEmpty(model2.toString()));
	}

	public void testParcel() throws NoSuchFieldException, JSONException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException {
		TestModel model = new TestModel();
		TestModel model2 = new TestModel();
		List<TestModel> list = new ArrayList<TestModel>();
		list.add(model);
		list.add(model2);
		model.setList(list);
		model.setSelf(model);
		model2.setList(list);
		model.setId(1);

		Parcel parcel = Parcel.obtain();
		model.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		TestModel newModel = (TestModel) TestModel.CREATOR.createFromParcel(parcel);
		assertEquals(model.toString(), newModel.toString());
		parcel.recycle();
	}

	public void testParcelListNonParcelable() throws NoSuchFieldException, JSONException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException {
		TestModel model = new TestModel();
		List<Test> list = new ArrayList<Test>();
		list.add(this);
		model.setListNP(list);

		Parcel parcel = Parcel.obtain();
		model.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		TestModel newModel = (TestModel) TestModel.CREATOR.createFromParcel(parcel);
		assertEquals(newModel.getListNP(), null);
		parcel.recycle();
	}

	public void testMap() throws NoSuchFieldException, JSONException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException {
		TestModel model = new TestModel();
		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		model.setMap(map);
		assertEquals(model.toString(), new TestModel(model.getAttributes()).toString());

		Parcel parcel = Parcel.obtain();
		model.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		TestModel newModel = (TestModel) TestModel.CREATOR.createFromParcel(parcel);
		assertEquals(model.toString(), newModel.toString());
		parcel.recycle();
	}

	public void testSet() throws NoSuchFieldException, JSONException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException {
		TestModel model = new TestModel();
		Set<String> set = new LinkedHashSet<String>();
		set.add("value1");
		set.add("value2");
		model.setSet(set);
		assertEquals(model.toString(), new TestModel(model.getAttributes()).toString());

		Parcel parcel = Parcel.obtain();
		model.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		TestModel newModel = (TestModel) TestModel.CREATOR.createFromParcel(parcel);
		assertEquals(model.toString(), newModel.toString());
		parcel.recycle();
	}

	public void testBool() throws NoSuchFieldException, JSONException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException {
		TestModel model = new TestModel();
		model.setBool(true);

		Parcel parcel = Parcel.obtain();
		model.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		TestModel newModel = (TestModel) TestModel.CREATOR.createFromParcel(parcel);
		assertTrue(newModel.isBool());
		parcel.recycle();

		model.setBool(false);

		parcel = Parcel.obtain();
		model.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		newModel = (TestModel) TestModel.CREATOR.createFromParcel(parcel);
		assertTrue(!newModel.isBool());
		parcel.recycle();
	}
}