package com.ai.htmlgen;
import com.ai.data.*;

/**
 * <pre>
 * Represents a hierarchical data set.
 * An hds is a collection of rows.
 * You can step through the rows using ILoopForwardIterator
 * You can find out about the columns via IMetaData.
 * An hds is also a collection loops orginated using the current row.
 * </pre>
 */
public interface ihds
        extends ILoopForwardIterator
{
    /**
     * Returns the parent if available
     * Returns null if there is no parent
     */
    public ihds getParent()
            throws DataException;
    /**
     * For the current row return a set of
     * child loop names. ILoopForwardIteraor determines
     * what the current row is.
     *
     * @see ILoopForwardIterator
     */
    public IIterator getChildNames()
            throws DataException;

    /**
     * Given a child name return the child java object
     * represented by ihds again
     */
    public ihds getChild(String childName)
            throws DataException;

    /**
     * returns a column that is similar to SUM, AVG etc of a
     * set of rows that are children to this row.
     */
    public String getAggregateValue(String keyname)
            throws DataException;

    /**
     * Returns the column names of this loop or table.
     * @see IMetaData
     */
    public IMetaData getMetaData()
            throws DataException;


    /**
     * Releases any resources that may be held by this loop of data
     * or table.
     */
    public void close() throws DataException;
}