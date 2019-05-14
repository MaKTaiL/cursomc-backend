package com.matheusc.cursomc.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matheusc.cursomc.domain.Estado;
import com.matheusc.cursomc.repositories.EstadoRepository;

@Service
public class EstadoService {

	@Autowired
	private EstadoRepository estadoRepository;
	
	public List<Estado> findAllByOrderByNome() {
		return estadoRepository.findAllByOrderByNome();
	}
}
