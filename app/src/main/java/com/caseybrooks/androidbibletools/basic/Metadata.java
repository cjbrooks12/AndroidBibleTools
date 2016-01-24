package com.caseybrooks.androidbibletools.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Metadata is essentially a wrapper around a HashMap that maps Strings to {@link Comparable} Object types,
 * and has convenience methods for several common datatypes: int, long, boolean, and String. It is
 * similar in function to a Bundle, but has a different purpose: Objects need not be Parcelable, and
 * the purpose of Metadata is not to marshall data between Android OS processes, but to give users
 * of this library a means to attach arbitrary data to verses and sort the verses by that data.
 * <p>
 * Because verses should be sortable by any key in the Metadata, all Obbjects in the map must
 * implement the Comparable interface, or be added with an appropriate Comparator.
 * <p>
 * Unlike a Bundle, the data within the map is not intended to be persisted, and any persistence of data
 * inside the Metadata must be done manually.
 * <p>
 * Metadata is generally typesafe, but should not be relied upon too heavily. Any of the convenience
 * methods for getting an Object out of the Metadata will throw a {@link ClassCastException} if the
 * object at that key does not match the return type. In addition, sorting pairs of Metadata by a
 * common key requires the classes of the Objects at that key are of the exact same class, and anything
 * other than an exact match, including any derived classes, will also throw a ClassCastException.
 */
public final class Metadata {
	private HashMap<String, Object> items;

	/**
	 * Create a new, empty map of String to Comparable Objects.
	 */
	public Metadata() {
		items = new HashMap<>();
	}

	/**
	 * Check if this Metadata contains the given key.
	 *
	 * @param key  the key to check
	 * @return true if the key exists in the map, false otherwise
	 */
	public boolean containsKey(String key) {
		return items.containsKey(key);
	}

	/**
	 * Get the Class corresponding to a particular key.
	 *
	 * @param key  the key to check the type of
	 * @return the Class corresponding to the object at that key, if it exists, otherwise null
	 */
	public Class checkType(String key) {
		return (containsKey(key)) ? items.get(key).getClass() : null;
	}

	/**
	 * Get the number of items in this map
	 *
	 * @return the number of items
	 */
	public int size() {
		return items.size();
	}

	/**
	 * Get the set of keys contained in this map.
	 *
	 * @return the keys in this map
	 */
	public Set<String> getKeys() {
		return items.keySet();
	}

	/**
	 * Put an arbitrary Object into the map at the given key. The Object added must be a Comparable
	 * type.
	 *
	 * @param key  the key
	 * @param value  the Comparable object
	 *
	 * @throws IllegalArgumentException if value does not implement Comparable interface
	 */
	public void put(String key, Object value) {
		if(value instanceof Comparable) {
			items.put(key, value);
		}
		else {
			throw new IllegalArgumentException(
					"Objects must implement " + Comparable.class.getName() + ". [" +
					value.getClass().getName() + "] does not name a Comparable type"
			);
		}
	}

	/**
	 * Put an int into the map at the given key.
	 *
	 * @param key  the key
	 * @param value  the int
	 */
	public void putInt(String key, int value) {
		put(key, Integer.valueOf(value));
	}

	/**
	 * Put a long into the map at the given key.
	 *
	 * @param key  the key
	 * @param value  the long
	 */
	public void putLong(String key, long value) {
		put(key, Long.valueOf(value));
	}

	/**
	 * Put a boolean value into the map at the given key.
	 *
	 * @param key  the key
	 * @param value  the boolean
	 */
	public void putBoolean(String key, boolean value) {
		put(key, Boolean.valueOf(value));
	}

	/**
	 * Put a String into the map at the given key.
	 *
	 * @param key  the key
	 * @param value  the String
	 */
	public void putString(String key, String value) {
		put(key, value);
	}

	/**
	 * Get the Object from the map at the given key. Since items can only be added when they are
	 * Comparable, there is no need to check that condition here.
	 *
	 * @param key  the key
	 */
	private Object get(String key) {
		return items.get(key);
	}

	/**
	 * Get the integer value of the object at the given key.
	 *
	 * @param key  the key
	 * @param defValue  if the key does not exist in the map, return defValue instead
	 * @return the value of the object at key
	 *
	 * @throws ClassCastException if the object at the given key is not an Integer
	 */
	public int getInt(String key, int defValue) {
		if(items.containsKey(key)) {
			Object item = items.get(key);
			if(item != null) {
				if(!items.get(key).getClass().equals(Integer.class)) {
					throw new ClassCastException(
							"Key [" + key + "] expected result of type [" + Integer.class.toString() + "], found [" + items
									.get(key)
									.getClass()
									.toString() + "]"
					);
				}
				else {
					return (int) item;
				}
			}
		}
		return defValue;
	}

	/**
	 * Get the integer value of the object at the given key.
	 *
	 * @param key  the key
	 * @return the value of the object at key. If the key does not exist, returns 0
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}

	/**
	 * Get the long value of the object at the given key.
	 *
	 * @param key  the key
	 * @param defValue  if the key does not exist in the map, return defValue instead
	 * @return the value of the object at key
	 *
	 * @throws ClassCastException if the object at the given key is not a Long
	 */
	public long getLong(String key, long defValue) {
		if(items.containsKey(key)) {
			Object item = items.get(key);
			if(item != null) {
				if(!items.get(key).getClass().equals(Long.class)) {
					throw new ClassCastException(
							"Key [" + key + "] expected result of type [" + Long.class.toString() + "], found [" + items
									.get(key)
									.getClass()
									.toString() + "]"
					);
				}
				else {
					return (long) item;
				}
			}
		}
		return defValue;
	}

