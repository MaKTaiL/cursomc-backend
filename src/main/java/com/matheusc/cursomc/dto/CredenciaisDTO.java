package com.matheusc.cursomc.dto;

import java.io.Serializable;

import com.matheusc.cursomc.domain.Cliente;

public class CredenciaisDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String email;
	private String senha;
	
	public CredenciaisDTO() {
	}
	
	public CredenciaisDTO(Cliente cli) {
		email = cli.getEmail();
		senha = cli.getSenha();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}
