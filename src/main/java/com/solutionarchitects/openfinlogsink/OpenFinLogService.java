package com.solutionarchitects.openfinlogsink;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RestController
@CrossOrigin("*")
public class OpenFinLogService {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    @Value("${logFolderRoot}")
    String logFolderRoot;


    @PostConstruct
    protected void Init(){
        logger.info("Log Folder Root : {} ", logFolderRoot);
    }

    @GetMapping("/logs")
    public String GetLogs(){
        return "Logs";
    }


    @PostMapping(value = "sendLogs/api/v1/logs",consumes = "multipart/form-data")
    public Object UpLoadLogs(@RequestParam(value = "desktopId") String desktopId,
                             @RequestParam(value = "appSessionId") String appSessionId,
                             @RequestParam(value = "closeReason") String closeReason,
                             @RequestParam(value = "runtimeVersion") String runtimeVersion,
                             @RequestParam(value = "rvmVersion") String rvmVersion,
                          @RequestParam(value = "appName") String appName,
                          @RequestParam(value = "userName") String UserName,
                          @RequestParam(value = "appConfigUrl") String appConfigUrl,
                          @RequestParam(value = "logFile") MultipartFile file
                            ){



        logger.info("Log For {} Desktop ID : {} App Name : {} AppConfigUrl : {} CloseReason : {} AppSessionId : {} RunTimeVersion : {} RvmVersion : {} ",
                UserName, desktopId, appName, appConfigUrl, closeReason, appSessionId, runtimeVersion, rvmVersion);



        String folderName = this.logFolderRoot + "/" + UserName + "/";
        File folder = new File(folderName);
        boolean folderCreated = folder.mkdirs();
        logger.info("Folder Created : {} ", folderCreated);
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());

        String fileName = folderName + appName + "-" + timeStamp + ".zip";
        File zipFile = new File(fileName);


        try {
            boolean created = zipFile.createNewFile();
            OutputStream
                    os
                    = new FileOutputStream(zipFile);

            os.write(file.getBytes());

            os.close();

        } catch (IOException e) {
            logger.error("Error Creating File ", e);
            ReturnCode returnCode = new ReturnCode();

            returnCode.response = new Response();
            returnCode.response.setId(UUID.randomUUID().toString());
            returnCode.error = e.getMessage();
            return returnCode;
        }

        ReturnCode returnCode = new ReturnCode();

        returnCode.response = new Response();
        returnCode.response.setId(UUID.randomUUID().toString());

        return  returnCode;

    }


}
