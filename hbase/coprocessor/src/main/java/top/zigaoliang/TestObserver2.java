package top.zigaoliang;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.coprocessor.*;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.filter.CompareFilter;
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
import java.util.ArrayList;
import java.util.List;

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author hanlin
 * @Date 2019/3/26 11:20
 * @Description 测试commit
 * @param 
 * @return
 **/
public class TestObserver2 extends BaseObserver {
    private static Logger log=Logger.getLogger(TestObserver.class);

    private String replace(String str){
        String result=str.substring(0,3);
        result+="****";
        result+=str.substring(7);
        return result;
    }

    private Cell replaceCell(Cell cell){
        if(cell == null) return null;
        String column=Bytes.toString(CellUtil.cloneQualifier(cell));
        if(column.equals("phone")){
            String val=Bytes.toString(CellUtil.cloneValue(cell));
            val=replace(val);
            Cell nc=new KeyValue(CellUtil.cloneRow(cell), CellUtil.cloneFamily(cell),CellUtil.cloneQualifier(cell), Bytes.toBytes(val));
            return nc;
        }else{
            return cell;
        }
    }
    @Override
    public void postGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
        outInfo("TestObserver2.postGetOp() " );
        //脱敏的情况
        List<Cell> list=new ArrayList<Cell>();
        for(Cell cell:results){
            Cell cell_new=replaceCell(cell);
            list.add(cell_new);
        }
        results.clear();
        results.addAll(list);
    }

    @Override
    public boolean postScannerNext(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s, List<Result> results, int limit, boolean hasMore) throws IOException {
        outInfo("TestObserver2.postScannerNext() ");
        for(Result result:results){
            Cell[] cells=result.rawCells();
            for(int i=0;i<cells.length;i++){
                cells[i]=replaceCell(cells[i]); //脱敏
            }
        }
        return hasMore;
    }


    @Override
    public void prePut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        outInfo("TestObserver2.prePut()");
        //测试bypass 测试是否会走后续的处理器 得到的结果是会走
//        e.bypass();
        Table table=e.getEnvironment().getTable(TableName.valueOf("t99"));
        if(table == null){
            outInfo("t99这张表不存在！");
        }else{
            outInfo("获取到了t99这张表！");
            if(MetaTableAccessor.tableExists(
                    ConnectionFactory.createConnection(e.getEnvironment().getConfiguration()),
                    table.getName())){
                outInfo("t99这张表再次判定是存在的！");
            }else{
                outInfo("t99这张表再次判定不存在！");
            }
            outInfo("t99这张表名称是："+table.getName().getNameAsString());
        }
        ConcurrentMap<String,Object> map=e.getEnvironment().getSharedData();
        System.out.println("获取到的ShareData是："+map);
        outInfo("获取到的ShareData是："+map);
        String row=Bytes.toString(put.getRow());
        if("create".equals(row)){
            outInfo("准备创建表t100！");
            Connection conn=ConnectionFactory.createConnection(e.getEnvironment().getConfiguration());
            HBaseAdmin admin=new HBaseAdmin(conn);
            HTableDescriptor htd=new HTableDescriptor(TableName.valueOf("t100"));
            HColumnDescriptor family=new HColumnDescriptor("f1");
            htd.addFamily(family);
            admin.createTable(htd);
            outInfo("t100这张表创建完成！");
        }
    }

    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        outInfo("TestObserver2.postPut()");
        //测试bypass 测试是否会走后续的处理器 得到的结果是会走
//        e.bypass();
        Configuration conf=e.getEnvironment().getConfiguration();
        System.out.println("配置文件configuration的大小："+conf.size());
        String str=conf.get("hbase.coprocessor.master.classes");
        outInfo("修改前从配置文件中读取到的项hbase.coprocessor.master.classes的值："+str);
        conf.set("hbase.coprocessor.master.classes","com.zaws.hbasedemo.coprocessor.TestObserver");
        conf.set("hbase.coprocessor.region.classes","com.zaws.hbasedemo.coprocessor.TestObserver");
        str=conf.get("hbase.coprocessor.master.classes");
        System.out.println("修改后从配置文件中读取到的项hbase.coprocessor.master.classes的值："+str);
        outInfo("从配置文件中读取到的项hbase.coprocessor.master.classes的值："+str);
        super.postPut(e, put, edit, durability);
    }

    @Override
    public void preCreateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        outInfo("TestObserver2.preCreateTable(不走后面的协处理器，建表应该要成功) " );
        //应该不走后面的协处理器，但本次操作要完成(已验证，确实如此)
