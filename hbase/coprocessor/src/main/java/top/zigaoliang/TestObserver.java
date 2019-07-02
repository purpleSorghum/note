package top.zigaoliang;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.coprocessor.*;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.io.FSDataInputStreamWrapper;
import org.apache.hadoop.hbase.io.Reference;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.master.RegionPlan;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.protobuf.generated.AdminProtos;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.protobuf.generated.QuotaProtos;
import org.apache.hadoop.hbase.protobuf.generated.SecureBulkLoadProtos;
import org.apache.hadoop.hbase.regionserver.*;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.regionserver.wal.HLogKey;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.replication.ReplicationEndpoint;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.WALKey;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;



public class TestObserver extends BaseObserver {
    private static Logger log=Logger.getLogger(TestObserver.class);
    @Override
    public void prePut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        log.info("TestObserver2.prePut()");
        outInfo("TestObserver2.prePut()");
        super.prePut(e, put, edit, durability);
    }

    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        log.info("TestObserver.postPut()");
        outInfo("TestObserver.postPut() ");
        super.postPut(e, put, edit, durability);
    }

    @Override
    public void preCreateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        outInfo("TestObserver.preCreateTable() " );
        super.preCreateTable(observerContext, hTableDescriptor, hRegionInfos);
    }

    @Override
    public void postCreateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        log.info("TestObserver.postCreateTable()  " );
        outInfo("TestObserver.postCreateTable()  ");
        super.postCreateTable(observerContext, hTableDescriptor, hRegionInfos);
    }

    @Override
    public void preCreateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        log.info("TestObserver.preCreateTableHandler() " );
        outInfo("TestObserver.preCreateTableHandler()  ");
        super.preCreateTableHandler(observerContext, hTableDescriptor, hRegionInfos);
    }

    @Override
    public void postCreateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        log.info("TestObserver.postCreateTableHandler() ");
        outInfo("TestObserver.postCreateTableHandler()");
        super.postCreateTableHandler(observerContext, hTableDescriptor, hRegionInfos);
    }
    @Override
    public void preDeleteTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        outInfo("TestObserver.preDeleteTable() ");
        super.preDeleteTable(observerContext, tableName);
    }
    @Override
    public void postDeleteTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postDeleteTable(observerContext, tableName);
        outInfo("TestObserver.postDeleteTable() ");
    }

    @Override
    public void preDeleteTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preDeleteTableHandler(observerContext, tableName);
        outInfo("TestObserver.preDeleteTableHandler() ");
    }

    @Override
    public void postDeleteTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postDeleteTableHandler(observerContext, tableName);
        outInfo("TestObserver.postDeleteTableHandler() ");
    }

    @Override
    public void preTruncateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preTruncateTable(observerContext, tableName);
        outInfo("TestObserver.preTruncateTable() ");
    }

    @Override
    public void postTruncateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postTruncateTable(observerContext, tableName);
        outInfo("TestObserver.postTruncateTable() ");
    }

    @Override
    public void preTruncateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preTruncateTableHandler(observerContext, tableName);
        outInfo("TestObserver.preTruncateTableHandler() ");
    }

    @Override
    public void postTruncateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postTruncateTableHandler(observerContext, tableName);
        outInfo("TestObserver.postTruncateTableHandler() ");
    }

    @Override
    public void preModifyTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        super.preModifyTable(observerContext, tableName, hTableDescriptor);
        outInfo("TestObserver.preModifyTable() ");
    }

    @Override
    public void postModifyTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        super.postModifyTable(observerContext, tableName, hTableDescriptor);
        outInfo("TestObserver.postModifyTable() ");
    }

    @Override
    public void preModifyTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        super.preModifyTableHandler(observerContext, tableName, hTableDescriptor);
        outInfo("TestObserver.preModifyTableHandler() ");
    }

    @Override
    public void postModifyTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        super.postModifyTableHandler(observerContext, tableName, hTableDescriptor);
        outInfo("TestObserver.postModifyTableHandler() ");
    }

    @Override
    public void preAddColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.preAddColumn(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver.preAddColumn() ");
    }

    @Override
    public void postAddColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.postAddColumn(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver.postAddColumn() ");
    }

    @Override
    public void preAddColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.preAddColumnHandler(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver.preAddColumnHandler() ");
    }

    @Override
    public void postAddColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.postAddColumnHandler(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver.postAddColumnHandler() ");
    }

    @Override
    public void preModifyColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.preModifyColumn(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver.preModifyColumn() ");
    }

    @Override
    public void postModifyColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.postModifyColumn(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver.postModifyColumn() ");
    }

    @Override
    public void preModifyColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.preModifyColumnHandler(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver.preModifyColumnHandler() ");
    }

    @Override
    public void postModifyColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.postModifyColumnHandler(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver.postModifyColumnHandler() ");
    }

    @Override
    public void preDeleteColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        super.preDeleteColumn(observerContext, tableName, bytes);
        outInfo("TestObserver.preDeleteColumn() ");
    }

    @Override
    public void postDeleteColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        super.postDeleteColumn(observerContext, tableName, bytes);
        outInfo("TestObserver.postDeleteColumn() ");
    }

    @Override
    public void preDeleteColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        super.preDeleteColumnHandler(observerContext, tableName, bytes);
        outInfo("TestObserver.preDeleteColumnHandler() ");
    }

    @Override
    public void postDeleteColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        super.postDeleteColumnHandler(observerContext, tableName, bytes);
        outInfo("TestObserver.postDeleteColumnHandler() ");
    }

    @Override
    public void preEnableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preEnableTable(observerContext, tableName);
        outInfo("TestObserver.preEnableTable() ");
    }

    @Override
    public void postEnableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postEnableTable(observerContext, tableName);
        outInfo("TestObserver.postEnableTable() ");
    }

    @Override
    public void preEnableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preEnableTableHandler(observerContext, tableName);
        outInfo("TestObserver.preEnableTableHandler() ");
    }

    @Override
    public void postEnableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postEnableTableHandler(observerContext, tableName);
        outInfo("TestObserver.postEnableTableHandler() ");
    }

    @Override
    public void preDisableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preDisableTable(observerContext, tableName);
        outInfo("TestObserver.preDisableTable() ");
    }

    @Override
    public void postDisableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postDisableTable(observerContext, tableName);
        outInfo("TestObserver.postDisableTable() ");
    }

    @Override
    public void preDisableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preDisableTableHandler(observerContext, tableName);
        outInfo("TestObserver.preDisableTableHandler() ");
    }

    @Override
    public void postDisableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postDisableTableHandler(observerContext, tableName);
        outInfo("TestObserver.postDisableTableHandler() ");
    }

    @Override
    public void preMove(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, ServerName serverName, ServerName serverName1) throws IOException {
        super.preMove(observerContext, hRegionInfo, serverName, serverName1);
        outInfo("TestObserver.preMove() ");
    }

    @Override
    public void postMove(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, ServerName serverName, ServerName serverName1) throws IOException {
        super.postMove(observerContext, hRegionInfo, serverName, serverName1);
        outInfo("TestObserver.postMove() ");
    }

    @Override
    public void preAbortProcedure(ObserverContext<MasterCoprocessorEnvironment> observerContext, ProcedureExecutor<MasterProcedureEnv> procedureExecutor, long l) throws IOException {
        super.preAbortProcedure(observerContext, procedureExecutor, l);
        outInfo("TestObserver.preAbortProcedure() ");
    }

    @Override
    public void postAbortProcedure(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.postAbortProcedure(observerContext);
        outInfo("TestObserver.postAbortProcedure() ");
    }

    @Override
    public void preListProcedures(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preListProcedures(observerContext);
        outInfo("TestObserver.preListProcedures() ");
    }

    @Override
    public void postListProcedures(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<ProcedureInfo> list) throws IOException {
        super.postListProcedures(observerContext, list);
        outInfo("TestObserver.postListProcedures() ");
    }

    @Override
    public void preAssign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        super.preAssign(observerContext, hRegionInfo);
        outInfo("TestObserver.preAssign() ");
    }

    @Override
    public void postAssign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        super.postAssign(observerContext, hRegionInfo);
        outInfo("TestObserver.postAssign() ");
    }

    @Override
    public void preUnassign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, boolean b) throws IOException {
        super.preUnassign(observerContext, hRegionInfo, b);
        outInfo("TestObserver.preUnassign() ");
    }

    @Override
    public void postUnassign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, boolean b) throws IOException {
        super.postUnassign(observerContext, hRegionInfo, b);
        outInfo("TestObserver.postUnassign() ");
    }

    @Override
    public void preRegionOffline(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        super.preRegionOffline(observerContext, hRegionInfo);
        outInfo("TestObserver.preRegionOffline() ");
    }

    @Override
    public void postRegionOffline(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        super.postRegionOffline(observerContext, hRegionInfo);
        outInfo("TestObserver.postRegionOffline() ");
    }

    @Override
    public void preBalance(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preBalance(observerContext);
        outInfo("TestObserver.preBalance() ");
    }

    @Override
    public void postBalance(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<RegionPlan> list) throws IOException {
        super.postBalance(observerContext, list);
        outInfo("TestObserver.postBalance() ");
    }


    @Override
    public boolean preBalanceSwitch(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b) throws IOException {
        outInfo("TestObserver.preBalanceSwitch() ");
        return super.preBalanceSwitch(observerContext, b);
    }

    @Override
    public void postBalanceSwitch(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, boolean b1) throws IOException {
        super.postBalanceSwitch(observerContext, b, b1);
        outInfo("TestObserver.postBalanceSwitch() ");
    }

    @Override
    public void preShutdown(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preShutdown(observerContext);
        outInfo("TestObserver.preShutdown() ");
    }

    @Override
    public void preStopMaster(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preStopMaster(observerContext);
        outInfo("TestObserver.preStopMaster() ");
    }

    @Override
    public void postStartMaster(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.postStartMaster(observerContext);
        outInfo("TestObserver.postStartMaster() ");
    }

    @Override
    public void preMasterInitialization(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preMasterInitialization(observerContext);
        outInfo("TestObserver.preMasterInitialization() ");
    }

    @Override
    public void preSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.preSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver.preSnapshot() ");
    }

    @Override
    public void postSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.postSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver.postSnapshot() ");
    }

    @Override
    public void preListSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        super.preListSnapshot(observerContext, snapshotDescription);
        outInfo("TestObserver.preListSnapshot() ");
    }

    @Override
    public void postListSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        super.postListSnapshot(observerContext, snapshotDescription);
        outInfo("TestObserver.postListSnapshot() ");
    }

    @Override
    public void preCloneSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.preCloneSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver.preCloneSnapshot() ");
    }

    @Override
    public void postCloneSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.postCloneSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver.postCloneSnapshot() ");
    }

    @Override
    public void preRestoreSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.preRestoreSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver.preRestoreSnapshot() ");
    }

    @Override
    public void postRestoreSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.postRestoreSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver.postRestoreSnapshot() ");
    }

    @Override
    public void preDeleteSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        super.preDeleteSnapshot(observerContext, snapshotDescription);
        outInfo("TestObserver.preDeleteSnapshot() ");
    }

    @Override
    public void postDeleteSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        super.postDeleteSnapshot(observerContext, snapshotDescription);
        outInfo("TestObserver.postDeleteSnapshot() ");
    }

    @Override
    public void preGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1) throws IOException {
        super.preGetTableDescriptors(observerContext, list, list1);
        outInfo("TestObserver.preGetTableDescriptors() ");
    }

    @Override
    public void postGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list) throws IOException {
        super.postGetTableDescriptors(observerContext, list);
        outInfo("TestObserver.postGetTableDescriptors() ");
    }

    @Override
    public void preGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1, String s) throws IOException {
        super.preGetTableDescriptors(observerContext, list, list1, s);
        outInfo("TestObserver.preGetTableDescriptors() ");
    }

    @Override
    public void postGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1, String s) throws IOException {
        super.postGetTableDescriptors(observerContext, list, list1, s);
        outInfo("TestObserver.postGetTableDescriptors() ");
    }

    @Override
    public void preGetTableNames(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list, String s) throws IOException {
        super.preGetTableNames(observerContext, list, s);
        outInfo("TestObserver.preGetTableNames() ");
    }

    @Override
    public void postGetTableNames(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list, String s) throws IOException {
        super.postGetTableNames(observerContext, list, s);
        outInfo("TestObserver.postGetTableNames() ");
    }

    @Override
    public void preCreateNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.preCreateNamespace(observerContext, namespaceDescriptor);
        outInfo("TestObserver.preCreateNamespace() ");
    }

    @Override
    public void postCreateNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.postCreateNamespace(observerContext, namespaceDescriptor);
        outInfo("TestObserver.postCreateNamespace() ");
    }

    @Override
    public void preDeleteNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        super.preDeleteNamespace(observerContext, s);
        outInfo("TestObserver.preDeleteNamespace() ");
    }

    @Override
    public void postDeleteNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        super.postDeleteNamespace(observerContext, s);
        outInfo("TestObserver.postDeleteNamespace() ");
    }

    @Override
    public void preModifyNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.preModifyNamespace(observerContext, namespaceDescriptor);
        outInfo("TestObserver.preModifyNamespace() ");
    }

    @Override
    public void postModifyNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.postModifyNamespace(observerContext, namespaceDescriptor);
        outInfo("TestObserver.postModifyNamespace() ");
    }

    @Override
    public void preGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        super.preGetNamespaceDescriptor(observerContext, s);
        outInfo("TestObserver.preGetNamespaceDescriptor() ");
    }

    @Override
    public void postGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.postGetNamespaceDescriptor(observerContext, namespaceDescriptor);
        outInfo("TestObserver.postGetNamespaceDescriptor() ");
    }

    @Override
    public void preListNamespaceDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<NamespaceDescriptor> list) throws IOException {
        super.preListNamespaceDescriptors(observerContext, list);
        outInfo("TestObserver.preListNamespaceDescriptors() ");
    }

    @Override
    public void postListNamespaceDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<NamespaceDescriptor> list) throws IOException {
        super.postListNamespaceDescriptors(observerContext, list);
        outInfo("TestObserver.postListNamespaceDescriptors() ");
    }

    @Override
    public void preTableFlush(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preTableFlush(observerContext, tableName);
        outInfo("TestObserver.preTableFlush() ");
    }

    @Override
    public void postTableFlush(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postTableFlush(observerContext, tableName);
        outInfo("TestObserver.postTableFlush() ");
    }

    @Override
    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetUserQuota(observerContext, s, quotas);
        outInfo("TestObserver.preSetUserQuota() ");
    }

    @Override
    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetUserQuota(observerContext, s, quotas);
        outInfo("TestObserver.postSetUserQuota() ");
    }

    @Override
    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetUserQuota(observerContext, s, tableName, quotas);
        outInfo("TestObserver.preSetUserQuota() ");
    }

    @Override
    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetUserQuota(observerContext, s, tableName, quotas);
        outInfo("TestObserver.postSetUserQuota() ");
    }

    @Override
    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, String s1, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetUserQuota(observerContext, s, s1, quotas);
        outInfo("TestObserver.preSetUserQuota() ");
    }

    @Override
    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, String s1, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetUserQuota(observerContext, s, s1, quotas);
        outInfo("TestObserver.postSetUserQuota() ");
    }

    @Override
    public void preSetTableQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetTableQuota(observerContext, tableName, quotas);
        outInfo("TestObserver.preSetTableQuota() ");
    }

    @Override
    public void postSetTableQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetTableQuota(observerContext, tableName, quotas);
        outInfo("TestObserver.postSetTableQuota() ");
    }

    @Override
    public void preSetNamespaceQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetNamespaceQuota(observerContext, s, quotas);
        outInfo("TestObserver.preSetNamespaceQuota() ");
    }

    @Override
    public void postSetNamespaceQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetNamespaceQuota(observerContext, s, quotas);
        outInfo("TestObserver.postSetNamespaceQuota() ");
    }

    @Override
    public void preDispatchMerge(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HRegionInfo hRegionInfo1) throws IOException {
        super.preDispatchMerge(observerContext, hRegionInfo, hRegionInfo1);
        outInfo("TestObserver.preDispatchMerge() ");
    }

    @Override
    public void postDispatchMerge(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HRegionInfo hRegionInfo1) throws IOException {
        super.postDispatchMerge(observerContext, hRegionInfo, hRegionInfo1);
        outInfo("TestObserver.postDispatchMerge() ");
    }

    @Override
    public void start(CoprocessorEnvironment e) throws IOException {
        super.start(e);
        outInfo("TestObserver.start() ");
    }

    @Override
    public void stop(CoprocessorEnvironment e) throws IOException {
        super.stop(e);
        outInfo("TestObserver.stop() ");
    }

    @Override
    public void preOpen(ObserverContext<RegionCoprocessorEnvironment> e) throws IOException {
        super.preOpen(e);
        outInfo("TestObserver.preOpen() ");
    }

    @Override
    public void postOpen(ObserverContext<RegionCoprocessorEnvironment> e) {
        super.postOpen(e);
        outInfo("TestObserver.postOpen() ");
    }

    @Override
    public void postLogReplay(ObserverContext<RegionCoprocessorEnvironment> e) {
        super.postLogReplay(e);
        outInfo("TestObserver.postLogReplay() ");
    }

    @Override
    public void preClose(ObserverContext<RegionCoprocessorEnvironment> c, boolean abortRequested) throws IOException {
        super.preClose(c, abortRequested);
        outInfo("TestObserver.preClose() ");
    }

    @Override
    public void postClose(ObserverContext<RegionCoprocessorEnvironment> e, boolean abortRequested) {
        super.postClose(e, abortRequested);
        outInfo("TestObserver.postClose() ");
    }

    @Override
    public InternalScanner preFlushScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, KeyValueScanner memstoreScanner, InternalScanner s) throws IOException {
        outInfo("TestObserver.preFlushScannerOpen() ");
        return super.preFlushScannerOpen(c, store, memstoreScanner, s);
    }

    @Override
    public void preFlush(ObserverContext<RegionCoprocessorEnvironment> e) throws IOException {
        super.preFlush(e);
        outInfo("TestObserver.preFlush() ");
    }

    @Override
    public void postFlush(ObserverContext<RegionCoprocessorEnvironment> e) throws IOException {
        super.postFlush(e);
        outInfo("TestObserver.postFlush() ");
    }

    @Override
    public InternalScanner preFlush(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner) throws IOException {
        outInfo("TestObserver.preFlush() ");
        return super.preFlush(e, store, scanner);
    }

    @Override
    public void postFlush(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile) throws IOException {
        super.postFlush(e, store, resultFile);
        outInfo("TestObserver.postFlush() ");
    }

    @Override
    public void preSplit(ObserverContext<RegionCoprocessorEnvironment> e) throws IOException {
        super.preSplit(e);
        outInfo("TestObserver.preSplit() ");
    }

    @Override
    public void preSplit(ObserverContext<RegionCoprocessorEnvironment> c, byte[] splitRow) throws IOException {
        super.preSplit(c, splitRow);
        outInfo("TestObserver.preSplit() ");
    }

    @Override
    public void preSplitBeforePONR(ObserverContext<RegionCoprocessorEnvironment> ctx, byte[] splitKey, List<Mutation> metaEntries) throws IOException {
        super.preSplitBeforePONR(ctx, splitKey, metaEntries);
        outInfo("TestObserver.preSplitBeforePONR() ");
    }

    @Override
    public void preSplitAfterPONR(ObserverContext<RegionCoprocessorEnvironment> ctx) throws IOException {
        super.preSplitAfterPONR(ctx);
        outInfo("TestObserver.preSplitAfterPONR() ");
    }

    @Override
    public void preRollBackSplit(ObserverContext<RegionCoprocessorEnvironment> ctx) throws IOException {
        super.preRollBackSplit(ctx);
        outInfo("TestObserver.preRollBackSplit() ");
    }

    @Override
    public void postRollBackSplit(ObserverContext<RegionCoprocessorEnvironment> ctx) throws IOException {
        super.postRollBackSplit(ctx);
        outInfo("TestObserver.postRollBackSplit() ");
    }

    @Override
    public void postCompleteSplit(ObserverContext<RegionCoprocessorEnvironment> ctx) throws IOException {
        super.postCompleteSplit(ctx);
        outInfo("TestObserver.postCompleteSplit() ");
    }

    @Override
    public void postSplit(ObserverContext<RegionCoprocessorEnvironment> e, Region l, Region r) throws IOException {
        super.postSplit(e, l, r);
        outInfo("TestObserver.postSplit() ");
    }

    @Override
    public void preCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<StoreFile> candidates) throws IOException {
        super.preCompactSelection(c, store, candidates);
        outInfo("TestObserver.preCompactSelection() ");
    }

    @Override
    public void preCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<StoreFile> candidates, CompactionRequest request) throws IOException {
        super.preCompactSelection(c, store, candidates, request);
        outInfo("TestObserver.preCompactSelection() ");
    }

    @Override
    public void postCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, ImmutableList<StoreFile> selected) {
        super.postCompactSelection(c, store, selected);
        outInfo("TestObserver.postCompactSelection() ");
    }

    @Override
    public void postCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, ImmutableList<StoreFile> selected, CompactionRequest request) {
        super.postCompactSelection(c, store, selected, request);
        outInfo("TestObserver.postCompactSelection() ");
    }

    @Override
    public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner, ScanType scanType) throws IOException {
        outInfo("TestObserver.preCompact() ");
        return super.preCompact(e, store, scanner, scanType);
    }

    @Override
    public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner, ScanType scanType, CompactionRequest request) throws IOException {
        outInfo("TestObserver.preCompact() ");
        return super.preCompact(e, store, scanner, scanType, request);
    }

    @Override
    public InternalScanner preCompactScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends KeyValueScanner> scanners, ScanType scanType, long earliestPutTs, InternalScanner s) throws IOException {
        outInfo("TestObserver.preCompactScannerOpen() ");
        return super.preCompactScannerOpen(c, store, scanners, scanType, earliestPutTs, s);
    }

    @Override
    public InternalScanner preCompactScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends KeyValueScanner> scanners, ScanType scanType, long earliestPutTs, InternalScanner s, CompactionRequest request) throws IOException {
        outInfo("TestObserver.preCompactScannerOpen() ");
        return super.preCompactScannerOpen(c, store, scanners, scanType, earliestPutTs, s, request);
    }

    @Override
    public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile) throws IOException {
        super.postCompact(e, store, resultFile);
        outInfo("TestObserver.postCompact() ");
    }

    @Override
    public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile, CompactionRequest request) throws IOException {
        super.postCompact(e, store, resultFile, request);
        outInfo("TestObserver.postCompact() ");
    }

    @Override
    public void preGetClosestRowBefore(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, Result result) throws IOException {
        super.preGetClosestRowBefore(e, row, family, result);
        outInfo("TestObserver.preGetClosestRowBefore() ");
    }

    @Override
    public void postGetClosestRowBefore(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, Result result) throws IOException {
        super.postGetClosestRowBefore(e, row, family, result);
        outInfo("TestObserver.postGetClosestRowBefore() ");
    }

    @Override
    public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
        outInfo("TestObserver.preGetOp() " );
    }

    @Override
    public void postGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
        outInfo("TestObserver.postGetOp() " );
    }

    @Override
    public boolean preExists(ObserverContext<RegionCoprocessorEnvironment> e, Get get, boolean exists) throws IOException {
        outInfo("TestObserver.preExists() ");
        return super.preExists(e, get, exists);
    }

    @Override
    public boolean postExists(ObserverContext<RegionCoprocessorEnvironment> e, Get get, boolean exists) throws IOException {
        outInfo("TestObserver.postExists() ");
        return super.postExists(e, get, exists);
    }

    @Override
    public void preDelete(ObserverContext<RegionCoprocessorEnvironment> e, Delete delete, WALEdit edit, Durability durability) throws IOException {
        outInfo("TestObserver.preDelete() " );
    }

    @Override
    public void prePrepareTimeStampForDeleteVersion(ObserverContext<RegionCoprocessorEnvironment> e, Mutation delete, Cell cell, byte[] byteNow, Get get) throws IOException {
        super.prePrepareTimeStampForDeleteVersion(e, delete, cell, byteNow, get);
        outInfo("TestObserver.prePrepareTimeStampForDeleteVersion() ");
    }

    @Override
    public void postDelete(ObserverContext<RegionCoprocessorEnvironment> e, Delete delete, WALEdit edit, Durability durability) throws IOException {
        outInfo("TestObserver.postDelete() " );
    }

    @Override
    public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
        super.preBatchMutate(c, miniBatchOp);
        outInfo("TestObserver.preBatchMutate() ");
    }

    @Override
    public void postBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
        super.postBatchMutate(c, miniBatchOp);
        outInfo("TestObserver.postBatchMutate() ");
    }

    @Override
    public void postBatchMutateIndispensably(ObserverContext<RegionCoprocessorEnvironment> ctx, MiniBatchOperationInProgress<Mutation> miniBatchOp, boolean success) throws IOException {
        super.postBatchMutateIndispensably(ctx, miniBatchOp, success);
        outInfo("TestObserver.postBatchMutateIndispensably() ");
    }

    @Override
    public boolean preCheckAndPut(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo("TestObserver.preCheckAndPut() ");
        return super.preCheckAndPut(e, row, family, qualifier, compareOp, comparator, put, result);
    }

    @Override
    public boolean preCheckAndPutAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo("TestObserver.preCheckAndPutAfterRowLock() ");
        return super.preCheckAndPutAfterRowLock(e, row, family, qualifier, compareOp, comparator, put, result);
    }

    @Override
    public boolean postCheckAndPut(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo("TestObserver.postCheckAndPut() ");
        return super.postCheckAndPut(e, row, family, qualifier, compareOp, comparator, put, result);
    }

    @Override
    public boolean preCheckAndDelete(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo("TestObserver.preCheckAndDelete() ");
        return super.preCheckAndDelete(e, row, family, qualifier, compareOp, comparator, delete, result);
    }

    @Override
    public boolean preCheckAndDeleteAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo("TestObserver.preCheckAndDeleteAfterRowLock() ");
        return super.preCheckAndDeleteAfterRowLock(e, row, family, qualifier, compareOp, comparator, delete, result);
    }

    @Override
    public boolean postCheckAndDelete(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo("TestObserver.postCheckAndDelete() ");
        return super.postCheckAndDelete(e, row, family, qualifier, compareOp, comparator, delete, result);
    }

    @Override
    public Result preAppend(ObserverContext<RegionCoprocessorEnvironment> e, Append append) throws IOException {
        outInfo("TestObserver.preAppend() ");
        return super.preAppend(e, append);
    }

    @Override
    public Result preAppendAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> e, Append append) throws IOException {
        outInfo("TestObserver.preAppendAfterRowLock() ");
        return super.preAppendAfterRowLock(e, append);
    }

    @Override
    public Result postAppend(ObserverContext<RegionCoprocessorEnvironment> e, Append append, Result result) throws IOException {
        outInfo("TestObserver.postAppend() ");
        return super.postAppend(e, append, result);
    }

    @Override
    public long preIncrementColumnValue(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, long amount, boolean writeToWAL) throws IOException {
        outInfo("TestObserver.preIncrementColumnValue() ");
        return super.preIncrementColumnValue(e, row, family, qualifier, amount, writeToWAL);
    }

    @Override
    public long postIncrementColumnValue(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, long amount, boolean writeToWAL, long result) throws IOException {
        outInfo("TestObserver.postIncrementColumnValue() ");
        return super.postIncrementColumnValue(e, row, family, qualifier, amount, writeToWAL, result);
    }

    @Override
    public Result preIncrement(ObserverContext<RegionCoprocessorEnvironment> e, Increment increment) throws IOException {
        outInfo("TestObserver.preIncrement() ");
        return super.preIncrement(e, increment);
    }

    @Override
    public Result preIncrementAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> e, Increment increment) throws IOException {
        outInfo("TestObserver.preIncrementAfterRowLock() ");
        return super.preIncrementAfterRowLock(e, increment);
    }

    @Override
    public Result postIncrement(ObserverContext<RegionCoprocessorEnvironment> e, Increment increment, Result result) throws IOException {
        outInfo("TestObserver.postIncrement() ");
        return super.postIncrement(e, increment, result);
    }

    @Override
    public RegionScanner preScannerOpen(ObserverContext<RegionCoprocessorEnvironment> e, Scan scan, RegionScanner s) throws IOException {
        Filter filter=scan.getFilter();
        String table=e.getEnvironment().getRegion().getTableDesc().getTableName().getNameAsString();
        outInfo("TestObserver.preScannerOpen()");
        outInfo("preScannerOpen-filter: "+filter);
        outInfo("preScannerOpen-table: "+table);
        String family="";
        byte[][] f=scan.getFamilies();
        for(int i=0;i<f.length;i++){
            family += Bytes.toString(f[i])+",";
        }
        outInfo("preScannerOpen-family: "+family);
        if(filter instanceof KeyOnlyFilter){
            Exception exception= new RuntimeException("查看当前的调用！");
            exception.printStackTrace();
        }
        return super.preScannerOpen(e, scan, s);
    }

    @Override
    public RegionScanner postScannerOpen(ObserverContext<RegionCoprocessorEnvironment> e, Scan scan, RegionScanner s) throws IOException {
        outInfo("TestObserver.postScannerOpen() ");
        return super.postScannerOpen(e, scan, s);
    }

    @Override
    public boolean preScannerNext(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s, List<Result> results, int limit, boolean hasMore) throws IOException {
        outInfo("TestObserver.preScannerNext() ");
        return super.preScannerNext(e, s, results, limit, hasMore);
    }

    @Override
    public boolean postScannerNext(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s, List<Result> results, int limit, boolean hasMore) throws IOException {
        outInfo("TestObserver.postScannerNext() ");
        return super.postScannerNext(e, s, results, limit, hasMore);
    }

    @Override
    public boolean postScannerFilterRow(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s, byte[] currentRow, int offset, short length, boolean hasMore) throws IOException {
        outInfo("TestObserver.postScannerFilterRow() ");
        return super.postScannerFilterRow(e, s, currentRow, offset, length, hasMore);
    }

    @Override
    public void preScannerClose(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s) throws IOException {
        super.preScannerClose(e, s);
        outInfo("TestObserver.preScannerClose() ");
    }

    @Override
    public void postScannerClose(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s) throws IOException {
        super.postScannerClose(e, s);
        outInfo("TestObserver.postScannerClose() ");
    }

    @Override
    public void preWALRestore(ObserverContext<? extends RegionCoprocessorEnvironment> env, HRegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
        super.preWALRestore(env, info, logKey, logEdit);
        outInfo("TestObserver.preWALRestore() ");
    }

    @Override
    public void preWALRestore(ObserverContext<RegionCoprocessorEnvironment> env, HRegionInfo info, HLogKey logKey, WALEdit logEdit) throws IOException {
        super.preWALRestore(env, info, logKey, logEdit);
        outInfo("TestObserver.preWALRestore() ");
    }

    @Override
    public void postWALRestore(ObserverContext<? extends RegionCoprocessorEnvironment> env, HRegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
        super.postWALRestore(env, info, logKey, logEdit);
        outInfo("TestObserver.postWALRestore() ");
    }

    @Override
    public void postWALRestore(ObserverContext<RegionCoprocessorEnvironment> env, HRegionInfo info, HLogKey logKey, WALEdit logEdit) throws IOException {
        super.postWALRestore(env, info, logKey, logEdit);
        outInfo("TestObserver.postWALRestore() ");
    }

    @Override
    public void preBulkLoadHFile(ObserverContext<RegionCoprocessorEnvironment> ctx, List<Pair<byte[], String>> familyPaths) throws IOException {
        super.preBulkLoadHFile(ctx, familyPaths);
        outInfo("TestObserver.preBulkLoadHFile() ");
    }

    @Override
    public boolean postBulkLoadHFile(ObserverContext<RegionCoprocessorEnvironment> ctx, List<Pair<byte[], String>> familyPaths, boolean hasLoaded) throws IOException {
        outInfo("TestObserver.postBulkLoadHFile() ");
        return super.postBulkLoadHFile(ctx, familyPaths, hasLoaded);
    }

    @Override
    public StoreFile.Reader preStoreFileReaderOpen(ObserverContext<RegionCoprocessorEnvironment> ctx, FileSystem fs, Path p, FSDataInputStreamWrapper in, long size, CacheConfig cacheConf, Reference r, StoreFile.Reader reader) throws IOException {
        outInfo("TestObserver.preStoreFileReaderOpen() ");
        return super.preStoreFileReaderOpen(ctx, fs, p, in, size, cacheConf, r, reader);
    }

    @Override
    public StoreFile.Reader postStoreFileReaderOpen(ObserverContext<RegionCoprocessorEnvironment> ctx, FileSystem fs, Path p, FSDataInputStreamWrapper in, long size, CacheConfig cacheConf, Reference r, StoreFile.Reader reader) throws IOException {
        outInfo("TestObserver.postStoreFileReaderOpen() ");
        return super.postStoreFileReaderOpen(ctx, fs, p, in, size, cacheConf, r, reader);
    }

    @Override
    public Cell postMutationBeforeWAL(ObserverContext<RegionCoprocessorEnvironment> ctx, MutationType opType, Mutation mutation, Cell oldCell, Cell newCell) throws IOException {
        outInfo("TestObserver.postMutationBeforeWAL() ");
        return super.postMutationBeforeWAL(ctx, opType, mutation, oldCell, newCell);
    }

    @Override
    public void postStartRegionOperation(ObserverContext<RegionCoprocessorEnvironment> ctx, Region.Operation op) throws IOException {
        super.postStartRegionOperation(ctx, op);
        outInfo("TestObserver.postStartRegionOperation() ");
    }

    @Override
    public void postCloseRegionOperation(ObserverContext<RegionCoprocessorEnvironment> ctx, Region.Operation op) throws IOException {
        super.postCloseRegionOperation(ctx, op);
        outInfo("TestObserver.postCloseRegionOperation() ");
    }

    @Override
    public DeleteTracker postInstantiateDeleteTracker(ObserverContext<RegionCoprocessorEnvironment> ctx, DeleteTracker delTracker) throws IOException {
        outInfo("TestObserver.postInstantiateDeleteTracker() ");
        return super.postInstantiateDeleteTracker(ctx, delTracker);
    }
