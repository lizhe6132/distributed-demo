package com.lizhe.distributeddemo.readfile;

import com.lizhe.distributeddemo.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

@Component
public class ReadDataFromDirctory {
    // 存储文件
    private final BlockingQueue<File> filesQueue = new ArrayBlockingQueue<>(10);
    private final BlockingQueue<String[]> sqlQueue = new ArrayBlockingQueue<>(500);
    private static final int MAX_THREADS = 10;
    private static final File DUMMY = new File("");// 结束标志
    private static final String[] END = new String[]{"THE END"};
    private final Executor executor = new ThreadPoolExecutor(10, 20, 0L, TimeUnit.SECONDS, new SynchronousQueue<>());
    private final Executor sqlExecutor = new ThreadPoolExecutor(10, 20, 0L, TimeUnit.SECONDS,new SynchronousQueue<>());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 准备文件
     * @param filePath
     * @throws InterruptedException
     */
    public void getXlsFiles(String filePath) throws InterruptedException {
        File file = new File(filePath);
        if (!file.isDirectory()) {
            return;
        }
        Runnable findFileR = () -> {
            Thread.currentThread().setName("add file");
            try {
                addFiles(file);
                filesQueue.put(DUMMY);
                System.out.println("加载文件结束");
            } catch (InterruptedException e) {
            }
        };
        // 单线程去拿文件。速度很快
        new Thread(findFileR).start();

    }

