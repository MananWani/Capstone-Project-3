package com.crimsonlogic.payrollmanagementsystem.controller;

import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.crimsonlogic.payrollmanagementsystem.dto.DesignationDTO;
import com.crimsonlogic.payrollmanagementsystem.service.DesignationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DesignationControllerTest {

    @InjectMocks
    private DesignationController designationController;

    @Mock
    private DesignationService designationService;

    private DesignationDTO designationDTO;

    @BeforeEach
    void setUp() {

        designationDTO = new DesignationDTO();
        designationDTO.setDesignationId("designation-1");
        designationDTO.setDesignationName("Manager");
    }

    @Test
    void testGetAllDesignations_Success() {
        List<DesignationDTO> designationList = Collections.singletonList(designationDTO);
        when(designationService.getAllDesignations()).thenReturn(designationList);

        ResponseEntity<List<DesignationDTO>> response = designationController.getAllDesignations();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getDesignationId()).isEqualTo(designationDTO.getDesignationId());
    }

    @Test
    void testAddDesignation_Success() {
        when(designationService.addDesignation(any(DesignationDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = designationController.addDesignation(designationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testAddDesignation_Failure() {
        when(designationService.addDesignation(any(DesignationDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = designationController.addDesignation(designationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateDesignation_Success() {
        when(designationService.updateDesignation(any(DesignationDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = designationController.updateDesignation(designationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testUpdateDesignation_NotFound() {
        when(designationService.updateDesignation(any(DesignationDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = designationController.updateDesignation(designationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