//    @Override
//    public boolean preSetSplitOrMergeEnabled(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, Admin.MasterSwitchType masterSwitchType) throws IOException {
//        outInfo("TestObserver.preSetSplitOrMergeEnabled() ");
//        return super.preSetSplitOrMergeEnabled(observerContext, b, masterSwitchType);
//    }
//
//    @Override
//    public void postSetSplitOrMergeEnabled(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, Admin.MasterSwitchType masterSwitchType) throws IOException {
//        outInfo("TestObserver.postSetSplitOrMergeEnabled() ");
//    }

    @Override
    public void prePrepareBulkLoad(ObserverContext<RegionCoprocessorEnvironment> observerContext, SecureBulkLoadProtos.PrepareBulkLoadRequest prepareBulkLoadRequest) throws IOException {
        outInfo("TestObserver.prePrepareBulkLoad() ");
    }

    @Override
    public void preCleanupBulkLoad(ObserverContext<RegionCoprocessorEnvironment> observerContext, SecureBulkLoadProtos.CleanupBulkLoadRequest cleanupBulkLoadRequest) throws IOException {
        outInfo("TestObserver.preCleanupBulkLoad() ");
    }

    @Override
    public void preStopRegionServer(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("TestObserver.preStopRegionServer() ");
    }

    @Override
    public void preMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {
        outInfo("TestObserver.preMerge() ");
    }

    @Override
    public void postMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, Region region2) throws IOException {
        outInfo("TestObserver.postMerge() ");
    }

    @Override
    public void preMergeCommit(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, List<Mutation> list) throws IOException {
        outInfo("TestObserver.preMergeCommit() ");
    }

    @Override
    public void postMergeCommit(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, Region region2) throws IOException {
        outInfo("TestObserver.postMergeCommit() ");
    }

    @Override
    public void preRollBackMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {
        outInfo("TestObserver.preRollBackMerge() ");
    }

    @Override
    public void postRollBackMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {
        outInfo("TestObserver.postRollBackMerge() ");
    }

    @Override
    public void preRollWALWriterRequest(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("TestObserver.preRollWALWriterRequest() ");
    }

    @Override
    public void postRollWALWriterRequest(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("TestObserver.postRollWALWriterRequest() ");
    }

    @Override
    public ReplicationEndpoint postCreateReplicationEndPoint(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, ReplicationEndpoint replicationEndpoint) {
        outInfo("TestObserver.postCreateReplicationEndPoint() ");
        return super.postCreateReplicationEndPoint(observerContext, replicationEndpoint);
    }

    @Override
    public void preReplicateLogEntries(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, List<AdminProtos.WALEntry> list, CellScanner cellScanner) throws IOException {
        outInfo("TestObserver.preReplicateLogEntries() ");
    }

    @Override
    public void postReplicateLogEntries(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, List<AdminProtos.WALEntry> list, CellScanner cellScanner) throws IOException {
        outInfo("TestObserver.postReplicateLogEntries() ");
    }

