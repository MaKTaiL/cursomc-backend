package com.matheusc.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.matheusc.cursomc.domain.Cidade;
import com.matheusc.cursomc.domain.Cliente;
import com.matheusc.cursomc.domain.Endereco;
import com.matheusc.cursomc.domain.enums.TipoCliente;
import com.matheusc.cursomc.dto.ClienteDTO;
import com.matheusc.cursomc.dto.ClienteNewDTO;
import com.matheusc.cursomc.repositories.CidadeRepository;
import com.matheusc.cursomc.repositories.ClienteRepository;
import com.matheusc.cursomc.repositories.EnderecoRepository;
import com.matheusc.cursomc.services.exceptions.DataIntegrityException;
import com.matheusc.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private CidadeRepository cidadeRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	public Cliente find(Integer id) {
		Optional<Cliente> obj = clienteRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = clienteRepository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		find(obj.getId());
		return clienteRepository.save(obj);
	}
	
	public void delete(Integer id) {
		find(id);
		
		try {
			clienteRepository.deleteById(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possivel excluir porque há pedidos relacionados");
		}
	}
	
	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(
				objDto.getId(), 
				objDto.getNome(), 
				objDto.getEmail(), 
				find(objDto.getId()).getCpfOuCnpj(), 
				find(objDto.getId()).getTipo());
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(
				null, 
				objDto.getNome(), 
				objDto.getEmail(), 
				objDto.getCpfOuCnpj(), 
				TipoCliente.toEnum(objDto.getTipo()));
		
		Cidade cid = cidadeRepository.getOne(objDto.getCidadeId());
		
		Endereco end = new Endereco(
				null, 
				objDto.getLogradouro(), 
				objDto.getNumero(), 
				objDto.getComplemento(), 
				objDto.getBairro(), 
				objDto.getCep(), cli, cid);
		
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		if(objDto.getTelefone2()!=null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}
		if(objDto.getTelefone3()!=null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}
		
		return cli;
	}
}