//        observerContext.complete();
        String tn=hTableDescriptor.getNameAsString()+"_test";
        hTableDescriptor=new HTableDescriptor(TableName.valueOf(tn),hTableDescriptor);
        hTableDescriptor.addFamily(new HColumnDescriptor("family"));
        hTableDescriptor.removeFamily(Bytes.toBytes("f5"));
        super.preCreateTable(observerContext, hTableDescriptor, hRegionInfos);
    }

    @Override
    public void postCreateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        log.info("TestObserver2.postCreateTable()  " );
        outInfo("TestObserver2.postCreateTable()  ");
        super.postCreateTable(observerContext, hTableDescriptor, hRegionInfos);
    }

    @Override
    public void preCreateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        log.info("TestObserver2.preCreateTableHandler() " );
        outInfo("TestObserver2.preCreateTableHandler()  ");
        super.preCreateTableHandler(observerContext, hTableDescriptor, hRegionInfos);
    }

    @Override
    public void postCreateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, HTableDescriptor hTableDescriptor, HRegionInfo[] hRegionInfos) throws IOException {
        log.info("TestObserver2.postCreateTableHandler() ");
        outInfo("TestObserver2.postCreateTableHandler()");
        super.postCreateTableHandler(observerContext, hTableDescriptor, hRegionInfos);
    }
    @Override
    public void preDeleteTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        outInfo("TestObserver2.preDeleteTable() ");
        super.preDeleteTable(observerContext, tableName);
    }
    @Override
    public void postDeleteTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postDeleteTable(observerContext, tableName);
        outInfo("TestObserver2.postDeleteTable() ");
    }

    @Override
    public void preDeleteTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preDeleteTableHandler(observerContext, tableName);
        outInfo("TestObserver2.preDeleteTableHandler() ");
    }

    @Override
    public void postDeleteTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postDeleteTableHandler(observerContext, tableName);
        outInfo("TestObserver2.postDeleteTableHandler() ");
    }

    @Override
    public void preTruncateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preTruncateTable(observerContext, tableName);
        outInfo("TestObserver2.preTruncateTable() ");
    }

    @Override
    public void postTruncateTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postTruncateTable(observerContext, tableName);
        outInfo("TestObserver2.postTruncateTable() ");
    }

    @Override
    public void preTruncateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preTruncateTableHandler(observerContext, tableName);
        outInfo("TestObserver2.preTruncateTableHandler() ");
    }

    @Override
    public void postTruncateTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postTruncateTableHandler(observerContext, tableName);
        outInfo("TestObserver2.postTruncateTableHandler() ");
    }

    @Override
    public void preModifyTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        super.preModifyTable(observerContext, tableName, hTableDescriptor);
        outInfo("TestObserver2.preModifyTable() ");
    }

    @Override
    public void postModifyTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        super.postModifyTable(observerContext, tableName, hTableDescriptor);
        outInfo("TestObserver2.postModifyTable() ");
    }

    @Override
    public void preModifyTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        super.preModifyTableHandler(observerContext, tableName, hTableDescriptor);
        outInfo("TestObserver2.preModifyTableHandler() ");
    }

    @Override
    public void postModifyTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HTableDescriptor hTableDescriptor) throws IOException {
        super.postModifyTableHandler(observerContext, tableName, hTableDescriptor);
        outInfo("TestObserver2.postModifyTableHandler() ");
    }

    @Override
    public void preAddColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.preAddColumn(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver2.preAddColumn() ");
    }

    @Override
    public void postAddColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.postAddColumn(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver2.postAddColumn() ");
    }

    @Override
    public void preAddColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.preAddColumnHandler(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver2.preAddColumnHandler() ");
    }

    @Override
    public void postAddColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.postAddColumnHandler(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver2.postAddColumnHandler() ");
    }

    @Override
    public void preModifyColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.preModifyColumn(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver2.preModifyColumn() ");
    }

    @Override
    public void postModifyColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.postModifyColumn(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver2.postModifyColumn() ");
    }

    @Override
    public void preModifyColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.preModifyColumnHandler(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver2.preModifyColumnHandler() ");
    }

    @Override
    public void postModifyColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, HColumnDescriptor hColumnDescriptor) throws IOException {
        super.postModifyColumnHandler(observerContext, tableName, hColumnDescriptor);
        outInfo("TestObserver2.postModifyColumnHandler() ");
    }

    @Override
    public void preDeleteColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        super.preDeleteColumn(observerContext, tableName, bytes);
        outInfo("TestObserver2.preDeleteColumn() ");
    }

    @Override
    public void postDeleteColumn(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        super.postDeleteColumn(observerContext, tableName, bytes);
        outInfo("TestObserver2.postDeleteColumn() ");
    }

    @Override
    public void preDeleteColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        super.preDeleteColumnHandler(observerContext, tableName, bytes);
        outInfo("TestObserver2.preDeleteColumnHandler() ");
    }

    @Override
    public void postDeleteColumnHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, byte[] bytes) throws IOException {
        super.postDeleteColumnHandler(observerContext, tableName, bytes);
        outInfo("TestObserver2.postDeleteColumnHandler() ");
    }

    @Override
    public void preEnableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preEnableTable(observerContext, tableName);
        outInfo("TestObserver2.preEnableTable() ");
    }

    @Override
    public void postEnableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postEnableTable(observerContext, tableName);
        outInfo("TestObserver2.postEnableTable() ");
    }

    @Override
    public void preEnableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preEnableTableHandler(observerContext, tableName);
        outInfo("TestObserver2.preEnableTableHandler() ");
    }

    @Override
    public void postEnableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postEnableTableHandler(observerContext, tableName);
        outInfo("TestObserver2.postEnableTableHandler() ");
    }

    @Override
    public void preDisableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preDisableTable(observerContext, tableName);
        outInfo("TestObserver2.preDisableTable() ");
    }

    @Override
    public void postDisableTable(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postDisableTable(observerContext, tableName);
        outInfo("TestObserver2.postDisableTable() ");
    }

    @Override
    public void preDisableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preDisableTableHandler(observerContext, tableName);
        outInfo("TestObserver2.preDisableTableHandler() ");
    }

    @Override
    public void postDisableTableHandler(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postDisableTableHandler(observerContext, tableName);
        outInfo("TestObserver2.postDisableTableHandler() ");
    }

    @Override
    public void preMove(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, ServerName serverName, ServerName serverName1) throws IOException {
        super.preMove(observerContext, hRegionInfo, serverName, serverName1);
        outInfo("TestObserver2.preMove() ");
    }

    @Override
    public void postMove(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, ServerName serverName, ServerName serverName1) throws IOException {
        super.postMove(observerContext, hRegionInfo, serverName, serverName1);
        outInfo("TestObserver2.postMove() ");
    }

    @Override
    public void preAbortProcedure(ObserverContext<MasterCoprocessorEnvironment> observerContext, ProcedureExecutor<MasterProcedureEnv> procedureExecutor, long l) throws IOException {
        super.preAbortProcedure(observerContext, procedureExecutor, l);
        outInfo("TestObserver2.preAbortProcedure() ");
    }

    @Override
    public void postAbortProcedure(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.postAbortProcedure(observerContext);
        outInfo("TestObserver2.postAbortProcedure() ");
    }

    @Override
    public void preListProcedures(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preListProcedures(observerContext);
        outInfo("TestObserver2.preListProcedures() ");
    }

    @Override
    public void postListProcedures(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<ProcedureInfo> list) throws IOException {
        super.postListProcedures(observerContext, list);
        outInfo("TestObserver2.postListProcedures() ");
    }

    @Override
    public void preAssign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        super.preAssign(observerContext, hRegionInfo);
        outInfo("TestObserver2.preAssign() ");
    }

    @Override
    public void postAssign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        super.postAssign(observerContext, hRegionInfo);
        outInfo("TestObserver2.postAssign() ");
    }

    @Override
    public void preUnassign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, boolean b) throws IOException {
        super.preUnassign(observerContext, hRegionInfo, b);
        outInfo("TestObserver2.preUnassign() ");
    }

    @Override
    public void postUnassign(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, boolean b) throws IOException {
        super.postUnassign(observerContext, hRegionInfo, b);
        outInfo("TestObserver2.postUnassign() ");
    }

    @Override
    public void preRegionOffline(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        super.preRegionOffline(observerContext, hRegionInfo);
        outInfo("TestObserver2.preRegionOffline() ");
    }

    @Override
    public void postRegionOffline(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo) throws IOException {
        super.postRegionOffline(observerContext, hRegionInfo);
        outInfo("TestObserver2.postRegionOffline() ");
    }

    @Override
    public void preBalance(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preBalance(observerContext);
        outInfo("TestObserver2.preBalance() ");
    }

    @Override
    public void postBalance(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<RegionPlan> list) throws IOException {
        super.postBalance(observerContext, list);
        outInfo("TestObserver2.postBalance() ");
    }

