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
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This class models a validity of an item S related to a reference T.
 * <p>
 * This class is required to be immutable, thread-safe and {@link Serializable}.
 * 
 * @author Anatole Tresch
 * 
 * @param <T>
 *            the item type, e.g. CurrencyUnit. T must be {@link Serializable}.
 * @param <R>
 *            the validity reference type, e.g. Region. R must be
 *            {@link Serializable}.
 */
public final class RelatedValidityInfo<T, R> extends ValidityInfo<T> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1258686258819748871L;

	/**
	 * THe reference type to which this validity is related.
	 */
	private final R relatedItem;

	/**
	 * Creates an instance of ValidityInfo.
	 * 
	 * @param item
	 *            the item, not null.
	 * @param referenceItem
	 *            , the reference, not null.
	 * @param validitySource
	 *            the validity source.
	 * @param from
	 *            the calendar instance, defining the start of the validity
	 *            range.
	 * @param to
	 *            the calendar instance, defining the end of the validity range.
	 */
	public RelatedValidityInfo(T item, R referenceItem,
			ValidityType validityType, String validitySource,
			Calendar from, Calendar to, String targetTimezoneId, Object userData) {
		super(item, validityType, validitySource, from, to, targetTimezoneId,
				userData);
		if (referenceItem == null) {
			throw new IllegalArgumentException("Reference Item required.");
		}
		this.relatedItem = referenceItem;
	}

	/**
	 * Creates an instance of ValidityInfo.
	 * 
	 * @param item
	 *            the item, not null.
	 * @param referenceItem
	 *            , the reference, not null.
	 * @param validitySource
	 *            the validity source.
	 * @param from
	 *            the UTC timestamp, defining the start of the validity range.
	 * @param to
	 *            the UTC timestamp, defining the end of the validity range.
	 */
	public RelatedValidityInfo(T item, R referenceItem,
			ValidityType validityType, String validitySource,
			Long from, Long to, String targetTimezoneId, Object userData) {
		super(item, validityType, validitySource, from, to, targetTimezoneId,
				userData);
		if (referenceItem == null) {
			throw new IllegalArgumentException("Reference Item required.");
		}
		this.relatedItem = referenceItem;
	}

	/**
	 * Access the type or range for which the item T is valid.
	 * 
	 * @return the ference instance, never null.
	 */
	public R getRelatedtem() {
		return relatedItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((relatedItem == null) ? 0 : relatedItem.hashCode());
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
		Calendar cal;
		@SuppressWarnings("rawtypes")
		RelatedValidityInfo other = (RelatedValidityInfo) obj;
		if (!super.equals(other)) {
			return false;
		}
		if (relatedItem == null) {
			if (other.relatedItem != null) {
				return false;
			}
		} else if (!relatedItem.equals(other.relatedItem)) {
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
		int compare = super.compareTo(other);
		if (compare != 0) {
			return compare;
		}
		if (!(other instanceof RelatedValidityInfo)) {
			return -1;
		}
		RelatedValidityInfo otherRef = (RelatedValidityInfo) other;

		if (compare == 0 && relatedItem instanceof Comparable) {
			if (relatedItem == null) {
				if (otherRef.relatedItem != null) {
					compare = 1;
				}
			} else {
				if (otherRef.relatedItem == null) {
					compare = -1;
				} else {
					compare = ((Comparable) relatedItem)
							.compareTo(otherRef.relatedItem);
				}
			}
		}
		return compare;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RelatedValidityInfo [item="
				+ getItem()
				+ ", ref="
				+ relatedItem
				+ ", from="
				+ formatTime(getFromTimeInMillis())
				+ ", to="
				+ formatTime(getToTimeInMillis())
				+ ", userData="
				+ (getUserData() == null ? "-" : getUserData().getClass()
						.getName()) + "]";
	}

	/**
	 * Format the given time as {@code "YYYY-MM-dd timezoneID"}.
	 * 
	 * @param time
	 *            the time
	 * @return the formatted time, or {@code '-'}, when time is {@code null}.
	 */
	private String formatTime(Long time) {
		if (time == null) {
			return "-";
		}
		StringBuilder b = new StringBuilder();
		GregorianCalendar cal = new GregorianCalendar(
				TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(time);
		b.append(cal.get(Calendar.YEAR)).append("-")
				.append(cal.get(Calendar.MONTH)).append("-")
				.append(cal.get(Calendar.DAY_OF_MONTH));
		b.append(" ").append(cal.getTimeZone().getID());
		return b.toString();
	}

}
