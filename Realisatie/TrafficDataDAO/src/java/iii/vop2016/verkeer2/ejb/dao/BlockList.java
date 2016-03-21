/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRouteData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tobia
 */
public class BlockList {

    private static final int blockSize = 1000000;
    private static final int minBlockSize = 1000;

    private ITrafficDataDAO dao;
    private BlockList parent;
    private long size;
    private long startId;
    private Date startDate;
    private Map<Long, BlockList> sublist;
    private boolean valid = false;

    //constructor called on root blocklist only
    public BlockList(long size, ITrafficDataDAO aThis) {
        this.dao = aThis;
        this.parent = null;
        this.size = size;
        this.startId = 1;
        this.sublist = new TreeMap<Long, BlockList>();
        try {
            IRouteData data = dao.getDataByID(startId);
            this.startDate = data.getTimestamp();

            int i = 0;
            for (i = 1; i < size; i += blockSize) {
                long subsize = blockSize;
                BlockList l = new BlockList(subsize, dao, this, i);
                if (l.valid) {
                    this.sublist.put((long) i, l);
                }

            }
            this.valid = true;
        } catch (Exception e) {
            this.valid = false;
        }
    }

    private BlockList(long size, ITrafficDataDAO dao, BlockList parent, long startIndex) {
        this.dao = dao;
        this.parent = parent;
        this.size = size;
        this.startId = startIndex;
        this.sublist = null;

        try {
            IRouteData data = dao.getDataByID(startId);
            this.startDate = data.getTimestamp();

            if (size > minBlockSize) {
                this.sublist = new TreeMap<>();

                long step = size / 10;
                long endIndex = startIndex + size;
                for (long i = startIndex; i < endIndex; i += step) {
                    long subsize = step;
                    if (endIndex < i + step) {
                        subsize = size % 10;
                    }
                    BlockList l = new BlockList(subsize, dao, this, i);
                    if (l.valid) {
                        this.sublist.put(i, l);
                    }
                }
            }
            this.valid = true;
        } catch (Exception e) {
            this.valid = false;
        }

    }

    public long[] getIdRange(Date date) {
        long[] i = new long[2];
        i[0] = getIdBeginRange(date);
        i[1] = getIdEndRange(date);
        return i;
    }
    
    public long[] getIdRange(Date dateStart, Date dateEnd) {
        long[] i = new long[2];
        i[0] = getIdBeginRange(dateStart);
        i[1] = getIdEndRange(dateEnd);
        return i;
    }

    public long getIdBeginRange(Date date) {
        Map.Entry<Long, BlockList> prevEntry = null;
        for (Map.Entry<Long, BlockList> entry : sublist.entrySet()) {
            if (prevEntry == null) {
                prevEntry = entry;
            }

            if (!entry.getValue().startDate.before(date)) {
                BlockList l = prevEntry.getValue();
                if (l.sublist != null) {
                    long id = l.getIdBeginRange(date);
                    return id;
                } else {
                    return l.startId;
                }
            }

            prevEntry = entry;
        }
        BlockList l = prevEntry.getValue();
        if (l.sublist != null) {
            long id = l.getIdBeginRange(date);
            return id;
        } else {
            return l.startId;
        }

    }

    public long getIdEndRange(Date date) {
        Map.Entry<Long, BlockList> prevEntry = null;
        for (Map.Entry<Long, BlockList> entry : sublist.entrySet()) {
            if (prevEntry == null) {
                prevEntry = entry;
            }

            if (entry.getValue().startDate.after(date)) {
                BlockList l = prevEntry.getValue();
                if (l.sublist != null) {
                    long id = l.getIdEndRange(date);
                    return id;
                } else {
                    return entry.getValue().startId;
                }
            }

            prevEntry = entry;
        }
        BlockList l = prevEntry.getValue();
        if (l.sublist != null) {
            long id = l.getIdEndRange(date);
            return id;
        } else {
            return prevEntry.getValue().startId;
        }

    }

}
