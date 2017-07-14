/*
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil.
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
package org.javamoney.currencies.internal.data;

import org.javamoney.currencies.spi.CurrencyUnitNamespaceProviderSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.inject.Singleton;
import javax.money.CurrencyContext;
import javax.money.CurrencyContextBuilder;
import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.spi.CurrencyProviderSpi;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.Currency;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Online implementation of a {@link CurrencyUnitNamespaceProviderSpi} that provides the
 * ISO 4217 currencies available from the JDK {@link Currency} class.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @see <a href="www.currency-iso.org">Currency Code Services – ISO 4217
 * Maintenance Agency</a>
 */
@Singleton
public class IsoCurrencyOnlineProvider implements CurrencyProviderSpi{

    private static final Logger LOGGER = LoggerFactory.getLogger(IsoCurrencyOnlineProvider.class);

    private static final CurrencyContext CURRENCY_CONTEXT = CurrencyContextBuilder.of("ISO").build();

    private static final String PROP_FILE = "/currencyprovider.properties";

    private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

    private Map<String,String> countryCodeMap = new ConcurrentHashMap<>();

    private Map<String,CurrencyUnit> currencies = new ConcurrentHashMap<>();

    private final Properties prop = new Properties();

    public IsoCurrencyOnlineProvider(){
        saxParserFactory.setNamespaceAware(false);
        saxParserFactory.setValidating(false);
        new CurrencyLoader().start();
    }

    @Override
    public String getProviderName(){
        return "ISO";
    }

