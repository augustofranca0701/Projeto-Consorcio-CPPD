package com.consorcio.api.dto.UserDTO;

import java.util.Date;

public record PaymentDTO(Long id, Date dataVencimento, Long valor, Boolean isPaid, String nomeGrupo)
{
}