/*    @Override
    public boolean preSetSplitOrMergeEnabled(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, Admin.MasterSwitchType masterSwitchType) throws IOException {
        outInfo("TestObserver2.preSetSplitOrMergeEnabled() ");
        return super.preSetSplitOrMergeEnabled(observerContext, b, masterSwitchType);
    }

    @Override
    public void postSetSplitOrMergeEnabled(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, Admin.MasterSwitchType masterSwitchType) throws IOException {
        super.postSetSplitOrMergeEnabled(observerContext, b, masterSwitchType);
        outInfo("TestObserver2.postSetSplitOrMergeEnabled() ");
    }*/

    @Override
    public boolean preBalanceSwitch(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b) throws IOException {
        outInfo("TestObserver2.preBalanceSwitch() ");
        return super.preBalanceSwitch(observerContext, b);
    }

    @Override
    public void postBalanceSwitch(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, boolean b1) throws IOException {
        super.postBalanceSwitch(observerContext, b, b1);
        outInfo("TestObserver2.postBalanceSwitch() ");
    }

    @Override
    public void preShutdown(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preShutdown(observerContext);
        outInfo("TestObserver2.preShutdown() ");
    }

    @Override
    public void preStopMaster(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preStopMaster(observerContext);
        outInfo("TestObserver2.preStopMaster() ");
    }

    @Override
    public void postStartMaster(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.postStartMaster(observerContext);
        outInfo("TestObserver2.postStartMaster() ");
    }

    @Override
    public void preMasterInitialization(ObserverContext<MasterCoprocessorEnvironment> observerContext) throws IOException {
        super.preMasterInitialization(observerContext);
        outInfo("TestObserver2.preMasterInitialization() ");
    }

    @Override
    public void preSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.preSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver2.preSnapshot() ");
    }

    @Override
    public void postSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.postSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver2.postSnapshot() ");
    }

    @Override
    public void preListSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        super.preListSnapshot(observerContext, snapshotDescription);
        outInfo("TestObserver2.preListSnapshot() ");
    }

    @Override
    public void postListSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        super.postListSnapshot(observerContext, snapshotDescription);
        outInfo("TestObserver2.postListSnapshot() ");
    }

    @Override
    public void preCloneSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.preCloneSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver2.preCloneSnapshot() ");
    }

    @Override
    public void postCloneSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.postCloneSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver2.postCloneSnapshot() ");
    }

    @Override
    public void preRestoreSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.preRestoreSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver2.preRestoreSnapshot() ");
    }

    @Override
    public void postRestoreSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription, HTableDescriptor hTableDescriptor) throws IOException {
        super.postRestoreSnapshot(observerContext, snapshotDescription, hTableDescriptor);
        outInfo("TestObserver2.postRestoreSnapshot() ");
    }

    @Override
    public void preDeleteSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        super.preDeleteSnapshot(observerContext, snapshotDescription);
        outInfo("TestObserver2.preDeleteSnapshot() ");
    }

    @Override
    public void postDeleteSnapshot(ObserverContext<MasterCoprocessorEnvironment> observerContext, HBaseProtos.SnapshotDescription snapshotDescription) throws IOException {
        super.postDeleteSnapshot(observerContext, snapshotDescription);
        outInfo("TestObserver2.postDeleteSnapshot() ");
    }

    @Override
    public void preGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1) throws IOException {
        super.preGetTableDescriptors(observerContext, list, list1);
        outInfo("TestObserver2.preGetTableDescriptors() ");
    }

    @Override
    public void postGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list) throws IOException {
        super.postGetTableDescriptors(observerContext, list);
        outInfo("TestObserver2.postGetTableDescriptors() ");
    }

    @Override
    public void preGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1, String s) throws IOException {
        super.preGetTableDescriptors(observerContext, list, list1, s);
        outInfo("TestObserver2.preGetTableDescriptors() ");
    }

    @Override
    public void postGetTableDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<TableName> list, List<HTableDescriptor> list1, String s) throws IOException {
        super.postGetTableDescriptors(observerContext, list, list1, s);
        outInfo("TestObserver2.postGetTableDescriptors() ");
    }

    @Override
    public void preGetTableNames(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list, String s) throws IOException {
        super.preGetTableNames(observerContext, list, s);
        outInfo("TestObserver2.preGetTableNames() ");
    }

    @Override
    public void postGetTableNames(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<HTableDescriptor> list, String s) throws IOException {
        super.postGetTableNames(observerContext, list, s);
        outInfo("TestObserver2.postGetTableNames() ");
    }

    @Override
    public void preCreateNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        String name=namespaceDescriptor.getName()+"test";
        namespaceDescriptor=NamespaceDescriptor.create(name).build();
        outInfo("TestObserver2.preCreateNamespace() ");
        super.preCreateNamespace(observerContext, namespaceDescriptor);
    }

    @Override
    public void postCreateNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.postCreateNamespace(observerContext, namespaceDescriptor);
        outInfo("TestObserver2.postCreateNamespace() ");
    }

    @Override
    public void preDeleteNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        s="ns56";
        super.preDeleteNamespace(observerContext, s);
        outInfo("TestObserver2.preDeleteNamespace() ");
        outInfo("参数中的s是： "+s);
    }

    @Override
    public void postDeleteNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        super.postDeleteNamespace(observerContext, s);
        outInfo("TestObserver2.postDeleteNamespace() ");
    }

    @Override
    public void preModifyNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.preModifyNamespace(observerContext, namespaceDescriptor);
        outInfo("TestObserver2.preModifyNamespace() ");
    }

    @Override
    public void postModifyNamespace(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.postModifyNamespace(observerContext, namespaceDescriptor);
        outInfo("TestObserver2.postModifyNamespace() ");
    }

    @Override
    public void preGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s) throws IOException {
        super.preGetNamespaceDescriptor(observerContext, s);
        outInfo("TestObserver2.preGetNamespaceDescriptor() ");
    }

    @Override
    public void postGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> observerContext, NamespaceDescriptor namespaceDescriptor) throws IOException {
        super.postGetNamespaceDescriptor(observerContext, namespaceDescriptor);
        outInfo("TestObserver2.postGetNamespaceDescriptor() ");
    }

    @Override
    public void preListNamespaceDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<NamespaceDescriptor> list) throws IOException {
        super.preListNamespaceDescriptors(observerContext, list);
        outInfo("TestObserver2.preListNamespaceDescriptors() ");
    }

    @Override
    public void postListNamespaceDescriptors(ObserverContext<MasterCoprocessorEnvironment> observerContext, List<NamespaceDescriptor> list) throws IOException {
        super.postListNamespaceDescriptors(observerContext, list);
        outInfo("TestObserver2.postListNamespaceDescriptors() ");
    }

    @Override
    public void preTableFlush(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.preTableFlush(observerContext, tableName);
        outInfo("TestObserver2.preTableFlush() ");
    }

    @Override
    public void postTableFlush(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName) throws IOException {
        super.postTableFlush(observerContext, tableName);
        outInfo("TestObserver2.postTableFlush() ");
    }

    @Override
    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetUserQuota(observerContext, s, quotas);
        outInfo("TestObserver2.preSetUserQuota() ");
    }

    @Override
    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetUserQuota(observerContext, s, quotas);
        outInfo("TestObserver2.postSetUserQuota() ");
    }

    @Override
    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetUserQuota(observerContext, s, tableName, quotas);
        outInfo("TestObserver2.preSetUserQuota() ");
    }

    @Override
    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetUserQuota(observerContext, s, tableName, quotas);
        outInfo("TestObserver2.postSetUserQuota() ");
    }

    @Override
    public void preSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, String s1, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetUserQuota(observerContext, s, s1, quotas);
        outInfo("TestObserver2.preSetUserQuota() ");
    }

    @Override
    public void postSetUserQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, String s1, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetUserQuota(observerContext, s, s1, quotas);
        outInfo("TestObserver2.postSetUserQuota() ");
    }

    @Override
    public void preSetTableQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetTableQuota(observerContext, tableName, quotas);
        outInfo("TestObserver2.preSetTableQuota() ");
    }

    @Override
    public void postSetTableQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, TableName tableName, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetTableQuota(observerContext, tableName, quotas);
        outInfo("TestObserver2.postSetTableQuota() ");
    }

    @Override
    public void preSetNamespaceQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        super.preSetNamespaceQuota(observerContext, s, quotas);
        outInfo("TestObserver2.preSetNamespaceQuota() ");
    }

    @Override
    public void postSetNamespaceQuota(ObserverContext<MasterCoprocessorEnvironment> observerContext, String s, QuotaProtos.Quotas quotas) throws IOException {
        super.postSetNamespaceQuota(observerContext, s, quotas);
        outInfo("TestObserver2.postSetNamespaceQuota() ");
    }

    @Override
    public void preDispatchMerge(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HRegionInfo hRegionInfo1) throws IOException {
        super.preDispatchMerge(observerContext, hRegionInfo, hRegionInfo1);
        outInfo("TestObserver2.preDispatchMerge() ");
    }

    @Override
    public void postDispatchMerge(ObserverContext<MasterCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HRegionInfo hRegionInfo1) throws IOException {
        super.postDispatchMerge(observerContext, hRegionInfo, hRegionInfo1);
        outInfo("TestObserver2.postDispatchMerge() ");
    }

    @Override
    public void start(CoprocessorEnvironment e) throws IOException {
        super.start(e);
        outInfo("TestObserver2.start() ");
    }

    @Override
    public void stop(CoprocessorEnvironment e) throws IOException {
        super.stop(e);
        outInfo("TestObserver2.stop() ");
    }

    @Override
    public void preOpen(ObserverContext<RegionCoprocessorEnvironment> e) throws IOException {
        super.preOpen(e);
        outInfo("TestObserver2.preOpen() ");
    }

    @Override
    public void postOpen(ObserverContext<RegionCoprocessorEnvironment> e) {
        super.postOpen(e);
        outInfo("TestObserver2.postOpen() ");
    }

    @Override
    public void postLogReplay(ObserverContext<RegionCoprocessorEnvironment> e) {
        super.postLogReplay(e);
        outInfo("TestObserver2.postLogReplay() ");
    }

    @Override
    public void preClose(ObserverContext<RegionCoprocessorEnvironment> c, boolean abortRequested) throws IOException {
        super.preClose(c, abortRequested);
        outInfo("TestObserver2.preClose() ");
    }

    @Override
    public void postClose(ObserverContext<RegionCoprocessorEnvironment> e, boolean abortRequested) {
        super.postClose(e, abortRequested);
        outInfo("TestObserver2.postClose() ");
    }

    @Override
    public InternalScanner preFlushScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, KeyValueScanner memstoreScanner, InternalScanner s) throws IOException {
        outInfo("TestObserver2.preFlushScannerOpen() ");
        return super.preFlushScannerOpen(c, store, memstoreScanner, s);
    }

    @Override
    public void preFlush(ObserverContext<RegionCoprocessorEnvironment> e) throws IOException {
        super.preFlush(e);
        outInfo("TestObserver2.preFlush() ");
    }

    @Override
    public void postFlush(ObserverContext<RegionCoprocessorEnvironment> e) throws IOException {
        super.postFlush(e);
        outInfo("TestObserver2.postFlush() ");
    }

    @Override
    public InternalScanner preFlush(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner) throws IOException {
        outInfo("TestObserver2.preFlush() ");
        return super.preFlush(e, store, scanner);
    }

    @Override
    public void postFlush(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile) throws IOException {
        super.postFlush(e, store, resultFile);
        outInfo("TestObserver2.postFlush() ");
    }

    @Override
    public void preSplit(ObserverContext<RegionCoprocessorEnvironment> e) throws IOException {
        super.preSplit(e);
        outInfo("TestObserver2.preSplit() ");
    }

    @Override
    public void preSplit(ObserverContext<RegionCoprocessorEnvironment> c, byte[] splitRow) throws IOException {
        super.preSplit(c, splitRow);
        outInfo("TestObserver2.preSplit() ");
    }

    @Override
    public void preSplitBeforePONR(ObserverContext<RegionCoprocessorEnvironment> ctx, byte[] splitKey, List<Mutation> metaEntries) throws IOException {
        super.preSplitBeforePONR(ctx, splitKey, metaEntries);
        outInfo("TestObserver2.preSplitBeforePONR() ");
    }

    @Override
    public void preSplitAfterPONR(ObserverContext<RegionCoprocessorEnvironment> ctx) throws IOException {
        super.preSplitAfterPONR(ctx);
        outInfo("TestObserver2.preSplitAfterPONR() ");
    }

    @Override
    public void preRollBackSplit(ObserverContext<RegionCoprocessorEnvironment> ctx) throws IOException {
        super.preRollBackSplit(ctx);
        outInfo("TestObserver2.preRollBackSplit() ");
    }

    @Override
    public void postRollBackSplit(ObserverContext<RegionCoprocessorEnvironment> ctx) throws IOException {
        super.postRollBackSplit(ctx);
        outInfo("TestObserver2.postRollBackSplit() ");
    }

    @Override
    public void postCompleteSplit(ObserverContext<RegionCoprocessorEnvironment> ctx) throws IOException {
        super.postCompleteSplit(ctx);
        outInfo("TestObserver2.postCompleteSplit() ");
    }

    @Override
    public void postSplit(ObserverContext<RegionCoprocessorEnvironment> e, Region l, Region r) throws IOException {
        super.postSplit(e, l, r);
        outInfo("TestObserver2.postSplit() ");
    }

    @Override
    public void preCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<StoreFile> candidates) throws IOException {
        super.preCompactSelection(c, store, candidates);
        outInfo("TestObserver2.preCompactSelection() ");
    }

    @Override
    public void preCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<StoreFile> candidates, CompactionRequest request) throws IOException {
        super.preCompactSelection(c, store, candidates, request);
        outInfo("TestObserver2.preCompactSelection() ");
    }

    @Override
    public void postCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, ImmutableList<StoreFile> selected) {
        super.postCompactSelection(c, store, selected);
        outInfo("TestObserver2.postCompactSelection() ");
    }

    @Override
    public void postCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, ImmutableList<StoreFile> selected, CompactionRequest request) {
        super.postCompactSelection(c, store, selected, request);
        outInfo("TestObserver2.postCompactSelection() ");
    }

    @Override
    public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner, ScanType scanType) throws IOException {
        outInfo("TestObserver2.preCompact() ");
        return super.preCompact(e, store, scanner, scanType);
    }

    @Override
    public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner, ScanType scanType, CompactionRequest request) throws IOException {
        outInfo("TestObserver2.preCompact() ");
        return super.preCompact(e, store, scanner, scanType, request);
    }

    @Override
    public InternalScanner preCompactScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends KeyValueScanner> scanners, ScanType scanType, long earliestPutTs, InternalScanner s) throws IOException {
        outInfo("TestObserver2.preCompactScannerOpen() ");
        return super.preCompactScannerOpen(c, store, scanners, scanType, earliestPutTs, s);
    }

    @Override
    public InternalScanner preCompactScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends KeyValueScanner> scanners, ScanType scanType, long earliestPutTs, InternalScanner s, CompactionRequest request) throws IOException {
        outInfo("TestObserver2.preCompactScannerOpen() ");
        return super.preCompactScannerOpen(c, store, scanners, scanType, earliestPutTs, s, request);
    }

    @Override
    public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile) throws IOException {
        super.postCompact(e, store, resultFile);
        outInfo("TestObserver2.postCompact() ");
    }

    @Override
    public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile, CompactionRequest request) throws IOException {
        super.postCompact(e, store, resultFile, request);
        outInfo("TestObserver2.postCompact() ");
    }

    @Override
    public void preGetClosestRowBefore(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, Result result) throws IOException {
        super.preGetClosestRowBefore(e, row, family, result);
        outInfo("TestObserver2.preGetClosestRowBefore() ");
    }

    @Override
    public void postGetClosestRowBefore(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, Result result) throws IOException {
        super.postGetClosestRowBefore(e, row, family, result);
        outInfo("TestObserver2.postGetClosestRowBefore() ");
    }

    @Override
    public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
        outInfo("TestObserver2.preGetOp() " );
        String row=Bytes.toString(get.getRow());
        String tn=e.getEnvironment().getRegionInfo().getTable().getNameAsString();
        outInfo("map结束＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝ ");

