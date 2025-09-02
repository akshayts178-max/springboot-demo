package com.example.demo.controller;

import com.example.demo.model.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/employees")
public class EmployeeConrtroller {
    private final Map<Long, Employee> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee body) {
        long id = idSeq.getAndIncrement();
        Employee emp = new Employee(id, body.getName(), body.getRole());
        store.put(id, emp);
        return new ResponseEntity<>(emp, HttpStatus.CREATED);
    }

    @GetMapping
    public Collection<Employee> getAll() {
        return store.values();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee body) {
        Employee existing = store.get(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.setName(body.getName());
        existing.setRole(body.getRole());
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return store.remove(id) != null ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