    @Override
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery query){
        if(query.getTimestamp() != null){
            return Collections.emptySet();
        }
        Set<CurrencyUnit> currencies = new HashSet<>();
        if(!query.getCurrencyCodes().isEmpty()){
            for(String code : query.getCurrencyCodes()){
                CurrencyUnit cu = this.currencies.get(code);
                if(cu != null && !isJDKCurrency(code)){
                    currencies.add(cu);
                }
            }
        }else{
            currencies.addAll(this.currencies.values());
        }
        return currencies;
    }

    private boolean isJDKCurrency(String code){
        try{
            Currency.getInstance(code);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public void loadCurrencies(){
        try(InputStream in = getClass().getResourceAsStream(PROP_FILE)){
            if(in == null){
                return;
            }
            prop.load(in);
            final String urlAddress = prop.getProperty(getClass().getSimpleName() + ".currencies");
            URL url = new URL(urlAddress);
            LOGGER.debug("Loading " + urlAddress);
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(url.openStream(), new CurrencyHandler());
        }
        catch(Exception e){
            LOGGER.warn("Error", e);
        }
    }

    public void loadCountries(){
        try(InputStream in = getClass().getResourceAsStream(PROP_FILE)){
            if(in == null){
                return;
            }
            prop.load(in);
            final String urlAddress = prop.getProperty(getClass().getSimpleName() + ".countries");
            LOGGER.debug("Loading " + urlAddress);
            URL url = new URL(urlAddress);
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(url.openStream(), new CountryHandler());
        }
        catch(Exception e){
            LOGGER.warn("Error", e);
        }
    }

    private final class ISOCurrency implements CurrencyUnit{
        private String currencyName;
        private String currencyCode;
        private int numericCode;
        private int minorUnits;

        public String getCurrencyCode(){
            return currencyCode;
        }

        public int getNumericCode(){
            return numericCode;
        }

        public int getDefaultFractionDigits(){
            return minorUnits;
        }

        @Override
        public CurrencyContext getCurrencyContext(){
            return CURRENCY_CONTEXT;
        }

        @Override
        public String toString(){
            return this.currencyCode;
        }

        public String getDisplayName(Locale locale){
            // TODO use Locale and add getDisplayName(), too
            return currencyName;
        }

        public int getCashRounding(){
            return -1;
        }

        @Override
        public int compareTo(CurrencyUnit o){
            return this.getCurrencyCode().compareTo(o.getCurrencyCode());
        }
    }

    private class CountryHandler extends DefaultHandler{

        // <ISO_3166-1_List_en xml:lang="en">
        // <ISO_3166-1_Entry>
        // <ISO_3166-1_Country_name>AFGHANISTAN</ISO_3166-1_Country_name>
        // <ISO_3166-1_Alpha-2_Code_element>AF</ISO_3166-1_Alpha-2_Code_element>
        // </ISO_3166-1_Entry>
        // ...
        // </ISO_3166-1_List_en xml:lang="en">

        private String name;
        private String alpha2Code;
        private StringBuilder text = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
            if("ISO_3166-1_Entry".equals(qName)){
                name = null;
                alpha2Code = null;
            }else if("ISO_3166-1_Country_name".equals(qName)){
                text.setLength(0);
            }else if("ISO_3166-1_Alpha-2_Code_element".equals(qName)){
                text.setLength(0);
            }
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException{
            text.append(ch, start, length);
            super.characters(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException{
            if("ISO_3166-1_Entry".equals(qName)){
                countryCodeMap.put(name, alpha2Code);
            }else if("ISO_3166-1_Country_name".equals(qName)){
                name = text.toString();
            }else if("ISO_3166-1_Alpha-2_Code_element".equals(qName)){
                alpha2Code = text.toString();
            }
            super.endElement(uri, localName, qName);
        }
    }

    private class CurrencyHandler extends DefaultHandler{

        // <ISO_CCY_CODES>
        // <ISO_CURRENCY>
        // <ENTITY>AFGHANISTAN</ENTITY>
        // <CURRENCY>Afghani</CURRENCY>
        // <ALPHABETIC_CODE>AFN</ALPHABETIC_CODE>
        // <NUMERIC_CODE>971</NUMERIC_CODE>
        // <MINOR_UNIT>2</MINOR_UNIT>
        // </ISO_CURRENCY>
        // ...
        // </ISO_CCY_CODES>

        private ISOCurrency currency = null;
        private StringBuilder text = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
            if("ISO_CURRENCY".equals(qName)){
                currency = new ISOCurrency();
            }else if("ENTITY".equals(qName)){
                text.setLength(0);
            }else if("CURRENCY".equals(qName)){
                text.setLength(0);
            }else if("ALPHABETIC_CODE".equals(qName)){
                text.setLength(0);
            }else if("NUMERIC_CODE".equals(qName)){
                text.setLength(0);
            }else if("MINOR_UNIT".equals(qName)){
                text.setLength(0);
            }
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException{
            text.append(ch, start, length);
            super.characters(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException{
            if("ISO_CURRENCY".equals(qName)){
                currencies.put(currency.currencyCode, currency);
            }else if("ENTITY".equals(qName)){
                String countryName = text.toString();
                String code = countryCodeMap.get(countryName);
                if(code != null){
                    new Locale("", code);
                }else{
                    // TODO is this a no-op?
                }
            }else if("CURRENCY".equals(qName)){
                currency.currencyName = text.toString();
            }else if("ALPHABETIC_CODE".equals(qName)){
                currency.currencyCode = text.toString();
            }else if("NUMERIC_CODE".equals(qName)){
                String value = text.toString();
                if(!value.isEmpty()){
                    try{
                        currency.numericCode = Integer.valueOf(value);
                    }
                    catch(NumberFormatException nfe){
                        currency.numericCode = -1;
                    }
                }else{
                    currency.numericCode = -1;
                }
            }else if("MINOR_UNIT".equals(qName)){
                String value = text.toString();
                if(!value.isEmpty()){
                    try{
                        currency.minorUnits = Integer.valueOf(value);
                    }
                    catch(NumberFormatException nfe){
                        currency.minorUnits = -1;
                    }
                }else{
                    currency.minorUnits = -1;
                }
            }
            super.endElement(uri, localName, qName);
        }
    }

    private final class CurrencyLoader extends Thread{

        public CurrencyLoader(){
            super("ISO Currency Online Loader");
        }

        public void run(){
            loadCountries();
            loadCurrencies();
            LOGGER.debug("Currencies loaded from ISO:" + IsoCurrencyOnlineProvider.this.currencies.values() +
                                 (IsoCurrencyOnlineProvider.this.countryCodeMap != null ? " for " +
                                         IsoCurrencyOnlineProvider.this.countryCodeMap.size() + " countries" : ""));
        }

    }


}
