/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.parsers;

import com.google.common.collect.*;
import com.google.inject.Inject;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.importer.config.DataOwnerId;
import org.apache.commons.io.FileUtils;
import org.joda.time.Instant;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static com.trifork.stamdata.Preconditions.checkArgument;

/**
 * Uses a file system directory as inbox.
 *
 * The inbox directory can contain sub-directories. Each one
 * represents an inbox element.
 *
 * The ordering of the dataSets are determined by their lexicographical
 * order. Thus using timestamps for when the sub-directories are placed
 * is a convenient naming convention.
 *
 * This implementation also makes sure that the input directories
 * are stable (no changes are occurring). This can help avoid problems
 * where files are only partly uploaded. Once stable, a data set is
 * marked as ready.
 *
 * If locked it will place a file called 'LOCKED' in the inbox directory.
 * This file will have to be remove manually in order to continue, one
 * the problem has been fixed.
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public class DirectoryInbox implements Inbox
{
    private static final ConcurrentMap<String, DirectoryState> sizeHistory = new MapMaker().expireAfterAccess(10, TimeUnit.MINUTES).makeMap();

    private final File inboxDirectory;
    private final File lockFile;
    
    /**
     * The dataSets in the current found by the latest call to {@code update}.
     */
    private Queue<File> dataSets = Lists.newLinkedList();
    private final int stabilizationPeriod;

    @Inject
    DirectoryInbox(@Named("rootDir") String root, @DataOwnerId String dataOwnerId, @Named("file.stabilization.period") int stabilizationPeriod) throws IOException
    {
        Preconditions.checkArgument(stabilizationPeriod >= 0, "stabilizationPeriod must be a non-negative number.");
        this.stabilizationPeriod = stabilizationPeriod;

        this.inboxDirectory = new File(root, dataOwnerId);
        this.lockFile = new File(inboxDirectory, "LOCKED");

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
    public File top()
    {
        Preconditions.checkState(!isLocked(), "The inbox is locked.");

        return dataSets.peek();
    }

    @Override
    public void advance() throws IOException
    {
        Preconditions.checkState(!isLocked(), "The inbox is locked.");

        File element = top();
        Preconditions.checkState(element != null, "You must not call advance when no data sets are present.");

        // First we have to successfully delete the
        // directory physically.
        //
        FileUtils.forceDelete(element);

        // Then we can pop it from the input queue.
        //
        dataSets.poll();
    }

    /**
     * The number of stable dataSets in the inbox.
     *
     * All items might not be stable and thus calls
     * to {@link #top()} may not always return an
     * element even when {@code readyCount() > 0}.
     *
     * @return The number of items in the inbox.
     */
    @Override
    public int readyCount()
    {
        Preconditions.checkState(!isLocked(), "The inbox is locked.");

        return dataSets.size();
    }

    @Override
    public void update() throws IOException
    {
        Preconditions.checkState(!isLocked(), "The inbox is locked.");

        final Ordering FILENAME_ORDERING = Ordering.usingToString();

        // Dispose of any previous dataSets and use lexicographical ordering
        // for the dataSets.
        //
        dataSets = new PriorityQueue<File>(10, Ordering.usingToString());

        // There is no guaranties that the filenames are sorted lexicographically
        // so we have to make sure ourselves.
        //
        File[] directories = inboxDirectory.listFiles();
        Arrays.sort(directories, FILENAME_ORDERING);

        boolean unstableEntryFound = false;

        for (File element : directories)
        {
            // Only files are considered in the inbox root.
            //
            if (element.isFile()) continue;

            DirectoryState previousState = sizeHistory.get(element.getPath());
            DirectoryState currentState = createState(element);

            if (previousState == null || currentState.size != previousState.size)
            {
                // Update the current state.
                //
                sizeHistory.put(element.getPath(), currentState);

                // Ensure that only stable directories (top down)
                // makes it into the dataSets queue.
                //
                // If this is not done we might end up
                // with items being imported in the
                // wrong order.
                //
                unstableEntryFound = true;
            }
            else if (!unstableEntryFound && previousState.timestamp.plus(stabilizationPeriod).isBefore(currentState.timestamp))
            {
                // If the content is stable add the element.
                //
                dataSets.add(element);
            }
        }
    }

    @Override
    public void lock()
    {
        try
        {
            lockFile.createNewFile();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not create lock file.", e);
        }
    }

    @Override
    public boolean isLocked()
    {
        // The lock file must be manually deleted.
        //
        return lockFile.exists();
    }

    private static DirectoryState createState(File directory)
    {
        checkArgument(directory.isDirectory(), "Cannot create a snapshot state of anything but a directory.");

        DirectoryState state = new DirectoryState();
        state.size = FileUtils.sizeOfDirectory(directory);
        state.timestamp = Instant.now();

        return state;
    }

    private static class DirectoryState
    {
        Long size = null;
        Instant timestamp = null;
    }
}
