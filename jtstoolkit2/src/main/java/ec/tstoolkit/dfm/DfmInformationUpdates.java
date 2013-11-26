/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.tstoolkit.dfm;

import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class DfmInformationUpdates {

    /**
     *
     */
    public static class Update {

        Update(final TsPeriod period, final int series) {
            this.period = period;
            this.series = series;
        }
        
        /**
         *
         * @return
         */
        public double getObservation(){
            return y;
        }
        /**
         *
         * @return
         */
        public double getForecast(){
            return fy;
        }
        /**
         *
         * @return
         */
        public double getNews(){
            return y-fy;
        }
        
        /**
         *
         */
        public final TsPeriod period;
        /**
         *
         */
        public final int series;
        
        double y, fy;
        
        @Override
        public String toString(){
            StringBuilder builder=new StringBuilder();
            builder.append("var:").append(series).append('\t').append(period)
                    .append('\t').append(y).append('\t').append(fy);
            return builder.toString();
        }
    }
    private final List<Update> updates_ = new ArrayList<Update>();
    
    DfmInformationUpdates(){
        
    }

    /**
     *
     * @param p
     * @param series
     */
    public void add(TsPeriod p, int series) {
        updates_.add(new Update(p, series));
    }

    /**
     *
     * @param freq
     * @return
     */
    public TsPeriod firstUpdate(TsFrequency freq) {
        TsPeriod first = null;
        for (Update update : updates_) {
            if (first == null) {
                if (update.period.getFrequency() == freq) {
                    first = update.period.clone();
                } else {
                    first = new TsPeriod(freq);
                    first.set(update.period.lastday());
                }
            } else {
                TsPeriod cur;
                if (update.period.getFrequency() == freq) {
                    cur = update.period.clone();
                } else {
                    cur = new TsPeriod(freq);
                    cur.set(update.period.lastday());
                }
                if (cur.isBefore(first)) {
                    first = cur;
                }
            }
        }
        return first;
    }

    /**
     *
     * @param freq
     * @return
     */
    public TsPeriod lastUpdate(TsFrequency freq) {
        TsPeriod last = null;
        for (Update update : updates_) {
            if (last == null) {
                if (update.period.getFrequency() == freq) {
                    last = update.period.clone();
                } else {
                    last = new TsPeriod(freq);
                    last.set(update.period.lastday());
                }
            } else {
                TsPeriod cur;
                if (update.period.getFrequency() == freq) {
                    cur = update.period.clone();
                } else {
                    cur = new TsPeriod(freq);
                    cur.set(update.period.lastday());
                }
                if (cur.isAfter(last)) {
                    last = cur;
                }
            }
        }
        return last;
    }

    /**
     *
     * @param freq
     * @return
     */
    public TsDomain updatesDomain(TsFrequency freq) {
        TsPeriod first = null;
        TsPeriod last = null;
        for (Update update : updates_) {
            if (first == null) {
                if (update.period.getFrequency() == freq) {
                    first = update.period.clone();
                } else {
                    first = new TsPeriod(freq);
                    first.set(update.period.lastday());
                }
                last = first;
            } else {
                TsPeriod cur;
                if (update.period.getFrequency() == freq) {
                    cur = update.period.clone();
                } else {
                    cur = new TsPeriod(freq);
                    cur.set(update.period.lastday());
                }
                if (cur.isBefore(first)) {
                    first = cur;
                }
                if (cur.isAfter(last)) {
                    last = cur;
                }
            }
        }
        if (last == null) {
            return null;
        }
        return new TsDomain(first, last.minus(first) + 1);
    }

    /**
     *
     * @return
     */
    public List<Update> updates() {
        return Collections.unmodifiableList(updates_);
    }
}