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
package org.javamoney.format;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javamoney.format.spi.TokenizeableFormatsSingletonSpi;

/**
 * This singleton accessor provides access to the formatting logic of JavaMoney. *
 * <p>
 * {@link ItemFormat} instances are not required to be thread-safe. Basically
 * when accessing an {@link ItemFormat} from the {@link ItemFormats}
 * singleton a new instance should be created on each access.<br/>
 * This class itself is thread-safe, delegating its calls to the
 * {@link org.javamoney.format.spi.TokenizeableFormatsSingletonSpi} registered using the
 * {@link ServiceLoader}.
 * 
 * @author Anatole Tresch
 */
public final class ItemFormats{
	/** SPI implementation loaded loaded from ServiceLodaer. */
	private static TokenizeableFormatsSingletonSpi monetaryFormatSpi = loadMonetaryFormatSpi();

	/**
	 * Singleton constructor.
	 */
	private ItemFormats() {
	}

	/**
	 * Loads the MonetaryFormatSpi the {@link org.javamoney.format.spi.TokenizeableFormatsSingletonSpi} used.
	 * 
	 * @return the MonetaryFormatSpi to be used.
	 */
	private static TokenizeableFormatsSingletonSpi loadMonetaryFormatSpi() {
		TokenizeableFormatsSingletonSpi spi = null;
		try {
			// try loading directly from ServiceLoader
			Iterator<TokenizeableFormatsSingletonSpi> instances = ServiceLoader
					.load(TokenizeableFormatsSingletonSpi.class).iterator();
			if (instances.hasNext()) {
				spi = instances.next();
				if (instances.hasNext()) {
					throw new IllegalStateException(
							"Ambigous reference to spi (only "
									+ "one can be registered: "
									+ TokenizeableFormatsSingletonSpi.class
											.getName());
				}
				return spi;
			}
		} catch (Exception e) {
			Logger.getLogger(ItemFormats.class.getName()).log(Level.INFO,
					"No MonetaryFormatSpi found, using  default.", e);
		}
		return new DefaultTokenizeableFormatsSpi();
	}

	/**
	 * Return the style id's supported by this {@link ItemFormatterFactorySpi}
	 * instance.
	 * 
	 * @see LocalizationContext#getId()
	 * @param targetType
	 *            the target type, never {@code null}.
	 * @return the supported style ids, never {@code null}.
	 */
	public static Collection<String> getSupportedStyleIds(Class<?> targetType) {
		Collection<String> styleIDs = monetaryFormatSpi
				.getSupportedStyleIds(targetType);
		if (styleIDs == null) {
			Logger.getLogger(ItemFormats.class.getName()).log(
					Level.WARNING,
					"MonetaryFormatSpi.getSupportedStyleIds returned null for "
							+ targetType);
			return Collections.emptySet();
		}
		return styleIDs;
	}

	/**
	 * Method allows to check if a named style is supported.
	 * 
	 * @param targetType
	 *            the target type, never {@code null}.
	 * @param styleId
	 *            The style id.
	 * @return true, if a spi implementation is able to provide an
	 *         {@link ItemFormat} for the given style.
	 */
	public static boolean isSupportedStyle(Class<?> targetType, String styleId) {
		return monetaryFormatSpi.isSupportedStyle(targetType, styleId);
	}

	/**
	 * Access a default {@link LocalizationContext}.
	 * 
	 * @param targetType
	 *            the target type.
	 * @return the default style, as defined for the targetType.
	 */
	public static LocalizationContext getLocalizationStyle(Class<?> targetType) {
		return monetaryFormatSpi.getLocalizationStyle(targetType,
				LocalizationContext.DEFAULT_ID);
	}

	/**
	 * Access the configuring {@link LocalizationContext},
	 * 
	 * @param targetType
	 *            the target type.
	 * @param styleId
	 *            the style's identifier.
	 * @return the style instance found.
	 */
	public static LocalizationContext getLocalizationStyle(Class<?> targetType,
			String styleId) {
		return monetaryFormatSpi.getLocalizationStyle(targetType, styleId);
	}

