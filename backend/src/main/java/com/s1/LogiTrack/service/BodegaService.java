package com.s1.LogiTrack.service;

import com.s1.LogiTrack.dto.request.BodegaRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;

import java.util.List;

public interface BodegaService {

    BodegaResponseDTO guardarBodega(BodegaRequestDTO dto);

    BodegaResponseDTO actualizarBodega(BodegaRequestDTO dto, Long id);

    void eliminarBodega(Long id);

    List<BodegaResponseDTO> listarBodega();

    BodegaResponseDTO buscarPorId (Long id);

}