	/**
	 * Get the long value of the object at the given key.
	 *
	 * @param key  the key
	 * @return the value of the object at key. If the key does not exist, returns 0l
	 */
	public long getLong(String key) {
		return getLong(key, 0l);
	}

	/**
	 * Get the boolean value of the object at the given key.
	 *
	 * @param key  the key
	 * @param defValue  if the key does not exist in the map, return defValue instead
	 * @return the value of the object at key
	 *
	 * @throws ClassCastException if the object at the given key is not a Boolean
	 */
	public boolean getBoolean(String key, boolean defValue) {
		if(items.containsKey(key)) {
			Object item = items.get(key);
			if(item != null) {
				if(!items.get(key).getClass().equals(Boolean.class)) {
					throw new ClassCastException(
							"Key [" + key + "] expected result of type [" + Boolean.class.toString() + "], found [" + items
									.get(key)
									.getClass()
									.toString() + "]"
					);
				}
				else {
					return (boolean) item;
				}
			}
		}
		return defValue;
	}

	/**
	 * Get the boolean value of the object at the given key.
	 *
	 * @param key  the key
	 * @return the value of the object at key. If the key does not exist, returns false
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	/**
	 * Get the integer value of the object at the given key.
	 *
	 * @param key  the key
	 * @param defValue  if the key does not exist in the map, return defValue instead
	 * @return the value of the object at key
	 *
	 * @throws ClassCastException if the object at the given key is not a String
	 */
	public String getString(String key, String defValue) {
		if(items.containsKey(key)) {
			Object item = items.get(key);
			if(item != null) {
				if(!items.get(key).getClass().equals(String.class)) {
					throw new ClassCastException(
							"Key [" + key + "] expected result of type [" + String.class.toString() + "], found [" + items
									.get(key)
									.getClass()
									.toString() + "]"
					);
				}
				else {
					return (String) item;
				}
			}
		}
		return defValue;
	}

	/**
	 * Get the String value of the object at the given key.
	 *
	 * @param key  the key
	 * @return the value of the object at key. If the key does not exist, returns the empty String
	 */
	public String getString(String key) {
		return getString(key, "");
	}

	//TODO: decide whether this should truly be here, in Metadata, or in AbstractVerse
	/**
	 * The class responsible for sorting AbstractVerses by their Metadata.
	 */
	public static final class Comparator implements java.util.Comparator<AbstractVerse> {
		public static String KEY_REFERENCE_CANONICAL = "KEY_REF_CANONICAL";
		public static String KEY_REFERENCE_ALPHABETICAL = "KEY_REFERENCE_ALPHABETICAL";
		private String key;

		/**
		 * Initialize this Comparator with the key of the object in Metadata to sort by.
		 *
		 * @param key  the key
		 */
		public Comparator(String key) {
			this.key = key;
		}

		@Override
		public int compare(AbstractVerse a, AbstractVerse b) {
			if(a == null || b == null)
				throw new IllegalArgumentException("Both objects to compare must be non-null");

			if(key.equals(KEY_REFERENCE_CANONICAL)) {
				return a.getReference().compareTo(b.getReference());
			}
			else if(key.equals(KEY_REFERENCE_ALPHABETICAL)) {
				return a.getReference().toString().compareTo(b.getReference().toString());
			}
			else {
				Object lhs = a.getMetadata().get(key);
				Object rhs = b.getMetadata().get(key);
				if(lhs == null || rhs == null) {
					throw new NullPointerException(
							"One or more objects at the given key are null"
					);
				}
				else if(lhs.getClass().equals(rhs.getClass())) {
					try {
						Comparable lhs_c = (Comparable) lhs;
						Comparable rhs_c = (Comparable) rhs;

						return lhs_c.compareTo(rhs_c);
					}
					catch(ClassCastException e) {
						throw new ClassCastException(
								"Object at [" + key + "] of type [" + lhs.getClass().toString() +
								" does not name a Comparable type."
						);
					}
				}
				else {
					throw new ClassCastException(
							"Objects are not of the same Class: " +
							lhs.getClass().toString() + " " +
							rhs.getClass().toString()
					);
				}
			}
		}
	}

	/**
	 * Sort AbstractVerses according to multiple criteria. In the event that all metadata values
	 * in these comparators match, sort by canonical order
	 */
	public static class MultiComparator implements java.util.Comparator<AbstractVerse> {
		ArrayList<Comparator> comparisonCriteria;

		public MultiComparator(ArrayList<Comparator> comparisonCriteria) {
			this.comparisonCriteria = comparisonCriteria;
		}

		@Override
		public int compare(AbstractVerse lhs, AbstractVerse rhs) {
			for(Comparator comparator : comparisonCriteria) {
				int comparison = comparator.compare(lhs, rhs);
				if(comparison != 0) {
					return comparison;
				}
			}
			return lhs.getReference().compareTo(rhs.getReference());
		}
	}
}
