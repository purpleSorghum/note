package top.zigaoliang;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.ProcedureInfo;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.coprocessor.*;
import org.apache.hadoop.hbase.master.RegionPlan;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.protobuf.generated.AdminProtos;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.protobuf.generated.QuotaProtos;
import org.apache.hadoop.hbase.protobuf.generated.SecureBulkLoadProtos;
import org.apache.hadoop.hbase.regionserver.Region;
import org.apache.hadoop.hbase.regionserver.wal.HLogKey;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.replication.ReplicationEndpoint;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALKey;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BaseObserver extends BaseRegionObserver implements MasterObserver,RegionServerObserver, BulkLoadObserver,WALObserver {
    private static Logger log=Logger.getLogger(BaseObserver.class);
    public void outInfo(String str){
        System.out.println(str);
        try {
            FileWriter fw = new FileWriter("/home/coprocessor.txt",true);
            fw.write(str + "\r\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void preCreateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        String tn = Bytes.toString(hTableDescriptor.getTableName().getName());
        outInfo("BaseObserver.postCreateTable() : tableName = " + tn);
    }

    public void postCreateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        String tn = Bytes.toString(hTableDescriptor.getTableName().getName());
        outInfo("BaseObserver.preCreateTable() : tableName = " + tn);
    }

    public void preCreateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        String tn = Bytes.toString(hTableDescriptor.getTableName().getName());
        outInfo("BaseObserver.preCreateTableHandler() : tableName = " + tn);
    }

    public void postCreateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        String tn = Bytes.toString(hTableDescriptor.getTableName().getName());
        outInfo("BaseObserver.postCreateTableHandler() : tableName = " + tn);
    }

    public void preDeleteTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preDeleteTable() : tableName = " + tn);
    }

    public void postDeleteTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postDeleteTable() : tableName = " + tn);
    }

    public void preDeleteTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preDeleteTableHandler() : tableName = " + tn);
    }

    public void postDeleteTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postDeleteTableHandler() : tableName = " + tn);
    }

    public void preTruncateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preTruncateTable() : tableName = " + tn);
    }

    public void postTruncateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preTruncateTable() : tableName = " + tn);
    }

    public void preTruncateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preTruncateTableHandler() : tableName = " + tn);
    }

    public void postTruncateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postTruncateTableHandler() : tableName = " + tn);
    }

    public void preModifyTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preModifyTable() : tableName = " + tn);
    }

    public void postModifyTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postModifyTable() : tableName = " + tn);
    }

    public void preModifyTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preModifyTableHandler() : tableName = " + tn);
    }

    public void postModifyTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postModifyTableHandler() : tableName = " + tn);
    }

    public void preAddColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preAddColumn() : tableName = " + tn);
    }

    public void postAddColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postAddColumn() : tableName = " + tn);
    }

    public void preAddColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preAddColumnHandler() : tableName = " + tn);
    }

    public void postAddColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postAddColumnHandler() : tableName = " + tn);
    }

    public void preModifyColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preModifyColumn() : tableName = " + tn);
    }

    public void postModifyColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postModifyColumn() : tableName = " + tn);
    }

    public void preModifyColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preModifyColumnHandler() : tableName = " + tn);
    }

    public void postModifyColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postModifyColumnHandler() : tableName = " + tn);
    }

    public void preDeleteColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preDeleteColumn() : tableName = " + tn);
    }

    public void postDeleteColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postDeleteColumn() : tableName = " + tn);
    }

    public void preDeleteColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preDeleteColumnHandler() : tableName = " + tn);
    }

    public void postDeleteColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postDeleteColumnHandler() : tableName = " + tn);
    }

    public void preEnableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preEnableTable() : tableName = " + tn);
    }

    public void postEnableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postEnableTable() : tableName = " + tn);
    }

    public void preEnableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preEnableTableHandler() : tableName = " + tn);
    }

    public void postEnableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postEnableTableHandler() : tableName = " + tn);
    }

    public void preDisableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preDisableTable() : tableName = " + tn);
    }

    public void postDisableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postDisableTable() : tableName = " + tn);
    }

    public void preDisableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.preDisableTableHandler() : tableName = " + tn);
    }

    public void postDisableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        String tn = Bytes.toString(tableName.getName());
        outInfo("BaseObserver.postDisableTableHandler() : tableName = " + tn);
    }

    public void preMove(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, ServerName serverName, ServerName serverName1) throws IOException {
        outInfo("BaseObserver.postDisableTableHandler() : tableName = " + serverName.getServerName());
    }

    public void postMove(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, ServerName serverName, ServerName serverName1) throws IOException {
        outInfo("BaseObserver.postDisableTableHandler() : tableName = " + serverName.getServerName());
    }

    public void preAbortProcedure(ObserverContext<MasterCoprocessorEnvironment> observerContext, ProcedureExecutor<MasterProcedureEnv> procedureExecutor, long l) throws IOException {
        outInfo("BaseObserver.preAbortProcedure()  " );
    }

    public void postAbortProcedure(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("BaseObserver.postAbortProcedure() " );
    }

    public void preListProcedures(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("BaseObserver.preListProcedures() " );
    }

    public void postListProcedures(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<ProcedureInfo> list) throws IOException {
        outInfo("BaseObserver.postListProcedures() " );
    }

    public void preAssign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        String rn = Bytes.toString(hRegionInfo.getRegionName());
        outInfo("BaseObserver.preAssign() : regionName = " + rn);
    }

    public void postAssign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        String rn = Bytes.toString(hRegionInfo.getRegionName());
        outInfo("BaseObserver.postAssign() : regionName = " + rn);
    }

    public void preUnassign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, boolean b) throws IOException {
        String rn = Bytes.toString(hRegionInfo.getRegionName());
        outInfo("BaseObserver.preUnassign() : regionName = " + rn);
    }

    public void postUnassign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, boolean b) throws IOException {
        String rn = Bytes.toString(hRegionInfo.getRegionName());
        outInfo("BaseObserver.postUnassign() : regionName = " + rn);
    }

    public void preRegionOffline(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        String rn = Bytes.toString(hRegionInfo.getRegionName());
        outInfo("BaseObserver.preRegionOffline() : regionName = " + rn);
    }

    public void postRegionOffline(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        String rn = Bytes.toString(hRegionInfo.getRegionName());
        outInfo("BaseObserver.postRegionOffline() : regionName = " + rn);
    }

    public void preBalance(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("BaseObserver.postRegionOffline() " );
    }

    public void postBalance(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<RegionPlan> list) throws IOException {
        outInfo("BaseObserver.postRegionOffline() " );
    }

    public boolean preSetSplitOrMergeEnabled(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, Admin.MasterSwitchType masterSwitchType) throws IOException {
        outInfo("BaseObserver.preSetSplitOrMergeEnabled() " );
        return false;
    }

    public void postSetSplitOrMergeEnabled(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, Admin.MasterSwitchType masterSwitchType) throws IOException {
        outInfo("BaseObserver.postSetSplitOrMergeEnabled() " );
    }

    public boolean preBalanceSwitch(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b) throws IOException {
        outInfo("BaseObserver.preBalanceSwitch() " );
        return false;
    }

    public void postBalanceSwitch(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, boolean b1) throws IOException {
        outInfo("BaseObserver.postBalanceSwitch() " );
    }

    public void preShutdown(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("BaseObserver.preShutdown() " );
    }

    public void preStopMaster(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("BaseObserver.preStopMaster() " );
    }

    public void postStartMaster(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("BaseObserver.postStartMaster() " );
    }

    public void preMasterInitialization(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("BaseObserver.preMasterInitialization() " );
    }

    public void preSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        outInfo("BaseObserver.preSnapshot() " );
    }

    public void postSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        outInfo("BaseObserver.postSnapshot() " );
    }

    public void preListSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        outInfo("BaseObserver.preListSnapshot() " );
    }

    public void postListSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        outInfo("BaseObserver.postListSnapshot() " );
    }

    public void preCloneSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        outInfo("BaseObserver.preCloneSnapshot() " );
    }

    public void postCloneSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        outInfo("BaseObserver.postCloneSnapshot() " );
    }

    public void preRestoreSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        outInfo("BaseObserver.preRestoreSnapshot() " );
    }

    public void postRestoreSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        outInfo("BaseObserver.postRestoreSnapshot() " );
    }

    public void preDeleteSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        outInfo("BaseObserver.preDeleteSnapshot() " );
    }

    public void postDeleteSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        outInfo("BaseObserver.postDeleteSnapshot() " );
    }

    public void preGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1) throws IOException {
        outInfo("BaseObserver.preGetTableDescriptors() " );
    }

    public void postGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list) throws IOException {
        outInfo("BaseObserver.postGetTableDescriptors() " );
    }

    public void preGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1, String s) throws IOException {
        outInfo("BaseObserver.preGetTableDescriptors() " );
    }

    public void postGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1, String s) throws IOException {
        outInfo("BaseObserver.postGetTableDescriptors() " );
    }

    public void preGetTableNames(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list, String s) throws IOException {
        outInfo("BaseObserver.preGetTableNames() " );
    }

    public void postGetTableNames(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list, String s) throws IOException {
        outInfo("BaseObserver.postGetTableNames() " );
    }

    public void preCreateNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        outInfo("BaseObserver.preCreateNamespace() " );
    }

    public void postCreateNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        outInfo("BaseObserver.postCreateNamespace() " );
    }

    public void preDeleteNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        outInfo("BaseObserver.preDeleteNamespace() " );
    }

    public void postDeleteNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        outInfo("BaseObserver.postDeleteNamespace() " );
    }

    public void preModifyNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        outInfo("BaseObserver.preModifyNamespace() " );
    }

    public void postModifyNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        outInfo("BaseObserver.postModifyNamespace() " );
    }

    public void preGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        outInfo("BaseObserver.preGetNamespaceDescriptor() " );
    }

    public void postGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        outInfo("BaseObserver.postGetNamespaceDescriptor() " );
    }

    public void preListNamespaceDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<NamespaceDescriptor> list) throws IOException {
        outInfo("BaseObserver.preListNamespaceDescriptors() " );
    }

    public void postListNamespaceDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<NamespaceDescriptor> list) throws IOException {
        outInfo("BaseObserver.postListNamespaceDescriptors() " );
    }

    public void preTableFlush(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        outInfo("BaseObserver.preTableFlush() " );
    }

    public void postTableFlush(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        outInfo("BaseObserver.postTableFlush() " );
    }

    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.preSetUserQuota() " );
    }

    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.postSetUserQuota() " );
    }

    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.preSetUserQuota() " );
    }

    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.postSetUserQuota() " );
    }

    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, String s1, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.preSetUserQuota() " );
    }

    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, String s1, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.postSetUserQuota() " );
    }

    public void preSetTableQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.preSetTableQuota() " );
    }

    public void postSetTableQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.postSetTableQuota() " );
    }

    public void preSetNamespaceQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.preSetNamespaceQuota() " );
    }

    public void postSetNamespaceQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        outInfo("BaseObserver.postSetNamespaceQuota() " );
    }

    public void preDispatchMerge(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HRegionInfo hRegionInfo1) throws IOException {
        outInfo("BaseObserver.preDispatchMerge() " );
    }

    public void postDispatchMerge(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HRegionInfo hRegionInfo1) throws IOException {
        outInfo("BaseObserver.postDispatchMerge() " );
    }

    public void prePrepareBulkLoad(ObserverContext<RegionCoprocessorEnvironment> observerContext, SecureBulkLoadProtos.PrepareBulkLoadRequest prepareBulkLoadRequest) throws IOException {

    }

    public void preCleanupBulkLoad(ObserverContext<RegionCoprocessorEnvironment> observerContext, SecureBulkLoadProtos.CleanupBulkLoadRequest cleanupBulkLoadRequest) throws IOException {

    }

    public void preStopRegionServer(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {

    }

    public void preMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {

    }

    public void postMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, Region region2) throws IOException {

    }

    public void preMergeCommit(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, List<Mutation> list) throws IOException {

    }

    public void postMergeCommit(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, Region region2) throws IOException {

    }

    public void preRollBackMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {

    }

    public void postRollBackMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {

    }

    public void preRollWALWriterRequest(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {

    }

    public void postRollWALWriterRequest(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {

    }

    public ReplicationEndpoint postCreateReplicationEndPoint(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, ReplicationEndpoint replicationEndpoint) {
        return null;
    }

    public void preReplicateLogEntries(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, List<AdminProtos.WALEntry> list, CellScanner cellScanner) throws IOException {

    }

    public void postReplicateLogEntries(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, List<AdminProtos.WALEntry> list, CellScanner cellScanner) throws IOException {

    }

    public boolean preWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, WALKey walKey, WALEdit walEdit) throws IOException {
        return false;
    }

    public boolean preWALWrite(ObserverContext<WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HLogKey hLogKey, WALEdit walEdit) throws IOException {
        return false;
    }

    public void postWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, WALKey walKey, WALEdit walEdit) throws IOException {

    }

    public void postWALWrite(ObserverContext<WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HLogKey hLogKey, WALEdit walEdit) throws IOException {

    }

    public void preWALRoll(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, Path path, Path path1) throws IOException {

    }

    public void postWALRoll(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, Path path, Path path1) throws IOException {

    }
}
