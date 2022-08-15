package com.example.modbus;

import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ModbusTcpSlaveConfig;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Slave {

    ModbusTcpSlaveConfig config = new ModbusTcpSlaveConfig.Builder().build();
    ModbusTcpSlave slave = new ModbusTcpSlave(config);
    int countOfWritingCoils;

    final RequestHandler requestHandler;

    public Slave(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        slave.setRequestHandler(requestHandler);
    }

    public void readRegisters(List<Short> readingRegisters) {
        requestHandler.setReadingRegisters(readingRegisters);
        slave.bind("localhost", 502);

    }

    public void onReadCoils(List<Byte> readingCoils) {
        requestHandler.setReadingCoils(readingCoils);
        slave.bind("localhost", 502);
    }

    public int onWriteSingleCoil() {
        slave.bind("localhost", 502);
        return requestHandler.getWritingCoil();
    }

    public short onWriteSingleRegister() {
        slave.bind("localhost", 502);
        return requestHandler.getWritingRegister();
    }

    public List<Short> onWriteMultipleRegisters() {
        slave.bind("localhost", 502);
        while (!requestHandler.isReadyToTranslate()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return requestHandler.getWritingRegisters();
    }

    public List<Byte> onWriteMultipleCoils() {
        slave.bind("localhost", 502);
        while (!requestHandler.isReadyToTranslate()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        countOfWritingCoils = requestHandler.getCountOfWritingCoils();
        return requestHandler.getWritingCoils();
    }

    public int getCountOfWritingCoils() {
        return countOfWritingCoils;
    }
}
