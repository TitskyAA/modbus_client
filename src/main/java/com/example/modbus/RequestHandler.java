package com.example.modbus;

import com.digitalpetri.modbus.requests.*;
import com.digitalpetri.modbus.responses.*;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class RequestHandler implements ServiceRequestHandler {

    private List<Short> readingRegisters;
    private List<Byte> readingCoils;
    private int writingCoil;
    private List<Byte> writingCoils = new ArrayList<>();
    private short writingRegister;
    private List<Short> writingRegisters = new ArrayList<>();
    private int countOfWritingCoils;

    private boolean readyToTranslate;


    @Override
    public void onReadHoldingRegisters(ServiceRequest<ReadHoldingRegistersRequest, ReadHoldingRegistersResponse> service) {
        ReadHoldingRegistersRequest request = service.getRequest();
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(request.getQuantity());
        for (int i=0; i < request.getQuantity(); i++) {
            buffer.writeShort(readingRegisters.get(i));
        }
        service.sendResponse(new ReadHoldingRegistersResponse(buffer));
        ReferenceCountUtil.release(request);
    }

    @Override
    public void onWriteSingleRegister(ServiceRequest<WriteSingleRegisterRequest, WriteSingleRegisterResponse> service) {
        WriteSingleRegisterRequest request = service.getRequest();
        service.sendResponse(new WriteSingleRegisterResponse(request.getAddress(), request.getValue()));
        writingRegister = (short) request.getValue();
    }

    @Override
    public void onWriteMultipleRegisters(ServiceRequest<WriteMultipleRegistersRequest, WriteMultipleRegistersResponse> service) {
        WriteMultipleRegistersRequest request = service.getRequest();
        service.sendResponse(new WriteMultipleRegistersResponse(request.getAddress(), request.getQuantity()));
        ByteBuf buffer = request.getValues();
        writingRegisters.clear();
        for (int i=0; i < request.getQuantity(); i++) {
            writingRegisters.add(buffer.readShort());
        }
        if (request.release()) {readyToTranslate = true;}
    }

    @Override
    public void onReadCoils(ServiceRequest<ReadCoilsRequest, ReadCoilsResponse> service) {
        ReadCoilsRequest request = service.getRequest();
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(request.getQuantity());
        for (Byte i: readingCoils) {
            buffer.writeByte(i);
        }
        service.sendResponse(new ReadCoilsResponse(buffer));
        ReferenceCountUtil.release(request);
    }

    @Override
    public void onWriteSingleCoil(ServiceRequest<WriteSingleCoilRequest, WriteSingleCoilResponse> service) {
        WriteSingleCoilRequest request = service.getRequest();
        service.sendResponse(new WriteSingleCoilResponse(request.getAddress(), request.getValue()));
        writingCoil = request.getValue();
    }

    @Override
    public void onWriteMultipleCoils(ServiceRequest<WriteMultipleCoilsRequest, WriteMultipleCoilsResponse> service) {
        WriteMultipleCoilsRequest request = service.getRequest();
        countOfWritingCoils = request.getQuantity();
        service.sendResponse(new WriteMultipleCoilsResponse(request.getAddress(), request.getQuantity()));
        ByteBuf buffer = request.getValues();
        writingCoils.clear();
        for (int i=0; i<request.getQuantity(); i=i+8) {
            writingCoils.add(buffer.readByte());
        }
        if (request.release()) {readyToTranslate = true;}
    }

    public void setReadingRegisters(List<Short> readingRegisters) {
        this.readingRegisters = readingRegisters;
    }

    public void setReadingCoils(List<Byte> readingCoils) {
        this.readingCoils = readingCoils;
    }

    public int getWritingCoil() {
        return writingCoil;
    }

    public short getWritingRegister() {
        return writingRegister;
    }

    public List<Short> getWritingRegisters() {
        return writingRegisters;
    }

    public List<Byte> getWritingCoils() {
        return writingCoils;
    }

    public boolean isReadyToTranslate() {
        return readyToTranslate;
    }

    public int getCountOfWritingCoils() {
        return countOfWritingCoils;
    }
}
