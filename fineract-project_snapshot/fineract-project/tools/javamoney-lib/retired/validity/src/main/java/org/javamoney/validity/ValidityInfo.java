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
import java.util.Calendar;

/**
 * This base class models a validity of an item T related to a timeline. Hereby
 * a validity may be undefined, when it starts or when it ends.
 * <p>
 * This class is immutable, thread-safe and {@link Serializable}. Also it
 * implements {@link Comparable}.
 * 
 * @author Anatole Tresch
 * 
 * @param <T>
 *            the item type, e.g. a Region
 */
public class ValidityInfo<T> implements Serializable,
		Comparable<ValidityInfo<T>> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1258686258819748870L;

	/** The item for which this ValidityInfo is for. */
	private final T item;
	/**
	 * The starting UTC timestamp for the validity period, or null.
	 */
	private final Long from;
	/**
	 * The ending UTC timestamp for the validity period, or null.
	 */
	private final Long to;
	/**
	 * The source that provides this validity data.
	 */
	private final String validitySource;

	/**
	 * The target timezone id of this validity instance, allowing to restore the
	 * correct local date.
	 */
	private final String targetTimezoneId;

	/**
	 * The type of validity.
	 */
	private ValidityType validityType;

	/**
	 * Additional user data associated with that {@link ValidityInfo} instance.
	 */
	private Object userData;

	/**
	 * Creates an instance of ValidityInfo.
	 * 
	 * @param item
	 *            the item, not null.
	 * @param validitySource
	 *            the validity source.
	 * @param from
	 *            the calendar instance, defining the start of the validity
	 *            range.
	 * @param to
	 *            the calendar instance, defining the end of the validity range.
	 */
	public ValidityInfo(T item, ValidityType validityType,
			String validitySource, Calendar from,
			Calendar to, String targetTimezoneId, Object userData) {
		if (item == null) {
			throw new IllegalArgumentException("Currency required.");
		}
		if (validityType == null) {
			throw new IllegalArgumentException("ValidityType required.");
		}
		if (validitySource == null) {
			throw new IllegalArgumentException("Validity Source required.");
		}
		this.item = item;
		this.validityType = validityType;
		this.validitySource = validitySource;
		if (from != null) {
			this.from = from.getTimeInMillis();
		} else {
			this.from = null;
		}
		if (to != null) {
			this.to = to.getTimeInMillis();
		} else {
			this.to = null;
		}
		this.targetTimezoneId = targetTimezoneId;
		this.userData = this.userData = userData;
	}

	/**
	 * Creates an instance of ValidityInfo.
	 * 
	 * @param item
	 *            the item, not null.
	 * @param validitySource
	 *            the validity source.
	 * @param from
	 *            the UTC timestamp, defining the start of the validity range.
	 * @param to
	 *            the UTC timestamp, defining the end of the validity range.
	 */
	public ValidityInfo(T item, ValidityType validityType,
			String validitySource, Long from, Long to,
			String targetTimezoneId, Object userData) {
		if (item == null) {
			throw new IllegalArgumentException("Currency required.");
		}
		if (validityType == null) {
			throw new IllegalArgumentException("ValidityType required.");
		}
		if (validitySource == null) {
			throw new IllegalArgumentException("Validity Source required.");
		}
		this.item = item;
		this.validityType = validityType;
		this.validitySource = validitySource;
		this.from = from;
		this.to = to;
		this.targetTimezoneId = targetTimezoneId;
		this.userData = userData;
	}

	/**
	 * Returns the type of validity this instance represents.
	 * 
	 * @see ValidityType
	 * @return the validity type, never {@code null}.
	 */
	public ValidityType getValidityType() {
		return this.validityType;
	}

	/**
	 * Access the user data.
	 * 
	 * @return the user data associated with this instance, or null.
	 */
	@SuppressWarnings("unchecked")
	public Object getUserData() {
		return (T) userData;
	}

	/**
	 * Method to quickly determine if a validity is not defined, meaning
	 * {@code from} as well as {@code to} is {@code null}.
	 * 
	 * @return {@code true} if the validity is undefined.
	 */
	public boolean isUndefined() {
		return from == null && to == null;
	}

	/**
	 * Method to quickly determine if a validity is valid for the current
	 * timestamp. A Validity is considered valid, if all the following is
	 * {@code true}:
	 * <ul>
	 * <li><@code from == null || from <= current UTC timestamp}</li>
	 * <li><@code to == null || to >= current UTC timestamp}</li>
	 * </ul>
	 * 
	 * @return {@code true} if the validity is currently valid.
	 */
	public boolean isValid() {
		long ts = System.currentTimeMillis();
		return (from == null || from <= ts)
				&& (to == null || to >= ts);
	}

	/**
	 * Method to easily check if the {@code from} is not {@code null}.
	 * 
	 * @return {@code true} if {@code from} is not {@code null}.
	 */
	public boolean isLowerBound() {
		return from != null;
	}

	/**
	 * Method to easily check if the {@code from} is not {@code null}.
	 * 
	 * @return {@code true} if {@code from} is not {@code null}.
	 */
	public boolean isUpperBound() {
		return to != null;
	}

	/**
	 * Access the item for which the validity is defined.
	 * 
	 * @return the item, never null.
	 */
	public T getItem() {
		return item;
	}

	/**
	 * Access the starting UTC timestamp from which the item T is valid, related
	 * to R.
	 * 
	 * @return the starting UTC timestamp, or null.
	 */
	public Long getFromTimeInMillis() {
		if (from != null) {
			return from;
		}
		return null;
	}

	/**
	 * Access the ending UTC timestamp until the item T is valid, related to R.
	 * 
	 * @return the ending UTC timestamp, or null.
	 */
	public Long getToTimeInMillis() {
		if (to != null) {
			return to;
		}
		return null;
	}

	/**
	 * Return the target time zone for the given validity, which allows to
	 * distinct validities that may embrace several timezones (e.g. for
	 * regions). Additionally the timezone, combined with the UTC timestamp
	 * allows to reconstruct the local date (e.g. within the according
	 * {@link Region}).
	 * 
	 * @return the target timezone id of this validity, never {@code null}.
	 */
	public String getTargetTimezoneId() {
		return targetTimezoneId;
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
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result
				+ ((validitySource == null) ? 0 : validitySource.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		ValidityInfo other = (ValidityInfo) obj;
		if (from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!from.equals(other.from)) {
			return false;
		}
		if (item == null) {
			if (other.item != null) {
				return false;
			}
		} else if (!item.equals(other.item)) {
			return false;
		}
		if (to == null) {
			if (other.to != null) {
				return false;
			}
		} else if (!to.equals(other.to)) {
			return false;
		}
		if (validitySource == null) {
			if (other.validitySource != null) {
				return false;
			}
		} else if (!validitySource.equals(other.validitySource)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compareTo(ValidityInfo other) {
		if (this == other) {
			return 0;
		}
		if (other == null) {
			return -1;
		}
		int compare = 0;
		if (item instanceof Comparable) {
			if (item == null) {
				if (other.item != null) {
					compare = 1;
				}
			} else {
				if (other.item == null) {
					compare = -1;
				} else {
					compare = ((Comparable) item).compareTo(other.item);
				}
			}
		}
		if (from == null) {
			if (other.from != null) {
				compare = 1;
			}
		} else {
			if (other.from == null) {
				compare = -1;
			} else {
				if (other.from == null) {
					compare = -1;
				} else {
					compare = ((Comparable) from).compareTo(other.from);
				}
			}
		}
		if (compare == 0) {
			if (to == null) {
				if (other.to != null) {
					compare = 1;
				}
			} else {
				if (other.to == null) {
					compare = -1;
				} else {
					compare = ((Comparable) to).compareTo(other.to);
				}
			}
		}
		if (compare == 0) {
			if (validitySource == null) {
				if (other.validitySource != null) {
					compare = 1;
				}
			} else {
				if (other.validitySource == null) {
					compare = -1;
				} else {
					compare = ((Comparable) validitySource)
							.compareTo(other.validitySource);
				}
			}
		}
		return compare;
	}
}
