package com.matheusc.cursomc.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.matheusc.cursomc.domain.Categoria;
import com.matheusc.cursomc.domain.Produto;
import com.matheusc.cursomc.domain.enums.Perfil;
import com.matheusc.cursomc.repositories.CategoriaRepository;
import com.matheusc.cursomc.repositories.ProdutoRepository;
import com.matheusc.cursomc.security.UserSS;
import com.matheusc.cursomc.services.exceptions.AuthorizationException;
import com.matheusc.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private S3Service s3Service;
	
	@Value("${img.prefix.produto}")
	private String prefix;
	
	@Value("${img.profile.size}")
	private int size;
	
	public Produto find(Integer id) {
		Optional<Produto> obj = produtoRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Produto.class.getName()));
	}
	
	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		List<Categoria> categorias = categoriaRepository.findAllById(ids);
		return produtoRepository.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageRequest);
	}
	
	public URI uploadProdutoPicture(MultipartFile multipartFile, int prodId) {
		UserSS user = UserService.authenticated();
		if(user == null || !user.hasRole(Perfil.ADMIN)) {
			throw new AuthorizationException("Acesso negado");
		}
		
		BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);
		BufferedImage smallJpgImage = imageService.cropSquare(jpgImage);
		smallJpgImage = imageService.rezise(smallJpgImage, size);
		String fileName = prefix + prodId + ".jpg";
		String smallFileName = prefix + prodId + "-small.jpg";
		
		s3Service.uploadFile(imageService.getInputStream(smallJpgImage, "jpg"), smallFileName, "image");
		return s3Service.uploadFile(imageService.getInputStream(jpgImage, "jpg"), fileName, "image");
	}
}
