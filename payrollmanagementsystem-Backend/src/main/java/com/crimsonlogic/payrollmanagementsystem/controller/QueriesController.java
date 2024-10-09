package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.QueriesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.QueriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
  Controller for managing queries within the payroll management system.
  This includes adding new queries, retrieving queries by employee ID,
  retrieving all queries, and responding to existing queries.

  @author abdulmanan
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/queries")
public class QueriesController {

    private final QueriesService queriesService;

    /**
     * Constructs a QueriesController with the specified QueriesService.
     *
     * @param queriesService the service used to handle query operations
     */
    QueriesController(QueriesService queriesService) {
        super();
        this.queriesService = queriesService;
    }

    /**
     * Adds a new query based on the provided QueriesDTO.
     *
     * @param queriesDTO the DTO containing the details of the query to be added
     * @return a ResponseEntity indicating the result of the addition operation
     */
    @PostMapping("/addquery")
    public ResponseEntity<String> addQuery(@RequestBody QueriesDTO queriesDTO) {
        try {
            queriesService.addQuery(queriesDTO);
            return ResponseEntity.status(201).build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves all queries for a specific employee by their ID.
     *
     * @param employeeId the ID of the employee whose queries are to be retrieved
     * @return a ResponseEntity containing a list of QueriesDTOs for the specified employee
     */
    @GetMapping("/getqueries")
    public ResponseEntity<List<QueriesDTO>> getQueries(@RequestParam String employeeId) {
        List<QueriesDTO> queriesDTO = queriesService.getQueryByEmployeeId(employeeId);
        return ResponseEntity.ok(queriesDTO);
    }

    /**
     * Retrieves all queries in the system.
     *
     * @return a ResponseEntity containing a list of all QueriesDTOs
     */
    @GetMapping("/getallqueries")
    public ResponseEntity<List<QueriesDTO>> getAllQueries() {
        List<QueriesDTO> queriesDTO = queriesService.getAllQueries();
        return ResponseEntity.ok(queriesDTO);
    }

    /**
     * Responds to an existing query based on the provided QueriesDTO.
     *
     * @param queriesDTO the DTO containing the response details for the query
     * @return a ResponseEntity indicating the result of the response operation
     */
    @PostMapping("/responsetoquery")
    public ResponseEntity<Void> responseToQuery(@RequestBody QueriesDTO queriesDTO) {
        boolean isUpdated = queriesService.responseToQuery(queriesDTO);

        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).build();
    }
}
