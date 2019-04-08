package com.matheusc.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matheusc.cursomc.domain.ItemPedido;
import com.matheusc.cursomc.domain.PagamentoComBoleto;
import com.matheusc.cursomc.domain.Pedido;
import com.matheusc.cursomc.domain.Produto;
import com.matheusc.cursomc.domain.enums.EstadoPagamento;
import com.matheusc.cursomc.repositories.ItemPedidoRepository;
import com.matheusc.cursomc.repositories.PagamentoRepository;
import com.matheusc.cursomc.repositories.PedidoRepository;
import com.matheusc.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repo;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}
	
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if(obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		
		for(ItemPedido ip : obj.getProdutos()) {
			ip.setDesconto(0.0);
			
			Produto p = produtoService.find(ip.getProduto().getId());
			ip.setPreco(p.getPreco());
			ip.setPedido(obj);
		}
		
		itemPedidoRepository.saveAll(obj.getProdutos());
		return obj;
	}
}