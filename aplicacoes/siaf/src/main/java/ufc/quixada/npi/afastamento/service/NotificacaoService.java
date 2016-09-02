package ufc.quixada.npi.afastamento.service;

import ufc.quixada.npi.afastamento.model.Notificacao;
import ufc.quixada.npi.afastamento.model.Reserva;

public interface NotificacaoService {

	void notificar(Reserva reserva, Notificacao tipoNotificacao, String autor);

}
