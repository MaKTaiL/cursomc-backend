package com.matheusc.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matheusc.cursomc.domain.Cidade;
import com.matheusc.cursomc.domain.Estado;
import com.matheusc.cursomc.repositories.CidadeRepository;
import com.matheusc.cursomc.repositories.EstadoRepository;
import com.matheusc.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CidadeService {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	public List<Cidade> findByEstado(Integer estado_id) {
		Estado estado = estadoRepository.findById(estado_id).orElse(null);
		if(estado == null) {
			throw new ObjectNotFoundException("Estado informado não existe. Id: " + estado_id + ", Tipo: " + Estado.class.getName());
		}
		return cidadeRepository.findCidades(estado_id);
	}
}
