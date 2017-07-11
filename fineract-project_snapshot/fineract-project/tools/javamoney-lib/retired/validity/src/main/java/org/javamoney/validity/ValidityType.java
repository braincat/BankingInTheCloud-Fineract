/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.validity;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class models the type of a given {@link ValidityInfo} as immutable value
 * type. Hereby the temporal existence of an item is the default, as modelled by
 * {@link ValidityType#EXISTENCE}. For querying any type of validities,
 * {@link ValidityType#ANY} should be used.
 * <p>
 * This class is immutable, thread-safe, {@link Serializable} and
 * {@link Comparable}.
 * 
 * @author Anatole Tresch
 */
public final class ValidityType implements Serializable,
		Comparable<ValidityType> {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7505497771490888058L;
	/** The id of this type. */
	private final String id;
	/** The cache of types. */
	private static final Map<String, ValidityType> CACHED_INSTANCES = new ConcurrentHashMap<String, ValidityType>();
	/** Default validity modelling the temporal existance of an item. */
	public static final ValidityType EXISTENCE = new ValidityType("EXISTENCE");
	/** Used for quering validities of any type. */
	public static final ValidityType ANY = new ValidityType("ANY");

	/**
	 * Creates a new instance.
	 * 
	 * @param id
	 *            The validity type identifier.
	 * @return The new validity type.
	 */
	public static ValidityType of(String id) {
		if (id == null) {
			throw new IllegalArgumentException("id required.");
		}
		ValidityType instance = CACHED_INSTANCES.get(id);
		if (instance == null) {
			instance = new ValidityType(id);
			CACHED_INSTANCES.put(id, instance);
		}
		return instance;
	}

	/**
	 * Constructs a new instance of an {@link ValidityType}.
	 * 
	 * @param id
	 *            The id of this type instance, never null.
	 */
	public ValidityType(String id) {
		if (id == null) {
			throw new IllegalArgumentException("Id must not be null.");
		}
		this.id = id;
	}

	/**
	 * Get the identifier of this instance.
	 * 
	 * @return The identifier, never null.
	 */
	public String getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidityType other = (ValidityType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValidityType [id=" + id + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ValidityType o) {
		if (o == null) {
			return -1;
		}
		int compare = id.compareTo(o.id);
		return compare;
	}

}
