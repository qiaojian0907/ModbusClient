package com.omt.modbus;

import com.omt.Util.HttpClientUtil;
import com.omt.Util.ModbusUtil;
import com.omt.Util.SerialPortWrapperImpl;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ModbusController {

    /**
     * 服务端读取Modbus数据
     * @return
     * @throws SocketException
     * @throws UnknownHostException
     */
    @RequestMapping(value = "/getRTUModbusData")
    public JSONObject getModbusData() throws SocketException, UnknownHostException {

        Map resultMap = new HashMap();
        String disc = "";

        ModbusMaster master = ModbusUtil.getRtuMaster();

        try{
            master.init();
            int slaveId = 1;        //设备ID

            disc = ModbusUtil.readDiscreteInput(master, slaveId, 0, 24);   /////读状态数据32
            //hold = ModbusUtil.readHoldingRegisters(master, slaveId, 16, 10);  ////读电压电流数据  ----//64
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            master.destroy();
        }
        String ip = ModbusUtil.getHostIp();
        String mac = ModbusUtil.getLocalMac();

        //String hold = "[220,220,220,220,220,220,5,6]";
        //String coil = "[true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,true,false,true,false,false,false,false]";
        resultMap.put("disc",subString(disc));
        resultMap.put("ip",ip);
        resultMap.put("mac",mac);

        JSONObject json = JSONObject.fromObject(resultMap);
        return json;
    }

    /**
     * 向服务端发送Modbus数据
     * @throws IOException
     */
    @RequestMapping(value = "/sendModbusData")
    public void sendModbusData() throws IOException {
        Map<String,Object> resultMap = new HashMap<String,Object>();

        String ip = ModbusUtil.getHostIp();
        String mac = ModbusUtil.getLocalMac();

        String hold = "[220,220,220,220,220,220,5,6]";
        String coil = "[true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,true,false,false,true,false,false,false,false]";
        resultMap.put("hold",subString(hold));
        resultMap.put("coil",subString(coil));
        resultMap.put("ip",ip);
        resultMap.put("mac",mac);

        JSONObject json = JSONObject.fromObject(resultMap);
        HttpClientUtil.doPostJson("http://192.168.31.230:8081/test1",json.toString());

    }

    /**
     * 截取掉第一位和最后一位
     * @param str
     * @return
     */
    public String subString(String str){
        String returnStr = str.substring(1,str.length()-1);
        return returnStr;
    }

}
