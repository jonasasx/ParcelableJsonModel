package com.jonasasx.parcelablejsonmodel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * The Class Model.
 */
public abstract class Model implements Parcelable {

	/** The declared fields. */
	private static ArrayList<Field>			sDeclaredFields;

	/** The Serialize from. */
	private Model							mSerializeFrom;

	/** The To string block list. */
	private static List<Model>				mToStringBlockList	= new CopyOnWriteArrayList<Model>();

	/** The Parcelable block map. */
	private static Map<Parcel, List<Model>>	mParcelableBlockMap	= new ConcurrentHashMap<Parcel, List<Model>>();

	/** The Hash list. */
	private static Map<Integer, Model>		mHashList			= new ConcurrentHashMap<Integer, Model>();

	/** The Hash. */
	private int								mHash				= new Random().nextInt();

	/**
	 * Instantiates a new model.
	 */
	public Model() {

	}

	/**
	 * Instantiates a new model.
	 *
	 * @param json the json attributes
	 */
	public Model(JSONObject json) {
		setAttributes(json);
	}

	/**
	 * New instance.
	 *
	 * @param c the class
	 * @return the model
	 */
	public final static Model newInstance(Class<? extends Model> c) {
		try {
			return c.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * New instance.
	 *
	 * @param c the class
	 * @param json the json attributes
	 * @return the model
	 */
	public static Model newInstance(Class<? extends Model> c, JSONObject json) {
		try {
			return c.getDeclaredConstructor(JSONObject.class).newInstance(json);
		} catch (Exception e) {
			try {
				return c.newInstance().setAttributes(json);
			} catch (Exception e1) {
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		List<Model> parcelableBlockList;
		parcelableBlockList = mParcelableBlockMap.get(dest);
		if (parcelableBlockList == null) {
			parcelableBlockList = new CopyOnWriteArrayList<Model>();
			mParcelableBlockMap.put(dest, parcelableBlockList);
		}
		if (parcelableBlockList.contains(this)) {
			dest.writeString(Integer.toString(mHash));
			return;
		}
		parcelableBlockList.add(this);
		try {
			dest.writeString(getClass().getName());
			dest.writeInt(mHash);
			ArrayList<Field> fields = getDeclaredFields(getClass());
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = null;
				try {
					value = field.get(this);
				} catch (IllegalAccessException e) {
				}
				int pos = dest.dataPosition();
				try {
					dest.writeValue(value);
				} catch (Exception e) {
					dest.setDataPosition(pos);
					dest.writeValue(null);
				}
			}
		} finally {
			parcelableBlockList.remove(this);
		}
	}

	/**
	 * Read from parcel.
	 *
	 * @param in parcel
	 */
	public void readFromParcel(Parcel in) {
		mHash = in.readInt();
		mHashList.put(mHash, this);
		ArrayList<Field> fields = getDeclaredFields(getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			Object value = in.readValue(Thread.currentThread().getContextClassLoader());
			try {
				field.set(this, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/** The Constant CREATOR. */
	public static final Parcelable.Creator<? extends Model>	CREATOR	= new Parcelable.Creator<Model>() {
																		@SuppressWarnings("unchecked")
																		public Model createFromParcel(Parcel in) {
																			Class<? extends Model> c;
																			try {
																				String className = in.readString();
																				try {
																					int hash = Integer.parseInt(className);
																					return mHashList.get(hash);
																				} catch (NumberFormatException e) {
																				}
																				c = (Class<? extends Model>) Class.forName(className);
																				try {
																					Constructor<? extends Model> parcelConstructor = c.getDeclaredConstructor(Parcel.class);
																					if (parcelConstructor != null)
																						return parcelConstructor.newInstance(in);
																				} catch (Exception e) {
																				}
																				Model o = newInstance(c);
																				o.readFromParcel(in);
																				return o;
																			} catch (Exception e) {
																				e.printStackTrace();
																			}
																			return null;
																		}

																		public Model[] newArray(int size) {
																			return new Model[size];
																		}
																	};

	/**
	 * Gets the json attributes.
	 *
	 * @return the attributes
	 */
	public JSONObject getAttributes() {
		JSONObject json = new JSONObject();
		ArrayList<Field> fields = getDeclaredFields(getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			ModelField annotation = field.getAnnotation(ModelField.class);
			String jsonKey = annotation.json();
			if (!TextUtils.isEmpty(jsonKey)) {
				try {
					json.put(annotation.json(), castValue(field.get(this)));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param json the json attributes to set
	 * @return the model
	 */
	public Model setAttributes(JSONObject json) {
		ArrayList<Field> fields = getDeclaredFields(getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			ModelField annotation = field.getAnnotation(ModelField.class);
			String jsonKey = annotation.json();
			if (!TextUtils.isEmpty(jsonKey) && json.has(jsonKey)) {
				field.setAccessible(true);
				try {
					Object value = json.get(annotation.json());
					field.set(this, castValue(field.getType(), field.getGenericType(), value));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}

		return this;
	}

	/**
	 * Cast value.
	 *
	 * @param tClass the t class
	 * @param paramType the param type
	 * @param value the value
	 * @return the object
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws JSONException the JSON exception
	 */
	@SuppressWarnings("unchecked")
	private Object castValue(Class<?> tClass, Type paramType, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, JSONException {
		try {
			if (Model.class.isAssignableFrom(tClass) && value instanceof JSONObject) {
				Class<? extends Model> c = (Class<? extends Model>) tClass;
				Model model = (Model) c.getMethod("newInstance", Class.class, JSONObject.class).invoke(null, c, (JSONObject) value);
				return model;
			} else if (Byte.class.isAssignableFrom(tClass) || byte.class.isAssignableFrom(tClass)) {
				if (value instanceof Number)
					return ((Number) value).byteValue();
				else if (value instanceof String)
					return Byte.valueOf((String) value);
			} else if (Short.class.isAssignableFrom(tClass) || short.class.isAssignableFrom(tClass)) {
				if (value instanceof Number)
					return ((Number) value).shortValue();
				else if (value instanceof String)
					return Short.valueOf((String) value);
			} else if (Integer.class.isAssignableFrom(tClass) || int.class.isAssignableFrom(tClass)) {
				if (value instanceof Number)
					return ((Number) value).intValue();
				else if (value instanceof String)

					return Integer.valueOf((String) value);

			} else if (Long.class.isAssignableFrom(tClass) || long.class.isAssignableFrom(tClass)) {
				if (value instanceof Number)
					return ((Number) value).longValue();
				else if (value instanceof String)
					return Long.valueOf((String) value);
			} else if (Float.class.isAssignableFrom(tClass) || float.class.isAssignableFrom(tClass)) {
				if (value instanceof Number)
					return ((Number) value).floatValue();
				else if (value instanceof String)
					return Float.valueOf((String) value);
			} else if (Double.class.isAssignableFrom(tClass) || double.class.isAssignableFrom(tClass)) {
				if (value instanceof Number)
					return ((Number) value).doubleValue();
				else if (value instanceof String)
					return Double.valueOf((String) value);
			} else if (Boolean.class.isAssignableFrom(tClass) || boolean.class.isAssignableFrom(tClass)) {
				if (value instanceof Number)
					return ((Number) value).byteValue() == 1;
				else if (value instanceof String)
					if (value.equals("true") || value.equals("false"))
						return Boolean.parseBoolean((String) value);
					else
						return Byte.valueOf((String) value) == 1;
				else if (value instanceof Boolean)
					return Boolean.valueOf((Boolean) value);
			} else if ((List.class.isAssignableFrom(tClass) || Set.class.isAssignableFrom(tClass)) && value instanceof JSONArray && paramType instanceof ParameterizedType) {
				Type listType = ((ParameterizedType) paramType).getActualTypeArguments()[0];
				JSONArray jsonArray = (JSONArray) value;
				Collection<Object> collection = null;
				if (!tClass.isInterface() && tClass.getDeclaredConstructor() != null) {
					try {
						collection = (Collection<Object>) tClass.newInstance();
					} catch (InstantiationException e) {
					}
				}
				if (collection == null)
					if (List.class.isAssignableFrom(tClass))
						collection = new ArrayList<Object>();
					else
						collection = new LinkedHashSet<Object>();
				for (int i = 0, m = jsonArray.length(); i < m; i++) {
					if (!jsonArray.isNull(i))
						collection.add(castValue((Class<?>) listType, listType, jsonArray.get(i)));
					else
						collection.add(null);
				}
				return collection;
			} else if (Map.class.isAssignableFrom(tClass) && value instanceof JSONObject && paramType instanceof ParameterizedType) {
				Type keyType = ((ParameterizedType) paramType).getActualTypeArguments()[0];
				Type mapType = ((ParameterizedType) paramType).getActualTypeArguments()[1];
				JSONObject jsonObject = (JSONObject) value;
				Map<Object, Object> map;
				try {
					map = (Map<Object, Object>) tClass.newInstance();
				} catch (InstantiationException e) {
					map = new HashMap<Object, Object>();
				}
				Iterator<String> iterator = jsonObject.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					if (!jsonObject.isNull(key))
						map.put(castValue((Class<?>) keyType, keyType, key), castValue((Class<?>) mapType, mapType, jsonObject.get(key)));
					else {
						map.put(key, null);
					}
				}
				return map;
			}
		} catch (NumberFormatException e) {
			return 0;
		}
		return value;
	}

	/**
	 * Cast value.
	 *
	 * @param value the value
	 * @return the object
	 */
	private Object castValue(Object value) {
		if (value instanceof Model) {
			Model model = (Model) value;
			if (model == this)
				return null;
			if (mSerializeFrom != model && model != null) {
				model.mSerializeFrom = this;
				JSONObject json = model.getAttributes();
				model.mSerializeFrom = null;
				return json;
			}
		} else if (value instanceof List || value instanceof Set) {
			JSONArray jsonArray = new JSONArray();
			if (value != null) {
				@SuppressWarnings("unchecked")
				Collection<Object> list = (Collection<Object>) value;
				for (Object item : list)
					jsonArray.put(castValue(item));
			}
			return jsonArray;
		} else if (value instanceof Map) {
			JSONObject jsonObject = new JSONObject();
			if (value != null) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) value;
				for (Entry<Object, Object> e : map.entrySet()) {
					try {
						jsonObject.put(e.getKey().toString(), castValue(e.getValue()));
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}

			}
			return jsonObject;
		}

		return value;
	}

	/**
	 * Gets the declared fields.
	 *
	 * @param c the class
	 * @return the declared fields
	 */
	private synchronized static ArrayList<Field> getDeclaredFields(Class<?> c) {
		if (sDeclaredFields != null)
			return sDeclaredFields;
		ArrayList<Field> sDeclaredFields = new ArrayList<Field>();
		ArrayList<Field> superFields = null;
		Class<?> superClass = c.getSuperclass();
		if (superClass != null)
			superFields = getDeclaredFields(superClass);
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(ModelField.class) != null) {
				if (superFields != null) {
					Iterator<Field> iter = superFields.iterator();
					while (iter.hasNext()) {
						Field superField = iter.next();
						if (field.getName().equals(superField.getName())) {
							iter.remove();
						}
					}
				}
				sDeclaredFields.add(field);
			}
		}
		if (superFields != null) {
			sDeclaredFields.addAll(superFields);
		}
		return sDeclaredFields;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (mToStringBlockList.contains(this))
			return "<Model object #" + Integer.toString(mHash) + ">";
		ArrayList<Field> list = getDeclaredFields(getClass());
		if (list.isEmpty()) {
			return "[]";
		}
		StringBuilder buffer = new StringBuilder(list.size() * 16);
		mToStringBlockList.add(this);
		try {
			buffer.append('[');
			Iterator<Field> it = list.iterator();
			while (it.hasNext()) {
				Field next = it.next();
				next.setAccessible(true);
				buffer.append(next.getName());
				buffer.append(": ");
				try {
					buffer.append(next.get(this));
				} catch (IllegalAccessException e) {
					buffer.append("?");
				}
				if (it.hasNext()) {
					buffer.append(", ");
				}
			}
			buffer.append(']');
		} finally {
			mToStringBlockList.remove(this);
		}
		return buffer.toString();
	}

}