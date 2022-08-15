package com.example.modbus.service;

import com.example.modbus.Slave;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientService {

    final Slave slave;

    public ClientService(Slave slave) {
        this.slave = slave;
    }

    public void onReadHoldingRegisters(List<Short> readingRegisters) {
        slave.readRegisters(readingRegisters);
    }

    public short onWriteSingleRegister() {
        return (slave.onWriteSingleRegister());
    }

    public List<Short> onWriteMultipleRegisters() {
        return slave.onWriteMultipleRegisters();
    }

    public void onReadCoils(List<Boolean> readingCoils) {
        byte iterator = 0b0000_0001;
        byte newByte = 0b0000_0000;
        int counter = 0;
        List<Byte> coils = new ArrayList<>();

        for (int i=0; i< readingCoils.size(); i++) {
            if (readingCoils.get(i)) {
                newByte = (byte) (newByte | (iterator << counter));
            }
            counter++;
            if ((i + 1) % 8 == 0) {
                coils.add(newByte);
                newByte = 0b0000_0000;
                counter = 0;
            }
        }
        if (readingCoils.size() % 8 != 0) {
            coils.add(newByte);
        }
        slave.onReadCoils(coils);
    }
    public int onWriteSingleCoil() {
        return (slave.onWriteSingleCoil() == 65280 ? 1 : 0);
    }

    public List<Byte> onWriteMultipleCoils() {
        byte iterator = 0b0000_0001;
        List<Byte> coils = slave.onWriteMultipleCoils();
        List<Byte> output = new ArrayList<>();

        for (int i=0; i<coils.size(); i++) {
            for (int j=0; j<8; j++) {
                if ((coils.get(i) & (iterator << j)) == (iterator << j)) {
                    output.add((byte) 1);
                } else {
                    output.add((byte) 0);
                }
            }
        }
        return output.stream().limit(slave.getCountOfWritingCoils()).toList();
    }
}