//    @Override
//    public KeyValueScanner preStoreScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, Scan scan, NavigableSet<byte[]> targetCols, KeyValueScanner s) throws IOException {
//        outInfo("TestObserver.preStoreScannerOpen() ");
//        return super.preStoreScannerOpen(c, store, scan, targetCols, s);
//    }

    @Override
    public boolean preWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, WALKey walKey, WALEdit walEdit) throws IOException {
        outInfo("TestObserver.preWALWrite(observerContext, hRegionInfo, walKey, walEdit) ");
        return super.preWALWrite(observerContext, hRegionInfo, walKey, walEdit);
    }

    @Override
    public boolean preWALWrite(ObserverContext<WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HLogKey hLogKey, WALEdit walEdit) throws IOException {
        outInfo("TestObserver.preWALWrite(observerContext, hRegionInfo, hLogKey, walEdit) ");
        return super.preWALWrite(observerContext, hRegionInfo, hLogKey, walEdit);
    }

    @Override
    public void postWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, WALKey walKey, WALEdit walEdit) throws IOException {
        outInfo("TestObserver.postWALWrite(observerContext, hRegionInfo, walKey, walEdit)");
        super.postWALWrite(observerContext, hRegionInfo, walKey, walEdit);
    }

    @Override
    public void postWALWrite(ObserverContext<WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HLogKey hLogKey, WALEdit walEdit) throws IOException {
        super.postWALWrite(observerContext, hRegionInfo, hLogKey, walEdit);
        outInfo("TestObserver.postWALWrite(observerContext, hRegionInfo, hLogKey, walEdit)");
    }

    @Override
    public void preWALRoll(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, Path path, Path path1) throws IOException {
        super.preWALRoll(observerContext, path, path1);
        outInfo("TestObserver.preWALRoll(observerContext, path, path1)");
    }

    @Override
    public void postWALRoll(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, Path path, Path path1) throws IOException {
        super.postWALRoll(observerContext, path, path1);
        outInfo("TestObserver.postWALRoll(observerContext, path, path1)");
    }
}
