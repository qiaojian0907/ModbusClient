package com.omt.Util;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;

public class ModbusUtil {

    /**
     * 获取modbus master工厂
     * @return
     */
    public static ModbusMaster getRtuMaster() {
        String commPortId = "COM6";
        int baudRate = 9600;
        int flowControlIn = 0;
        int flowControlOut = 0;
        int dataBits = 8;
        int stopBits = 1;
        int parity = 0;
        SerialPortWrapperImpl wrapper = new SerialPortWrapperImpl(commPortId, baudRate, dataBits, stopBits, parity, flowControlIn, flowControlOut);
        IpParameters ipParameters = new IpParameters();
        ipParameters.setPort(502);
        ModbusFactory modbusFactory = new ModbusFactory();

        ModbusMaster master = modbusFactory.createRtuMaster(wrapper);
        return master;
        //ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, false);
        /*SerialParameters serialParameters = new SerialParameters();
        // 设定MODBUS通讯的串行口
        serialParameters.setCommPortId("COM5");
        // 设置端口波特率
        serialParameters.setBaudRate(9600);
        //硬件之间输入流应答控制
        serialParameters.setFlowControlIn(0);
        //硬件之间输出流应答控制
        serialParameters.setFlowControlOut(0);
        //设定数据位的位数  RTU:8位    ASCII:7位
        serialParameters.setDataBits(8);
        //奇偶校验位  无校验：0 奇校验：1 偶校验：2
        serialParameters.setParity(0);
        //停止位的位数，如果无奇偶校验为2，有奇偶校验为1
        serialParameters.setStopBits(1);
        // 设置端口名称
        serialParameters.setPortOwnerName("RTU");
        // 创建ModbusMaster工厂实例
        return new ModbusFactory().createRtuMaster(serialParameters);*/
    }
    /**
     * @Function 02
     * @param master
     * @param slaveId
     * @param start
     * @param len
     */
    public static String readDiscreteInput(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveId, start, len);
            ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getBooleanData()));
            return Arrays.toString(response.getBooleanData());
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
            return "None";
        }
    }

    /**
     * Function 03
     * @param master
     * @param slaveId
     * @param start
     * @param len
     */
    public static String readHoldingRegisters(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, len);
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);

            if (response.isException())
                System.out.println("1Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getShortData()));
            return(Arrays.toString(response.getShortData()));
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
            return "None";
        }
    }

    /**
     * 批量写数据到保持寄存器
     * @param master 主站
     * @param slaveId 从站地址
     * @param start 起始地址的偏移量
     * @param values 待写数据
     */
    public static void writeRegisters(ModbusMaster master, int slaveId, int start, short[] values) {
        try {
            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, start, values);
            WriteRegistersResponse response = (WriteRegistersResponse) master.send(request);
            if (response.isException()){
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            }
            else {
                System.out.println("Success");
            }
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void writeCoil(ModbusMaster master, int slaveId, int start, boolean value){
        try {
            WriteCoilRequest request = new WriteCoilRequest(slaveId, start, value);
            WriteCoilResponse response = (WriteCoilResponse)master.send(request);
            if (response.isException()){
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            }
            else {
                System.out.println("Success");
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void writeCoils(ModbusMaster master, int slaveId, int start, boolean[] values){
        try {
            WriteCoilsRequest request = new WriteCoilsRequest(slaveId, start, values);
            WriteCoilsResponse response = (WriteCoilsResponse)master.send(request);
            if (response.isException()){
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            }
            else {
                System.out.println("Success");
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取客户端ip
     * @return
     */
    public static String getHostIp(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        //System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取客户端mac地址
     * @return
     * @throws SocketException
     */
    public static String  getLocalMac() throws SocketException, UnknownHostException {
        InetAddress ia = InetAddress.getLocalHost();
        // TODO Auto-generated method stub
        //获取网卡，获取地址
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        //System.out.println("mac数组长度："+mac.length);
        StringBuffer sb = new StringBuffer("");
        for(int i=0; i<mac.length; i++) {
            if(i!=0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i]&0xff;
            String str = Integer.toHexString(temp);
            //System.out.println("每8位:"+str);
            if(str.length()==1) {
                sb.append("0"+str);
            }else {
                sb.append(str);
            }
        }
        //System.out.println("本机MAC地址:"+sb.toString().toUpperCase());
        return sb.toString().toUpperCase();
    }
}