	/**
	 * This method returns an instance of an {@link ItemFormat} .
	 * 
	 * @param targetType
	 *            the target type, never {@code null}.
	 * @param style
	 *            the {@link LocalizationContext} to be attached to this
	 *            {@link ItemFormat}, which also contains the target
	 *            {@link Locale} instances to be used, as well as other
	 *            attributes configuring this instance.
	 * @return the formatter required, if available.
	 * @throws ItemFormatException
	 *             if the {@link LocalizationContext} passed can not be used for
	 *             configuring the {@link ItemFormat} and no matching
	 *             {@link ItemFormat} could be provided.
	 */
	public static <T> ItemFormat<T> getItemFormat(Class<T> targetType)
			throws ItemFormatException {
		LocalizationContext style = getLocalizationStyle(targetType,
				LocalizationContext.DEFAULT_ID);
		if (style == null) {
			throw new ItemFormatException("No default style present for "
					+ targetType);
		}
		return getItemFormat(targetType, style);
	}

	/**
	 * This method returns an instance of an {@link ItemFormat} .
	 * 
	 * @param targetType
	 *            the target type, never {@code null}.
	 * @param style
	 *            the {@link LocalizationContext} to be attached to this
	 *            {@link ItemFormat}, which also contains the target
	 *            {@link Locale} instances to be used, as well as other
	 *            attributes configuring this instance.
	 * @return the formatter required, if available.
	 * @throws ItemFormatException
	 *             if the {@link LocalizationContext} passed can not be used for
	 *             configuring the {@link ItemFormat} and no matching
	 *             {@link ItemFormat} could be provided.
	 */
	public static <T> ItemFormat<T> getItemFormat(Class<T> targetType,
			String styleId) throws ItemFormatException {

		return getItemFormat(targetType,
				getLocalizationStyle(targetType, styleId));
	}

	/**
	 * This method returns an instance of an {@link ItemFormat} .
	 * 
	 * @param targetType
	 *            the target type, never {@code null}.
	 * @param style
	 *            the {@link LocalizationContext} to be attached to this
	 *            {@link ItemFormat}, which also contains the target
	 *            {@link Locale} instances to be used, as well as other
	 *            attributes configuring this instance.
	 * @return the formatter required, if available.
	 * @throws ItemFormatException
	 *             if the {@link LocalizationContext} passed can not be used for
	 *             configuring the {@link ItemFormat} and no matching
	 *             {@link ItemFormat} could be provided.
	 */
	public static <T> ItemFormat<T> getItemFormat(Class<T> targetType,
			LocalizationContext style)
			throws ItemFormatException {
		if (style == null) {
			style = LocalizationContext.of(targetType);
		}
		if (targetType == null) {
			throw new IllegalArgumentException("targetType required.");
		}
		try {
			ItemFormat<T> f = monetaryFormatSpi
					.getItemFormat(targetType, style);
			if (f != null) {
				return f;
			}
			throw new ItemFormatException("No formatter available for "
					+ targetType + " and " + style);
		} catch (Exception e) {
			throw new ItemFormatException("Error accessing formatter for "
					+ targetType + " and " + style, e);
		}
	}

	/**
	 * Default SPI implementation of {@link org.javamoney.format.spi.TokenizeableFormatsSingletonSpi}, only
	 * used when no corresponding SPI was registered into {@link ServiceLoader}.
	 * 
	 * @author Anatole Tresch
	 */
	private static final class DefaultTokenizeableFormatsSpi implements TokenizeableFormatsSingletonSpi{
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.money.format.spi.TokenizeableFormatsSingletonSpi#getSupportedStyleIds
		 * (java.lang.Class)
		 */
		@Override
		public Collection<String> getSupportedStyleIds(Class<?> targetType) {
			return LocalizationContext.getSupportedStyleIds(targetType);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.money.format.spi.TokenizeableFormatsSingletonSpi#isSupportedStyle
		 * (java.lang.Class, java.lang.String)
		 */
		@Override
		public boolean isSupportedStyle(Class<?> targetType, String styleId) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.money.format.spi.TokenizeableFormatsSingletonSpi#getItemFormat(
		 * java.lang.Class, javax.money.format.LocalizationStyle)
		 */
		@Override
		public <T> ItemFormat<T> getItemFormat(Class<T> targetType,
				LocalizationContext style) throws ItemFormatException {
			throw new ItemFormatException("No MonetaryFormatSpi registered.");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.money.format.spi.TokenizeableFormatsSingletonSpi#getLocalizationStyle
		 * (java.lang.Class, java.lang.String)
		 */
		@Override
		public LocalizationContext getLocalizationStyle(Class<?> targetType,
				String styleId) {
			return LocalizationContext.of(targetType, styleId);
		}

	}
}
