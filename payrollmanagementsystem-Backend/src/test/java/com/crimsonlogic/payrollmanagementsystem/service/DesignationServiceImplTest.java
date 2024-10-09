package com.crimsonlogic.payrollmanagementsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import com.crimsonlogic.payrollmanagementsystem.domain.Designation;
import com.crimsonlogic.payrollmanagementsystem.dto.DesignationDTO;
import com.crimsonlogic.payrollmanagementsystem.repository.DesignationRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.DesignationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DesignationServiceImplTest {

    @InjectMocks
    private DesignationServiceImpl designationService;

    @Mock
    private DesignationRepository designationRepository;

    private Designation designation;

    private DesignationDTO designationDTO;

    @BeforeEach
    void setUp() {

        designation = new Designation();
        designation.setDesignationId("des1");
        designation.setDesignationName("Software Engineer");

        designationDTO = new DesignationDTO();
        designationDTO.setDesignationId("des1");
        designationDTO.setDesignationName("Senior Software Engineer");
    }

    @Test
    void testGetAllDesignations() {
        when(designationRepository.findAll()).thenReturn(Collections.singletonList(designation));

        List<DesignationDTO> designationsList = designationService.getAllDesignations();

        assertThat(designationsList)
                .isNotNull()
                .hasSize(1);
        assertThat(designationsList.get(0).getDesignationName()).isEqualTo("Software Engineer");
        verify(designationRepository).findAll();
    }

    @Test
    void testAddDesignation_Success() {
        designationDTO.setDesignationName("New Designation");

        boolean result = designationService.addDesignation(designationDTO);

        assertThat(result).isTrue();
        verify(designationRepository).save(any(Designation.class));
    }

    @Test
    void testAddDesignation_NameMissing() {
        designationDTO.setDesignationName(null);

        boolean result = designationService.addDesignation(designationDTO);

        assertThat(result).isFalse();
        verify(designationRepository, never()).save(any(Designation.class));
    }

    @Test
    void testUpdateDesignation_Success() {
        when(designationRepository.findById(designation.getDesignationId())).thenReturn(Optional.of(designation));

        boolean result = designationService.updateDesignation(designationDTO);

        assertThat(result).isTrue();
        assertThat(designation.getDesignationName()).isEqualTo(designationDTO.getDesignationName());
        verify(designationRepository).save(designation);
    }

    @Test
    void testUpdateDesignation_NotFound() {
        when(designationRepository.findById(anyString())).thenReturn(Optional.empty());

        boolean result = designationService.updateDesignation(designationDTO);

        assertThat(result).isFalse();
        verify(designationRepository, never()).save(any(Designation.class));
    }
}