    private void addFiles(File file) throws InterruptedException {
        File[] files = file.listFiles();
        for (File f: files) {
            if (f.isDirectory()) {
                addFiles(f);
            } else {
                String name = f.getName();
                if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
                    filesQueue.put(f);
                }
            }
        }
    }

    public void processExcelFiles() {
        for (int i = 0; i < MAX_THREADS; i++) {
            Runnable process = () -> {
                boolean done = false;
                while (!done) {
                    try {
                        File file = filesQueue.take();
                        if (file == DUMMY) {
                            filesQueue.put(file);
                            done = true;
                            System.out.println("解析文件结束");
                        } else {
                            analysis(file);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            executor.execute(process);
        }
    }

    /**
     * 解析excel文件组织sql
     * @param file
     */
    private void analysis(File file) {
        /*Set<String> citySet = new HashSet<>();
        Set<String> countySet = new HashSet<>();*/
        Set<String> townSet = new HashSet<>();
        //Set<String> villageSet = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        try {
            List<List<Object>> list = FileUtils.processExcelByRowAndCol(file, 5, 0, 11);
            if (list != null && list.size() > 0) {
                //city表插入sql
                List<String> citySqlList = new ArrayList<>(50);
                List<String> countySqlList = new ArrayList<>(50);
                List<String> townSqlList = new ArrayList<>(50);
                List<String> villageSqlList = new ArrayList<>(50);
                for (List<Object> list1: list) {
                    /*createCitySql(citySet, sb, citySqlList, list1);
                    createCountySql(countySet, sb, countySqlList, list1);*/
                    createTownSql(townSet, sb, townSqlList, list1);
                    //createVillageSql(villageSet, sb, villageSqlList, list1);
                }
                /*String[] temp = new String[citySqlList.size()];
                sqlQueue.put(citySqlList.toArray(temp));
                citySqlList.clear();
                String[] tempCounty = new String[countySqlList.size()];
                sqlQueue.put(countySqlList.toArray(tempCounty));
                countySqlList.clear();*/
                String[] tempTown = new String[townSqlList.size()];
                sqlQueue.put(townSqlList.toArray(tempTown));
                townSqlList.clear();
                /*String[] tempVillage = new String[villageSqlList.size()];
                sqlQueue.put(villageSqlList.toArray(tempVillage));
                villageSqlList.clear();*/
            }
        } catch (Exception e) {
            System.out.println("失败文件:" + file.getName());
            e.printStackTrace();
        }
    }

    /**
     * 侯建villageSql
     * @param villageSet
     * @param sb
     * @param villageSqlList
     * @param list1
     */
    private void createVillageSql(Set<String> villageSet, StringBuilder sb, List<String> villageSqlList, List<Object> list1) throws InterruptedException {
        String vNo = list1.get(9) != null ? list1.get(9).toString() : null;
        if (vNo == null) {
            return;
        }
        if ("".equals(vNo)) {
            return;
        }
        if (!villageSet.contains(vNo)) {
            villageSet.add(vNo);
            String townNo = list1.get(7) != null ? list1.get(7).toString() : "";
            String vName = list1.get(10) != null ? list1.get(10).toString() : "";
            sb.append("INSERT INTO VILLAGE VALUES('").append(townNo).append("','").append(vNo)
                    .append("','").append(vName).append("')");
            if (villageSqlList.size() > 49) {
                String[] temp = new String[villageSqlList.size()];
                sqlQueue.put(villageSqlList.toArray(temp));
                villageSqlList.clear();
            }
            villageSqlList.add(sb.toString());
            sb.setLength(0);
        }
    }

    /**
     * 构建town表sql
     * @param townSet
     * @param sb
     * @param townSqlList
     * @param list1
     * @throws InterruptedException
     */
    private void createTownSql(Set<String> townSet, StringBuilder sb, List<String> townSqlList, List<Object> list1) throws InterruptedException {
        String townNo = list1.get(7) != null ? list1.get(7).toString() : null;
        if (townNo == null) {
            return;
        }
        if ("".equals(townNo)) {
            return;
        }
        if (!townSet.contains(townNo)) {
            townSet.add(townNo);
            String cityNo = list1.get(5) != null ? list1.get(5).toString() : "";
            String townName = list1.get(8) != null ? list1.get(8).toString() : "";
            sb.append("INSERT INTO TOWN VALUES('").append(cityNo).append("','").append(townNo)
                    .append("','").append(townName).append("')");
            if (townSqlList.size() > 49) {
                String[] temp = new String[townSqlList.size()];
                sqlQueue.put(townSqlList.toArray(temp));
                townSqlList.clear();
            }
            townSqlList.add(sb.toString());
            sb.setLength(0);
        }
    }

    /**
     * 构建COUNTY表sql
     * @param countySet
     * @param sb
     * @param countySqlList
     * @param list1
     * @throws InterruptedException
     */
    private void createCountySql(Set<String> countySet, StringBuilder sb, List<String> countySqlList, List<Object> list1) throws InterruptedException {
        String countyNo = list1.get(5) != null ? list1.get(5).toString() : null;
        if (countyNo == null) {
            return;
        }
        if ("".equals(countyNo)) {
            return;
        }
        if (!countySet.contains(countyNo)) {
            countySet.add(countyNo);
            String cityNo = list1.get(3) != null ? list1.get(3).toString() : "";
            String countyName = list1.get(6) != null ? list1.get(6).toString() : "";
            sb.append("INSERT INTO COUNTY VALUES('").append(cityNo).append("','").append(countyNo)
                    .append("','").append(countyName).append("')");
            if (countySqlList.size() > 49) {
                String[] temp = new String[countySqlList.size()];
                sqlQueue.put(countySqlList.toArray(temp));
                countySqlList.clear();
            }
            countySqlList.add(sb.toString());
            sb.setLength(0);
        }
    }

    private void createCitySql(Set<String> citySet, StringBuilder sb, List<String> citySqlList, List<Object> list1) throws InterruptedException {
        String cityNo = list1.get(3) != null ? list1.get(3).toString() : null;
        if (null == cityNo) {
            return;
        }
        if ("".equals(cityNo)) {
            return;
        }
        if (!citySet.contains(cityNo)) {
            citySet.add(cityNo);
            String provNo = list1.get(1) != null ? list1.get(1).toString() : "";
            String cityName = list1.get(4) != null ? list1.get(4).toString() : "";
            sb.append("INSERT INTO CITY VALUES('").append(provNo).append("','").append(cityNo)
                    .append("','").append(cityName).append("')");
            if (citySqlList.size() > 49) {
                String[] temp = new String[citySqlList.size()];
                sqlQueue.put(citySqlList.toArray(temp));
                citySqlList.clear();
            }
            citySqlList.add(sb.toString());
            sb.setLength(0);
        }
    }

    public void doSql() {
        for (int i = 0; i < 20; i++) {
            Runnable process = () -> {
                boolean done = false;
                while (!done) {
                    try {
                        String[] sqls = sqlQueue.take();
                        if (sqls == END) {
                            sqlQueue.put(END);
                            done = true;
                            System.out.println("任务结束");
                        } else {
                            jdbcTemplate.batchUpdate(sqls);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            sqlExecutor.execute(process);
        }
    }

}
