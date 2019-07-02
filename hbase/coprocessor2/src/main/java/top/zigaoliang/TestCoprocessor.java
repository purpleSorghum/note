package top.zigaoliang;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.io.FSDataInputStreamWrapper;
import org.apache.hadoop.hbase.io.Reference;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.regionserver.FlushLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.regionserver.MiniBatchOperationInProgress;
import org.apache.hadoop.hbase.regionserver.Region;
import org.apache.hadoop.hbase.regionserver.RegionScanner;
import org.apache.hadoop.hbase.regionserver.ScanOptions;
import org.apache.hadoop.hbase.regionserver.ScanType;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.regionserver.StoreFile;
import org.apache.hadoop.hbase.regionserver.StoreFileReader;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.regionserver.querymatcher.DeleteTracker;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKey;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName TestCoprocessor
 * @Author hanlin
 * @Date 2019/7/1 16:50
 **/
public class TestCoprocessor implements RegionObserver,RegionCoprocessor,MasterObserver,MasterCoprocessor {
    private static final Log LOG = LogFactory.getLog(TestCoprocessor.class);

    private void outInfo(String str){
        str = Thread.currentThread().getStackTrace()[1].getClassName() + "." +str;
        System.out.println(str);
        LOG.info(str);
        try {
            FileWriter fw = new FileWriter("/home/coprocessor.txt",true);
            fw.write(new Date() +" | "+ str + "\r\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Optional<MasterObserver> getMasterObserver() {
        return Optional.of(this);
    }

    @Override
    public Optional<RegionObserver> getRegionObserver() {
        return Optional.of(this);
    }

    @Override
    public void start(CoprocessorEnvironment env) throws IOException {
        LOG.info("========== start =========");
    }

    @Override
    public void stop(CoprocessorEnvironment env) throws IOException {
        LOG.info("========== end =========");
    }

    @Override
    public void preOpen(ObserverContext<RegionCoprocessorEnvironment> c) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postOpen(ObserverContext<RegionCoprocessorEnvironment> c) {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preFlush(ObserverContext<RegionCoprocessorEnvironment> c, FlushLifeCycleTracker tracker) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preFlushScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, ScanOptions options, FlushLifeCycleTracker tracker) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public InternalScanner preFlush(ObserverContext<RegionCoprocessorEnvironment> c, Store store, InternalScanner scanner, FlushLifeCycleTracker tracker) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return scanner;
    }

    @Override
    public void postFlush(ObserverContext<RegionCoprocessorEnvironment> c, FlushLifeCycleTracker tracker) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postFlush(ObserverContext<RegionCoprocessorEnvironment> c, Store store, StoreFile resultFile, FlushLifeCycleTracker tracker) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preMemStoreCompaction(ObserverContext<RegionCoprocessorEnvironment> c, Store store) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preMemStoreCompactionCompactScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, ScanOptions options) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public InternalScanner preMemStoreCompactionCompact(ObserverContext<RegionCoprocessorEnvironment> c, Store store, InternalScanner scanner) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return scanner;
    }

    @Override
    public void postMemStoreCompaction(ObserverContext<RegionCoprocessorEnvironment> c, Store store) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends StoreFile> candidates, CompactionLifeCycleTracker tracker) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends StoreFile> selected, CompactionLifeCycleTracker tracker, CompactionRequest request) {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preCompactScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, ScanType scanType, ScanOptions options, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> c, Store store, InternalScanner scanner, ScanType scanType, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return scanner;
    }

    @Override
    public void postCompact(ObserverContext<RegionCoprocessorEnvironment> c, Store store, StoreFile resultFile, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preClose(ObserverContext<RegionCoprocessorEnvironment> c, boolean abortRequested) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postClose(ObserverContext<RegionCoprocessorEnvironment> c, boolean abortRequested) {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> c, Get get, List<Cell> result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postGetOp(ObserverContext<RegionCoprocessorEnvironment> c, Get get, List<Cell> result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public boolean preExists(ObserverContext<RegionCoprocessorEnvironment> c, Get get, boolean exists) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return exists;
    }

    @Override
    public boolean postExists(ObserverContext<RegionCoprocessorEnvironment> c, Get get, boolean exists) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return exists;
    }

    @Override
    public void prePut(ObserverContext<RegionCoprocessorEnvironment> c, Put put, WALEdit edit, Durability durability) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> c, Put put, WALEdit edit, Durability durability) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preDelete(ObserverContext<RegionCoprocessorEnvironment> c, Delete delete, WALEdit edit, Durability durability) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void prePrepareTimeStampForDeleteVersion(ObserverContext<RegionCoprocessorEnvironment> c, Mutation mutation, Cell cell, byte[] byteNow, Get get) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postDelete(ObserverContext<RegionCoprocessorEnvironment> c, Delete delete, WALEdit edit, Durability durability) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postStartRegionOperation(ObserverContext<RegionCoprocessorEnvironment> ctx, Region.Operation operation) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postCloseRegionOperation(ObserverContext<RegionCoprocessorEnvironment> ctx, Region.Operation operation) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postBatchMutateIndispensably(ObserverContext<RegionCoprocessorEnvironment> ctx, MiniBatchOperationInProgress<Mutation> miniBatchOp, boolean success) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public boolean preCheckAndPut(ObserverContext<RegionCoprocessorEnvironment> c, byte[] row, byte[] family, byte[] qualifier, CompareOperator op, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return result;
    }

    @Override
    public boolean preCheckAndPutAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> c, byte[] row, byte[] family, byte[] qualifier, CompareOperator op, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return result;
    }

    @Override
    public boolean postCheckAndPut(ObserverContext<RegionCoprocessorEnvironment> c, byte[] row, byte[] family, byte[] qualifier, CompareOperator op, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return result;
    }

    @Override
    public boolean preCheckAndDelete(ObserverContext<RegionCoprocessorEnvironment> c, byte[] row, byte[] family, byte[] qualifier, CompareOperator op, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return result;
    }

    @Override
    public boolean preCheckAndDeleteAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> c, byte[] row, byte[] family, byte[] qualifier, CompareOperator op, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return result;
    }

    @Override
    public boolean postCheckAndDelete(ObserverContext<RegionCoprocessorEnvironment> c, byte[] row, byte[] family, byte[] qualifier, CompareOperator op, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return result;
    }

    @Override
    public Result preAppend(ObserverContext<RegionCoprocessorEnvironment> c, Append append) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return null;
    }

    @Override
    public Result preAppendAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> c, Append append) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return null;
    }

    @Override
    public Result postAppend(ObserverContext<RegionCoprocessorEnvironment> c, Append append, Result result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return result;
    }

    @Override
    public Result preIncrement(ObserverContext<RegionCoprocessorEnvironment> c, Increment increment) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return null;
    }

    @Override
    public Result preIncrementAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> c, Increment increment) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return null;
    }

    @Override
    public Result postIncrement(ObserverContext<RegionCoprocessorEnvironment> c, Increment increment, Result result) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return result;
    }

    @Override
    public void preScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Scan scan) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public RegionScanner postScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Scan scan, RegionScanner s) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return s;
    }

    @Override
    public boolean preScannerNext(ObserverContext<RegionCoprocessorEnvironment> c, InternalScanner s, List<Result> result, int limit, boolean hasNext) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return hasNext;
    }

    @Override
    public boolean postScannerNext(ObserverContext<RegionCoprocessorEnvironment> c, InternalScanner s, List<Result> result, int limit, boolean hasNext) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return hasNext;
    }

    @Override
    public boolean postScannerFilterRow(ObserverContext<RegionCoprocessorEnvironment> c, InternalScanner s, Cell curRowCell, boolean hasMore) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return hasMore;
    }

    @Override
    public void preScannerClose(ObserverContext<RegionCoprocessorEnvironment> c, InternalScanner s) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postScannerClose(ObserverContext<RegionCoprocessorEnvironment> ctx, InternalScanner s) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preStoreScannerOpen(ObserverContext<RegionCoprocessorEnvironment> ctx, Store store, ScanOptions options) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preReplayWALs(ObserverContext<? extends RegionCoprocessorEnvironment> ctx, RegionInfo info, Path edits) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postReplayWALs(ObserverContext<? extends RegionCoprocessorEnvironment> ctx, RegionInfo info, Path edits) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preWALRestore(ObserverContext<? extends RegionCoprocessorEnvironment> ctx, RegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postWALRestore(ObserverContext<? extends RegionCoprocessorEnvironment> ctx, RegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preBulkLoadHFile(ObserverContext<RegionCoprocessorEnvironment> ctx, List<Pair<byte[], String>> familyPaths) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void preCommitStoreFile(ObserverContext<RegionCoprocessorEnvironment> ctx, byte[] family, List<Pair<Path, Path>> pairs) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postCommitStoreFile(ObserverContext<RegionCoprocessorEnvironment> ctx, byte[] family, Path srcPath, Path dstPath) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public void postBulkLoadHFile(ObserverContext<RegionCoprocessorEnvironment> ctx, List<Pair<byte[], String>> stagingFamilyPaths, Map<byte[], List<Path>> finalPaths) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Override
    public StoreFileReader preStoreFileReaderOpen(ObserverContext<RegionCoprocessorEnvironment> ctx, FileSystem fs, Path p, FSDataInputStreamWrapper in, long size, CacheConfig cacheConf, Reference r, StoreFileReader reader) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return reader;
    }

    @Override
    public StoreFileReader postStoreFileReaderOpen(ObserverContext<RegionCoprocessorEnvironment> ctx, FileSystem fs, Path p, FSDataInputStreamWrapper in, long size, CacheConfig cacheConf, Reference r, StoreFileReader reader) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return reader;
    }

    @Override
    public Cell postMutationBeforeWAL(ObserverContext<RegionCoprocessorEnvironment> ctx, MutationType opType, Mutation mutation, Cell oldCell, Cell newCell) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return newCell;
    }

    @Override
    public DeleteTracker postInstantiateDeleteTracker(ObserverContext<RegionCoprocessorEnvironment> ctx, DeleteTracker delTracker) throws IOException {
        outInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        return delTracker;
    }
}
