package com.jonasasx.parcelablejsonmodel.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.jonasasx.parcelablejsonmodel.Model;
import com.jonasasx.parcelablejsonmodel.ModelField;

public class TestModel extends Model {

	public TestModel() {
		super();
	}

	public TestModel(JSONObject json) {
		super(json);
	}

	@ModelField(json = "id")
	private int						mId;

	@ModelField(json = "list")
	private List<TestModel>			mList;

	@ModelField(json = "self")
	private TestModel				mSelf;

	@ModelField
	private List<?>					mListNP;

	@ModelField(json = "map")
	private HashMap<String, String>	mMap;

	@ModelField(json = "set")
	private Set<String>				mSet;

	@ModelField(json = "bool")
	private boolean					mBool;

	public int getId() {
		return mId;
	}

	public TestModel setId(int i) {
		mId = i;
		return this;
	}

	public List<TestModel> getList() {
		return mList;
	}

	public TestModel setList(List<TestModel> list) {
		mList = list;
		return this;
	}

	public List<?> getListNP() {
		return mListNP;
	}

	public TestModel setListNP(List<?> list) {
		mListNP = list;
		return this;
	}

	public Map<String, String> getMap() {
		return mMap;
	}

	public TestModel setMap(Map<String, String> map) {
		mMap = (HashMap<String, String>) map;
		return this;
	}

	public TestModel setSelf(TestModel self) {
		mSelf = self;
		return this;
	}

	public Set<String> getSet() {
		return mSet;
	}

	public TestModel setSet(Set<String> set) {
		mSet = set;
		return this;
	}

	public boolean isBool() {
		return mBool;
	}

	public void setBool(boolean bool) {
		mBool = bool;
	}
}