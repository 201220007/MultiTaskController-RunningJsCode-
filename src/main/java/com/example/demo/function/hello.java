package com.example.demo.function;

import com.alibaba.fastjson.JSONObject;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class hello {
    public static OshiControl oshiControl;
    public static void main(String[] args) throws UnknownHostException {
        JSONObject a= OshiControl.getCpuInfo();
        JSONObject b= OshiControl.getJvmInfo();
        JSONObject c= OshiControl.getMemInfo();
        JSONObject m= OshiControl.getInfo();
        System.out.println(m);
    }
}

