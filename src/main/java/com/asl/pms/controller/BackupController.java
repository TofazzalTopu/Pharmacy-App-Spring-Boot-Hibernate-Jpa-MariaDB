package com.asl.pms.controller;


import com.asl.pms.util.DBConfigService;
import com.asl.pms.util.GlobalMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping(value = "/backup")
public class BackupController {

    @Autowired
    private static DBConfigService dbConfigService;

    @Autowired
    GlobalMethod globalMethod;

    @RequestMapping(value = "/executeBackup")
    public String check() {
        boolean success = backup();
        if (success) {
            return "success";
        } else {
            return "fails";
        }
    }


    @RequestMapping(value = "/viewDBBackupForm", method = RequestMethod.GET)
    public ModelAndView viewPurchaseForm(Model model) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        ModelAndView view = new ModelAndView("dashboard/backup/view");
        return view;
    }

    public static boolean backup() {

        String url = dbConfigService.getUrl();
        String username = dbConfigService.getUserName();
        String password = dbConfigService.getPassword();
        String mysqlBinPath = dbConfigService.getMysqlBinPath() + File.separator;
        String backupPath = dbConfigService.getBackupPath() + File.separator;

        boolean status = false;
        String database = url.split("/")[3];
        String addDrop = " add-drop-database -B";

        String fileName = database + "_" + System.currentTimeMillis() + ".sql";

        Process p = null;
        String command = mysqlBinPath + "mysqldump -u" + username + " -p" + password + " --add-drop-database -B " + database + " -r " + backupPath + database + "_" + System.currentTimeMillis() + ".sql";

        try {
            System.out.println("command== " + command);

            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(command);
//            p = runtime.exec("C:/Program Files/MySQL/MySQL Server 5.7/bin/mysqldump -uroot -proot --add-drop-database -B zpharma -r " + backupPath + "db" + ".sql");

            int processComplete = p.waitFor();
            if (processComplete == 0) {
                System.out.println("DatabaseManager.backup: Backup Successfull");
                status = true;
            } else {
                System.out.println("DatabaseManager.backup: Backup Failure!");
            }
        } catch (IOException ioe) {
            System.out.println("Exception IO");
            ioe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
        return status;
    }

/*

    //    @GetMapping("/download/{fileName:.+}")
    public static ResponseEntity downloadFileFromLocal(String fileBasePath, String fileName) {
        Path path = Paths.get(fileBasePath + fileName);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
*/


    public void test() {
        try {
            File dir = new File("/root/");
            Runtime runtime = Runtime.getRuntime();
            Process p = runtime.exec("mysqldump database_name >/path-for-backpfile/database.sql", null, dir);

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = reader.readLine();


            while (line != null) {
                System.out.println(line);

                line = reader.readLine();
            }

        } catch (IOException e1) {
        }
    }

    public void restore() {
        String[] restoreCmd = null;
//        String[] restoreCmd = new String[]{"mysql ", "--user=" + dbUserName, "--password=" + dbPassword, "-e", "source " + source};

        Process runtimeProcess;
        try {

            runtimeProcess = Runtime.getRuntime().exec(restoreCmd);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                System.out.println("Restored successfully!");
            } else {
                System.out.println("Could not restore the backup!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
