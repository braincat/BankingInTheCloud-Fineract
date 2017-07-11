/*
 *  Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.javamoney.data.icu4j;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.spi.Bootstrap;

import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.data.AbstractXmlResourceLoaderListener;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class CLDRCurrencyRegionData extends AbstractXmlResourceLoaderListener {

    private static final Logger LOG = Logger.getLogger(CLDRCurrencyRegionData.class.getName());
    private static final CLDRCurrencyRegionData INSTANCE = createInstance();

    /*
     * <region iso3166="AD"> <currency iso4217="EUR" from="1999-01-01"/>
     * <currency iso4217="ESP" from="1873-01-01" to="2002-02-28"/> <currency
     * iso4217="FRF" from="1960-01-01" to="2002-02-17"/> <currency iso4217="ADP"
     * from="1936-01-01" to="2001-12-31"/> </region>
     */
    private Map<String, Currency4Region> currencyRegionData;

    private CLDRCurrencyRegionData() throws MalformedURLException {
        Bootstrap.getService(LoaderService.class).addLoaderListener(this, "CLDR-SupplementalData");
        try {
            Bootstrap.getService(LoaderService.class).loadData("CLDR-SupplementalData");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error loading CLDR supplemental data.", e);
        }
    }

    public static CLDRCurrencyRegionData getInstance() {
        return INSTANCE;
    }

    private static CLDRCurrencyRegionData createInstance() {
        try {
            return new CLDRCurrencyRegionData();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(
                    "Error creating CLDR SupplementalData.", e);
        }
    }

    @Override
    protected void loadDocument(Document document) {
        // load currencies
        Map<String, Currency4Region> data = new HashMap<>();
        try {
            NodeList nl = document.getDocumentElement()
                    .getElementsByTagName("region");
            for (int i = 0; i < nl.getLength(); i++) {
                Node childNode = nl.item(i);
                String regionCode = childNode.getAttributes()
                        .getNamedItem("iso3166").getNodeValue();
                Currency4Region currencyEntry = new Currency4Region(
                        regionCode, childNode);
                data.put(regionCode, currencyEntry);
            }
            this.currencyRegionData = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Currency4Region getCurrencyData(String countryCode) {
        if (this.currencyRegionData == null) {
            return null;
        }
        return this.currencyRegionData.get(countryCode);
    }

    public Collection<Currency4Region> getAllCurrencyData() {
        if (this.currencyRegionData == null) {
            return Collections.emptySet();
        }
        return this.currencyRegionData.values();
    }

    public boolean isCurrencyDataAvailable(String countryCode) {
        if (this.currencyRegionData == null) {
            return false;
        }
        return this.currencyRegionData.containsKey(countryCode);
    }

    public String getSource() {
        return "http://unicode.org/repos/cldr/trunk/common/supplemental/supplementalData.xml";
    }


    public static final class Currency4Region implements Serializable {

        private String regionCode;
        private List<CurrencyRegionRecord> entries = new ArrayList<>();

        public Currency4Region(String regionCode, Node item) {
            this.regionCode = regionCode;
            NodeList nl = item.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node childNode = nl.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    entries.add(new CurrencyRegionRecord(regionCode, childNode));
                }
            }
            // <region iso3166="AF">
            // <currency iso4217="AFN" from="2002-10-07"/>
            // <currency iso4217="AFA" from="1927-03-14" to="2002-12-31"/>
        }

        public String getRegionCode() {
            return regionCode;
        }


        public List<CurrencyRegionRecord> getEntries() {
            return Collections.unmodifiableList(this.entries);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Currency4Region [regionCode=" + regionCode + ", entries="
                    + entries + "]";
        }

    }

    public static final class CurrencyRegionRecord implements Serializable {
        private String regionCode;
        private String currencyCode;
        private int[] fromYMD;
        private int[] toYMD;
        private boolean legalTender = true;

        public CurrencyRegionRecord(String regionCode, Node item) {
            // <region iso3166="AF">
            // <currency iso4217="AFN" from="2002-10-07"/>
            // <currency iso4217="AFA" from="1927-03-14" to="2002-12-31"/>
            this.regionCode = regionCode;
            NamedNodeMap attrs = item.getAttributes();
            this.currencyCode = attrs.getNamedItem("iso4217").getNodeValue();
            if (attrs.getNamedItem("from") != null) {
                String from = attrs.getNamedItem("from").getNodeValue();
                String[] dtParts = from.split("-");
                this.fromYMD = new int[3];
                this.fromYMD[0] = Integer.parseInt(dtParts[0]);
                this.fromYMD[1] = Integer.parseInt(dtParts[1]);
                this.fromYMD[2] = Integer.parseInt(dtParts[2]);
            }
            if (attrs.getNamedItem("to") != null) {
                String to = attrs.getNamedItem("to").getNodeValue();
                String[] dtParts = to.split("-");
                this.toYMD = new int[3];
                this.toYMD[0] = Integer.parseInt(dtParts[0]);
                this.toYMD[1] = Integer.parseInt(dtParts[1]);
                this.toYMD[2] = Integer.parseInt(dtParts[2]);
            }
            if (attrs.getNamedItem("tender") != null) {
                this.legalTender = Boolean.parseBoolean(attrs.getNamedItem(
                        "tender").getNodeValue());
            }
        }

        public String getRegionCode() {
            return regionCode;
        }

        /**
         * @return the currencyCode
         */
        public final String getCurrencyCode() {
            return currencyCode;
        }

        /**
         * @return the fromYMD
         */
        public final int[] getFromYMD() {
            return fromYMD;
        }

        /**
         * @return the toYMD
         */
        public final int[] getToYMD() {
            return toYMD;
        }

        public boolean isLegalTender() {
            return legalTender;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "CurrencyRegionRecord [regionCode=" + regionCode
                    + ", currencyCode=" + currencyCode + ", fromYMD="
                    + Arrays.toString(fromYMD) + ", toYMD="
                    + Arrays.toString(toYMD) + "]";
        }

    }

}