//        Get ng=new Get(Bytes.toBytes("r2"));
//        ng.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"));
//        get=ng;
    }



    @Override
    public boolean preExists(ObserverContext<RegionCoprocessorEnvironment> e, Get get, boolean exists) throws IOException {
        outInfo("TestObserver2.preExists() ");
        return super.preExists(e, get, exists);
    }

    @Override
    public boolean postExists(ObserverContext<RegionCoprocessorEnvironment> e, Get get, boolean exists) throws IOException {
        outInfo("TestObserver2.postExists() ");
        return super.postExists(e, get, exists);
    }

    @Override
    public void preDelete(ObserverContext<RegionCoprocessorEnvironment> e, Delete delete, WALEdit edit, Durability durability) throws IOException {
        outInfo("TestObserver2.preDelete() " );
    }

    @Override
    public void prePrepareTimeStampForDeleteVersion(ObserverContext<RegionCoprocessorEnvironment> e, Mutation delete, Cell cell, byte[] byteNow, Get get) throws IOException {
        super.prePrepareTimeStampForDeleteVersion(e, delete, cell, byteNow, get);
        outInfo("TestObserver2.prePrepareTimeStampForDeleteVersion() ");
    }

    @Override
    public void postDelete(ObserverContext<RegionCoprocessorEnvironment> e, Delete delete, WALEdit edit, Durability durability) throws IOException {
        outInfo("TestObserver2.postDelete() " );
    }

    @Override
    public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
        super.preBatchMutate(c, miniBatchOp);
        outInfo("TestObserver2.preBatchMutate() ");
    }

    @Override
    public void postBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
        super.postBatchMutate(c, miniBatchOp);
        outInfo("TestObserver2.postBatchMutate() ");
    }

    @Override
    public void postBatchMutateIndispensably(ObserverContext<RegionCoprocessorEnvironment> ctx, MiniBatchOperationInProgress<Mutation> miniBatchOp, boolean success) throws IOException {
        super.postBatchMutateIndispensably(ctx, miniBatchOp, success);
        outInfo("TestObserver2.postBatchMutateIndispensably() ");
    }

    @Override
    public boolean preCheckAndPut(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo("TestObserver2.preCheckAndPut() ");
        return super.preCheckAndPut(e, row, family, qualifier, compareOp, comparator, put, result);
    }

    @Override
    public boolean preCheckAndPutAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo("TestObserver2.preCheckAndPutAfterRowLock() ");
        return super.preCheckAndPutAfterRowLock(e, row, family, qualifier, compareOp, comparator, put, result);
    }

    @Override
    public boolean postCheckAndPut(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
        outInfo("TestObserver2.postCheckAndPut() ");
        return super.postCheckAndPut(e, row, family, qualifier, compareOp, comparator, put, result);
    }

    @Override
    public boolean preCheckAndDelete(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo("TestObserver2.preCheckAndDelete() ");
        return super.preCheckAndDelete(e, row, family, qualifier, compareOp, comparator, delete, result);
    }

    @Override
    public boolean preCheckAndDeleteAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo("TestObserver2.preCheckAndDeleteAfterRowLock() ");
        return super.preCheckAndDeleteAfterRowLock(e, row, family, qualifier, compareOp, comparator, delete, result);
    }

    @Override
    public boolean postCheckAndDelete(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
        outInfo("TestObserver2.postCheckAndDelete() ");
        return super.postCheckAndDelete(e, row, family, qualifier, compareOp, comparator, delete, result);
    }

    @Override
    public Result preAppend(ObserverContext<RegionCoprocessorEnvironment> e, Append append) throws IOException {
        outInfo("TestObserver2.preAppend() ");
        return super.preAppend(e, append);
    }

    @Override
    public Result preAppendAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> e, Append append) throws IOException {
        outInfo("TestObserver2.preAppendAfterRowLock() ");
        return super.preAppendAfterRowLock(e, append);
    }

    @Override
    public Result postAppend(ObserverContext<RegionCoprocessorEnvironment> e, Append append, Result result) throws IOException {
        outInfo("TestObserver2.postAppend() ");
        return super.postAppend(e, append, result);
    }

    @Override
    public long preIncrementColumnValue(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, long amount, boolean writeToWAL) throws IOException {
        outInfo("TestObserver2.preIncrementColumnValue() ");
        return super.preIncrementColumnValue(e, row, family, qualifier, amount, writeToWAL);
    }

    @Override
    public long postIncrementColumnValue(ObserverContext<RegionCoprocessorEnvironment> e, byte[] row, byte[] family, byte[] qualifier, long amount, boolean writeToWAL, long result) throws IOException {
        outInfo("TestObserver2.postIncrementColumnValue() ");
        return super.postIncrementColumnValue(e, row, family, qualifier, amount, writeToWAL, result);
    }

    @Override
    public Result preIncrement(ObserverContext<RegionCoprocessorEnvironment> e, Increment increment) throws IOException {
        outInfo("TestObserver2.preIncrement() ");
        return super.preIncrement(e, increment);
    }

    @Override
    public Result preIncrementAfterRowLock(ObserverContext<RegionCoprocessorEnvironment> e, Increment increment) throws IOException {
        outInfo("TestObserver2.preIncrementAfterRowLock() ");
        return super.preIncrementAfterRowLock(e, increment);
    }

    @Override
    public Result postIncrement(ObserverContext<RegionCoprocessorEnvironment> e, Increment increment, Result result) throws IOException {
        outInfo("TestObserver2.postIncrement() ");
        return super.postIncrement(e, increment, result);
    }

    @Override
    public RegionScanner preScannerOpen(ObserverContext<RegionCoprocessorEnvironment> e, Scan scan, RegionScanner s) throws IOException {
        outInfo("TestObserver2.preScannerOpen() ");
        return super.preScannerOpen(e, scan, s);
    }

    @Override
    public RegionScanner postScannerOpen(ObserverContext<RegionCoprocessorEnvironment> e, Scan scan, RegionScanner s) throws IOException {
        outInfo("TestObserver2.postScannerOpen() ");
        return super.postScannerOpen(e, scan, s);
    }

    @Override
    public boolean preScannerNext(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s, List<Result> results, int limit, boolean hasMore) throws IOException {
        outInfo("TestObserver2.preScannerNext() ");

        return super.preScannerNext(e, s, results, limit, hasMore);
    }


    @Override
    public boolean postScannerFilterRow(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s, byte[] currentRow, int offset, short length, boolean hasMore) throws IOException {
        outInfo("TestObserver2.postScannerFilterRow() ");
        return super.postScannerFilterRow(e, s, currentRow, offset, length, hasMore);
    }

    @Override
    public void preScannerClose(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s) throws IOException {
        super.preScannerClose(e, s);
        outInfo("TestObserver2.preScannerClose() ");
    }

    @Override
    public void postScannerClose(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s) throws IOException {
        super.postScannerClose(e, s);
        outInfo("TestObserver2.postScannerClose() ");
    }

    @Override
    public void preWALRestore(ObserverContext<? extends RegionCoprocessorEnvironment> env, HRegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
        super.preWALRestore(env, info, logKey, logEdit);
        outInfo("TestObserver2.preWALRestore() ");
    }

    @Override
    public void preWALRestore(ObserverContext<RegionCoprocessorEnvironment> env, HRegionInfo info, HLogKey logKey, WALEdit logEdit) throws IOException {
        super.preWALRestore(env, info, logKey, logEdit);
        outInfo("TestObserver2.preWALRestore() ");
    }

    @Override
    public void postWALRestore(ObserverContext<? extends RegionCoprocessorEnvironment> env, HRegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
        super.postWALRestore(env, info, logKey, logEdit);
        outInfo("TestObserver2.postWALRestore() ");
    }

    @Override
    public void postWALRestore(ObserverContext<RegionCoprocessorEnvironment> env, HRegionInfo info, HLogKey logKey, WALEdit logEdit) throws IOException {
        super.postWALRestore(env, info, logKey, logEdit);
        outInfo("TestObserver2.postWALRestore() ");
    }

    @Override
    public void preBulkLoadHFile(ObserverContext<RegionCoprocessorEnvironment> ctx, List<Pair<byte[], String>> familyPaths) throws IOException {
        super.preBulkLoadHFile(ctx, familyPaths);
        outInfo("TestObserver2.preBulkLoadHFile() ");
    }

    @Override
    public boolean postBulkLoadHFile(ObserverContext<RegionCoprocessorEnvironment> ctx, List<Pair<byte[], String>> familyPaths, boolean hasLoaded) throws IOException {
        outInfo("TestObserver2.postBulkLoadHFile() ");
        return super.postBulkLoadHFile(ctx, familyPaths, hasLoaded);
    }

    @Override
    public StoreFile.Reader preStoreFileReaderOpen(ObserverContext<RegionCoprocessorEnvironment> ctx, FileSystem fs, Path p, FSDataInputStreamWrapper in, long size, CacheConfig cacheConf, Reference r, StoreFile.Reader reader) throws IOException {
        outInfo("TestObserver2.preStoreFileReaderOpen() ");
        return super.preStoreFileReaderOpen(ctx, fs, p, in, size, cacheConf, r, reader);
    }

    @Override
    public StoreFile.Reader postStoreFileReaderOpen(ObserverContext<RegionCoprocessorEnvironment> ctx, FileSystem fs, Path p, FSDataInputStreamWrapper in, long size, CacheConfig cacheConf, Reference r, StoreFile.Reader reader) throws IOException {
        outInfo("TestObserver2.postStoreFileReaderOpen() ");
        return super.postStoreFileReaderOpen(ctx, fs, p, in, size, cacheConf, r, reader);
    }

    @Override
    public Cell postMutationBeforeWAL(ObserverContext<RegionCoprocessorEnvironment> ctx, MutationType opType, Mutation mutation, Cell oldCell, Cell newCell) throws IOException {
        outInfo("TestObserver2.postMutationBeforeWAL() ");
        return super.postMutationBeforeWAL(ctx, opType, mutation, oldCell, newCell);
    }

    @Override
    public void postStartRegionOperation(ObserverContext<RegionCoprocessorEnvironment> ctx, Region.Operation op) throws IOException {
        super.postStartRegionOperation(ctx, op);
        outInfo("TestObserver2.postStartRegionOperation() ");
    }

    @Override
    public void postCloseRegionOperation(ObserverContext<RegionCoprocessorEnvironment> ctx, Region.Operation op) throws IOException {
        super.postCloseRegionOperation(ctx, op);
        outInfo("TestObserver2.postCloseRegionOperation() ");
    }

    @Override
    public DeleteTracker postInstantiateDeleteTracker(ObserverContext<RegionCoprocessorEnvironment> ctx, DeleteTracker delTracker) throws IOException {
        outInfo("TestObserver2.postInstantiateDeleteTracker() ");
        return super.postInstantiateDeleteTracker(ctx, delTracker);
    }
    @Override
    public boolean preSetSplitOrMergeEnabled(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, Admin.MasterSwitchType masterSwitchType) throws IOException {
        outInfo("TestObserver2.preSetSplitOrMergeEnabled() ");
        return super.preSetSplitOrMergeEnabled(observerContext, b, masterSwitchType);
    }

    @Override
    public void postSetSplitOrMergeEnabled(ObserverContext<MasterCoprocessorEnvironment> observerContext, boolean b, Admin.MasterSwitchType masterSwitchType) throws IOException {
        outInfo("TestObserver2.postSetSplitOrMergeEnabled() ");

    }

    @Override
    public void prePrepareBulkLoad(ObserverContext<RegionCoprocessorEnvironment> observerContext, SecureBulkLoadProtos.PrepareBulkLoadRequest prepareBulkLoadRequest) throws IOException {
        outInfo("TestObserver2.prePrepareBulkLoad() ");
    }

    @Override
    public void preCleanupBulkLoad(ObserverContext<RegionCoprocessorEnvironment> observerContext, SecureBulkLoadProtos.CleanupBulkLoadRequest cleanupBulkLoadRequest) throws IOException {
        outInfo("TestObserver2.preCleanupBulkLoad() ");
    }

    @Override
    public void preStopRegionServer(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("TestObserver2.preStopRegionServer() ");
    }

    @Override
    public void preMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {
        outInfo("TestObserver2.preMerge() ");
    }

    @Override
    public void postMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, Region region2) throws IOException {
        outInfo("TestObserver2.postMerge() ");
    }

    @Override
    public void preMergeCommit(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, List<Mutation> list) throws IOException {
        outInfo("TestObserver2.preMergeCommit() ");
    }

    @Override
    public void postMergeCommit(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1, Region region2) throws IOException {
        outInfo("TestObserver2.postMergeCommit() ");
    }

    @Override
    public void preRollBackMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {
        outInfo("TestObserver2.preRollBackMerge() ");
    }

    @Override
    public void postRollBackMerge(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, Region region, Region region1) throws IOException {
        outInfo("TestObserver2.postRollBackMerge() ");
    }

    @Override
    public void preRollWALWriterRequest(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("TestObserver2.preRollWALWriterRequest() ");
    }

    @Override
    public void postRollWALWriterRequest(ObserverContext<RegionServerCoprocessorEnvironment> observerContext) throws IOException {
        outInfo("TestObserver2.postRollWALWriterRequest() ");
    }

    @Override
    public ReplicationEndpoint postCreateReplicationEndPoint(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, ReplicationEndpoint replicationEndpoint) {
        outInfo("TestObserver2.postCreateReplicationEndPoint() ");
        return super.postCreateReplicationEndPoint(observerContext, replicationEndpoint);
    }

    @Override
    public void preReplicateLogEntries(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, List<AdminProtos.WALEntry> list, CellScanner cellScanner) throws IOException {
        outInfo("TestObserver2.preReplicateLogEntries() ");
    }

    @Override
    public void postReplicateLogEntries(ObserverContext<RegionServerCoprocessorEnvironment> observerContext, List<AdminProtos.WALEntry> list, CellScanner cellScanner) throws IOException {
        outInfo("TestObserver2.postReplicateLogEntries() ");
    }

    @Override
    public KeyValueScanner preStoreScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store, Scan scan, NavigableSet<byte[]> targetCols, KeyValueScanner s) throws IOException {
        outInfo("TestObserver2.preStoreScannerOpen() ");
        return super.preStoreScannerOpen(c, store, scan, targetCols, s);
    }

    @Override
    public boolean preWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, WALKey walKey, WALEdit walEdit) throws IOException {
        outInfo("TestObserver2.preWALWrite(observerContext, hRegionInfo, walKey, walEdit) ");
        return super.preWALWrite(observerContext, hRegionInfo, walKey, walEdit);
    }

    @Override
    public boolean preWALWrite(ObserverContext<WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HLogKey hLogKey, WALEdit walEdit) throws IOException {
        outInfo("TestObserver2.preWALWrite(observerContext, hRegionInfo, hLogKey, walEdit) ");
        return super.preWALWrite(observerContext, hRegionInfo, hLogKey, walEdit);
    }

    @Override
    public void postWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, WALKey walKey, WALEdit walEdit) throws IOException {
        outInfo("TestObserver2.postWALWrite(observerContext, hRegionInfo, walKey, walEdit)");
        super.postWALWrite(observerContext, hRegionInfo, walKey, walEdit);
    }

    @Override
    public void postWALWrite(ObserverContext<WALCoprocessorEnvironment> observerContext, HRegionInfo hRegionInfo, HLogKey hLogKey, WALEdit walEdit) throws IOException {
        super.postWALWrite(observerContext, hRegionInfo, hLogKey, walEdit);
        outInfo("TestObserver2.postWALWrite(observerContext, hRegionInfo, hLogKey, walEdit)");
    }

    @Override
    public void preWALRoll(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, Path path, Path path1) throws IOException {
        super.preWALRoll(observerContext, path, path1);
        outInfo("TestObserver2.preWALRoll(observerContext, path, path1)");
    }

    @Override
    public void postWALRoll(ObserverContext<? extends WALCoprocessorEnvironment> observerContext, Path path, Path path1) throws IOException {
        super.postWALRoll(observerContext, path, path1);
        outInfo("TestObserver2.postWALRoll(observerContext, path, path1)");
    }
}
