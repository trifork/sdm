package com.trifork.stamdata.importer.jobs;

import antlr.ParserSharedInputState;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;
import com.trifork.stamdata.importer.parsers.Parser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Uses a file system directory as inbox.
 *
 * The inbox directory can contain sub-directories. Each one
 * represents an inbox element.
 *
 * The ordering of the elements are determined by their lexicographical
 * order. Thus using timestamps for when the sub-directories are placed
 * is a convenient naming convention.
 *
 * This implementation also makes sure that the input directories
 * are stable (no changes are occurring). This can help avoid problems
 * where files are only partly uploaded.
 */
public class DirectoryInbox implements Inbox
{
    private static final ConcurrentMap<String, Long> sizeHistory = new MapMaker().expireAfterAccess(10, TimeUnit.MINUTES).makeMap();

    private final File inboxDirectory;

    /**
     * The elements in the current found by the latest call to {@code update}.
     */
    private Queue<File> elements;

    @Inject
    DirectoryInbox(@Named("rootDir") String root, Parser parser) throws IOException
    {
        inboxDirectory = new File(root + File.pathSeparator + parser.identifier());

        // Make sure the directory exists.
        //
        FileUtils.forceMkdir(inboxDirectory);
    }

    /**
     * Returns the top-most sub-directory.
     *
     * This will not remove the element from the inbox.
     *
     * This will return null if the top-most directory is not
     * yet stable. In such a case you should try again later.
     *
     * @return the top-most if it exists and is stable, else null.
     */
    @Override
    public File peek()
    {
        return elements.peek();
    }

    @Override
    public void progress() throws IOException
    {
        // First we have to successfully delete the
        // directory physically.
        //
        FileUtils.deleteDirectory(peek());

        // Then we can pop it from the input queue.
        //
        elements.poll();
    }

    /**
     * The number of stable elements in the inbox.
     *
     * All items might not be stable and thus calls
     * to {@link #peek()} may not always return an
     * element even when {@code size > 0}.
     *
     * @return The number of items in the inbox.
     */
    @Override
    public int size()
    {
        return elements.size();
    }

    @Override
    public void update() throws IOException
    {
        // Allow IO to
        //
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        // Dispose of any previous elements and use lexicographical ordering
        // for the elements.
        //
        elements = new PriorityQueue<File>(10, Ordering.usingToString());

        for (File element : inboxDirectory.listFiles())
        {
            // Only files are considered.
            //
            if (element.isFile()) continue;

            Long previousSize = sizeHistory.get(element.getPath());
            Long currentSize = FileUtils.sizeOfDirectory(element);

            if (previousSize == null || previousSize != currentSize)
            {
                // Update the current.
                //
                sizeHistory.put(element.getPath(), currentSize);

                // Ensure that only stable directories
                // makes it into the elements queue.
                //
                // If this is not done we might end up
                // with items being imported in the
                // wrong order.
                //
                break;
            }
            else
            {
                // If the content is stable add the element.
                //
                elements.add(element);
            }
        }
    }
}
