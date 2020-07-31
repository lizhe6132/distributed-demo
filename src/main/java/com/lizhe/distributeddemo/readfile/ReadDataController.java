package com.lizhe.distributeddemo.readfile;

import com.lizhe.distributeddemo.readfile.ReadDataFromDirctory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadDataController {
    @Autowired
    private ReadDataFromDirctory readDataFromDirctory;
    @RequestMapping("/excel/read")
    public void readFile() throws InterruptedException {
        String filePath = "D:\\work-ronghui\\hainan\\city3";
        readDataFromDirctory.getXlsFiles(filePath);
        readDataFromDirctory.processExcelFiles();
        readDataFromDirctory.doSql();
    }
}
