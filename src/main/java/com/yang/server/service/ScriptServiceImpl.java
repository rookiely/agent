package com.yang.server.service;

import com.yang.thrift.script.ScriptService;
import com.yang.thrift.script.ShellData;
import org.apache.thrift.TException;
import org.springframework.stereotype.Controller;

import java.io.*;

@Controller
public class ScriptServiceImpl implements ScriptService.Iface {

    @Override
    public boolean createScriptFile(ShellData shellData) throws TException {
        String shellFileName = shellData.shellName;
        String shellFileContent = shellData.shellContent;
        String[] contentArr = shellFileContent.split("\n");

        File sh = new File("D:\\" + shellFileName + ".sh");
        if (sh.exists()) {
            sh.delete();
        }

        try {
            boolean isCreated = sh.createNewFile();
            boolean isExecutable = sh.setExecutable(true);
            FileWriter fw = new FileWriter(sh);
            BufferedWriter bf = new BufferedWriter(fw);
            for (int i = 0; i < contentArr.length; i++) {
                bf.write(contentArr[i]);
                if (i < contentArr.length - 1) {
                    bf.newLine();
                }
            }
            bf.flush();
            bf.close();
            return isCreated && isExecutable;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean execScript(String shellName) throws TException {
        try {
            Process ps = Runtime.getRuntime().exec("D:\\" + shellName + ".sh");
            ps.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
