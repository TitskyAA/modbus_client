package com.example.modbus.controller;

import com.example.modbus.service.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ModBusController {

    final ClientService service;

    public ModBusController(ClientService service) {
        this.service = service;
    }

    @PostMapping("/onReadHoldingRegisters")
    public void onReadHoldingRegisters(@RequestBody List<Short> readingRegisters) {
        service.onReadHoldingRegisters(readingRegisters);
    }

    @GetMapping("/onWriteSingleRegister")
    public short onWriteSingleRegister() {
        return service.onWriteSingleRegister();
    }

    @GetMapping("/onWriteMultipleRegisters")
    public List<Short> onWriteMultipleRegisters() {
        return service.onWriteMultipleRegisters();
    }

    @PostMapping("/onReadCoils")
    public void onReadCoils(@RequestBody List<Boolean> readingCoils) {
        service.onReadCoils(readingCoils);
    }

    @GetMapping("/onWriteSingleCoil")
    public int onWriteSingleCoil() {
        return service.onWriteSingleCoil();
    }

    @GetMapping("/onWriteMultipleCoils")
    public List<Byte> onWriteMultipleCoils() {
        return service.onWriteMultipleCoils();
    }
}
