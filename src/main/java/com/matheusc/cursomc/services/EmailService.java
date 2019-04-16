package com.matheusc.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.matheusc.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
}
