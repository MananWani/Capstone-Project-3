package com.crimsonlogic.payrollmanagementsystem.controller;


import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import com.crimsonlogic.payrollmanagementsystem.dto.QueriesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.QueriesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class QueriesControllerTest {

    @InjectMocks
    private QueriesController queriesController;

    @Mock
    private QueriesService queriesService;

    private QueriesDTO queriesDTO;

    @BeforeEach
    void setUp() {

        queriesDTO = new QueriesDTO();
        queriesDTO.setQueryId("query-1");
        queriesDTO.setEmployeeId("employee-1");
        queriesDTO.setQueryDescription("Problem");
    }

    @Test
    void testAddQuery_Success() throws ResourceNotFoundException {
        doNothing().when(queriesService).addQuery(any(QueriesDTO.class));

        ResponseEntity<String> response = queriesController.addQuery(queriesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testAddQuery_Failure() throws ResourceNotFoundException {
        doThrow(new ResourceNotFoundException("Resource not found")).when(queriesService).addQuery(any(QueriesDTO.class));

        ResponseEntity<String> response = queriesController.addQuery(queriesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetQueries_Success() {
        List<QueriesDTO> queriesList = Collections.singletonList(queriesDTO);
        when(queriesService.getQueryByEmployeeId("employee-1")).thenReturn(queriesList);

        ResponseEntity<List<QueriesDTO>> response = queriesController.getQueries("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getQueryId()).isEqualTo(queriesDTO.getQueryId());
    }

    @Test
    void testGetAllQueries_Success() {
        List<QueriesDTO> queriesList = Collections.singletonList(queriesDTO);
        when(queriesService.getAllQueries()).thenReturn(queriesList);

        ResponseEntity<List<QueriesDTO>> response = queriesController.getAllQueries();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testResponseToQuery_Success() {
        when(queriesService.responseToQuery(any(QueriesDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = queriesController.responseToQuery(queriesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testResponseToQuery_NotFound() {
        when(queriesService.responseToQuery(any(QueriesDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = queriesController.responseToQuery(queriesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
