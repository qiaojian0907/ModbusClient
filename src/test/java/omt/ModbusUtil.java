package omt;

import com.omt.Util.SerialPortWrapperImpl;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;

import java.util.Arrays;

import static com.omt.Util.ModbusUtil.getHostIp;
import static com.omt.Util.ModbusUtil.getLocalMac;
import static com.omt.Util.ModbusUtil.readDiscreteInput;

public class ModbusUtil {

    public static void main(String[] args) throws Exception {
        String commPortId = "COM6";
        int baudRate = 2400;
        int flowControlIn = 0;
        int flowControlOut = 0;
        int dataBits = 8;
        int stopBits = 1;
        int parity = 0;

        SerialPortWrapperImpl wrapper = new SerialPortWrapperImpl(commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity);
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost("127.0.0.1");   //设备ip地址
        ipParameters.setPort(502);
        com.serotonin.modbus4j.ModbusFactory modbusFactory = new ModbusFactory();

        ModbusMaster master = modbusFactory.createRtuMaster(wrapper);  //RTU
        //ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, false);   //TCP

        try{
            master.init();
            int slaveId = 1;        //设备ID
            //com.omt.Util.ModbusUtil.readHoldingRegisters(master, slaveId, 0, 24);
            //readDiscreteInputTest(master, slaveId, 0, 24);   /////读状态数据32
            //---- readHoldingRegistersTest(master, slaveId, 16, 10);  ////读电压电流数据  ----//64

            String ip = getHostIp();
            String mac = getLocalMac();
            System.out.println(ip+"---"+mac);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            master.destroy();
        }

    }

}
